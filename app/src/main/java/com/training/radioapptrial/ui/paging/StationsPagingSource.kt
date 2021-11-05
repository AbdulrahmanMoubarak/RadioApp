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
            Log.d("here", "${response.body()}")
            responseData.addAll(data)
            LoadResult.Page(
                responseData,
                prevKey = if (page <= 1) null else page-1,
                nextKey = if (page >= 3) null else  page+1
            )

        } catch (e: Exception) {
            Log.d("here", "${e.message}")
            LoadResult.Error(e)

        }
    }
}