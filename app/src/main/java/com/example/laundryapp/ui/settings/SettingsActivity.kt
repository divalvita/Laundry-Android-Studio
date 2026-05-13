package com.example.laundryapp.ui.settings

import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.R
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbarSettings: MaterialToolbar
    private lateinit var switchNotification: Switch
    private lateinit var switchAutoRefresh: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        toolbarSettings = findViewById(R.id.toolbarSettings)
        switchNotification = findViewById(R.id.switchNotification)
        switchAutoRefresh = findViewById(R.id.switchAutoRefresh)

        toolbarSettings.title = "Pengaturan"
        toolbarSettings.setNavigationOnClickListener {
            finish()
        }

        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                if (isChecked) "Notifikasi aktif" else "Notifikasi nonaktif",
                Toast.LENGTH_SHORT
            ).show()
        }

        switchAutoRefresh.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                if (isChecked) "Auto refresh aktif" else "Auto refresh nonaktif",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}