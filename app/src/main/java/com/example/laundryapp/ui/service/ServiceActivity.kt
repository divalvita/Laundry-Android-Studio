package com.example.laundryapp.ui.service

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivityServiceBinding // Import otomatis

class ServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Binding
        binding = ActivityServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sekarang kamu bisa akses komponen dengan mudah, contoh:
        // binding.toolbarService.title = "Layanan Laundry"
        // binding.fabAddService.setOnClickListener { ... }
    }
}