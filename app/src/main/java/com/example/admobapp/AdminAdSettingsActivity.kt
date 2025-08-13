package com.example.admobapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.admobapp.ads.AdIds
import com.example.admobapp.ui.theme.AdMobAppTheme
import com.google.firebase.database.FirebaseDatabase

class AdminAdSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdMobAppTheme {
                AdminAdSettingsScreen(
                    onBack = { finish() },
                    onSaved = {
                        Toast.makeText(this, "Ad unit IDs saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAdSettingsScreen(onBack: () -> Unit, onSaved: () -> Unit) {
    var appOpen by remember { mutableStateOf("") }
    var interstitial by remember { mutableStateOf("") }
    var banner by remember { mutableStateOf("") }
    var rewarded by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("ad_ids")
        ref.get().addOnSuccessListener { snap ->
            appOpen = snap.child("app_open_ad").getValue(String::class.java) ?: AdIds.appOpenAdUnitId
            interstitial = snap.child("interstitial_ad").getValue(String::class.java) ?: AdIds.interstitialAdUnitId
            banner = snap.child("banner_ad").getValue(String::class.java) ?: AdIds.bannerAdUnitId
            rewarded = snap.child("rewarded_ad").getValue(String::class.java) ?: AdIds.rewardedAdUnitId
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0E13))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Ad Settings",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = appOpen,
                onValueChange = { appOpen = it },
                label = { Text("App Open Ad Unit ID", color = Color(0xFF8FA0FF)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAAB4FF),
                    unfocusedBorderColor = Color(0xFF394367),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color(0xFF8FA0FF)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = interstitial,
                onValueChange = { interstitial = it },
                label = { Text("Interstitial Ad Unit ID", color = Color(0xFF8FA0FF)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAAB4FF),
                    unfocusedBorderColor = Color(0xFF394367),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color(0xFF8FA0FF)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = banner,
                onValueChange = { banner = it },
                label = { Text("Banner Ad Unit ID", color = Color(0xFF8FA0FF)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAAB4FF),
                    unfocusedBorderColor = Color(0xFF394367),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color(0xFF8FA0FF)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = rewarded,
                onValueChange = { rewarded = it },
                label = { Text("Rewarded Ad Unit ID", color = Color(0xFF8FA0FF)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFAAB4FF),
                    unfocusedBorderColor = Color(0xFF394367),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color(0xFF8FA0FF)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121826)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = Color.White)
                }
                Button(
                    onClick = {
                        val ref = FirebaseDatabase.getInstance().getReference("ad_ids")
                        val payload = mapOf(
                            "app_open_ad" to appOpen.trim(),
                            "interstitial_ad" to interstitial.trim(),
                            "banner_ad" to banner.trim(),
                            "rewarded_ad" to rewarded.trim()
                        )
                        ref.setValue(payload).addOnCompleteListener {
                            // Update runtime cache immediately
                            AdIds.appOpenAdUnitId = appOpen.trim()
                            AdIds.interstitialAdUnitId = interstitial.trim()
                            AdIds.bannerAdUnitId = banner.trim()
                            AdIds.rewardedAdUnitId = rewarded.trim()
                            onSaved()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B2A49)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save", color = Color.White)
                }
            }
        }
    }
}

