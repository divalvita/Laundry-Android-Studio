package com.example.laundryapp.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    // Inisialisasi View Binding
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menghubungkan XML dengan Activity
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Contoh Logika Tombol Register
        binding.btnRegister.setOnClickListener {
            val nama = binding.etRegName.text.toString()
            val email = binding.etRegEmail.text.toString()
            val pass = binding.etRegPassword.text.toString()

            if (nama.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
            } else {
                // Di sini nanti tempat memanggil API dari folder data/api
                Toast.makeText(this, "Mencoba mendaftarkan $nama...", Toast.LENGTH_SHORT).show()
            }
        }

        // Kembali ke Login
        binding.tvLoginLink.setOnClickListener {
            finish() // Menutup halaman register dan kembali ke login
        }
    }
}