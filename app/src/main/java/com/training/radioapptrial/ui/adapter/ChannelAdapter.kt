package com.training.radioapptrial.ui.adapter;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.training.radioapptrial.R
import com.training.radioapptrial.model.RadioChannelModel

class ChannelAdapter() : PagingDataAdapter<RadioChannelModel, ChannelAdapter.ChannelViewHolder>(diffCallback) {


    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<RadioChannelModel>() {
            override fun areItemsTheSame(oldItem: RadioChannelModel, newItem: RadioChannelModel): Boolean {
                return oldItem.channel_id == newItem.channel_id
            }

            override fun areContentsTheSame(oldItem: RadioChannelModel, newItem: RadioChannelModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        return ChannelViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.channelName.text = getItem(position)?.name
        val imageUrl = getItem(position)?.image_url
        holder.channelImage.load(imageUrl){
            crossfade(true)
            crossfade(1000)
        }
    }

    class ChannelViewHolder : RecyclerView.ViewHolder {

        var channelImage: ImageView
        var channelName: TextView
        constructor(itemView: View) : super(itemView) {
            channelImage = itemView.findViewById(R.id.imageViewChannel)
            channelName = itemView.findViewById(R.id.channel_name)
        }
    }
}