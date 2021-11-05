package com.training.radioapptrial.model

data class ApiResponseModel(
    var has_more: Boolean,
    var page_count: Int,
    var current_page: Int,
    var radios: List<RadioChannelModel>
)