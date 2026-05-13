package com.example.laundryapp.ui.customer

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laundryapp.R
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.CustomerRequest
import com.example.laundryapp.data.model.CustomerResponse
import com.example.laundryapp.databinding.ActivityCustomerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarCustomer)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarCustomer.setNavigationOnClickListener {
            finish()
        }

        binding.rvCustomer.layoutManager =
            LinearLayoutManager(this)

        getCustomers()

        binding.fabAddCustomer.setOnClickListener {
            showCustomerDialog(null)
        }
    }

    private fun getCustomers() {

        RetrofitClient.apiService.getCustomers()
            .enqueue(object : Callback<List<CustomerResponse>> {

                override fun onResponse(
                    call: Call<List<CustomerResponse>>,
                    response: Response<List<CustomerResponse>>
                ) {

                    if (response.isSuccessful) {

                        val customers =
                            response.body() ?: emptyList()

                        binding.rvCustomer.adapter =
                            CustomerAdapter(

                                customers,

                                onEditClick = { customer ->
                                    showCustomerDialog(customer)
                                },

                                onDeleteClick = { customer ->
                                    confirmDeleteCustomer(customer)
                                }
                            )

                    } else {

                        Toast.makeText(
                            this@CustomerActivity,
                            "Gagal mengambil data customer",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<CustomerResponse>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@CustomerActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showCustomerDialog(
        customer: CustomerResponse?
    ) {

        val view = layoutInflater.inflate(
            R.layout.dialog_customer,
            null
        )

        val etName =
            view.findViewById<EditText>(R.id.etCustomerName)

        val etPhone =
            view.findViewById<EditText>(R.id.etCustomerPhone)

        val etAddress =
            view.findViewById<EditText>(R.id.etCustomerAddress)

        if (customer != null) {

            etName.setText(customer.name)
            etPhone.setText(customer.phone)
            etAddress.setText(customer.address)
        }

        AlertDialog.Builder(this)

            .setTitle(
                if (customer == null)
                    "Tambah Pelanggan"
                else
                    "Edit Pelanggan"
            )

            .setView(view)

            .setPositiveButton(
                if (customer == null)
                    "Simpan"
                else
                    "Update"
            ) { _, _ ->

                val name =
                    etName.text.toString().trim()

                val phone =
                    etPhone.text.toString().trim()

                val address =
                    etAddress.text.toString().trim()

                if (name.length < 3) {

                    Toast.makeText(
                        this,
                        "Nama minimal 3 karakter",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                if (phone.length < 10) {

                    Toast.makeText(
                        this,
                        "Nomor HP minimal 10 angka",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                if (address.length < 5) {

                    Toast.makeText(
                        this,
                        "Alamat minimal 5 karakter",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                val request = CustomerRequest(
                    name = name,
                    phone = phone,
                    address = address
                )

                if (customer == null) {

                    createCustomer(request)

                } else {

                    updateCustomer(customer.id, request)
                }
            }

            .setNegativeButton("Batal", null)

            .show()
    }

    private fun createCustomer(
        customer: CustomerRequest
    ) {

        RetrofitClient.apiService.createCustomer(customer)
            .enqueue(object : Callback<CustomerResponse> {

                override fun onResponse(
                    call: Call<CustomerResponse>,
                    response: Response<CustomerResponse>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@CustomerActivity,
                            "Customer berhasil ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()

                        getCustomers()

                    } else {

                        Toast.makeText(
                            this@CustomerActivity,
                            "Gagal tambah: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<CustomerResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@CustomerActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun updateCustomer(
        id: Int,
        customer: CustomerRequest
    ) {

        RetrofitClient.apiService.updateCustomer(
            id,
            customer
        ).enqueue(object : Callback<CustomerResponse> {

            override fun onResponse(
                call: Call<CustomerResponse>,
                response: Response<CustomerResponse>
            ) {

                if (response.isSuccessful) {

                    Toast.makeText(
                        this@CustomerActivity,
                        "Customer berhasil diupdate",
                        Toast.LENGTH_SHORT
                    ).show()

                    getCustomers()

                } else {

                    Toast.makeText(
                        this@CustomerActivity,
                        "Gagal update: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<CustomerResponse>,
                t: Throwable
            ) {

                Toast.makeText(
                    this@CustomerActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun confirmDeleteCustomer(
        customer: CustomerResponse
    ) {

        AlertDialog.Builder(this)

            .setTitle("Hapus Pelanggan")

            .setMessage(
                "Yakin mau hapus ${customer.name}?"
            )

            .setPositiveButton("Hapus") { _, _ ->

                deleteCustomer(customer.id)
            }

            .setNegativeButton("Batal", null)

            .show()
    }

    private fun deleteCustomer(id: Int) {

        RetrofitClient.apiService.deleteCustomer(id)
            .enqueue(object : Callback<Unit> {

                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@CustomerActivity,
                            "Customer berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()

                        getCustomers()

                    } else {

                        Toast.makeText(
                            this@CustomerActivity,
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
                        this@CustomerActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}