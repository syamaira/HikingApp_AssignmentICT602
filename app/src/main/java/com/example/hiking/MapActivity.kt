package com.example.hiking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan nama file layout kau betul: activity_map atau layout_map
        setContentView(R.layout.layout_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val lat = intent.getDoubleExtra("LAT", 0.0)
        val lng = intent.getDoubleExtra("LNG", 0.0)
        val name = intent.getStringExtra("NAME") ?: "Lokasi Mendaki"

        if (lat != 0.0 && lng != 0.0) {
            val lokasi = LatLng(lat, lng)
            mMap.addMarker(MarkerOptions().position(lokasi).title(name))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15f))
        }
    }
}