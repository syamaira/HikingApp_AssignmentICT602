package com.example.hiking

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var signupButton: Button
    private lateinit var loginRedirect: TextView

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        nameInput = findViewById(R.id.name_input_signup)
        emailInput = findViewById(R.id.email_input_signup)
        passwordInput = findViewById(R.id.password_input_signup)
        confirmPasswordInput = findViewById(R.id.confirm_password_input)
        signupButton = findViewById(R.id.register_button)
        loginRedirect = findViewById(R.id.tv_back_to_login)


        mAuth = FirebaseAuth.getInstance()

        signupButton.setOnClickListener {
            registerUser()
        }

        loginRedirect.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
            TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }


        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid ?: ""


                    val ref = FirebaseDatabase.getInstance().getReference("Users")

                    val userMap = HashMap<String, Any>()
                    userMap["name"] = name
                    userMap["email"] = email
                    userMap["uid"] = userId
                    userMap["location"] = "Belum ditetapkan"

                    ref.child(userId).setValue(userMap).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}