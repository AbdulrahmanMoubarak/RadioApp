package com.training.radioapptrial.channelsGetViewPlay.model

import java.io.Serializable

abstract class SoundItem(
    var image_url: String,
    var name: String,
): Serializable {
}