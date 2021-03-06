package com.training.radioapptrial.ui

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.training.radioapptrial.R
import com.training.radioapptrial.model.RadioChannelModel
import com.training.radioapptrial.ui.adapter.ChannelAdapter
import com.training.radioapptrial.ui.paging.StationsPagingSource
import com.training.radioapptrial.viewmodel.MediaViewModel
import com.training.radioapptrial.viewmodel.NetworkViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_radio_channels.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentRadioChannels : Fragment() {

    private val networkViewModel: NetworkViewModel by viewModels()
    private val mediaViewModel: MediaViewModel by viewModels()

    private var recyclerAdapter = ChannelAdapter()
    val normalRecyclerAdapter = recyclerAdapter.NormalRecyclerAdapter(::onChannelClick)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_radio_channels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channels_recycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = normalRecyclerAdapter
        }
        initExoPlayer()

        loadChannels()

        subscribeGenres()

        requestAudioFocus()

        miniPlayer.animate().translationYBy(300f).setDuration(0).start()
        genresSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                normalRecyclerAdapter.selectedFilter = parent?.getItemAtPosition(p2).toString()

                normalRecyclerAdapter.submitList(recyclerAdapter.snapshot().items as ArrayList<RadioChannelModel>)

                channels_recycler.adapter = normalRecyclerAdapter
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        button_play_pause.setOnClickListener {
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
        if(miniPlayer.visibility == View.INVISIBLE) {
            miniPlayer.visibility = View.VISIBLE
            miniPlayer.animate().translationYBy(-300f).setDuration(300).start()
        }
        imageView_channel.load(channel.image_url) {
            crossfade(true)
            crossfade(1000)
        }
        button_play_pause.setImageResource(R.drawable.ic_pause_svgrepo_com)
        textView_channelName.text = channel.name
        playNewStation(channel)
    }

    private fun pausePlayer() {
        if(!mediaViewModel.isFailure) {
            mediaViewModel.pausePlayer()
            button_play_pause.setImageResource(R.drawable.ic_play_svgrepo_com)
        }
    }

    private fun resumePlayer() {
        if(!mediaViewModel.isFailure) {
            mediaViewModel.resumePlayer()
            button_play_pause.setImageResource(R.drawable.ic_pause_svgrepo_com)
        }
    }

    private fun onPlayerError(){
        mediaViewModel.isPlaying = false
        mediaViewModel.isFailure = true
        button_play_pause.setImageResource(R.drawable.ic_play_svgrepo_com)
        Toast.makeText(requireContext(), "Error playing the channel", Toast.LENGTH_SHORT).show()
    }

    private fun displayProgressbar(isDisplayed: Boolean) {
        progress_bar.visibility = if (isDisplayed) View.VISIBLE else View.GONE
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val am = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.requestAudioFocus(
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(addOnAudioFocusChangedListener()).build()
            )
        }
    }

    private fun addOnAudioFocusChangedListener(): OnAudioFocusChangeListener{
        return OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    resumePlayer()
                }
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> {
                    resumePlayer()
                }

                AudioManager.AUDIOFOCUS_LOSS -> {
                    pausePlayer()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    pausePlayer()
                }
            }
        }
    }
}