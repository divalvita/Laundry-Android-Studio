package com.example.laundryapp.data.model

import com.google.gson.annotations.SerializedName

data class CustomerRequest(
    @SerializedName("name")    val name: String,
    @SerializedName("phone")   val phone: String,
    @SerializedName("address") val address: String
)

data class CustomerResponse(
    @SerializedName("id")      val id: Int,
    @SerializedName("name")    val name: String,
    @SerializedName("phone")   val phone: String,
    @SerializedName("address") val address: String
)