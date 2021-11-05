package com.training.radioapptrial.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.training.radioapptrial.R
import com.training.radioapptrial.ui.adapter.ChannelAdapter
import com.training.radioapptrial.viewmodel.NetworkViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_radio_channels.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FragmentRadioChannels : Fragment() {

    private val viewModel: NetworkViewModel by viewModels()
    private var recyclerAdapter = ChannelAdapter()
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
            this.adapter = recyclerAdapter
        }

        loadChannels()
    }

    private fun loadChannels() {
        lifecycleScope.launch {
            viewModel.Stations.collect {
                recyclerAdapter.submitData(it)
            }
        }
    }
}