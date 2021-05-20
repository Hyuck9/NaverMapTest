package com.example.navermaptest.ui

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.navermaptest.R
import com.example.navermaptest.databinding.ActivityMainBinding
import com.example.navermaptest.extensions.getStringLonLat
import com.example.navermaptest.extensions.observeLiveData
import com.example.navermaptest.extensions.onClick
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.ArrowheadPathOverlay
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.GeometryUtils
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val binding: ActivityMainBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }
    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var naverMap: NaverMap
    private var trackingEnabled = false
    private var locationEnabled = false
    private var waiting = false
    private var pathCoords = mutableListOf<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = mainViewModel

            mapView.getMapAsync(this@MainActivity)

            buttonDirections.onClick {
                mainViewModel.getPath("127.06439971923828,37.5416145324707", "126.98096285561193,37.565341558443954")
//                mainViewModel.getPath("127.05606699932702,37.54458881537507", "127.06763807196779,37.57010162090559")
            }
        }

    }

    var pathOverlay: PathOverlay? = null
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        observeLiveData(mainViewModel.responseData) { res ->

            res.firstRoute?.let {  route ->
                pathCoords = route.coords as MutableList<LatLng>
                pathOverlay = PathOverlay().also {
                    it.coords = pathCoords
                    it.width = 32
                    it.outlineWidth = 8
                    it.color = ResourcesCompat.getColor(resources, R.color.purple_700, theme)
                    it.outlineColor = Color.WHITE
                    it.passedColor = Color.GRAY
                    it.passedOutlineColor = Color.WHITE
                    it.map = naverMap
                }



                /*route.guide?.forEach { guide ->
                    try {
                        arrowheadPathOverlay(
                            mutableListOf(pathCoords[guide.pointIndex-1],
                                pathCoords[guide.pointIndex],
                                pathCoords[guide.pointIndex+1])
                        )
                    } catch (e: IndexOutOfBoundsException) {
                        Timber.i(e)
                    }

                    infoWindow(pathCoords[guide.pointIndex], guide.instructions!!)
                }*/
            }
        }

        setFab()








    }

    private fun arrowheadPathOverlay(latLngs: MutableList<LatLng>) {
        ArrowheadPathOverlay().apply {
            coords = latLngs
            width = 32
            color = Color.WHITE
            outlineWidth = 8
            outlineColor = ResourcesCompat.getColor(resources, R.color.teal_200, theme)
            map = naverMap
        }
    }

    private fun infoWindow(latLng: LatLng, msg: String) {
        InfoWindow().apply {
            position = latLng
            adapter = object: InfoWindow.DefaultTextAdapter(this@MainActivity) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return msg
                }
            }
            setOnClickListener {
                close()
                true
            }
            open(naverMap)
        }
    }

    private fun setFab() {
        binding.fab.setOnClickListener {
            if (trackingEnabled) {
                disableLocation()
                binding.fab.setImageResource(R.drawable.ic_my_location_black_24dp)
            } else {
                binding.fab.setImageDrawable(CircularProgressDrawable(this).apply {
                    setStyle(CircularProgressDrawable.LARGE)
                    setColorSchemeColors(Color.WHITE)
                    start()
                })
                tryEnableLocation()
            }
            trackingEnabled = !trackingEnabled
        }
    }


    private fun disableLocation() {
        if (!locationEnabled) {
            return
        }
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback)
        locationEnabled = false
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            val lastLocation = locationResult?.lastLocation ?: return
            val coord = LatLng(lastLocation)
            val locationOverlay = naverMap.locationOverlay
            locationOverlay.position = coord
            locationOverlay.bearing = lastLocation.bearing
            naverMap.moveCamera(CameraUpdate.scrollTo(coord))
            if (waiting) {
                mainViewModel.getPath(coord.getStringLonLat(), "127.06763807196779,37.57010162090559")
                waiting = false
                binding.fab.setImageResource(R.drawable.ic_location_disabled_black_24dp)
                locationOverlay.isVisible = true
            } else {
                val minIndex = getIndexOfMinDistance(coord)
                val progress = minIndex.toDouble() / pathCoords.size
                val progress2 = GeometryUtils.getProgress(pathCoords, coord)
                Timber.i("프로그래스 값1 : $progress")
                Timber.i("프로그래스 값2 : $progress2")

                pathOverlay?.progress = progress2
            }
        }
    }

    private fun getIndexOfMinDistance(coord: LatLng): Int {
        var distance = 100000.0
        var minIndex = 0
        pathCoords.forEachIndexed { index, latLng ->
            if (index == 0)
                distance = distance(coord, latLng, "meter")
            else {
                if (distance > distance(coord, latLng, "meter")) {
                    minIndex = index
                }
            }
        }
        Timber.i("###### 최소 인덱스 : $minIndex")
        return minIndex
    }

    private fun distance(latLng1: LatLng, latLng2: LatLng, unit: String): Double {
        fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }
        fun rad2deg(rad: Double): Double {
            return rad * 180 / Math.PI
        }
        val theta = latLng1.longitude - latLng2.longitude
        var dist = sin(deg2rad(latLng1.latitude)) * sin(deg2rad(latLng2.latitude)) + cos(deg2rad(latLng1.latitude)) * cos(deg2rad(latLng2.latitude)) * cos(deg2rad(theta))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        if (unit === "kilometer") {
            dist *= 1.609344
        } else if (unit === "meter") {
            dist *= 1609.344
        }
        return dist
    }

    private fun tryEnableLocation() {
        if (PERMISSIONS.all { ContextCompat.checkSelfPermission(this, it) == PermissionChecker.PERMISSION_GRANTED }) {
            enableLocation()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE)
        }
    }

    private fun enableLocation() {
        GoogleApiClient.Builder(this)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                @SuppressLint("MissingPermission")
                override fun onConnected(bundle: Bundle?) {
                    val locationRequest = LocationRequest().apply {
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        interval = LOCATION_REQUEST_INTERVAL.toLong()
                        fastestInterval = LOCATION_REQUEST_INTERVAL.toLong()
                    }

                    LocationServices.getFusedLocationProviderClient(this@MainActivity)
                        .requestLocationUpdates(locationRequest, locationCallback, null)
                    locationEnabled = true
                    waiting = true
                }

                override fun onConnectionSuspended(i: Int) {
                }
            })
            .addApi(LocationServices.API)
            .build()
            .connect()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PermissionChecker.PERMISSION_GRANTED }) {
                enableLocation()
            } else {
                binding.fab.setImageResource(R.drawable.ic_my_location_black_24dp)
            }
            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    companion object {
        private const val LOCATION_REQUEST_INTERVAL = 1000
        private const val PERMISSION_REQUEST_CODE = 100
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }
}