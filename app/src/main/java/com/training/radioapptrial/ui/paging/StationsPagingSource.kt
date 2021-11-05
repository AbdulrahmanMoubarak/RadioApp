package com.training.radioapptrial.ui.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.training.radioapptrial.api.RadioStationsClient
import com.training.radioapptrial.model.RadioChannelModel

class StationsPagingSource(var client: RadioStationsClient) :
    PagingSource<Int, RadioChannelModel>() {
    override fun getRefreshKey(state: PagingState<Int, RadioChannelModel>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RadioChannelModel> {
        return try {
            val page = params.key?: 1

            val response = client.getRadioStations(page)
            val responseData = mutableListOf<RadioChannelModel>()
            val data = response.body()?.radios ?: emptyList()

            responseData.addAll(data)
            Log.d("here", "load: got data")
            LoadResult.Page(
                responseData,
                if (page <= 1) null else page.minus(1),
                if (page >= 3) null else  page.plus(1)
            )

        } catch (e: Exception) {
            Log.d("here", "load: failed to get data")
            LoadResult.Error(e)
        }
    }
}