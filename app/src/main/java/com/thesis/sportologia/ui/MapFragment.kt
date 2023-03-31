package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thesis.sportologia.databinding.FragmentMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentMapBinding.inflate(inflater, container, false)

        initMap()
        renderMap()

        return binding.root
    }

    private fun initMap() {
        MapKitFactory.setApiKey("9eb8aa69-aac3-42cb-b67f-fbe4c5bff23b")
        MapKitFactory.initialize(context)
    }

    private fun renderMap() {
        mapView = binding.mapView
        /*mapView.map.move(CameraPosition(Point(), 11.0f, 0.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 300f), null)*/
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

}