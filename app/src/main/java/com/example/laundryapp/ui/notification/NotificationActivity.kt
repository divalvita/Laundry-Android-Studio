package com.example.laundryapp.ui.notification

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laundryapp.MainActivity
import com.example.laundryapp.R
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.NotificationResponse
import com.example.laundryapp.databinding.ActivityNotificationBinding
import com.example.laundryapp.ui.customer.CustomerActivity
import com.example.laundryapp.ui.order.CreateOrderActivity
import com.example.laundryapp.ui.order.OrderHistoryActivity
import com.example.laundryapp.ui.service.ServiceActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private val notifications = mutableListOf<NotificationResponse>()
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackNotification.setOnClickListener {
            finish()
        }

        adapter = NotificationAdapter(notifications) { notification ->
            deleteNotification(notification)
        }

        binding.rvNotification.layoutManager = LinearLayoutManager(this)
        binding.rvNotification.adapter = adapter

        binding.btnClearNotification.setOnClickListener {
            confirmDeleteAll()
        }

        setupBottomNav()
        getNotifications()
    }

    private fun setupBottomNav() {
        binding.bottomNavNotification.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.nav_order -> {
                    startActivity(Intent(this, CreateOrderActivity::class.java))
                    true
                }

                R.id.nav_laporan -> {
                    startActivity(Intent(this, OrderHistoryActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun getNotifications() {
        RetrofitClient.apiService.getNotifications()
            .enqueue(object : Callback<List<NotificationResponse>> {

                override fun onResponse(
                    call: Call<List<NotificationResponse>>,
                    response: Response<List<NotificationResponse>>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body() ?: emptyList()

                        notifications.clear()
                        notifications.addAll(data.reversed())
                        adapter.notifyDataSetChanged()

                        updateState()
                    } else {
                        Toast.makeText(
                            this@NotificationActivity,
                            "Gagal mengambil notifikasi: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<NotificationResponse>>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@NotificationActivity,
                        "Error notifikasi: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun deleteNotification(notification: NotificationResponse) {
        RetrofitClient.apiService.deleteNotification(notification.id)
            .enqueue(object : Callback<Unit> {

                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        notifications.remove(notification)
                        adapter.notifyDataSetChanged()
                        updateState()

                        Toast.makeText(
                            this@NotificationActivity,
                            "Notifikasi dihapus",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@NotificationActivity,
                            "Gagal hapus: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(
                        this@NotificationActivity,
                        "Error hapus: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun confirmDeleteAll() {
        if (notifications.isEmpty()) return

        AlertDialog.Builder(this)
            .setTitle("Hapus semua notifikasi?")
            .setMessage("Semua notifikasi akan dihapus dari daftar.")
            .setPositiveButton("Hapus") { _, _ ->
                deleteAllNotifications()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteAllNotifications() {
        val copy = notifications.toList()

        copy.forEach { notif ->
            RetrofitClient.apiService.deleteNotification(notif.id)
                .enqueue(object : Callback<Unit> {

                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        notifications.remove(notif)
                        adapter.notifyDataSetChanged()
                        updateState()
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {}
                })
        }
    }

    private fun updateState() {
        binding.tvNotificationCount.text = "${notifications.size} notifikasi"

        if (notifications.isEmpty()) {
            binding.rvNotification.visibility = View.GONE
            binding.tvEmptyNotification.visibility = View.VISIBLE
            binding.btnClearNotification.visibility = View.GONE
        } else {
            binding.rvNotification.visibility = View.VISIBLE
            binding.tvEmptyNotification.visibility = View.GONE
            binding.btnClearNotification.visibility = View.VISIBLE
        }

        getSharedPreferences("NOTIF_PREF", MODE_PRIVATE)
            .edit()
            .putInt("READ_NOTIF_COUNT", notifications.size)
            .apply()
    }
}