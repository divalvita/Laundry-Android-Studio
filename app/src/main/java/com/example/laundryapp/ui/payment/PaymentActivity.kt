package com.example.laundryapp.ui.payment

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.R
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.NotificationRequest
import com.example.laundryapp.data.model.NotificationResponse
import com.example.laundryapp.data.model.PaymentRequest
import com.example.laundryapp.data.model.PaymentResponse
import com.example.laundryapp.databinding.ActivityPaymentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding

    private var orderId: Int = 0
    private var customerId: Int = 0
    private var totalPrice: Int = 0
    private var selectedMethod: String = "cash"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarPayment)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarPayment.setNavigationOnClickListener {
            finish()
        }

        orderId = intent.getIntExtra("order_id", 0)
        customerId = intent.getIntExtra("customer_id", 0)
        totalPrice = intent.getIntExtra("total_price", 0)

        binding.tvTotalAmount.text = "Rp $totalPrice"

        binding.rbCash.isChecked = true

        binding.rgPaymentMethod.setOnCheckedChangeListener { _, checkedId ->
            selectedMethod = when (checkedId) {
                R.id.rbCash -> "cash"
                R.id.rbTransfer -> "transfer"
                else -> "cash"
            }
        }

        binding.btnConfirmPayment.setOnClickListener {
            confirmPayment()
        }
    }

    private fun confirmPayment() {
        if (orderId == 0) {
            Toast.makeText(
                this,
                "Order ID tidak ditemukan",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (totalPrice <= 0) {
            Toast.makeText(
                this,
                "Total pembayaran tidak valid",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val request = PaymentRequest(
            orderId = orderId,
            paymentMethod = selectedMethod,
            amountPaid = totalPrice
        )

        RetrofitClient.apiService.createPayment(request)
            .enqueue(object : Callback<PaymentResponse> {

                override fun onResponse(
                    call: Call<PaymentResponse>,
                    response: Response<PaymentResponse>
                ) {
                    if (response.isSuccessful) {

                        createPaymentNotification()

                        Toast.makeText(
                            this@PaymentActivity,
                            "Pembayaran berhasil",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {
                        Toast.makeText(
                            this@PaymentActivity,
                            "Gagal bayar: ${response.code()} ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<PaymentResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Error payment: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun createPaymentNotification() {
        if (customerId == 0) {
            return
        }

        val request = NotificationRequest(
            customerId = customerId,
            title = "Pembayaran Berhasil",
            message = "Pembayaran untuk order #$orderId berhasil sebesar Rp $totalPrice"
        )

        RetrofitClient.apiService.createNotification(request)
            .enqueue(object : Callback<NotificationResponse> {

                override fun onResponse(
                    call: Call<NotificationResponse>,
                    response: Response<NotificationResponse>
                ) {
                    // sukses notif
                }

                override fun onFailure(
                    call: Call<NotificationResponse>,
                    t: Throwable
                ) {
                    // abaikan
                }
            })
    }
}