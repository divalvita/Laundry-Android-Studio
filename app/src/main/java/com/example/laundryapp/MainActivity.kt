package com.example.laundryapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivityMainBinding
import com.example.laundryapp.ui.customer.CustomerActivity
import com.example.laundryapp.ui.service.ServiceActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Navigasi ke Halaman Customer
        binding.cardCustomer.setOnClickListener {
            val intent = Intent(this, CustomerActivity::class.java)
            startActivity(intent)
        }

        // Setup Bottom Navigation
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dash -> true
                R.id.nav_service -> {
                    startActivity(Intent(this, ServiceActivity::class.java))
                    true
                }
                // Tambahkan case menu lainnya sesuai menu XML kamu
                else -> false
            }
        }
    }
}