package com.example.laundryapp.ui.expense

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.ExpenseResponse
import com.example.laundryapp.databinding.ActivityExpenseHistoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExpenseHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExpenseHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarExpenseHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarExpenseHistory.setNavigationOnClickListener {
            finish()
        }

        binding.rvExpenseHistory.layoutManager = LinearLayoutManager(this)

        binding.fabAddExpense.setOnClickListener {
            startActivity(
                Intent(this, ExpenseActivity::class.java)
            )
        }

        getExpenses()
    }

    override fun onResume() {
        super.onResume()
        getExpenses()
    }

    private fun getExpenses() {
        RetrofitClient.apiService.getExpenses()
            .enqueue(object : Callback<List<ExpenseResponse>> {

                override fun onResponse(
                    call: Call<List<ExpenseResponse>>,
                    response: Response<List<ExpenseResponse>>
                ) {
                    if (response.isSuccessful) {
                        val expenses = response.body() ?: emptyList()

                        binding.rvExpenseHistory.adapter = ExpenseAdapter(
                            expenses = expenses,

                            onEditClick = { expense ->
                                val intent = Intent(
                                    this@ExpenseHistoryActivity,
                                    ExpenseActivity::class.java
                                )

                                intent.putExtra("expense_id", expense.id)
                                intent.putExtra("item_name", expense.itemName)
                                intent.putExtra("amount", expense.amount)
                                intent.putExtra("category", expense.category)
                                intent.putExtra("date", expense.date)

                                startActivity(intent)
                            },

                            onDeleteClick = { expense ->
                                confirmDeleteExpense(expense)
                            }
                        )

                    } else {
                        Toast.makeText(
                            this@ExpenseHistoryActivity,
                            "Gagal mengambil expense: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<ExpenseResponse>>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@ExpenseHistoryActivity,
                        "Gagal terhubung ke server: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun confirmDeleteExpense(expense: ExpenseResponse) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Expense")
            .setMessage("Yakin mau hapus ${expense.itemName}?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteExpense(expense.id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteExpense(id: Int) {
        RetrofitClient.apiService.deleteExpense(id)
            .enqueue(object : Callback<Unit> {

                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ExpenseHistoryActivity,
                            "Expense berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()

                        getExpenses()

                    } else {
                        Toast.makeText(
                            this@ExpenseHistoryActivity,
                            "Gagal hapus expense: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@ExpenseHistoryActivity,
                        "Error DELETE: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}