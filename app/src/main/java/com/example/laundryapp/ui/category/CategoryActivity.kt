package com.example.laundryapp.ui.category

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laundryapp.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(binding.toolbarCategory)

        supportActionBar?.apply {
            title = "Category"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbarCategory.setNavigationOnClickListener {
            finish()
        }

        // RecyclerView
        binding.rvCategory.layoutManager =
            LinearLayoutManager(this)

        // Nanti adapter category dipasang di sini
        // binding.rvCategory.adapter = CategoryAdapter(...)
    }
}