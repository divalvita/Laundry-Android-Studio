package com.example.laundryapp

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.DashboardResponse
import com.example.laundryapp.data.model.NotificationResponse
import com.example.laundryapp.data.model.OrderResponse
import com.example.laundryapp.data.model.OrderUpdateRequest
import com.example.laundryapp.data.model.WeeklyIncome
import com.example.laundryapp.databinding.ActivityMainBinding
import com.example.laundryapp.ui.customer.CustomerActivity
import com.example.laundryapp.ui.expense.ExpenseActivity
import com.example.laundryapp.ui.notification.NotificationActivity
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

    private var currentNotificationCount: Int = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        requestNotificationPermission()

        saveIntentUserToSession()
        loadUserSession()

        setupClickListeners()
        loadDashboardData()
        loadNotificationBadge()
    }

    override fun onResume() {
        super.onResume()

        loadUserSession()
        loadDashboardData()
        loadNotificationBadge()
    }

    private fun saveIntentUserToSession() {
        val pref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)

        val userId = intent.getIntExtra("user_id", 0)
        val userName = intent.getStringExtra("user_name")
        val userEmail = intent.getStringExtra("user_email")
        val userRole = intent.getStringExtra("user_role")

        if (userId != 0) {
            pref.edit()
                .putInt("user_id", userId)
                .putString("user_name", userName)
                .putString("user_email", userEmail)
                .putString("user_role", userRole)
                .apply()
        }
    }

    private fun loadUserSession() {
        val pref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)

        val userName = pref.getString("user_name", "Admin") ?: "Admin"

        binding.tvGreeting.text = "Halo, $userName"
    }

    private fun setupClickListeners() {
        binding.imgProfile.setOnClickListener {
            val pref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)

            val profileIntent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("user_id", pref.getInt("user_id", 0))
                putExtra("user_name", pref.getString("user_name", "Admin"))
                putExtra("user_email", pref.getString("user_email", "-"))
                putExtra("user_role", pref.getString("user_role", "admin"))
            }

            startActivity(profileIntent)
        }

        binding.cardNotification.setOnClickListener {
            markNotificationsAsRead()
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        binding.btnClosePopup.setOnClickListener {
            hidePopupNotification()
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

    private fun loadNotificationBadge() {
        val settingsPref = getSharedPreferences("APP_SETTINGS", MODE_PRIVATE)
        val notificationEnabled = settingsPref.getBoolean("notification_enabled", true)

        if (!notificationEnabled) {
            updateBadge(0)
            return
        }

        RetrofitClient.apiService.getNotifications()
            .enqueue(object : Callback<List<NotificationResponse>> {

                override fun onResponse(
                    call: Call<List<NotificationResponse>>,
                    response: Response<List<NotificationResponse>>
                ) {
                    if (response.isSuccessful) {
                        val notifications = response.body() ?: emptyList()

                        currentNotificationCount = notifications.size

                        val pref = getSharedPreferences("NOTIF_PREF", MODE_PRIVATE)
                        val readCount = pref.getInt("READ_NOTIF_COUNT", 0)
                        val unreadCount = (currentNotificationCount - readCount).coerceAtLeast(0)

                        updateBadge(unreadCount)

                        if (notifications.isNotEmpty()) {
                            checkAndShowNewNotification(notifications)
                        }
                    } else {
                        Log.e("NOTIF", "Gagal notif: ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: Call<List<NotificationResponse>>,
                    t: Throwable
                ) {
                    Log.e("NOTIF", "Error notif: ${t.message}")
                }
            })
    }

    private fun updateBadge(count: Int) {
        if (count > 0) {
            binding.tvNotificationBadge.visibility = View.VISIBLE
            binding.tvNotificationBadge.text = if (count > 99) "99+" else count.toString()
        } else {
            binding.tvNotificationBadge.visibility = View.GONE
        }
    }

    private fun checkAndShowNewNotification(notifications: List<NotificationResponse>) {
        val latestNotif = notifications.maxByOrNull { it.id } ?: return

        val pref = getSharedPreferences("NOTIF_PREF", MODE_PRIVATE)
        val lastNotifId = pref.getInt("LAST_NOTIF_ID", 0)

        if (lastNotifId == 0) {
            pref.edit()
                .putInt("LAST_NOTIF_ID", latestNotif.id)
                .apply()
            return
        }

        if (latestNotif.id > lastNotifId) {
            showTopNotification(latestNotif)

            pref.edit()
                .putInt("LAST_NOTIF_ID", latestNotif.id)
                .apply()
        }
    }

    private fun showTopNotification(notification: NotificationResponse) {
        binding.cardPopupNotification.visibility = View.VISIBLE

        binding.tvPopupTitle.text = notification.title
        binding.tvPopupMessage.text = notification.message
        binding.tvPopupTime.text = "sekarang"

        binding.cardPopupNotification.translationY = -250f
        binding.cardPopupNotification.alpha = 0f

        binding.cardPopupNotification.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(350)
            .start()

        binding.cardPopupNotification.setOnClickListener {
            markNotificationsAsRead()
            hidePopupNotification()
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            hidePopupNotification()
        }, 5000)
    }

    private fun hidePopupNotification() {
        if (binding.cardPopupNotification.visibility == View.VISIBLE) {
            binding.cardPopupNotification.animate()
                .translationY(-250f)
                .alpha(0f)
                .setDuration(250)
                .withEndAction {
                    binding.cardPopupNotification.visibility = View.GONE
                }
                .start()
        }
    }

    private fun markNotificationsAsRead() {
        val pref = getSharedPreferences("NOTIF_PREF", MODE_PRIVATE)

        pref.edit()
            .putInt("READ_NOTIF_COUNT", currentNotificationCount)
            .apply()

        updateBadge(0)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
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
        RetrofitClient.apiService.getDashboard()
            .enqueue(object : Callback<DashboardResponse> {

                override fun onResponse(
                    call: Call<DashboardResponse>,
                    response: Response<DashboardResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!

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
                        Log.e(
                            "LAUNDRY_DEBUG",
                            "Response Error: ${response.code()} - ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<DashboardResponse>,
                    t: Throwable
                ) {
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

    private fun showStatusDialog(
        fromStatus: String,
        toStatus: String,
        title: String,
        emptyMsg: String
    ) {
        RetrofitClient.apiService.getOrders()
            .enqueue(object : Callback<List<OrderResponse>> {

                override fun onResponse(
                    call: Call<List<OrderResponse>>,
                    response: Response<List<OrderResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val filtered = response.body()!!.filter {
                            it.status.equals(fromStatus, ignoreCase = true)
                        }

                        if (filtered.isEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                emptyMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }

                        val labels = filtered.map {
                            "Order #${it.id} - ${it.weight}kg"
                        }.toTypedArray()

                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(title)
                            .setItems(labels) { _, which ->
                                updateOrderStatus(filtered[which].id, toStatus)
                            }
                            .setNegativeButton("Batal", null)
                            .show()
                    }
                }

                override fun onFailure(
                    call: Call<List<OrderResponse>>,
                    t: Throwable
                ) {
                    Log.e("API", "Gagal fetch orders: ${t.message}")
                }
            })
    }

    private fun updateOrderStatus(orderId: Int, newStatus: String) {
        RetrofitClient.apiService.updateOrderStatus(
            orderId,
            OrderUpdateRequest(newStatus)
        ).enqueue(object : Callback<OrderResponse> {

            override fun onResponse(
                call: Call<OrderResponse>,
                response: Response<OrderResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity,
                        "Berhasil diperbarui",
                        Toast.LENGTH_SHORT
                    ).show()

                    loadDashboardData()
                }
            }

            override fun onFailure(
                call: Call<OrderResponse>,
                t: Throwable
            ) {
                Log.e("API", "Gagal update status")
            }
        })
    }

    private fun formatRupiah(number: Int): String {
        val format = java.text.NumberFormat
            .getCurrencyInstance(java.util.Locale("in", "ID"))

        return format.format(number.toDouble())
            .replace("Rp", "Rp ")
            .replace(",00", "")
    }
}