package com.example.laundryapp.model

data class ServiceRequest(
    val category_id: Int,
    val service_name: String,
    val price_per_kg: Int,
    val estimated_days: Int,
    val image_url: String? = null
)