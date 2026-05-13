package com.example.laundryapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.databinding.ActivityMainBinding
import com.example.laundryapp.ui.customer.CustomerActivity
import com.example.laundryapp.ui.expense.ExpenseActivity
import com.example.laundryapp.ui.order.CreateOrderActivity
import com.example.laundryapp.ui.order.OrderHistoryActivity
import com.example.laundryapp.ui.service.ServiceActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // ================= DASHBOARD CARD =================

        binding.cardCustomer.setOnClickListener {
            startActivity(Intent(this, CustomerActivity::class.java))
        }

        binding.cardService.setOnClickListener {
            startActivity(Intent(this, ServiceActivity::class.java))
        }

        binding.cardOrder.setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        binding.cardExpense.setOnClickListener {
            startActivity(Intent(this, ExpenseActivity::class.java))
        }

        // ================= BOTTOM NAVIGATION =================

        binding.bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_dash -> true

                R.id.nav_service -> {
                    startActivity(Intent(this, ServiceActivity::class.java))
                    true
                }

                R.id.nav_order -> {
                    startActivity(Intent(this, CreateOrderActivity::class.java))
                    true
                }

                R.id.nav_report -> {
                    startActivity(Intent(this, OrderHistoryActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
}