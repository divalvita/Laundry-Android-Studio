package com.example.laundryapp.ui.order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivityCreateOrderBinding
import com.example.laundryapp.model.Customer
import com.example.laundryapp.model.Order
import com.example.laundryapp.model.OrderRequest
import com.example.laundryapp.model.Service
import com.example.laundryapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateOrderBinding

    private var customers: List<Customer> = emptyList()
    private var services: List<Service> = emptyList()

    private var selectedCustomer: Customer? = null
    private var selectedService: Service? = null

    private var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarOrder)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarOrder.setNavigationOnClickListener {
            finish()
        }

        loadCustomers()
        loadServices()

        binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateTotal()
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {}
        })

        binding.btnNextPayment.setOnClickListener {
            createOrder()
        }
    }

    private fun loadCustomers() {
        RetrofitClient.apiService.getCustomers()
            .enqueue(object : Callback<List<Customer>> {

                override fun onResponse(
                    call: Call<List<Customer>>,
                    response: Response<List<Customer>>
                ) {
                    if (response.isSuccessful) {
                        customers = response.body() ?: emptyList()

                        val customerNames = customers.map { it.name }

                        val adapter = ArrayAdapter(
                            this@CreateOrderActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            customerNames
                        )

                        binding.autoCompleteCustomer.setAdapter(adapter)

                        binding.autoCompleteCustomer.setOnItemClickListener { _, _, position, _ ->
                            selectedCustomer = customers[position]
                        }
                    } else {
                        Toast.makeText(
                            this@CreateOrderActivity,
                            "Gagal mengambil customer",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
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
            .enqueue(object : Callback<List<Service>> {

                override fun onResponse(
                    call: Call<List<Service>>,
                    response: Response<List<Service>>
                ) {
                    if (response.isSuccessful) {
                        services = response.body() ?: emptyList()

                        val serviceNames = services.map {
                            "${it.service_name} - Rp ${it.price_per_kg}/Kg"
                        }

                        val adapter = ArrayAdapter(
                            this@CreateOrderActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            serviceNames
                        )

                        binding.autoCompleteService.setAdapter(adapter)

                        binding.autoCompleteService.setOnItemClickListener { _, _, position, _ ->
                            selectedService = services[position]
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

                override fun onFailure(call: Call<List<Service>>, t: Throwable) {
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
        val weight = binding.etWeight.text.toString().toDoubleOrNull() ?: 0.0

        totalPrice = if (service != null && weight > 0) {
            (service.price_per_kg * weight).toInt()
        } else {
            0
        }

        binding.tvTotalPrice.text = "Rp $totalPrice"
    }

    private fun createOrder() {
        val customer = selectedCustomer
        val service = selectedService
        val weight = binding.etWeight.text.toString().toDoubleOrNull()

        if (customer == null) {
            Toast.makeText(this, "Pilih customer dulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (service == null) {
            Toast.makeText(this, "Pilih layanan dulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (weight == null || weight <= 0) {
            Toast.makeText(this, "Berat harus lebih dari 0", Toast.LENGTH_SHORT).show()
            return
        }

        val request = OrderRequest(
            customer_id = customer.id,
            service_id = service.id,
            weight = weight
        )

        RetrofitClient.apiService.createOrder(request)
            .enqueue(object : Callback<Order> {

                override fun onResponse(
                    call: Call<Order>,
                    response: Response<Order>
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
                            "Gagal buat order: ${response.code()} ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Order>, t: Throwable) {
                    Toast.makeText(
                        this@CreateOrderActivity,
                        "Error order: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}