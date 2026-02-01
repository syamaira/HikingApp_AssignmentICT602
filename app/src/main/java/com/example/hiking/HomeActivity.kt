package com.example.hiking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {

    private lateinit var tvWelcomeName: TextView
    private lateinit var tvCoordinates: TextView
    private lateinit var tvLatestNews: TextView // Tambah ni
    private lateinit var btnMap: Button
    private lateinit var btnTrails: Button
    private lateinit var btnProfile: Button
    private lateinit var btnAbout: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var newsRef: DatabaseReference // Tambah ni

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid

        tvWelcomeName = findViewById(R.id.tvWelcomeName)
        tvCoordinates = findViewById(R.id.tvCurrentCoordinates)
        tvLatestNews = findViewById(R.id.tvLatestNews) // Bind ID News
        btnMap = findViewById(R.id.btnMap)
        btnTrails = findViewById(R.id.btnTrails)
        btnProfile = findViewById(R.id.btnProfile)
        btnAbout = findViewById(R.id.btnAbout)

        if (userId != null) {
            // 1. Logic tarik data User
            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            mDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").value?.toString() ?: "Hiker"
                        tvWelcomeName.text = "Hello, $name!"

                        val lat = snapshot.child("latitude").value?.toString() ?: snapshot.child("lat").value?.toString()
                        val lng = snapshot.child("longitude").value?.toString() ?: snapshot.child("lng").value?.toString()

                        if (lat != null && lng != null) {
                            tvCoordinates.text = "Lat: $lat, Lng: $lng"
                        } else {
                            tvCoordinates.text = "Location not set"
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

            // 2. Logic tarik data News (TAMBAHAN BARU)
            newsRef = FirebaseDatabase.getInstance().getReference("News")
            newsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val news = snapshot.child("latestNews").value?.toString() ?: "No updates for today."
                        val status = snapshot.child("status").value?.toString() ?: "normal"

                        tvLatestNews.text = news

                        // Aktifkan marquee (text jalan)
                        tvLatestNews.isSelected = true

                        // Tukar warna kalau warning
                        if (status == "warning") {
                            tvLatestNews.setTextColor(Color.RED)
                        } else {
                            tvLatestNews.setTextColor(Color.parseColor("#558B2F"))
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        // --- Navigation Buttons ---
        btnMap.setOnClickListener {
            startActivity(Intent(this, HazardMapActivity::class.java))
        }

        btnAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnTrails.setOnClickListener {
            startActivity(Intent(this, HikingListActivity::class.java))
        }
    }

    // ... (onCreateOptionsMenu & signOut tetap sama) ...
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        mAuth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}