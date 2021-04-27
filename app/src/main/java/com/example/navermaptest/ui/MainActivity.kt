package com.example.navermaptest.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.example.navermaptest.R
import com.example.navermaptest.databinding.ActivityMainBinding
import com.example.navermaptest.extensions.observeLiveData
import com.example.navermaptest.extensions.onClick
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val binding: ActivityMainBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }
    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = mainViewModel

            mapView.getMapAsync(this@MainActivity)

            buttonDirections.onClick {
                mainViewModel.getPath("126.98096285561193,37.565341558443954", "127.06439971923828,37.5416145324707")
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        observeLiveData(mainViewModel.pathLiveData) { latlngs ->
            PathOverlay().also {
                it.coords = latlngs
                it.width = 32
                it.outlineWidth = 8
                it.color = ResourcesCompat.getColor(resources, R.color.purple_700, theme)
                it.outlineColor = Color.WHITE
                it.passedColor = ResourcesCompat.getColor(resources, R.color.white, theme)
                it.passedOutlineColor = Color.WHITE
                it.map = naverMap
            }
        }
    }

}