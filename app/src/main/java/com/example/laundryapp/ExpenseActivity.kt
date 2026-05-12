package com.example.laundryapp // Sesuaikan dengan package name kamu

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivityExpenseBinding

class ExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbarExpense)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarExpense.setNavigationOnClickListener { onBackPressed() }

        // Setup Dropdown Kategori
        setupCategoryDropdown()

        // Button Save Click
        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun setupCategoryDropdown() {
        val categories = arrayOf("Operasional", "Gaji", "Listrik & Air", "Sewa", "Lain-lain")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        binding.autoCompleteExpenseCategory.setAdapter(adapter)
    }

    private fun saveExpense() {
        val name = binding.etItemName.text.toString()
        val amount = binding.etAmount.text.toString()
        val category = binding.autoCompleteExpenseCategory.text.toString()
        val date = binding.etExpenseDate.text.toString()

        if (name.isEmpty() || amount.isEmpty() || category.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
        } else {
            // Di sini nanti panggil Retrofit:
            // RetrofitClient.instance.createExpense(ExpenseRequest(name, amount.toInt(), category, date))
            Toast.makeText(this, "Pengeluaran Berhasil Dicatat", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}