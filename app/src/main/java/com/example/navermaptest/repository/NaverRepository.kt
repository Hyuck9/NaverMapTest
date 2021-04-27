package com.example.navermaptest.repository

import androidx.annotation.WorkerThread
import com.example.navermaptest.network.NaverAPI
import com.skydoves.sandwich.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
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
        onError: (String?) -> Unit
    )  = flow {

        naverAPI.getDirection5(start, goal)
            .suspendOnSuccess {
                Timber.i("suspendOnSuccess")
                data?.let {
                    emit(it)
                }
            }
            .suspendOnFailure { Timber.i("suspendOnFailure") }
            .onError { onError(message()) }
            .onException { onError(message) }

    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)

}