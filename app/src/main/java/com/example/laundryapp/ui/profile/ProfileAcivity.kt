package com.example.laundryapp.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundryapp.R
import com.example.laundryapp.data.api.RetrofitClient
import com.example.laundryapp.data.model.UserResponse
import com.example.laundryapp.data.model.UserUpdateRequest
import com.example.laundryapp.databinding.ActivityProfileBinding
import com.example.laundryapp.ui.auth.LoginActivity
import com.example.laundryapp.ui.help.HelpActivity
import com.example.laundryapp.ui.notification.NotificationActivity
import com.example.laundryapp.ui.settings.SettingsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.laundryapp.ui.about.AboutAppActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private var userId: Int = 0
    private var userName: String = "User Laundry"
    private var userEmail: String = "-"
    private var userRole: String = "staff"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserData()
        setupView()
        setupClick()
    }

    private fun loadUserData() {
        val pref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)

        userId = intent.getIntExtra(
            "user_id",
            pref.getInt("user_id", 0)
        )

        userName = intent.getStringExtra("user_name")
            ?: pref.getString("user_name", "User Laundry")
                    ?: "User Laundry"

        userEmail = intent.getStringExtra("user_email")
            ?: pref.getString("user_email", "-")
                    ?: "-"

        userRole = intent.getStringExtra("user_role")
            ?: pref.getString("user_role", "staff")
                    ?: "staff"

        saveUserData()
    }

    private fun saveUserData() {
        val pref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        pref.edit()
            .putInt("user_id", userId)
            .putString("user_name", userName)
            .putString("user_email", userEmail)
            .putString("user_role", userRole)
            .apply()
    }

    private fun setupView() {
        binding.tvInitial.text = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
        binding.tvProfileName.text = userName
        binding.tvProfileRoleTop.text = userRole.replaceFirstChar { it.uppercase() }

        binding.tvNameValue.text = userName
        binding.tvEmailValue.text = userEmail
        binding.tvRoleValue.text = userRole.replaceFirstChar { it.uppercase() }
    }

    private fun setupClick() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.cardAccount.setOnClickListener {
            showEditProfileDialog()
        }

        binding.rowNotification.setOnClickListener {
            startActivity(Intent(this, AboutAppActivity::class.java))
        }

        binding.rowSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.rowHelp.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            val pref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
            pref.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showEditProfileDialog() {
        if (userId == 0) {
            Toast.makeText(
                this,
                "User ID tidak ditemukan. Login ulang dulu.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val view = layoutInflater.inflate(R.layout.dialog_edit_profile, null)

        val etName = view.findViewById<EditText>(R.id.etEditName)
        val etEmail = view.findViewById<EditText>(R.id.etEditEmail)
        val etRole = view.findViewById<EditText>(R.id.etEditRole)

        etName.setText(userName)
        etEmail.setText(userEmail)
        etRole.setText(userRole)

        AlertDialog.Builder(this)
            .setTitle("Edit Profile")
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = etName.text.toString().trim()
                val newEmail = etEmail.text.toString().trim()
                val newRole = etRole.text.toString().trim()

                if (newName.length < 3) {
                    Toast.makeText(this, "Nama minimal 3 karakter", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newEmail.isEmpty() || !newEmail.contains("@")) {
                    Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newRole.isEmpty()) {
                    Toast.makeText(this, "Role tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                updateUserProfile(newName, newEmail, newRole)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateUserProfile(
        newName: String,
        newEmail: String,
        newRole: String
    ) {
        val request = UserUpdateRequest(
            name = newName,
            email = newEmail,
            role = newRole
        )

        RetrofitClient.apiService.updateUser(userId, request)
            .enqueue(object : Callback<UserResponse> {

                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful) {
                        val updatedUser = response.body()

                        if (updatedUser != null) {
                            userName = updatedUser.name
                            userEmail = updatedUser.email
                            userRole = updatedUser.role

                            saveUserData()
                            setupView()

                            Toast.makeText(
                                this@ProfileActivity,
                                "Profile berhasil diupdate",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Gagal update profile: ${response.code()} ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Error update profile: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}