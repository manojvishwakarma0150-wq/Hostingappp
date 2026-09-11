package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdMobManager(private val context: Context) {

    companion object {
        private const val TAG = "AdMobManager"
        // Official Google Sample Ad Unit ID for Rewarded Ads
        private const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    private var rewardedAd: RewardedAd? = null
    private var isLoadingAd: Boolean = false
    private var isInitialized: Boolean = false

    fun initialize() {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) {
                isInitialized = true
                loadRewardedAd()
            }
        } catch (e: Exception) {
            Log.e(TAG, "AdMob initialization failed", e)
        }
    }

    fun loadRewardedAd(onLoaded: (() -> Unit)? = null) {
        if (isLoadingAd || rewardedAd != null) return
        isLoadingAd = true

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            TEST_REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoadingAd = false
                    Log.d(TAG, "Rewarded ad successfully loaded")
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isLoadingAd = false
                    Log.w(TAG, "Rewarded ad failed to load: ${error.message}")
                }
            }
        )
    }

    fun isAdReady(): Boolean = rewardedAd != null

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: (amount: Int, type: String) -> Unit,
        onAdClosed: () -> Unit,
        onAdUnavailableFallback: () -> Unit
    ) {
        val ad = rewardedAd
        if (ad != null) {
            ad.show(activity) { rewardItem ->
                onRewardEarned(rewardItem.amount, rewardItem.type)
            }
            rewardedAd = null
            // Pre-load next ad
            loadRewardedAd()
            onAdClosed()
        } else {
            // If ad is not ready or failed to load in emulator, trigger fallback handler
            onAdUnavailableFallback()
            loadRewardedAd()
        }
    }
}
