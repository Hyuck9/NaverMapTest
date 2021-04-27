package com.example.navermaptest.di

import com.example.navermaptest.network.HttpRequestInterceptor
import com.example.navermaptest.network.NaverAPI
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {

    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpRequestInterceptor())
            .build()
    }

    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl("https://naveropenapi.apigw.ntruss.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(NaverAPI::class.java) }
}