package com.example.hiking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {

    private lateinit var tvWelcomeName: TextView
    private lateinit var tvCoordinates: TextView
    private lateinit var tvLatestNews: TextView
    private lateinit var btnMap: Button
    private lateinit var btnTrails: Button
    private lateinit var btnProfile: Button
    private lateinit var btnAbout: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var newsRef: DatabaseReference

    // GPS Client
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        // Initialize GPS Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid

        // Bind Views
        tvWelcomeName = findViewById(R.id.tvWelcomeName)
        tvCoordinates = findViewById(R.id.tvCurrentCoordinates)
        tvLatestNews = findViewById(R.id.tvLatestNews)
        btnMap = findViewById(R.id.btnMap)
        btnTrails = findViewById(R.id.btnTrails)
        btnProfile = findViewById(R.id.btnProfile)
        btnAbout = findViewById(R.id.btnAbout)

        if (userId != null) {
            // Update GPS to Firebase on Start
            updateLocationToFirebase(userId)

            // Listen to User Data
            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            mDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").value?.toString() ?: "Hiker"
                        tvWelcomeName.text = "Hello, $name!"

                        val lat = snapshot.child("latitude").value?.toString()
                        val lng = snapshot.child("longitude").value?.toString()

                        if (lat != null && lng != null) {
                            tvCoordinates.text = "Lat: $lat, Lng: $lng"
                        } else {
                            tvCoordinates.text = "Searching for GPS..."
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

            // Listen to News Data
            newsRef = FirebaseDatabase.getInstance().getReference("News")
            newsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val news = snapshot.child("latestNews").value?.toString() ?: "No updates."
                        val status = snapshot.child("status").value?.toString() ?: "normal"

                        tvLatestNews.text = news
                        tvLatestNews.isSelected = true

                        if (status == "warning") {
                            tvLatestNews.setTextColor(Color.RED)
                        } else {
                            tvLatestNews.setTextColor(Color.parseColor("#558B2F"))
                        }
                    } else {
                        tvLatestNews.text = "No news found in database"
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HomeActivity, "News Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Click Listeners
        btnMap.setOnClickListener { startActivity(Intent(this, HazardMapActivity::class.java)) }
        btnAbout.setOnClickListener { startActivity(Intent(this, AboutActivity::class.java)) }
        btnProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        btnTrails.setOnClickListener { startActivity(Intent(this, HikingListActivity::class.java)) }
    }

    // Logic to fetch GPS and send to Firebase
    private fun updateLocationToFirebase(uid: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)
                    val updates = HashMap<String, Any>()
                    updates["latitude"] = location.latitude
                    updates["longitude"] = location.longitude

                    userRef.updateChildren(updates).addOnFailureListener {
                        Toast.makeText(this, "Failed to sync GPS", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    // Handle User clicking "Allow" or "Deny" on GPS permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mAuth.currentUser?.uid?.let { updateLocationToFirebase(it) }
        } else {
            Toast.makeText(this, "Permission denied. GPS won't update.", Toast.LENGTH_SHORT).show()
        }
    }

    // Menu Logic
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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