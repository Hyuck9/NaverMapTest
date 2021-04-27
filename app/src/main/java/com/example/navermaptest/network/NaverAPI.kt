package com.example.navermaptest.network

import com.example.navermaptest.model.DirectionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverAPI {

    @GET("map-direction/v1/driving")
    suspend fun getDirection5(
        @Header("X-NCP-APIGW-API-KEY-ID") apiKeyID: String,
        @Header("X-NCP-APIGW-API-KEY") apiKey: String,
        @Query("start") start: String,
        @Query("goal") goal: String,
    ): Call<DirectionsResponse>

}