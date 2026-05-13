package com.example.laundryapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.MainActivity
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.UserResponse
import com.example.laundryapp.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Harap isi email dan password", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.apiService.getUsers()
            .enqueue(object : Callback<List<UserResponse>> {

                override fun onResponse(
                    call: Call<List<UserResponse>>,
                    response: Response<List<UserResponse>>
                ) {
                    if (response.isSuccessful) {
                        val users = response.body()
                        val matchedUser = users?.find { it.email == email }

                        if (matchedUser == null) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Email tidak ditemukan!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Selamat datang, ${matchedUser.name}!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("user_id", matchedUser.id)
                            intent.putExtra("user_name", matchedUser.name)
                            intent.putExtra("user_email", matchedUser.email)
                            intent.putExtra("user_role", matchedUser.role)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Gagal mengambil data: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Gagal terhubung ke server: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}