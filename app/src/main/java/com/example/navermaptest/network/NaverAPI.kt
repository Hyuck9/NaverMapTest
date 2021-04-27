package com.example.navermaptest.network

import com.example.navermaptest.common.Constants
import com.example.navermaptest.model.DirectionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NaverAPI {

    @Headers("X-NCP-APIGW-API-KEY-ID:${Constants.NAVER_CLIENT_ID}", "X-NCP-APIGW-API-KEY:${Constants.NAVER_CLIENT_SECRET}")
    @GET("map-direction/v1/driving")
    suspend fun getDirection5(
        @Query("start") start: String,
        @Query("goal") goal: String,
    ): Call<DirectionsResponse>

}