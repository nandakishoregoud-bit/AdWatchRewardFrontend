package com.example.myandroidapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.myandroidapp.models.PaymentDetails
import com.example.myandroidapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WalletActivity : AppCompatActivity() {

    private lateinit var balanceTextView: TextView
    private lateinit var pointsEditText: EditText
    private lateinit var rewardTypeSpinner: Spinner
    private lateinit var redeemButton: Button
    private lateinit var paymentDetailsContainer: LinearLayout
    private lateinit var adsWatchedTextView: TextView
    private lateinit var successCard: LinearLayout

    private var selectedRewardType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        // Set Custom ActionBar
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)
            setDisplayHomeAsUpEnabled(true)

            // Set dynamic title
            val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
            titleTextView?.text = "Wallet"
        }

        // Initialize success card
        successCard = findViewById(R.id.successCard)


        // Initialize views
        balanceTextView = findViewById(R.id.balanceTextView)
        pointsEditText = findViewById(R.id.pointsEditText)
        rewardTypeSpinner = findViewById(R.id.rewardTypeSpinner)
        redeemButton = findViewById(R.id.redeemButton)
        paymentDetailsContainer = findViewById(R.id.paymentDetailsLayout)
        adsWatchedTextView = findViewById(R.id.tv_ads_watched)

        val rewardTypes = listOf("UPI", "BANK TRANSFER", "PAYPAL", "AMAZON PAY")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rewardTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rewardTypeSpinner.adapter = adapter

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("USER_ID", -1)

        // Fetch wallet balance
        getWalletBalance(userId)

        // Load and display ad count
        updateAdsWatchedCount()

        // Handle reward type selection
        rewardTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRewardType = rewardTypes[position]
                showPaymentDetailsInput(selectedRewardType!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Redeem button click
        redeemButton.setOnClickListener {
            val points = pointsEditText.text.toString().toDoubleOrNull()

            // Check if points are valid and at least $1.00 (10,000 points)
            if (points == null || points < 1.0) {
                Toast.makeText(this, "Minimum withdrawal is $1.00", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedRewardType == null) {
                Toast.makeText(this, "Please select a reward type.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentDetails = collectPaymentDetails()
            if (paymentDetails == null) {
                Toast.makeText(this, "Enter valid payment details.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Directly redeem points (which are already in double format)
            redeemPoints(userId, points, selectedRewardType!!, paymentDetails)

            // Show success message
            showSuccessCard()
        }


    }

    private fun updateAdsWatchedCount() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val adsWatchedToday = sharedPreferences.getInt("ADS_WATCHED_TODAY", 0)

        adsWatchedTextView.text = "Ads Watched Today: $adsWatchedToday"
    }

    private fun getWalletBalance(userId: Long) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = RetrofitClient.walletApiService.getWalletBalance(userId)
                if (response.isSuccessful) {
                    balanceTextView.text = "Balance: ${response.body()} "
                } else {
                    Toast.makeText(this@WalletActivity, "Failed to fetch balance", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@WalletActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun redeemPoints(userId: Long, points: Double, rewardType: String, paymentDetails: PaymentDetails) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = RetrofitClient.walletApiService.redeemPoints(
                    userId, points, rewardType,
                    paymentDetails.upiId, paymentDetails.bankAccountNumber,
                    paymentDetails.bankIFSC, paymentDetails.paypalEmail,
                    paymentDetails.amazonPayNumber
                )
                if (response.isSuccessful) {
                    Toast.makeText(this@WalletActivity, response.body(), Toast.LENGTH_SHORT).show()
                    getWalletBalance(userId)
                } else {
                    Toast.makeText(this@WalletActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@WalletActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPaymentDetailsInput(rewardType: String) {
        paymentDetailsContainer.removeAllViews()
        paymentDetailsContainer.visibility = View.VISIBLE
        when (rewardType) {
            "UPI" -> {
                val upiInput = createInputField("Enter UPI ID")
                paymentDetailsContainer.addView(upiInput)
            }
            "BANK TRANSFER" -> {
                val bankAccountInput = createInputField("Enter Bank Account Number")
                val ifscInput = createInputField("Enter IFSC Code")
                paymentDetailsContainer.addView(bankAccountInput)
                paymentDetailsContainer.addView(ifscInput)
            }
            "PAYPAL" -> {
                val paypalInput = createInputField("Enter PayPal Email")
                paymentDetailsContainer.addView(paypalInput)
            }
            "AMAZON PAY" -> {
                val amazonPayInput = createInputField("Enter Amazon Pay Number")
                paymentDetailsContainer.addView(amazonPayInput)
            }
        }
    }

    private fun createInputField(hint: String): EditText {
        return EditText(this).apply {
            this.hint = hint
            this.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun collectPaymentDetails(): PaymentDetails? {
        val inputs = paymentDetailsContainer.children.toList().filterIsInstance<EditText>()
        return when (selectedRewardType) {
            "UPI" -> {
                val upiId = inputs.getOrNull(0)?.text?.toString()
                if (!upiId.isNullOrBlank()) PaymentDetails(upiId = upiId) else null
            }
            "BANK TRANSFER" -> {
                val bankAccount = inputs.getOrNull(0)?.text?.toString()
                val ifscCode = inputs.getOrNull(1)?.text?.toString()
                if (!bankAccount.isNullOrBlank() && !ifscCode.isNullOrBlank()) {
                    PaymentDetails(bankAccountNumber = bankAccount, bankIFSC = ifscCode)
                } else null
            }
            "PAYPAL" -> {
                val paypalEmail = inputs.getOrNull(0)?.text?.toString()
                if (!paypalEmail.isNullOrBlank()) PaymentDetails(paypalEmail = paypalEmail) else null
            }
            "AMAZON PAY" -> {
                val amazonPayNumber = inputs.getOrNull(0)?.text?.toString()
                if (!amazonPayNumber.isNullOrBlank()) PaymentDetails(amazonPayNumber = amazonPayNumber) else null
            }
            else -> null
        }
    }

    override fun onResume() {
        super.onResume()
        updateAdsWatchedCount()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showSuccessCard() {
        successCard.visibility = View.VISIBLE
    }

}
