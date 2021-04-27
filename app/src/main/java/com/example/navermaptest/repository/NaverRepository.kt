package com.example.navermaptest.repository

import androidx.annotation.WorkerThread
import com.example.navermaptest.model.DirectionsResponse
import com.example.navermaptest.network.NaverAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber

class NaverRepository constructor(
    private val naverAPI: NaverAPI
) {

    init {
        Timber.d("Injection NaverRepository")
    }

    @WorkerThread
    fun getDirection5(
        start: String,
        goal: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
    ): Flow<DirectionsResponse> {
        return flow {
            val direction5 = naverAPI.getDirection5(start, goal)
            if ( direction5.code == 0 ) {
                emit(naverAPI.getDirection5(start, goal))
            }

        }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)
    }

}