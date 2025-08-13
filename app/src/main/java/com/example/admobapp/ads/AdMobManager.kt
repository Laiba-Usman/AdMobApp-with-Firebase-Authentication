package com.example.admobapp.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.database.*
import java.util.*

object AdIds {
    // Defaults to Google test IDs; override from Firebase
    @Volatile var appOpenAdUnitId: String = "ca-app-pub-3940256099942544/3419835294"
    @Volatile var interstitialAdUnitId: String = "ca-app-pub-3940256099942544/1033173712"
    @Volatile var bannerAdUnitId: String = "ca-app-pub-3940256099942544/6300978111"
    @Volatile var rewardedAdUnitId: String = "ca-app-pub-3940256099942544/5224354917"
}

class AdMobManager(private val context: Context) {
    private var appOpenAd: AppOpenAd? = null
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var isLoadingAd = false
    private var appOpenAdLoadTime: Long = 0

    private val database = FirebaseDatabase.getInstance()
    private val adIdsRef = database.getReference("ad_ids")

    // Ad Unit IDs (may be overridden by Firebase values at runtime)
    private val APP_OPEN_AD_UNIT_ID get() = AdIds.appOpenAdUnitId
    private val INTERSTITIAL_AD_UNIT_ID get() = AdIds.interstitialAdUnitId
    private val BANNER_AD_UNIT_ID get() = AdIds.bannerAdUnitId
    private val REWARDED_AD_UNIT_ID get() = AdIds.rewardedAdUnitId

    companion object {
        private const val TAG = "AdMobManager"
        private const val AD_TIMEOUT_MS = 4000L // 4 seconds timeout
    }

    init {
        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(context)
        // Optional: mark emulator as test device
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
            .build()
        MobileAds.setRequestConfiguration(configuration)

        fetchAdIdsFromFirebase()
    }

    private fun fetchAdIdsFromFirebase() {
        adIdsRef.get().addOnSuccessListener { snapshot ->
            snapshot.child("app_open_ad").getValue(String::class.java)?.let { AdIds.appOpenAdUnitId = it }
            snapshot.child("interstitial_ad").getValue(String::class.java)?.let { AdIds.interstitialAdUnitId = it }
            snapshot.child("banner_ad").getValue(String::class.java)?.let { AdIds.bannerAdUnitId = it }
            snapshot.child("rewarded_ad").getValue(String::class.java)?.let { AdIds.rewardedAdUnitId = it }
        }
    }

    fun loadAppOpenAd() {
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()

        AppOpenAd.load(
            context,
            APP_OPEN_AD_UNIT_ID,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.d(TAG, "App open ad loaded")
                    appOpenAd = ad
                    appOpenAdLoadTime = Date().time
                    isLoadingAd = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "App open ad failed to load: ${loadAdError.message}")
                    isLoadingAd = false
                }
            }
        )
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - appOpenAdLoadTime
        val numMilliSecondsPerHour = 3600000L
        return dateDifference < (numMilliSecondsPerHour * numHours)
    }

    fun showAppOpenAd(activity: Activity, onAdDismissed: (() -> Unit)? = null) {
        if (!isAdAvailable()) {
            loadAppOpenAd()
            onAdDismissed?.invoke()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "App open ad dismissed")
                appOpenAd = null
                isLoadingAd = false
                loadAppOpenAd()
                onAdDismissed?.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "App open ad failed to show: ${adError.message}")
                appOpenAd = null
                isLoadingAd = false
                loadAppOpenAd()
                onAdDismissed?.invoke()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "App open ad showed")
            }
        }

        appOpenAd?.show(activity)
    }

    fun loadInterstitialAd(onAdLoaded: (() -> Unit)? = null, onAdFailedToLoad: (() -> Unit)? = null) {
        val request = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded")
                    interstitialAd = ad
                    onAdLoaded?.invoke()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                    interstitialAd = null
                    onAdFailedToLoad?.invoke()
                }
            }
        )
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: (() -> Unit)? = null) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed")
                    interstitialAd = null
                    onAdDismissed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                    interstitialAd = null
                    onAdDismissed?.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad showed")
                }
            }

            interstitialAd?.show(activity)
        } else {
            Log.d(TAG, "Interstitial ad not ready")
            onAdDismissed?.invoke()
        }
    }

    fun loadRewardedAd(onAdLoaded: (() -> Unit)? = null) {
        val request = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            request,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Rewarded ad loaded")
                    rewardedAd = ad
                    onAdLoaded?.invoke()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                    rewardedAd = null
                }
            }
        )
    }

    fun showRewardedAd(activity: Activity, onUserEarnedReward: ((Int) -> Unit)? = null, onAdDismissed: (() -> Unit)? = null) {
        if (rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Rewarded ad dismissed")
                    rewardedAd = null
                    loadRewardedAd()
                    onAdDismissed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Rewarded ad failed to show: ${adError.message}")
                    rewardedAd = null
                    loadRewardedAd()
                    onAdDismissed?.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Rewarded ad showed")
                }
            }

            rewardedAd?.show(activity) { rewardItem ->
                Log.d(TAG, "User earned reward: ${rewardItem.amount}")
                onUserEarnedReward?.invoke(rewardItem.amount)
            }
        } else {
            Log.d(TAG, "Rewarded ad not ready")
            onAdDismissed?.invoke()
        }
    }

    fun isInterstitialAdLoaded(): Boolean {
        return interstitialAd != null
    }

    fun isRewardedAdLoaded(): Boolean {
        return rewardedAd != null
    }
}