package com.example.laundryapp.data.model

import com.google.gson.annotations.SerializedName

data class ExpenseRequest(
    @SerializedName("item_name") val itemName: String,
    @SerializedName("amount")    val amount: Int,
    @SerializedName("category")  val category: String,
    @SerializedName("date")      val date: String
)

data class ExpenseResponse(
    @SerializedName("id")        val id: Int,
    @SerializedName("item_name") val itemName: String,
    @SerializedName("amount")    val amount: Int,
    @SerializedName("category")  val category: String,
    @SerializedName("date")      val date: String
)