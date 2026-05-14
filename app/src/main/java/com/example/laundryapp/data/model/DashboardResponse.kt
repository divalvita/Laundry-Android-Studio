package com.example.laundryapp.data.model

import com.google.gson.annotations.SerializedName

data class WeeklyIncome(
    @SerializedName("label")  val label: String,
    @SerializedName("amount") val amount: Int
)

data class DashboardResponse(
    @SerializedName("active_orders")  val activeOrders: Int,
    @SerializedName("done_orders")    val doneOrders: Int,
    @SerializedName("taken_orders")   val takenOrders: Int,
    @SerializedName("total_orders")   val totalOrders: Int,
    @SerializedName("total_income")   val totalIncome: Int,
    @SerializedName("total_expense")  val totalExpense: Int,
    @SerializedName("total_profit")   val totalProfit: Int,
    @SerializedName("weekly_income")  val weeklyIncome: List<WeeklyIncome>,
    @SerializedName("monthly_income") val monthlyIncome: List<WeeklyIncome>
)