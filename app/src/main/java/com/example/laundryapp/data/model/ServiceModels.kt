package com.example.laundryapp.data.model

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("category_name")
    val categoryName: String,

    @SerializedName("description")
    val description: String?
)

data class ServiceRequest(

    @SerializedName("service_name")
    val serviceName: String,

    @SerializedName("price_per_kg")
    val pricePerKg: Int,

    @SerializedName("estimated_days")
    val estimatedDays: Int,

    @SerializedName("image_url")
    val imageUrl: String? = null
)

data class ServiceResponse(

    @SerializedName("id")
    val id: Int,

    @SerializedName("service_name")
    val serviceName: String,

    @SerializedName("price_per_kg")
    val pricePerKg: Int,

    @SerializedName("estimated_days")
    val estimatedDays: Int,

    @SerializedName("image_url")
    val imageUrl: String?
)