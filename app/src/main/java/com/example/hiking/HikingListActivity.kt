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


        val query = FirebaseDatabase.getInstance().getReference("HikingPlaces")


        val options = FirebaseRecyclerOptions.Builder<HikingPlace>()
            .setQuery(query, HikingPlace::class.java)
            .build()


        adapter = object : FirebaseRecyclerAdapter<HikingPlace, HikingViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HikingViewHolder {

                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_hiking, parent, false)
                return HikingViewHolder(view)
            }

            override fun onBindViewHolder(holder: HikingViewHolder, position: Int, model: HikingPlace) {
                holder.tvName.text = model.name
                holder.tvLocation.text = model.location
                holder.tvDifficulty.text = model.difficulty


                holder.itemView.setOnClickListener {

                    val intent = Intent(this@HikingListActivity, MapActivity::class.java)


                    intent.putExtra("LAT", model.lat)
                    intent.putExtra("LNG", model.lng)
                    intent.putExtra("NAME", model.name)

                    startActivity(intent)
                }
            }
        }

        recyclerView.adapter = adapter
    }


    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
}