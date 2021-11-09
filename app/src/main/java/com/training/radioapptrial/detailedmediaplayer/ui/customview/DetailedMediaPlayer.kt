package com.training.radioapptrial.detailedmediaplayer.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
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
    private var isPlaying = false

    init {
        viewItem = inflate(getContext(), R.layout.detailed_media_player, this)
        playPauseButton = viewItem.detail_buttonPlayPause
    }

    fun loadMedia(soundItem: RadioChannelModel){
        isPlaying = false

        viewItem.imageViewDetailedSound.load(soundItem.image_url){
            crossfade(true)
            crossfade(500)
        }
        viewItem.detail_channelName.text = soundItem.name
        viewItem.detail_buttonPlayPause.setImageResource(R.drawable.ic_play_detail_svgrepo_com)
        viewItem.detail_genre.text = viewItem.detail_genre.text.toString() + soundItem.genre
        viewItem.detail_country.text = viewItem.detail_country.text.toString() + soundItem.countryCode
    }

    fun play(){
        if(!isPlaying){
            isPlaying = true
            viewItem.detail_buttonPlayPause.animate().rotationBy(180f).setDuration(200).start()
            viewItem.detail_buttonPlayPause.load(R.drawable.ic_pause_detail_svgrepo_com)
            viewItem.detail_buttonPlayPause.animate().rotationBy(180f).setDuration(200).start()
        }
    }

    fun pause(){
        if(isPlaying){
            isPlaying = false
            viewItem.detail_buttonPlayPause.animate().rotationBy(180f).setDuration(200).start()
            viewItem.detail_buttonPlayPause.load(R.drawable.ic_play_detail_svgrepo_com)
            viewItem.detail_buttonPlayPause.animate().rotationBy(180f).setDuration(200).start()
        }
    }
}