package com.example.myandroidapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myandroidapp.network.RetrofitClient
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdWatchActivity : AppCompatActivity() {

    private var rewardedAd: RewardedAd? = null
    private val adUnitId = "ca-app-pub-3940256099942544/5224354917" // Test Ad Unit ID
    private var adStartTime: Long = 0L
    private var maxAdWatchLimit = 50
    private var targetActivity: Class<*>? = null // Store target activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_watch)

        // Set Custom ActionBar
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setCustomView(R.layout.custom_action_bar)

            // Set dynamic title
            val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.actionbar_title)
            titleTextView?.text = "Ads Community"
        }

        // Get the target activity class from intent
        val targetActivityClassName = intent.getStringExtra("TARGET_ACTIVITY_CLASS")
        targetActivity = targetActivityClassName?.let {
            try {
                Class.forName(it)
            } catch (e: ClassNotFoundException) {
                Log.e("AdWatchActivity", "Target activity not found: ${e.message}")
                null
            }
        }

        // Check how many ads the user has watched today
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val adsWatchedToday = sharedPreferences.getInt("ADS_WATCHED_TODAY", 0)

        if (adsWatchedToday < maxAdWatchLimit) {
            // Load the rewarded ad and show the prompt after loading
            loadRewardedAd()
        } else {
            // If the user has reached the daily limit, skip the ad and navigate
            navigateToTargetActivity()
        }
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                Log.d("AdWatchActivity", "Ad loaded successfully")
                showAdPrompt() // Show the popup only after ad is loaded
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("AdWatchActivity", "Ad failed to load: ${adError.message}")
                Toast.makeText(this@AdWatchActivity, "Ad failed to load. Skipping...", Toast.LENGTH_SHORT).show()
                navigateToTargetActivity() // Skip and go to the next screen
            }
        })
    }

    private fun showAdPrompt() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Watch Ad")
            .setMessage("Do you want to watch an ad and earn rewards?")
            .setPositiveButton("Yes") { _, _ ->
                showRewardedAd()
            }
            .setNegativeButton("No") { _, _ ->
                navigateToTargetActivity()
            }
            .setCancelable(false)
            .show()
    }

    private fun showRewardedAd() {
        rewardedAd?.let { ad ->
            adStartTime = System.currentTimeMillis()
            ad.show(this, OnUserEarnedRewardListener { rewardItem ->
                val adDuration = ((System.currentTimeMillis() - adStartTime) / 1000).toInt()
                Toast.makeText(this, "You earned ${rewardItem.amount} points!", Toast.LENGTH_SHORT).show()

                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getLong("USER_ID", -1)

                if (userId != -1L) {
                    incrementAdWatchCount()
                    sendAdWatchDataToBackend(userId, adDuration)
                } else {
                    Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show()
                }

                navigateToTargetActivity()
            })
        } ?: run {
            Toast.makeText(this, "Ad not loaded. Please try again later.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun incrementAdWatchCount() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val lastUpdatedDate = sharedPreferences.getString("LAST_AD_WATCH_DATE", "")
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Reset count if the date has changed
        if (lastUpdatedDate != currentDate) {
            editor.putInt("ADS_WATCHED_TODAY", 0)
            editor.putString("LAST_AD_WATCH_DATE", currentDate)
        }

        val adsWatchedToday = sharedPreferences.getInt("ADS_WATCHED_TODAY", 0)
        if (adsWatchedToday < maxAdWatchLimit) {
            editor.putInt("ADS_WATCHED_TODAY", adsWatchedToday + 1)
        }

        editor.apply()
    }


    private fun navigateToTargetActivity() {
        targetActivity?.let {
            startActivity(Intent(this, it))
        }
        finish()
    }

    private fun sendAdWatchDataToBackend(userId: Long, adDuration: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.adApiService.recordAdWatch(userId, adDuration)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdWatchActivity, "Ad watch recorded!", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("AdWatchActivity", "Failed to record ad watch. Response Code: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("AdWatchActivity", "Exception: ${e.message}")
                }
            }
        }
    }

}
