package com.example.laundryapp.data.model

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("name")    val name: String,
    @SerializedName("email")   val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role")    val role: String = "admin"
)

data class UserResponse(
    @SerializedName("id")    val id: Int,
    @SerializedName("name")  val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("role")  val role: String
)