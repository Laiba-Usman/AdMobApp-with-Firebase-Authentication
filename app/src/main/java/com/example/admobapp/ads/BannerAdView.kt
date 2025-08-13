package com.example.admobapp.ads

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.example.admobapp.utils.NetworkUtils

@Composable
fun BannerAdView() {
    val context = LocalContext.current
    var adView: AdView? by remember { mutableStateOf(null) }

    if (NetworkUtils.isNetworkAvailable(context)) {
        AndroidView(
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = AdIds.bannerAdUnitId
                    loadAd(AdRequest.Builder().build())
                    adView = this
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            adView?.destroy()
        }
    }
}

@Composable
fun MediumBannerAdView() {
    val context = LocalContext.current
    var adView: AdView? by remember { mutableStateOf(null) }

    if (NetworkUtils.isNetworkAvailable(context)) {
        AndroidView(
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.MEDIUM_RECTANGLE)
                    adUnitId = AdIds.bannerAdUnitId
                    loadAd(AdRequest.Builder().build())
                    adView = this
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            adView?.destroy()
        }
    }
}
