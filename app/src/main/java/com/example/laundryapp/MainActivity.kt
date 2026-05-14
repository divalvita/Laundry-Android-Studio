package com.example.laundryapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.DashboardResponse
import com.example.laundryapp.data.model.OrderResponse
import com.example.laundryapp.data.model.OrderUpdateRequest
import com.example.laundryapp.data.model.WeeklyIncome
import com.example.laundryapp.databinding.ActivityMainBinding
import com.example.laundryapp.ui.customer.CustomerActivity
import com.example.laundryapp.ui.expense.ExpenseActivity
import com.example.laundryapp.ui.order.CreateOrderActivity
import com.example.laundryapp.ui.order.OrderHistoryActivity
import com.example.laundryapp.ui.profile.ProfileActivity
import com.example.laundryapp.ui.service.ServiceActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isWeeklySelected = true
    private var cachedWeekly: List<WeeklyIncome> = emptyList()
    private var cachedMonthly: List<WeeklyIncome> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Setup UI Awal
        val userName = intent.getStringExtra("user_name") ?: "Admin"
        binding.tvGreeting.text = "Halo, $userName 👋"

        setupClickListeners()
        loadDashboardData()
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData() // Refresh otomatis saat kembali ke halaman utama
    }

    private fun setupClickListeners() {
        // Navigasi Profile
        binding.imgProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("user_id", intent.getIntExtra("user_id", 0))
                putExtra("user_name", intent.getStringExtra("user_name"))
                putExtra("user_email", intent.getStringExtra("user_email"))
                putExtra("user_role", intent.getStringExtra("user_role"))
            }
            startActivity(intent)
        }

        // Navigasi Menu Utama
        binding.cardCustomer.setOnClickListener { startActivity(Intent(this, CustomerActivity::class.java)) }
        binding.cardService.setOnClickListener { startActivity(Intent(this, ServiceActivity::class.java)) }
        binding.cardOrder.setOnClickListener { startActivity(Intent(this, CreateOrderActivity::class.java)) }
        binding.cardExpense.setOnClickListener { startActivity(Intent(this, ExpenseActivity::class.java)) }

        // Bottom Navigation
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

        // TOMBOL AKSI CEPAT (Sesuai Status Backend)
        binding.btnMarkDone.setOnClickListener {
            showStatusDialog(
                fromStatus = "processing",
                toStatus = "done",
                title = "Tandai Selesai",
                emptyMsg = "Tidak ada order yang sedang diproses."
            )
        }

        binding.btnMarkTaken.setOnClickListener {
            showStatusDialog(
                fromStatus = "done",
                toStatus = "taken",
                title = "Tandai Diambil",
                emptyMsg = "Tidak ada order yang siap diambil."
            )
        }

        // TOGGLE CHART
        binding.btnTabWeekly.setOnClickListener {
            if (!isWeeklySelected) {
                isWeeklySelected = true
                updateTabUI()
                renderChart(cachedWeekly)
            }
        }

        binding.btnTabMonthly.setOnClickListener {
            if (isWeeklySelected) {
                isWeeklySelected = false
                updateTabUI()
                renderChart(cachedMonthly)
            }
        }
    }

    private fun updateTabUI() {
        if (isWeeklySelected) {
            binding.btnTabWeekly.setTextColor(getColor(android.R.color.white))
            binding.btnTabMonthly.setTextColor(0xFF7EAADC.toInt())
        } else {
            binding.btnTabMonthly.setTextColor(getColor(android.R.color.white))
            binding.btnTabWeekly.setTextColor(0xFF7EAADC.toInt())
        }
    }

    private fun loadDashboardData() {
        RetrofitClient.apiService.getDashboard().enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    // DEBUG: Tambahkan Log ini untuk melihat data asli dari server
                    Log.d("LAUNDRY_DEBUG", "Data dari Server: Active=${data.activeOrders}, Done=${data.doneOrders}")

                    binding.apply {
                        tvActiveOrder.text = data.activeOrders.toString()
                        tvDoneOrder.text = data.doneOrders.toString()
                        tvTakenOrder.text = data.takenOrders.toString()
                        tvDailyIncome.text = formatRupiah(data.totalIncome)
                        tvProfitLabel.text = "Profit: ${formatRupiah(data.totalProfit)}"
                    }

                    cachedWeekly = data.weeklyIncome
                    cachedMonthly = data.monthlyIncome
                    renderChart(if (isWeeklySelected) cachedWeekly else cachedMonthly)
                } else {
                    Log.e("LAUNDRY_DEBUG", "Response Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                Log.e("LAUNDRY_DEBUG", "Koneksi Gagal: ${t.message}")
            }
        })
    }

    private fun renderChart(data: List<WeeklyIncome>) {
        if (data.isEmpty()) {
            binding.lineChart.clear()
            binding.lineChart.setNoDataText("Data omzet tidak tersedia")
            return
        }

        val entries = data.mapIndexed { i, item ->
            Entry(i.toFloat(), item.amount.toFloat())
        }

        val dataSet = LineDataSet(entries, "Omzet").apply {
            color = 0xFF4DC875.toInt()
            setCircleColor(0xFF4DC875.toInt())
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawFilled(true)
            fillColor = 0xFF4DC875.toInt()
            fillAlpha = 50
            valueTextColor = 0xFFFFFFFF.toInt()
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        binding.lineChart.apply {
            this.data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(data.map { it.label })
                position = XAxis.XAxisPosition.BOTTOM
                textColor = 0xFF8AADD4.toInt()
                setDrawGridLines(false)
                granularity = 1f
            }
            axisLeft.textColor = 0xFF8AADD4.toInt()
            axisRight.isEnabled = false
            animateX(500)
            invalidate()
        }
    }

    private fun showStatusDialog(fromStatus: String, toStatus: String, title: String, emptyMsg: String) {
        RetrofitClient.apiService.getOrders().enqueue(object : Callback<List<OrderResponse>> {
            override fun onResponse(call: Call<List<OrderResponse>>, response: Response<List<OrderResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    // Filter berdasarkan status dari backend
                    val filtered = response.body()!!.filter {
                        it.status.equals(fromStatus, ignoreCase = true)
                    }

                    if (filtered.isEmpty()) {
                        Toast.makeText(this@MainActivity, emptyMsg, Toast.LENGTH_SHORT).show()
                        return
                    }

                    val labels = filtered.map { "Order #${it.id} - ${it.weight}kg" }.toTypedArray()

                    AlertDialog.Builder(this@MainActivity)
                        .setTitle(title)
                        .setItems(labels) { _, which ->
                            updateOrderStatus(filtered[which].id, toStatus)
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                }
            }

            override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                Log.e("API", "Gagal fetch orders: ${t.message}")
            }
        })
    }

    private fun updateOrderStatus(orderId: Int, newStatus: String) {
        RetrofitClient.apiService.updateOrderStatus(orderId, OrderUpdateRequest(newStatus))
            .enqueue(object : Callback<OrderResponse> {
                override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        loadDashboardData() // Refresh statistik secara real-time
                    }
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    Log.e("API", "Gagal update status")
                }
            })
    }

    private fun formatRupiah(number: Int): String {
        val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("in", "ID"))
        return format.format(number.toDouble()).replace("Rp", "Rp ").replace(",00", "")
    }
}