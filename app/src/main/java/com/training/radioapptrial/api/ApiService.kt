package com.training.radioapptrial.api

import com.training.radioapptrial.model.ApiResponseModel
import com.training.radioapptrial.util.NetworkConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(NetworkConstants.END_POINT)
    suspend fun getRadioStations(
        @Query(NetworkConstants.COUNTRY_CODE_PARAM) query:String=NetworkConstants.COUNTRY_CODE,
        @Query(NetworkConstants.PAGE_PARAM) page:Int
    ): Response<ApiResponseModel>
}