package com.training.radioapptrial.ui.adapter;

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingData
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
        val genre = getItem(position)?.genre
        holder.channelName.text = getItem(position)?.name
        val imageUrl = getItem(position)?.image_url
        holder.channelImage.load(imageUrl){
            crossfade(true)
            crossfade(1000)
        }
    }

    inner class NormalRecyclerAdapter: RecyclerView.Adapter<ChannelViewHolder>(){
        var selectedFilter = "Genres"
        private var Item_List = ArrayList<RadioChannelModel>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
            return ChannelViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
            val genre = Item_List.get(position).genre
            holder.channelName.text = Item_List.get(position).name
            val imageUrl = Item_List.get(position).image_url
            holder.channelImage.load(imageUrl){
                crossfade(true)
                crossfade(1000)
            }


        }

        fun filter(){
            if(selectedFilter != "Genres") {
                val iter = Item_List.iterator()
                while(iter.hasNext()){
                    val item = iter.next()
                    item.genre.let { g ->
                        if (!g.contains(selectedFilter)) {
                            iter.remove()
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return Item_List.count()
        }

        fun submitList(list: ArrayList<RadioChannelModel>){
            Item_List = list
            filter()
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