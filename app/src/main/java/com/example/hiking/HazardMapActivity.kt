package com.example.hiking

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HazardMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hazard_map)


        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        progressBar.visibility = View.GONE


        val kinabaluHazard = LatLng(6.075, 116.558)


        mMap.addMarker(MarkerOptions()
            .position(kinabaluHazard)
            .title("Hazard Area: Landslide Risk")
            .snippet("Be careful during rainy season"))


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kinabaluHazard, 12f))


        mMap.uiSettings.isZoomControlsEnabled = true

        Toast.makeText(this, "Map Ready for Adventure!", Toast.LENGTH_SHORT).show()
    }
}