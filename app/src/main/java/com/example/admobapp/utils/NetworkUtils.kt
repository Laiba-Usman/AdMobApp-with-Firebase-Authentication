package com.example.admobapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

    fun isSlowNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return true
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return true

            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    val linkDownstreamBandwidth = networkCapabilities.linkDownstreamBandwidthKbps
                    linkDownstreamBandwidth < 1000 // Less than 1 Mbps considered slow
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    val linkDownstreamBandwidth = networkCapabilities.linkDownstreamBandwidthKbps
                    linkDownstreamBandwidth < 500 // Less than 500 Kbps considered slow for cellular
                }
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            when (networkInfo?.type) {
                ConnectivityManager.TYPE_MOBILE -> {
                    when (networkInfo.subtype) {
                        android.telephony.TelephonyManager.NETWORK_TYPE_1xRTT,
                        android.telephony.TelephonyManager.NETWORK_TYPE_CDMA,
                        android.telephony.TelephonyManager.NETWORK_TYPE_EDGE,
                        android.telephony.TelephonyManager.NETWORK_TYPE_GPRS,
                        android.telephony.TelephonyManager.NETWORK_TYPE_IDEN -> true
                        else -> false
                    }
                }
                else -> false
            }
        }
    }
}

