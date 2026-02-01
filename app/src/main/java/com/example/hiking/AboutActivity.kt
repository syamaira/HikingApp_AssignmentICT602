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

        // 1. Initialize UI Components
        val tvUserAgent = findViewById<TextView>(R.id.tvUserAgent)
        val tvGithubLink = findViewById<TextView>(R.id.tvGithubLink) // ID baru dari XML tadi
        val btnBackHome = findViewById<Button>(R.id.btnBackHome)

        // 2. Set User-Agent (Penting untuk markah Server-Side Metadata)
        val userAgent = try {
            WebSettings.getDefaultUserAgent(this)
        } catch (e: Exception) {
            "Android ${Build.VERSION.RELEASE}; ${Build.MODEL}"
        }
        tvUserAgent.text = userAgent

        // 3. Logic untuk Clickable GitHub Link (Kriteria Markah: Clickable URL)
        tvGithubLink.setOnClickListener {
            val githubUrl = "https://github.com/cikguhawa/hiking" // Ganti dengan link repository group kau
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
            startActivity(intent)
        }

        // 4. Back Button
        btnBackHome.setOnClickListener {
            finish()
        }
    }
}