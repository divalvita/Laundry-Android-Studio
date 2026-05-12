package com.example.laundryapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
// Jika ini merah, pastikan nama file XML-mu adalah activity_splash.xml
import com.example.laundryapp.databinding.ActivitySplashBinding
import com.example.laundryapp.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {

    // Deklarasi binding
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            // Pastikan class LoginActivity sudah kamu buat
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}