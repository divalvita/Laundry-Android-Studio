package com.example.laundryapp.ui.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laundryapp.databinding.ActivityCustomerBinding

class CustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbarCustomer)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarCustomer.setNavigationOnClickListener { onBackPressed() }

        // Setup RecyclerView
        binding.rvCustomer.layoutManager = LinearLayoutManager(this)

        // Catatan: Di sini kamu nantinya butuh Adapter untuk menampilkan data.
        // Untuk sementara, kamu bisa membuat class CustomerAdapter secara terpisah.

        binding.fabAddCustomer.setOnClickListener {
            // Logika untuk menambah pelanggan baru
        }
    }
}