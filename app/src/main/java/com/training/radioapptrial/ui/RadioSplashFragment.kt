package com.training.radioapptrial.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import coil.load
import com.training.radioapptrial.R
import kotlinx.android.synthetic.main.fragment_radio_splash.view.*


class RadioSplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_radio_splash, container, false)
        view.imageSplash.apply {
            load(R.drawable.ic_radio_svgrepo_com) {
                crossfade(true)
                crossfade(2000)
            }
            animate().translationYBy(100f).setDuration(1500).start()
        }
        Handler(Looper.getMainLooper())
            .postDelayed({
                findNavController().navigate(R.id.action_radioSplashFragment_to_fragmentRadioChannels)
            }, 2500)
        return view
    }
}