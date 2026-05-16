package com.example.laundryapp.ui.service

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laundryapp.MainActivity
import com.example.laundryapp.R
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.ServiceRequest
import com.example.laundryapp.data.model.ServiceResponse
import com.example.laundryapp.databinding.ActivityServiceBinding
import com.example.laundryapp.ui.order.CreateOrderActivity
import com.example.laundryapp.ui.order.OrderHistoryActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarService)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarService.setNavigationOnClickListener {
            finish()
        }

        binding.rvService.layoutManager = LinearLayoutManager(this)

        setupBottomNavigation()

        getServices()

        binding.fabAddService.setOnClickListener {
            showServiceDialog(null)
        }
    }

    override fun onResume() {
        super.onResume()
        getServices()
    }

    private fun setupBottomNavigation() {

        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->

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

    private fun getServices() {

        RetrofitClient.apiService.getServices()
            .enqueue(object : Callback<List<ServiceResponse>> {

                override fun onResponse(
                    call: Call<List<ServiceResponse>>,
                    response: Response<List<ServiceResponse>>
                ) {

                    if (response.isSuccessful) {

                        val services = response.body() ?: emptyList()

                        binding.rvService.adapter = ServiceAdapter(
                            services,

                            onEditClick = { service ->
                                showServiceDialog(service)
                            },

                            onDeleteClick = { service ->
                                confirmDeleteService(service)
                            }
                        )

                    } else {

                        Toast.makeText(
                            this@ServiceActivity,
                            "Gagal mengambil data: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<ServiceResponse>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ServiceActivity,
                        "Gagal terhubung ke server: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun showServiceDialog(service: ServiceResponse?) {

        val view = layoutInflater.inflate(R.layout.dialog_service, null)

        val etName = view.findViewById<EditText>(R.id.etServiceName)
        val etPrice = view.findViewById<EditText>(R.id.etServicePrice)
        val etDays = view.findViewById<EditText>(R.id.etServiceDays)

        if (service != null) {

            etName.setText(service.serviceName
            )
            etPrice.setText(service.pricePerKg.toString())
            etDays.setText(service.estimatedDays.toString())
        }

        AlertDialog.Builder(this)
            .setTitle(
                if (service == null)
                    "Tambah Layanan"
                else
                    "Edit Layanan"
            )

            .setView(view)

            .setPositiveButton(
                if (service == null)
                    "Simpan"
                else
                    "Update"
            ) { _, _ ->

                val name = etName.text.toString().trim()
                val price = etPrice.text.toString().trim()
                val days = etDays.text.toString().trim()

                if (name.length < 3) {

                    Toast.makeText(
                        this,
                        "Nama layanan minimal 3 karakter",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                if (price.toIntOrNull() == null || price.toInt() <= 0) {

                    Toast.makeText(
                        this,
                        "Harga harus lebih dari 0",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                if (days.toIntOrNull() == null || days.toInt() <= 0) {

                    Toast.makeText(
                        this,
                        "Estimasi hari harus lebih dari 0",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val request = ServiceRequest(
                    serviceName = name,
                    pricePerKg = price.toInt(),
                    estimatedDays = days.toInt(),
                    imageUrl = service?.imageUrl ?: ""
                )

                if (service == null) {

                    createService(request)

                } else {

                    updateService(service.id, request)
                }
            }

            .setNegativeButton("Batal", null)

            .show()
    }

    private fun createService(service: ServiceRequest) {

        RetrofitClient.apiService.createService(service)
            .enqueue(object : Callback<ServiceResponse> {

                override fun onResponse(
                    call: Call<ServiceResponse>,
                    response: Response<ServiceResponse>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@ServiceActivity,
                            "Service berhasil ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()

                        getServices()

                    } else {

                        Toast.makeText(
                            this@ServiceActivity,
                            "Gagal tambah: ${response.code()} ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ServiceResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ServiceActivity,
                        "Error POST: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun updateService(
        id: Int,
        service: ServiceRequest
    ) {

        RetrofitClient.apiService.updateService(id, service)
            .enqueue(object : Callback<ServiceResponse> {

                override fun onResponse(
                    call: Call<ServiceResponse>,
                    response: Response<ServiceResponse>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@ServiceActivity,
                            "Service berhasil diupdate",
                            Toast.LENGTH_SHORT
                        ).show()

                        getServices()

                    } else {

                        Toast.makeText(
                            this@ServiceActivity,
                            "Gagal update: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ServiceResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ServiceActivity,
                        "Error UPDATE: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun confirmDeleteService(service: ServiceResponse) {

        AlertDialog.Builder(this)
            .setTitle("Hapus Layanan")

            .setMessage(
                "Yakin mau hapus ${service.serviceName}?"
            )

            .setPositiveButton("Hapus") { _, _ ->

                deleteService(service.id)
            }

            .setNegativeButton("Batal", null)

            .show()
    }

    private fun deleteService(id: Int) {

        RetrofitClient.apiService.deleteService(id)
            .enqueue(object : Callback<Unit> {

                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@ServiceActivity,
                            "Service berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()

                        getServices()

                    } else {

                        Toast.makeText(
                            this@ServiceActivity,
                            "Gagal hapus: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ServiceActivity,
                        "Error DELETE: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}