package com.training.radioapptrial.api

import com.training.radioapptrial.model.ApiResponseModel
import com.training.radioapptrial.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(Constants.END_POINT)
    suspend fun getRadioStations(
        @Query(Constants.COUNTRY_CODE_PARAM) query:String=Constants.COUNTRY_CODE,
        @Query(Constants.PAGE_PARAM) page:Int
    ): Response<ApiResponseModel>
}