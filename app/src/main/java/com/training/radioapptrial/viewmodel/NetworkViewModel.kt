package com.training.radioapptrial.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.training.radioapptrial.api.ApiService
import com.training.radioapptrial.api.RadioStationsClient
import com.training.radioapptrial.ui.paging.StationsPagingSource
import com.training.radioapptrial.util.NetworkConstants
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