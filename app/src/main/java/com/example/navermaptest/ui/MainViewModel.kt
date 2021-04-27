package com.example.navermaptest.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navermaptest.repository.NaverRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(private val naverRepository: NaverRepository): ViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getPath(start:String, goal:String) {
        viewModelScope.launch {
            getDirection5(start, goal).collect {
                if ( it.code == 0 ) {
                    Timber.i("route : ${it.firstRoute}")
                } else {
                    Timber.i("DirectionsResponse : $it")
                }
            }
        }
    }

    private fun getDirection5(start:String, goal:String) = naverRepository.getDirection5(
        start = start,
        goal = goal,
        onStart = { _isLoading.postValue(true) },
        onComplete = { _isLoading.postValue(false) },
        onError = { Timber.i("onError : $it") }
    )

}