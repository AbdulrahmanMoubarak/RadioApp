package com.training.radioapptrial.channelsGetViewPlay.ui.paging

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.training.radioapptrial.channelsGetViewPlay.channelsapi.RadioStationsClient
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel

class StationsPagingSource(var client: RadioStationsClient) :
    PagingSource<Int, RadioChannelModel>() {
    override fun getRefreshKey(state: PagingState<Int, RadioChannelModel>): Int? {
        return null
    }

    companion object{
        val genresLiveData: MutableLiveData<List<String>> =
            MutableLiveData()
    }

    var genreList = mutableListOf("Genres")

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RadioChannelModel> {
        return try {
            val page = params.key?: 1

            val response = client.getRadioStations(page)
            var responseData = mutableListOf<RadioChannelModel>()
            val data = response.body()?.radios ?: emptyList()
            Log.d("here", "${response.body()}")
            responseData.addAll(data)
            collectGenres(responseData)
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


    private fun collectGenres(list: MutableList<RadioChannelModel>){
        for(item in list){
            val genres = item.genre.split(',')
            for(genre in genres) {
                if (genre != "") {
                    if (!genreList.contains(genre)) {
                        genreList.add(genre)
                    }
                }
            }
        }
        genreList.remove("Genres")
        genreList.sort()
        genreList.add(0, "Genres")
        genresLiveData.postValue(genreList)
    }
}