package com.example.laundryapp.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.DashboardResponse
import com.example.laundryapp.databinding.ActivitySettingsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = getSharedPreferences("APP_SETTINGS", MODE_PRIVATE)

        binding.btnBackSettings.setOnClickListener {
            finish()
        }

        binding.switchNotification.isChecked =
            pref.getBoolean("notification_enabled", true)

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            pref.edit()
                .putBoolean("notification_enabled", isChecked)
                .apply()

            Toast.makeText(
                this,
                if (isChecked) "Notifikasi diaktifkan" else "Notifikasi dimatikan",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnCheckApi.setOnClickListener {
            checkApiStatus()
        }

        binding.btnResetNotif.setOnClickListener {
            getSharedPreferences("NOTIF_PREF", MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            Toast.makeText(
                this,
                "Badge notifikasi berhasil direset",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkApiStatus() {
        binding.tvApiStatus.text = "Mengecek koneksi..."

        RetrofitClient.apiService.getDashboard()
            .enqueue(object : Callback<DashboardResponse> {

                override fun onResponse(
                    call: Call<DashboardResponse>,
                    response: Response<DashboardResponse>
                ) {
                    if (response.isSuccessful) {
                        binding.tvApiStatus.text = "API Online"
                    } else {
                        binding.tvApiStatus.text = "API Offline (${response.code()})"
                    }
                }

                override fun onFailure(
                    call: Call<DashboardResponse>,
                    t: Throwable
                ) {
                    binding.tvApiStatus.text = "API Offline"
                }
            })
    }
}