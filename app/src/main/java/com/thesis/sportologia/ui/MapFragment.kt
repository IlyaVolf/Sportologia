package com.thesis.sportologia.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.thesis.sportologia.databinding.FragmentMapBinding
import com.thesis.sportologia.ui.events.CreateEditEventFragment
import com.thesis.sportologia.ui.posts.CreateEditPostFragment
import com.thesis.sportologia.ui.users.SearchContainerFragmentUsers
import com.thesis.sportologia.utils.toast
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView


class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView

    private lateinit var client: FusedLocationProviderClient
    private lateinit var requestCode: String
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestCode = arguments?.getString("requestCode") ?: throw Exception()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentMapBinding.inflate(inflater, container, false)

        initOnSwitchToListButtonPressed()
        initLocationTracker()
        renderMap()

        return binding.root
    }

    private fun initOnSwitchToListButtonPressed() {
        binding.switchToListButton.setOnClickListener {
            sendSwitchAction()
        }
    }

    private fun sendSwitchAction() {
        requireActivity().supportFragmentManager.setFragmentResult(
            requestCode,
            bundleOf()
        )
    }

    private fun  initLocationTracker() {
        client = LocationServices.getFusedLocationProviderClient(context!!)
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // When permission is granted
            // Call method
            getCurrentLocation()
        } else {
            // When permission is not granted
            // Call method
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
        }
    }

    private fun renderMap() {
        mapView = binding.mapView
        /*if (location != null) {
            mapView.map.move(
                CameraPosition(
                    Point(location!!.latitude, location!!.longitude),
                    11.0f,
                    0.0f,
                    0.0f,
                ),
                Animation(Animation.Type.SMOOTH, 300f), null
            )
        }*/
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()

        super.onStop()
    }

    override fun onStart() {
        mapView.onStart()
        MapKitFactory.getInstance().onStart()

        super.onStart()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )
        // Check condition
        if (requestCode == 100 && grantResults.isNotEmpty()
            && (grantResults[0] + grantResults[1]
                    == PackageManager.PERMISSION_GRANTED)
        ) {
            getCurrentLocation()
        } else {
            // When permission are denied
            // Display toast
            toast(context, "Permission denied")
        }
    }

    private fun getCurrentLocation() {
        // Initialize Location manager
        val locationManager =
            context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            // When location service is enabled
            // Get last location
            client.lastLocation.addOnCompleteListener { task ->
                // Initialize location
                val receivedLocation = task.result
                // Check condition
                if (receivedLocation != null) {
                    location = receivedLocation
                    mapView.map.move(
                        CameraPosition(
                            Point(location!!.latitude, location!!.longitude),
                            15.0f,
                            0.0f,
                            0.0f,
                        ),
                        Animation(Animation.Type.SMOOTH, 3f), null
                    )
                } else {
                    val locationRequest = LocationRequest()
                        .setPriority(
                            LocationRequest.PRIORITY_HIGH_ACCURACY
                        )
                        .setInterval(10000)
                        .setFastestInterval(
                            1000
                        )
                        .setNumUpdates(1)

                    // Initialize location call back
                    val locationCallback: LocationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            // Initialize
                            // location
                            val location1: Location? = locationResult.lastLocation
                            if (location1 != null) {
                                location = location1
                                mapView.map.move(
                                    CameraPosition(
                                        Point(location!!.latitude, location!!.longitude),
                                        15.0f,
                                        0.0f,
                                        0.0f,
                                    ),
                                    Animation(Animation.Type.SMOOTH, 3f), null
                                )
                            }
                        }
                    }

                    // Request location updates
                    client.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper()
                    )
                }
            }
        }
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): MapFragment {
            val myFragment = MapFragment()
            val args = Bundle()
            args.putString("requestCode", userId)
            myFragment.arguments = args
            return myFragment
        }
    }

}