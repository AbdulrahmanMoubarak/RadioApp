package com.training.radioapptrial.detailedmediaplayer.ui.customview

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import coil.load
import com.training.radioapptrial.R
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import kotlinx.android.synthetic.main.detailed_media_player.view.*

class DetailedMediaPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var viewItem: View
    var playPauseButton: ImageView
    var recordButton: ImageView
    private var isPlaying = true

    init {
        viewItem = inflate(getContext(), R.layout.detailed_media_player, this)
        playPauseButton = viewItem.detail_buttonPlayPause
        recordButton = viewItem.channel_record
    }

    fun loadMedia(soundItem: RadioChannelModel){
        isPlaying = true

        viewItem.imageViewDetailedSound.load(soundItem.image_url){
            crossfade(true)
            crossfade(500)
        }
        viewItem.detail_channelName.apply {
            text = soundItem.name
            isSelected = true
        }
        viewItem.detail_buttonPlayPause.setImageResource(R.drawable.ic_pause_detail_svgrepo_com)

        viewItem.detail_genre.apply {
            text = soundItem.genre
            viewItem.detail_genre.isSelected = true
        }

        viewItem.detail_country.apply {
            text = text.toString() + soundItem.countryCode
            isSelected = true
        }
    }

    fun play(){
        if(!isPlaying){
            isPlaying = true
            viewItem.detail_buttonPlayPause.animate().rotationBy(360f).setDuration(500).start()
            viewItem.detail_buttonPlayPause.setImageResource(R.drawable.ic_pause_detail_svgrepo_com)
        }
    }

    fun pause(){
        if(isPlaying){
            isPlaying = false
            viewItem.detail_buttonPlayPause.animate().rotationBy(360f).setDuration(500).start()
            viewItem.detail_buttonPlayPause.setImageResource(R.drawable.ic_play_detail_svgrepo_com)
        }
    }

}