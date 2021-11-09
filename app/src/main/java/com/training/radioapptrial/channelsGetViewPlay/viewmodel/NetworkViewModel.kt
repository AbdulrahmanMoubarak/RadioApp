package com.training.radioapptrial.channelsGetViewPlay.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.training.radioapptrial.channelsGetViewPlay.channelsapi.RadioStationsClient
import com.training.radioapptrial.channelsGetViewPlay.ui.paging.StationsPagingSource
import com.training.radioapptrial.channelsGetViewPlay.util.NetworkConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel
@Inject
constructor(
    val client: RadioStationsClient
): ViewModel() {

    var Stations = Pager(PagingConfig(NetworkConstants.PAGE_SIZE)) {
        StationsPagingSource(client)
    }.flow.cachedIn(viewModelScope)

}