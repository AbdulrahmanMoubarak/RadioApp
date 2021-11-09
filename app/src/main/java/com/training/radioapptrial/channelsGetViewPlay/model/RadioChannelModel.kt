package com.training.radioapptrial.channelsGetViewPlay.model

class RadioChannelModel(
    image_url: String,
    name: String,
    var uri: String,
    var channel_id: Int,
    var countryCode: String,
    var genre: String
): SoundItem(image_url, name)