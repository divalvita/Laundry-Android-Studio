package com.example.laundryapp.ui.help

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivitySimpleMenuBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimpleMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySimpleMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarSimple.title = "Bantuan"
        binding.toolbarSimple.setNavigationOnClickListener {
            finish()
        }

        binding.tvSimpleTitle.text = "Bantuan Aplikasi"
        binding.tvSimpleDescription.text =
            "Cara menggunakan aplikasi:\n\n" +
                    "1. Tambahkan customer terlebih dahulu.\n" +
                    "2. Tambahkan layanan laundry.\n" +
                    "3. Buat order baru dari menu New Order.\n" +
                    "4. Lanjutkan ke pembayaran.\n" +
                    "5. Cek riwayat/report untuk melihat data order."
    }
}