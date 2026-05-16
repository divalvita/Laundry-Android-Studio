package com.example.laundryapp.ui.expense

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.ExpenseRequest
import com.example.laundryapp.data.model.ExpenseResponse
import com.example.laundryapp.databinding.ActivityExpenseBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseBinding

    private var expenseId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarExpense)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarExpense.setNavigationOnClickListener {
            finish()
        }

        setupCategoryDropdown()

        expenseId = intent.getIntExtra("expense_id", 0)

        if (expenseId != 0) {
            setupEditMode()
        }

        binding.etExpenseDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun setupEditMode() {
        val itemName = intent.getStringExtra("item_name")
        val amount = intent.getIntExtra("amount", 0)
        val category = intent.getStringExtra("category")
        val date = intent.getStringExtra("date")

        binding.etItemName.setText(itemName)
        binding.etAmount.setText(amount.toString())
        binding.autoCompleteExpenseCategory.setText(category, false)
        binding.etExpenseDate.setText(date)

        binding.btnSaveExpense.text = "Update Pengeluaran"
    }

    private fun setupCategoryDropdown() {
        val categories = arrayOf(
            "Operasional",
            "Gaji",
            "Listrik & Air",
            "Sewa",
            "Lain-lain"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            categories
        )

        binding.autoCompleteExpenseCategory.setAdapter(adapter)
    }

    private fun saveExpense() {
        val name = binding.etItemName.text.toString().trim()
        val amountText = binding.etAmount.text.toString().trim()
        val category = binding.autoCompleteExpenseCategory.text.toString().trim()
        val date = binding.etExpenseDate.text.toString().trim()

        if (
            name.isEmpty() ||
            amountText.isEmpty() ||
            category.isEmpty() ||
            date.isEmpty()
        ) {
            Toast.makeText(
                this,
                "Harap isi semua kolom",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val amount = amountText.toIntOrNull()

        if (amount == null || amount <= 0) {
            Toast.makeText(
                this,
                "Nominal harus lebih dari 0",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val request = ExpenseRequest(
            itemName = name,
            amount = amount,
            category = category,
            date = date
        )

        if (expenseId == 0) {
            createExpense(request)
        } else {
            updateExpense(expenseId, request)
        }
    }

    private fun createExpense(request: ExpenseRequest) {
        RetrofitClient.apiService.createExpense(request)
            .enqueue(object : Callback<ExpenseResponse> {

                override fun onResponse(
                    call: Call<ExpenseResponse>,
                    response: Response<ExpenseResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ExpenseActivity,
                            "Pengeluaran berhasil dicatat",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {
                        Toast.makeText(
                            this@ExpenseActivity,
                            "Gagal simpan: ${response.code()} ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ExpenseResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@ExpenseActivity,
                        "Error POST: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun updateExpense(
        id: Int,
        request: ExpenseRequest
    ) {
        RetrofitClient.apiService.updateExpense(id, request)
            .enqueue(object : Callback<ExpenseResponse> {

                override fun onResponse(
                    call: Call<ExpenseResponse>,
                    response: Response<ExpenseResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ExpenseActivity,
                            "Pengeluaran berhasil diupdate",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {
                        Toast.makeText(
                            this@ExpenseActivity,
                            "Gagal update: ${response.code()} ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ExpenseResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@ExpenseActivity,
                        "Error UPDATE: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                val format = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

                binding.etExpenseDate.setText(
                    format.format(calendar.time)
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }
}