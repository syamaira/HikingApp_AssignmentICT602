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
        val userId = mAuth.currentUser?.uid


        tvName = findViewById(R.id.tvProfileName)
        tvEmail = findViewById(R.id.tvProfileEmail)
        btnLogout = findViewById(R.id.btnLogout)
        btnEdit = findViewById(R.id.btnEditProfile)
        ivProfilePic = findViewById(R.id.ivProfilePic)


        if (userId != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId)

            mDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

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


        btnLogout.setOnClickListener {
            mAuth.signOut()


            val intent = Intent(this, LoginActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show()
        }


        btnEdit.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Edit Nama")


            val input = android.widget.EditText(this)
            input.setText(tvName.text.toString())
            builder.setView(input)

            builder.setPositiveButton("Simpan") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotEmpty()) {

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