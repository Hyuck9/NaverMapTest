package com.example.navermaptest

import android.app.Application
import com.example.navermaptest.common.Constants
import com.example.navermaptest.di.networkModule
import com.example.navermaptest.di.repositoryModule
import com.example.navermaptest.di.viewModelModule
import com.naver.maps.map.NaverMapSdk
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(networkModule, repositoryModule, viewModelModule))
        }

        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(Constants.NAVER_CLIENT_ID)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}