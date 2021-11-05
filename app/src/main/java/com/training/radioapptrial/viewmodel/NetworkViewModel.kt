package com.training.radioapptrial.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.training.radioapptrial.api.ApiService
import com.training.radioapptrial.api.RadioStationsClient
import com.training.radioapptrial.ui.paging.StationsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel
@Inject
constructor(
    val client: RadioStationsClient
): ViewModel() {

    val Stations = Pager(PagingConfig(50)){
        StationsPagingSource(client)
    }.flow.cachedIn(viewModelScope)
}