package com.example.hiking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    // Declare variables
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnEdit: Button
    private lateinit var ivProfilePic: ImageView

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // 1. Initialize Firebase Auth & Database
        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid

        // 2. Hubungkan dengan ID kat XML tadi
        tvName = findViewById(R.id.tvProfileName)
        tvEmail = findViewById(R.id.tvProfileEmail)
        btnLogout = findViewById(R.id.btnLogout)
        btnEdit = findViewById(R.id.btnEditProfile)
        ivProfilePic = findViewById(R.id.ivProfilePic)

        // 3. Tarik data dari Firebase Realtime Database
        if (userId != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId)

            mDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Ambil value ikut 'key' yang kau buat masa Sign Up tadi
                        val name = snapshot.child("name").value.toString()
                        val email = snapshot.child("email").value.toString()

                        tvName.text = name
                        tvEmail.text = email
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // 4. Fungsi Butang Logout
        btnLogout.setOnClickListener {
            mAuth.signOut() // Padam session login kat Firebase

            // Lepas logout, hantar balik ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            // Flag ni penting supaya user tak boleh tekan 'Back' masuk profile balik
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show()
        }

        // 5. Fungsi Butang Edit (Optional)
        btnEdit.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Edit Nama")

            // Tambah EditText dalam pop-up
            val input = android.widget.EditText(this)
            input.setText(tvName.text.toString())
            builder.setView(input)

            builder.setPositiveButton("Simpan") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotEmpty()) {
                    // Update nama dalam Firebase
                    mDatabase.child("name").setValue(newName).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Profil dikemaskini!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Gagal update!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            builder.setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }
            builder.show()
        }
    }
}