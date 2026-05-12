package com.example.laundryapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.MainActivity
import com.example.laundryapp.ui.auth.RegisterActivity
import com.example.laundryapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi ViewBinding sesuai nama layout XML kamu (login_activity.xml)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sembunyikan ActionBar agar tampilan Login terlihat full dan bersih
        supportActionBar?.hide()

        // 1. Logika Tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validasi Input Sederhana
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi email dan password", Toast.LENGTH_SHORT).show()
            } else {
                // Simulasi Login (Nantinya cek ke tabel users)
                if (email == "admin@diva.com" && password == "admin123") {
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                    // Berpindah ke MainActivity (Dashboard)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Tutup halaman login agar tidak bisa di-back
                } else {
                    Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 2. Link ke Sign Up (Register Activity)
        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}