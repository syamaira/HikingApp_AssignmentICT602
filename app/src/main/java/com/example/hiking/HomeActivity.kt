package com.example.hiking

import android.content.Intent
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
    private lateinit var btnMap: Button
    private lateinit var btnTrails: Button
    private lateinit var btnProfile: Button
    private lateinit var btnAbout: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 1. Setup Toolbar supaya Menu muncul
        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        // 2. Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid

        // 3. Initialize UI Components
        tvWelcomeName = findViewById(R.id.tvWelcomeName)
        tvCoordinates = findViewById(R.id.tvCurrentCoordinates) // Dari CardView XML kau
        btnMap = findViewById(R.id.btnMap)
        btnTrails = findViewById(R.id.btnTrails)
        btnProfile = findViewById(R.id.btnProfile)
        btnAbout = findViewById(R.id.btnAbout)

        // 4. Ambil data User dari Firebase Database
        if (userId != null) {
            // Kita ambil dari path "Users" yang kau simpan masa SignUp
            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId)

            mDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // 1. Ambil Nama
                        val name = snapshot.child("name").value?.toString() ?: "Hiker"
                        tvWelcomeName.text = "Hello, $name!"

                        // 2. Ambil Lokasi (Pastikan Key "location" ada dalam DB)
                        val location = snapshot.child("location").value?.toString() ?: "Lokasi belum ditetapkan"

                        // Kalau kau guna koordinat lat/lng
                        val lat = snapshot.child("lat").value?.toString()
                        val lng = snapshot.child("lng").value?.toString()

                        if (lat != null && lng != null) {
                            tvCoordinates.text = "Lat: $lat, Lng: $lng"
                        } else {
                            tvCoordinates.text = location
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HomeActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })

            // OPTIONAL: Ambil koordinat yang MainActivity simpan tadi untuk display
            val locRef = FirebaseDatabase.getInstance().getReference("users_location").child(userId)
            locRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val lat = snapshot.child("lat").value
                    val lng = snapshot.child("lng").value
                    tvCoordinates.text = "Lat: $lat, Lng: $lng"
                }
            }
        }

        // 5. Button Listeners
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
            // Buka activity senarai hiking yang kau dah buat tu
            val intent = Intent(this, HikingListActivity::class.java)
            startActivity(intent)
        }
    }

    // --- LOGIK MENU ---

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        // Guna file main_menu.xml yang kau tunjuk tadi
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
        // Logout dari Google juga supaya user boleh tukar akaun lain kali
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}