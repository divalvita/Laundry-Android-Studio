package com.example.laundryapp.data.model

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("service_id")  val serviceId: Int,
    @SerializedName("weight")      val weight: Float
)

data class OrderUpdateRequest(
    @SerializedName("status") val status: String
)

data class OrderResponse(
    @SerializedName("id")          val id: Int,
    @SerializedName("user_id")     val userId: Int,
    @SerializedName("customer_id") val customerId: Int,
    @SerializedName("service_id")  val serviceId: Int,
    @SerializedName("weight")      val weight: Float,
    @SerializedName("total_price") val totalPrice: Int,
    @SerializedName("status")      val status: String,
    @SerializedName("order_date")  val orderDate: String
)