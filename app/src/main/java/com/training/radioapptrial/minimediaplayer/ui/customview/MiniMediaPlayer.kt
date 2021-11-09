package com.training.radioapptrial.minimediaplayer.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.training.radioapptrial.R
import com.training.radioapptrial.channelsGetViewPlay.model.SoundItem
import kotlinx.android.synthetic.main.mini_media_player.view.*

class MiniMediaPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var viewItem: View
    var playPauseButton: ImageView
    private var isPlaying = false

    init {
        viewItem = inflate(getContext(), R.layout.mini_media_player, this)
        playPauseButton = viewItem.button_play_pause
    }

    fun loadMedia(soundItem: SoundItem){
        isPlaying = false
        viewItem.imageView_sound.load(soundItem.image_url){
            crossfade(true)
            crossfade(500)
        }
        viewItem.textView_trackName.text = soundItem.name
        viewItem.button_play_pause.setImageResource(R.drawable.ic_play_svgrepo_com)
    }

    fun play(){
        if(!isPlaying){
            isPlaying = true
            viewItem.button_play_pause.animate().rotationBy(180f).setDuration(200).start()
            viewItem.button_play_pause.load(R.drawable.ic_pause_svgrepo_com)
            viewItem.button_play_pause.animate().rotationBy(180f).setDuration(200).start()
        }
    }

    fun pause(){
        if(isPlaying){
            isPlaying = false
            viewItem.button_play_pause.animate().rotationBy(180f).setDuration(200).start()
            viewItem.button_play_pause.load(R.drawable.ic_play_svgrepo_com)
            viewItem.button_play_pause.animate().rotationBy(180f).setDuration(200).start()
        }
    }
}