package com.training.radioapptrial.channelsGetViewPlay.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media.AudioManagerCompat.requestAudioFocus
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.training.radioapptrial.R
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import com.training.radioapptrial.channelsGetViewPlay.ui.adapter.ChannelAdapter
import com.training.radioapptrial.channelsGetViewPlay.ui.paging.StationsPagingSource
import com.training.radioapptrial.channelsGetViewPlay.viewmodel.MediaViewModel
import com.training.radioapptrial.channelsGetViewPlay.viewmodel.NetworkViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_radio_channels.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentRadioChannels : Fragment() {

    private val networkViewModel: NetworkViewModel by viewModels()
    private val mediaViewModel: MediaViewModel by viewModels()
    var animTime = 0

    private var recyclerAdapter = ChannelAdapter()
    val normalRecyclerAdapter = recyclerAdapter.NormalRecyclerAdapter(::onChannelClick, ::displayProgressbar)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_radio_channels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        displayProgressbar(true)
        super.onViewCreated(view, savedInstanceState)
        channels_recycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = normalRecyclerAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.recycler_animation)
            //scheduleLayoutAnimation()
        }

        initExoPlayer()

        loadChannels()

        subscribeGenres()


        miniPlayer.animate().translationYBy(300f).setDuration(0).start()
        genresSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                normalRecyclerAdapter.selectedFilter = parent?.getItemAtPosition(p2).toString()

                normalRecyclerAdapter.submitList(recyclerAdapter.snapshot().items as ArrayList<RadioChannelModel>)

                channels_recycler.adapter = normalRecyclerAdapter

                if(parent?.getItemAtPosition(p2).toString() != "Genres" || animTime < 1) {
                    animTime += 1
                    channels_recycler.scheduleLayoutAnimation()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        miniPlayer.playPauseButton.setOnClickListener {
            if (mediaViewModel.isPlaying) {
                pausePlayer()
            } else {
                resumePlayer()
            }
        }
    }

    private fun loadChannels() {
        lifecycleScope.launch {
            networkViewModel.Stations.collect {
                recyclerAdapter.submitData(it)
                normalRecyclerAdapter.submitList(recyclerAdapter.snapshot().items as ArrayList<RadioChannelModel>)
            }
        }
    }

    private fun subscribeGenres() {
        StationsPagingSource.genresLiveData.observe(requireActivity()) {
            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item_selected, it)
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            genresSelector.adapter = adapter
        }
    }

    private fun initExoPlayer() {
        mediaViewModel.setPlayerEvents(::onPlayerError, ::displayProgressbar)
    }

    private fun playNewStation(channel: RadioChannelModel) {
        mediaViewModel.playNewStation(channel)
    }

    private fun onChannelClick(channel: RadioChannelModel) {
        /*
        Bundle().let {
            it.putSerializable("channel", channel)
            findNavController().navigate(R.id.action_fragmentRadioChannels_to_channelDetailFragment, it)
        }

         */


        if(miniPlayer.visibility == View.INVISIBLE) {
            miniPlayer.visibility = View.VISIBLE
            miniPlayer.animate().translationYBy(-300f).setDuration(800).start()
        }
        miniPlayer.loadMedia(channel)
        miniPlayer.play()
        playNewStation(channel)
        /**/
    }

    private fun pausePlayer() {
        if(!mediaViewModel.isFailure) {
            mediaViewModel.pausePlayer()
            miniPlayer.pause()
        }
    }

    private fun resumePlayer() {
        if(!mediaViewModel.isFailure) {
            mediaViewModel.resumePlayer()
            miniPlayer.play()
        }
    }

    private fun onPlayerError(){
        mediaViewModel.isPlaying = false
        mediaViewModel.isFailure = true
        miniPlayer.pause()
        Toast.makeText(requireContext(), "Error playing the channel", Toast.LENGTH_SHORT).show()
    }

    private fun displayProgressbar(isDisplayed: Boolean) {
        //miniPlayer.pause()
        if(progress_bar != null) {
            progress_bar.visibility = if (isDisplayed) View.VISIBLE else View.GONE
        }
    }


}