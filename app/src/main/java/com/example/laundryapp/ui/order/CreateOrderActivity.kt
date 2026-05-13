package com.example.laundryapp.ui.order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.CustomerResponse
import com.example.laundryapp.data.model.OrderRequest
import com.example.laundryapp.data.model.OrderResponse
import com.example.laundryapp.data.model.ServiceResponse
import com.example.laundryapp.databinding.ActivityCreateOrderBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateOrderBinding

    private var customers: List<CustomerResponse> =
        emptyList()

    private var services: List<ServiceResponse> =
        emptyList()

    private var selectedCustomer:
            CustomerResponse? = null

    private var selectedService:
            ServiceResponse? = null

    private var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityCreateOrderBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbarOrder)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarOrder.setNavigationOnClickListener {
            finish()
        }

        loadCustomers()

        loadServices()

        binding.etWeight.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {
                    calculateTotal()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
            }
        )

        binding.btnNextPayment.setOnClickListener {
            createOrder()
        }
    }

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

                        val customerNames =
                            customers.map { it.name }

                        val adapter = ArrayAdapter(
                            this@CreateOrderActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            customerNames
                        )

                        binding.autoCompleteCustomer
                            .setAdapter(adapter)

                        binding.autoCompleteCustomer
                            .setOnItemClickListener { _, _, position, _ ->

                                selectedCustomer =
                                    customers[position]
                            }

                    } else {

                        Toast.makeText(
                            this@CreateOrderActivity,
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
                        this@CreateOrderActivity,
                        "Error customer: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

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

                        val serviceNames =
                            services.map {

                                "${it.serviceName} - Rp ${it.pricePerKg}/Kg"
                            }

                        val adapter = ArrayAdapter(
                            this@CreateOrderActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            serviceNames
                        )

                        binding.autoCompleteService
                            .setAdapter(adapter)

                        binding.autoCompleteService
                            .setOnItemClickListener { _, _, position, _ ->

                                selectedService =
                                    services[position]

                                calculateTotal()
                            }

                    } else {

                        Toast.makeText(
                            this@CreateOrderActivity,
                            "Gagal mengambil layanan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<ServiceResponse>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@CreateOrderActivity,
                        "Error service: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun calculateTotal() {

        val service = selectedService

        val weight =
            binding.etWeight.text.toString()
                .toDoubleOrNull() ?: 0.0

        totalPrice =
            if (service != null && weight > 0) {

                (service.pricePerKg * weight).toInt()

            } else {

                0
            }

        binding.tvTotalPrice.text =
            "Rp $totalPrice"
    }

    private fun createOrder() {

        val customer = selectedCustomer

        val service = selectedService

        val weight =
            binding.etWeight.text.toString()
                .toFloatOrNull()

        if (customer == null) {

            Toast.makeText(
                this,
                "Pilih customer dulu",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (service == null) {

            Toast.makeText(
                this,
                "Pilih layanan dulu",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (weight == null || weight <= 0f) {

            Toast.makeText(
                this,
                "Berat harus lebih dari 0",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val request = OrderRequest(
            customerId = customer.id,
            serviceId = service.id,
            weight = weight
        )

        RetrofitClient.apiService.createOrder(request)
            .enqueue(object : Callback<OrderResponse> {

                override fun onResponse(
                    call: Call<OrderResponse>,
                    response: Response<OrderResponse>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@CreateOrderActivity,
                            "Order berhasil dibuat",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this@CreateOrderActivity,
                            "Gagal buat order: ${response.code()} ${
                                response.errorBody()?.string()
                            }",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<OrderResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@CreateOrderActivity,
                        "Error order: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}