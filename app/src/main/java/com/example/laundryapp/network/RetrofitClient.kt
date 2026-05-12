package com.example.laundryapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Kalau pakai EMULATOR Android Studio
    private const val BASE_URL = "http://192.168.1.175:8000/"

    // Kalau pakai HP asli, ganti jadi IP laptop, contoh:
    // private const val BASE_URL = "http://192.168.1.25:8000/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}