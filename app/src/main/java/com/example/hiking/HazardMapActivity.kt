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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class HazardMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var progressBar: ProgressBar
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hazard_map)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE


        mDatabase = FirebaseDatabase.getInstance().getReference("Hazards")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        progressBar.visibility = View.GONE

        // 2. Tarik data dari Firebase
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mMap.clear() // Clear marker lama supaya tak bertindih

                if (snapshot.exists()) {
                    for (placeSnapshot in snapshot.children) {
                        val name = placeSnapshot.child("name").value.toString()
                        val lat = placeSnapshot.child("lat").value.toString().toDoubleOrNull()
                        val lng = placeSnapshot.child("lng").value.toString().toDoubleOrNull()
                        val difficulty = placeSnapshot.child("difficulty").value.toString()

                        if (lat != null && lng != null) {
                            val location = LatLng(lat, lng)

                            // 3. Tambah Marker. Kalau difficulty 'Extreme', kita guna warna merah (Hazard)
                            val markerColor = if (difficulty == "Extreme") {
                                BitmapDescriptorFactory.HUE_RED
                            } else {
                                BitmapDescriptorFactory.HUE_AZURE
                            }

                            mMap.addMarker(
                                MarkerOptions()
                                    .position(location)
                                    .title(name)
                                    .snippet("Difficulty: $difficulty")
                                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                            )
                        }
                    }


                    val malaysia = LatLng(4.2105, 101.9758)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(malaysia, 6f))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HazardMapActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        mMap.uiSettings.isZoomControlsEnabled = true
    }
}