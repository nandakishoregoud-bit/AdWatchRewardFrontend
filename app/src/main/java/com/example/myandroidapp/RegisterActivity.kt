package com.example.myandroidapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myandroidapp.models.UserRegistrationDTO
import com.example.myandroidapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var verificationCodeEditText: EditText
    private lateinit var sendVerificationButton: Button
    private lateinit var registerButton: Button
    private lateinit var codeErrorTextView: TextView

    private var verificationCode: String? = null // Code received from backend

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        // Set Custom ActionBar
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)
            // Enable back button in ActionBar
            setDisplayHomeAsUpEnabled(true)

            // Set dynamic title
            val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
            titleTextView?.text = "Registaion"
        }

        nameEditText = findViewById(R.id.name)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        verificationCodeEditText = findViewById(R.id.verification_code)
        sendVerificationButton = findViewById(R.id.send_verification_button)
        registerButton = findViewById(R.id.register_button)
        codeErrorTextView = findViewById(R.id.code_error_text_view)

        sendVerificationButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (isValidEmail(email)) {
                sendVerificationCode(email)
            } else {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            }
        }

        // Add TextWatcher to verify the code in real time
        verificationCodeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val enteredCode = s.toString()
                if (verificationCode != null && enteredCode == verificationCode) {
                    codeErrorTextView.text = "Code verified!"
                    codeErrorTextView.setTextColor(getColor(android.R.color.holo_green_dark))
                    registerButton.isEnabled = true // Enable the register button
                } else {
                    codeErrorTextView.text = "Invalid verification code"
                    codeErrorTextView.setTextColor(getColor(android.R.color.holo_red_dark))
                    registerButton.isEnabled = false // Disable the register button
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun sendVerificationCode(email: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = RetrofitClient.userApiService.sendVerificationCode(email)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    verificationCode = responseBody?.code // Store the verification code
                    Toast.makeText(this@RegisterActivity, "Verification code sent to $email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RegisterActivity, "Failed to send verification code", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser() {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        val registrationDTO = UserRegistrationDTO(name, email, password, null)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = RetrofitClient.userApiService.registerUser(registrationDTO)
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()

                    // Redirect to Login Activity
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Close RegisterActivity

                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }




    // Handle back button click
    override fun onSupportNavigateUp(): Boolean {
        finish() // Closes this activity and goes back
        return true
    }
}
