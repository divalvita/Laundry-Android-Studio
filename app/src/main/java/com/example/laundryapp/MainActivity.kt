package com.example.laundryapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.DashboardResponse
import com.example.laundryapp.databinding.ActivityMainBinding
import com.example.laundryapp.ui.customer.CustomerActivity
import com.example.laundryapp.ui.expense.ExpenseActivity
import com.example.laundryapp.ui.order.CreateOrderActivity
import com.example.laundryapp.ui.order.OrderHistoryActivity
import com.example.laundryapp.ui.profile.ProfileActivity
import com.example.laundryapp.ui.service.ServiceActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var userName: String? = null
    private var userEmail: String? = null
    private var userRole: String? = null
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        userId = intent.getIntExtra("user_id", 0)
        userName = intent.getStringExtra("user_name")
        userEmail = intent.getStringExtra("user_email")
        userRole = intent.getStringExtra("user_role")

        loadDashboardData()

        binding.imgProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user_id", userId)
            intent.putExtra("user_name", userName)
            intent.putExtra("user_email", userEmail)
            intent.putExtra("user_role", userRole)
            startActivity(intent)
        }

        binding.cardCustomer.setOnClickListener {
            startActivity(Intent(this, CustomerActivity::class.java))
        }

        binding.cardService.setOnClickListener {
            startActivity(Intent(this, ServiceActivity::class.java))
        }

        binding.cardOrder.setOnClickListener {
            startActivity(Intent(this, CreateOrderActivity::class.java))
        }

        binding.cardExpense.setOnClickListener {
            startActivity(Intent(this, ExpenseActivity::class.java))
        }

        binding.bottomNav.selectedItemId = R.id.nav_home

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true

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

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    private fun loadDashboardData() {
        RetrofitClient.apiService.getDashboard()
            .enqueue(object : Callback<DashboardResponse> {

                override fun onResponse(
                    call: Call<DashboardResponse>,
                    response: Response<DashboardResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!

                        binding.tvActiveOrder.text = data.order_aktif.toString()

                        binding.tvDoneOrder.text = data.selesai.toString()

                        binding.tvDailyIncome.text = formatOmzet(data.omzet)
                    } else {
                        Log.e("Dashboard", "Response gagal: ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: Call<DashboardResponse>,
                    t: Throwable
                ) {
                    Log.e("Dashboard", "Error: ${t.message}")
                }
            })
    }

    private fun formatOmzet(value: Int): String {
        return when {
            value >= 1_000_000 -> "${value / 1_000_000}jt"
            value >= 1_000 -> "${value / 1_000}k"
            else -> value.toString()
        }
    }
}