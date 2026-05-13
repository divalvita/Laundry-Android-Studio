package com.example.laundryapp.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.UserRequest
import com.example.laundryapp.data.model.UserResponse
import com.example.laundryapp.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnRegister.setOnClickListener {
            performRegister()
        }

        binding.tvLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun performRegister() {
        val nama = binding.etRegName.text.toString().trim()
        val email = binding.etRegEmail.text.toString().trim()
        val pass = binding.etRegPassword.text.toString().trim()

        // 1. Validasi Input sesuai skema Python (min_length=3 untuk nama, min_length=8 untuk pass)
        if (nama.length < 3) {
            binding.etRegName.error = "Nama minimal 3 karakter"
            return
        }

        if (email.isEmpty()) {
            binding.etRegEmail.error = "Email wajib diisi"
            return
        }

        if (pass.length < 8) {
            binding.etRegPassword.error = "Password minimal 8 karakter"
            return
        }

        // 2. Bungkus data ke dalam Model UserRequest
        val registerRequest = UserRequest(
            name = nama,
            email = email,
            password = pass,
            role = "admin"
        )

        // 3. Memanggil createUser (Nama fungsi baru di ApiService)
        RetrofitClient.apiService.createUser(registerRequest).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // Menampilkan pesan error asli dari server (penting untuk debug 422)
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@RegisterActivity, "Gagal: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Kesalahan Jaringan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}