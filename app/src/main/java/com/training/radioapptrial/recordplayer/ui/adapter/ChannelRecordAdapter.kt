package com.training.radioapptrial.recordplayer.ui.adapter;

import android.content.ClipData
import android.graphics.Color.red
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.training.radioapptrial.R
import com.training.radioapptrial.recordplayer.model.ChannelMediaRecordModel
import kotlinx.android.synthetic.main.record_item.view.*

class ChannelRecordAdapter(var onClick :(ChannelMediaRecordModel) -> Unit) :
    RecyclerView.Adapter<ChannelRecordAdapter.ChannelRecordViewHolder>() {
    private var Item_List = ArrayList<ChannelMediaRecordModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelRecordViewHolder {
        return ChannelRecordViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChannelRecordViewHolder, position: Int) {
        holder.recordName.text = Item_List.get(position).recordName
        holder.recordName.isSelected = true

        holder.itemView.setOnClickListener {
            onClick(Item_List.get(position))
        }
    }

    override fun getItemCount(): Int {
        return Item_List.size
        notifyDataSetChanged()
    }

    fun setItem_List(list: List<ChannelMediaRecordModel>) {
        Item_List = list as ArrayList<ChannelMediaRecordModel>
    }

    class ChannelRecordViewHolder : RecyclerView.ViewHolder {

        var recordName: TextView
        var layout: ConstraintLayout
        constructor(itemView: View) : super(itemView) {
            recordName = itemView.findViewById(R.id.rec_name_text)
            layout = itemView.rec_layout
        }
    }
}