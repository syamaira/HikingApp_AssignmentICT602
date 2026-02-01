package com.example.hiking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

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

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userId = currentUser?.uid

        tvName = findViewById(R.id.tvProfileName)
        tvEmail = findViewById(R.id.tvProfileEmail)
        btnLogout = findViewById(R.id.btnLogout)
        btnEdit = findViewById(R.id.btnEditProfile)
        ivProfilePic = findViewById(R.id.ivProfilePic)


        if (currentUser != null) {
            val photoUrl = currentUser.photoUrl
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_user_placeholder)
                    .into(ivProfilePic)
            }
            // Set email directly from Auth
            tvEmail.text = currentUser.email ?: "No Email Provided"
        }


        if (userId != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            mDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").value.toString()
                        tvName.text = name
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }


        btnLogout.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            Toast.makeText(this, "Successfully logged out!", Toast.LENGTH_SHORT).show()
        }


        btnEdit.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Edit Display Name")

            val input = android.widget.EditText(this)
            input.setText(tvName.text.toString())
            builder.setView(input)

            builder.setPositiveButton("Save") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    mDatabase.child("name").setValue(newName).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()
        }
    }
}