package com.example.laundryapp.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 1. Ganti ke URL Ngrok static kamu
    private const val BASE_URL = "https://quicken-churn-tarmac.ngrok-free.dev/"

    // 2. Buat Client untuk menyelipkan tiket "bypass" Ngrok
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                // Header wajib agar Ngrok langsung memberikan JSON, bukan halaman HTML "Visit Site"
                .addHeader("ngrok-skip-browser-warning", "true")
                .build()
            chain.proceed(request)
        }
        .build()

    // 3. Bangun objek Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Hubungkan client yang sudah kita buat di atas
            .build()
    }

    // 4. Inisialisasi ApiService yang akan dipanggil di Activity
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}