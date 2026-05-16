package com.example.laundryapp.ui.report

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivityReportBinding
import com.example.laundryapp.ui.expense.ExpenseHistoryActivity
import com.example.laundryapp.ui.order.OrderHistoryActivity

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardOrderHistory.setOnClickListener {
            startActivity(
                Intent(this, OrderHistoryActivity::class.java)
            )
        }

        binding.cardExpenseHistory.setOnClickListener {
            startActivity(
                Intent(this, ExpenseHistoryActivity::class.java)
            )
        }
    }
}