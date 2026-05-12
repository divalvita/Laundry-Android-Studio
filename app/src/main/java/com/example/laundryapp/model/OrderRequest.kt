package com.example.laundryapp.model

data class OrderRequest(
    val customer_id: Int,
    val service_id: Int,
    val weight: Double
)