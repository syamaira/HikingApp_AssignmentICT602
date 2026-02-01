package com.example.hiking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class HikingListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: FirebaseRecyclerAdapter<HikingPlace, HikingViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hiking_list)

        recyclerView = findViewById(R.id.rvHikingList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 1. Point ke node "HikingPlaces" dalam Firebase
        val query = FirebaseDatabase.getInstance().getReference("HikingPlaces")

        // 2. Configure options untuk adapter
        val options = FirebaseRecyclerOptions.Builder<HikingPlace>()
            .setQuery(query, HikingPlace::class.java)
            .build()

        // 3. Setup Adapter
        adapter = object : FirebaseRecyclerAdapter<HikingPlace, HikingViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HikingViewHolder {
                // Gunakan LayoutInflater yang betul
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_hiking, parent, false)
                return HikingViewHolder(view)
            }

            override fun onBindViewHolder(holder: HikingViewHolder, position: Int, model: HikingPlace) {
                holder.tvName.text = model.name
                holder.tvLocation.text = model.location
                holder.tvDifficulty.text = model.difficulty

                // UPDATE DI SINI:
                holder.itemView.setOnClickListener {
                    // Kita tak nak buka Google Maps app luar lagi,
                    // kita nak buka MapActivity yang kau baru buat tu.
                    val intent = Intent(this@HikingListActivity, MapActivity::class.java)

                    // Pastikan nama variable dalam model kau (lat, lng, name)
                    // sama dengan apa yang kau tulis kat sini.
                    intent.putExtra("LAT", model.lat)
                    intent.putExtra("LNG", model.lng)
                    intent.putExtra("NAME", model.name)

                    startActivity(intent)
                }
            }
        }

        recyclerView.adapter = adapter
    }

    // 4. Mesti ada onStart & onStop supaya data auto-update
    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
}