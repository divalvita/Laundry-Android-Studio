package com.example.laundryapp.data.model

import com.google.gson.annotations.SerializedName

// Untuk POST /notifications/
data class NotificationRequest(

    @SerializedName("customer_id")
    val customerId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String
)

// Untuk response GET & POST /notifications/
data class NotificationResponse(

    @SerializedName("id")
    val id: Int,

    @SerializedName("customer_id")
    val customerId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("sent_at")
    val sentAt: String
)