package com.example.myandroidapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myandroidapp.models.UserLoginDTO
import com.example.myandroidapp.models.UserLoginResponseDTO
import com.example.myandroidapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // Set Custom ActionBar
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)

            // Set dynamic title
            val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
            titleTextView?.text = "Login"
        }


        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        registerTextView = findViewById(R.id.register_link)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            val loginDTO = UserLoginDTO(email, password)

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val response = RetrofitClient.userApiService.loginUser(loginDTO)
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        // Log the entire login response
                        Log.d("LoginDebug", "Login Response: $loginResponse")

                        // Store login response in SharedPreferences
                        storeUserData(loginResponse)

                        Toast.makeText(this@LoginActivity, "Welcome, ${loginResponse?.name}", Toast.LENGTH_SHORT).show()

                        // Check if the user is admin
                        if (loginResponse?.isAdmin == true) {
                            val intent = Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this@LoginActivity, EmptyScreenActivity::class.java)
                            startActivity(intent)
                        }
                        finish() // Optional: Close LoginActivity
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set click listener for the register link
        registerTextView.setOnClickListener {
            onRegisterClick(it)
        }
    }

    // Method to handle register link click
    fun onRegisterClick(view: android.view.View) {
        // Intent to navigate to RegisterActivity
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    // Function to store user data in SharedPreferences
    private fun storeUserData(loginResponse: UserLoginResponseDTO?) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_NAME", loginResponse?.name)
        editor.putLong("USER_ID", loginResponse?.id ?: -1)
        editor.putString("USER_EMAIL", loginResponse?.email)
        editor.putBoolean("IS_ADMIN", loginResponse?.isAdmin ?: false)
        editor.apply()
    }
}
