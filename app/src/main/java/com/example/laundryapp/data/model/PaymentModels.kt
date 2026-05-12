package com.example.laundryapp.data.model

import com.google.gson.annotations.SerializedName

data class PaymentRequest(
    @SerializedName("order_id")       val orderId: Int,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("amount_paid")    val amountPaid: Int
)

data class PaymentResponse(
    @SerializedName("id")             val id: Int,
    @SerializedName("order_id")       val orderId: Int,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("amount_paid")    val amountPaid: Int,
    @SerializedName("payment_status") val paymentStatus: String,
    @SerializedName("payment_date")   val paymentDate: String
)