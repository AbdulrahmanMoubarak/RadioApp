package com.training.radioapptrial.channelsGetViewPlay.channelsapi

import com.training.radioapptrial.channelsGetViewPlay.model.ApiResponseModel
import com.training.radioapptrial.channelsGetViewPlay.util.NetworkConstants
import okhttp3.internal.EMPTY_RESPONSE
import retrofit2.Response
import javax.inject.Inject

class RadioStationsClient
@Inject
constructor(var service: ApiService) {


    suspend fun getRadioStations(page: Int): Response<ApiResponseModel>{
        if(page in 1..3) {
            return service.getRadioStations(NetworkConstants.COUNTRY_CODE, page)
        }
        else{
            return Response.error(0, EMPTY_RESPONSE)
        }
    }
}