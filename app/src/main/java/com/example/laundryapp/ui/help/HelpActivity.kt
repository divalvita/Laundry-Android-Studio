package com.example.laundryapp.ui.help

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackHelp.setOnClickListener {
            finish()
        }
    }
}