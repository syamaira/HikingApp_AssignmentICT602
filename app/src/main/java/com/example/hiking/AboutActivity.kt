package com.example.hiking

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)


        val tvUserAgent = findViewById<TextView>(R.id.tvUserAgent)
        val tvGithubLink = findViewById<TextView>(R.id.tvGithubLink)
        val btnBackHome = findViewById<Button>(R.id.btnBackHome)


        val userAgent = try {
            WebSettings.getDefaultUserAgent(this)
        } catch (e: Exception) {
            "Android ${Build.VERSION.RELEASE}; ${Build.MODEL}"
        }
        tvUserAgent.text = userAgent


        tvGithubLink.setOnClickListener {
            val githubUrl = "https://github.com/syamaira/HikingApp_AssignmentICT602.git"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
            startActivity(intent)
        }


        btnBackHome.setOnClickListener {
            finish()
        }
    }
}