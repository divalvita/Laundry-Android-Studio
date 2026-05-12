package com.example.laundryapp.model

data class Order(
    val id: Int,
    val user_id: Int,
    val customer_id: Int,
    val service_id: Int,
    val weight: Double,
    val total_price: Int,
    val status: String,
    val order_date: String
)