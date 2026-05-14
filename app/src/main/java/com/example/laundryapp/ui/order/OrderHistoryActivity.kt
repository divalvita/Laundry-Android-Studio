package com.example.laundryapp.ui.order

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.* // Memastikan OrderUpdateRequest terimport
import com.example.laundryapp.databinding.ActivityOrderHistoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private var customers: List<CustomerResponse> = emptyList()
    private var services: List<ServiceResponse> = emptyList()

    private var isManageMode = false
    private var filterStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil parameter dari Intent
        isManageMode = intent.getBooleanExtra("IS_MANAGE", false)
        filterStatus = intent.getStringExtra("FILTER_STATUS")

        setupUI()
        loadInitialData()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbarOrderHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Atur Judul Dinamis
        supportActionBar?.title = when (filterStatus) {
            "processing" -> "Pesanan Perlu Selesai"
            "done" -> "Pesanan Perlu Diambil"
            else -> "Riwayat Order"
        }

        binding.toolbarOrderHistory.setNavigationOnClickListener { finish() }
        binding.rvOrder.layoutManager = LinearLayoutManager(this)

        // Sembunyikan FAB jika dalam mode kelola agar fokus
        if (isManageMode) binding.fabAddOrder.visibility = View.GONE

        binding.fabAddOrder.setOnClickListener {
            startActivity(Intent(this, CreateOrderActivity::class.java))
        }
    }

    private fun loadInitialData() {
        // Alur: Load Customer -> Load Service -> Load Order
        RetrofitClient.apiService.getCustomers().enqueue(object : Callback<List<CustomerResponse>> {
            override fun onResponse(call: Call<List<CustomerResponse>>, response: Response<List<CustomerResponse>>) {
                if (response.isSuccessful) {
                    customers = response.body() ?: emptyList()
                    loadServices()
                }
            }
            override fun onFailure(call: Call<List<CustomerResponse>>, t: Throwable) {}
        })
    }

    private fun loadServices() {
        RetrofitClient.apiService.getServices().enqueue(object : Callback<List<ServiceResponse>> {
            override fun onResponse(call: Call<List<ServiceResponse>>, response: Response<List<ServiceResponse>>) {
                if (response.isSuccessful) {
                    services = response.body() ?: emptyList()
                    fetchOrders()
                }
            }
            override fun onFailure(call: Call<List<ServiceResponse>>, t: Throwable) {}
        })
    }

    private fun fetchOrders() {
        RetrofitClient.apiService.getOrders().enqueue(object : Callback<List<OrderResponse>> {
            override fun onResponse(call: Call<List<OrderResponse>>, response: Response<List<OrderResponse>>) {
                if (response.isSuccessful) {
                    var orders = response.body() ?: emptyList()

                    // Terapkan Filter jika sedang mode kelola
                    if (isManageMode && filterStatus != null) {
                        orders = orders.filter { it.status == filterStatus }
                    }

                    binding.rvOrder.adapter = OrderAdapter(
                        orders, customers, services, isManageMode
                    ) { orderId ->
                        val nextStatus = if (filterStatus == "processing") "done" else "taken"
                        performUpdateStatus(orderId, nextStatus)
                    }
                }
            }
            override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {}
        })
    }

    private fun performUpdateStatus(id: Int, status: String) {
        // Membungkus status ke dalam model OrderUpdateRequest
        val request = OrderUpdateRequest(status)

        // Pastikan nama fungsi di sini sama dengan di ApiService kamu (updateOrderStatus)
        RetrofitClient.apiService.updateOrderStatus(id, request).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@OrderHistoryActivity, "Berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    // Memuat ulang data dari awal agar daftar di layar sinkron dengan database
                    loadInitialData()
                } else {
                    // Menampilkan kode error (misal 404 atau 500) jika gagal di sisi server
                    Toast.makeText(this@OrderHistoryActivity, "Gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                // Menampilkan pesan jika terjadi masalah jaringan
                Toast.makeText(this@OrderHistoryActivity, "Koneksi Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadInitialData()
    }
}