package com.training.radioapptrial.recordplayer.ui

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.training.radioapptrial.R
import com.training.radioapptrial.channelsGetViewPlay.viewmodel.MediaViewModel
import com.training.radioapptrial.recordplayer.model.ChannelMediaRecordModel
import com.training.radioapptrial.recordplayer.ui.adapter.ChannelRecordAdapter
import com.training.radioapptrial.recordplayer.viewmodel.RecordsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_record_list.*

@AndroidEntryPoint
class RecordListActivity : AppCompatActivity() {
    private val recordsViewModel: RecordsViewModel by viewModels()
    private var recyclerAdapter = ChannelRecordAdapter(::onRecordClick)
    private var isPlaying = false
    private var isStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_record_list)
        supportActionBar?.hide()

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        record_name.isSelected = true
    }

    override fun onStart() {
        super.onStart()
        recyclerAdapter.setItem_List(recordsViewModel.getRecordList(this))
        record_recycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = recyclerAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(this@RecordListActivity, R.anim.recycler_animation)
        }

        img_buttonPauseResume.setOnClickListener {
            if(isStarted){
                if(isPlaying){
                    pauseRecord()
                }else{
                    resumeRecord()
                }
            }
        }
    }

    private fun onRecordClick(rec: ChannelMediaRecordModel){
        recordsViewModel.playNewRecord(this, rec)
        img_buttonPauseResume.setImageResource(R.drawable.ic_pause_detail_svgrepo_com)
        isStarted = true
        isPlaying = true
        record_name.text = rec.recordName
    }

    private fun resumeRecord(){
        img_buttonPauseResume.setImageResource(R.drawable.ic_pause_detail_svgrepo_com)
        recordsViewModel.resumeRecord()
        isPlaying = true
    }

    private fun pauseRecord(){
        img_buttonPauseResume.setImageResource(R.drawable.ic_play_detail_svgrepo_com)
        recordsViewModel.pauseRecord()
        isPlaying = false
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onDestroy() {
        super.onDestroy()
        recordsViewModel.releasePlayer()
    }

}