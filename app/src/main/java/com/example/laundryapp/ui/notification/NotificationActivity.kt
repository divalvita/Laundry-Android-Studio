package com.example.laundryapp.ui.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivitySimpleMenuBinding

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimpleMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySimpleMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarSimple.title = "Notifikasi"
        binding.toolbarSimple.setNavigationOnClickListener {
            finish()
        }

        binding.tvSimpleTitle.text = "Notifikasi"
        binding.tvSimpleDescription.text =
            "Belum ada notifikasi baru.\n\n" +
                    "Nanti halaman ini bisa dipakai untuk menampilkan update order, pembayaran, atau status laundry pelanggan."
    }
}