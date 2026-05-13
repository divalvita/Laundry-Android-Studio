package com.example.laundryapp.ui.order

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.CustomerResponse
import com.example.laundryapp.data.model.OrderResponse
import com.example.laundryapp.data.model.ServiceResponse
import com.example.laundryapp.databinding.ActivityOrderHistoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding

    // Simpan semua data customer
    private var customers: List<CustomerResponse> =
        emptyList()

    // Simpan semua data service
    private var services: List<ServiceResponse> =
        emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityOrderHistoryBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbarOrderHistory)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarOrderHistory
            .setNavigationOnClickListener {

                finish()
            }

        binding.rvOrder.layoutManager =
            LinearLayoutManager(this)

        // Mulai load data
        loadCustomers()

        // Tombol tambah order
        binding.fabAddOrder.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CreateOrderActivity::class.java
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()

        // Refresh data saat kembali
        loadCustomers()
    }

    // ============================
    // LOAD CUSTOMERS
    // ============================

    private fun loadCustomers() {

        RetrofitClient.apiService.getCustomers()
            .enqueue(object :
                Callback<List<CustomerResponse>> {

                override fun onResponse(
                    call: Call<List<CustomerResponse>>,
                    response: Response<List<CustomerResponse>>
                ) {

                    if (response.isSuccessful) {

                        customers =
                            response.body() ?: emptyList()

                        // lanjut load service
                        loadServices()

                    } else {

                        Toast.makeText(
                            this@OrderHistoryActivity,
                            "Gagal mengambil customer",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<CustomerResponse>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@OrderHistoryActivity,
                        "Error customer: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // ============================
    // LOAD SERVICES
    // ============================

    private fun loadServices() {

        RetrofitClient.apiService.getServices()
            .enqueue(object :
                Callback<List<ServiceResponse>> {

                override fun onResponse(
                    call: Call<List<ServiceResponse>>,
                    response: Response<List<ServiceResponse>>
                ) {

                    if (response.isSuccessful) {

                        services =
                            response.body() ?: emptyList()

                        // lanjut ambil orders
                        getOrders()

                    } else {

                        Toast.makeText(
                            this@OrderHistoryActivity,
                            "Gagal mengambil service",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<ServiceResponse>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@OrderHistoryActivity,
                        "Error service: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    // ============================
    // GET ORDERS
    // ============================

    private fun getOrders() {

        RetrofitClient.apiService.getOrders()
            .enqueue(object :
                Callback<List<OrderResponse>> {

                override fun onResponse(
                    call: Call<List<OrderResponse>>,
                    response: Response<List<OrderResponse>>
                ) {

                    if (response.isSuccessful) {

                        val orders =
                            response.body() ?: emptyList()

                        binding.rvOrder.adapter =
                            OrderAdapter(
                                orders,
                                customers,
                                services
                            )

                    } else {

                        Toast.makeText(
                            this@OrderHistoryActivity,
                            "Gagal mengambil order",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<OrderResponse>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@OrderHistoryActivity,
                        "Error order: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}