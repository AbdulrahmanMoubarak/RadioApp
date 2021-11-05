package com.training.radioapptrial.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.training.radioapptrial.R
import com.training.radioapptrial.model.RadioChannelModel
import com.training.radioapptrial.ui.adapter.ChannelAdapter
import com.training.radioapptrial.ui.paging.StationsPagingSource
import com.training.radioapptrial.viewmodel.NetworkViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_radio_channels.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FragmentRadioChannels : Fragment() {

    private val viewModel: NetworkViewModel by viewModels()
    private var recyclerAdapter = ChannelAdapter()
    val normalRecyclerAdapter = recyclerAdapter.NormalRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_radio_channels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channels_recycler.apply{
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
            this.adapter = normalRecyclerAdapter
        }

        loadChannels()

        subscribeGenres()

        genresSelector.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                normalRecyclerAdapter.selectedFilter = parent?.getItemAtPosition(p2).toString()

                normalRecyclerAdapter.submitList(recyclerAdapter.snapshot().items as ArrayList<RadioChannelModel>)

                channels_recycler.adapter = normalRecyclerAdapter
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    private fun loadChannels() {
        lifecycleScope.launch {
            viewModel.Stations.collect {
                recyclerAdapter.submitData(it)
                normalRecyclerAdapter.submitList(recyclerAdapter.snapshot().items as ArrayList<RadioChannelModel>)
            }
        }
    }

    private fun filterByGenre(genre: String):  MutableList<RadioChannelModel>{
        val list = recyclerAdapter.snapshot().items as MutableList<RadioChannelModel>
        if(genre != "Genre") {
            for (item in list) {
                if (!item.genre.equals(genre)) {
                    list.remove(item)
                }
            }
        }
        return list
    }


    private fun subscribeGenres(){
        StationsPagingSource.genresLiveData.observe(requireActivity()){
            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item_selected, it)
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            genresSelector.adapter = adapter
        }
    }
}