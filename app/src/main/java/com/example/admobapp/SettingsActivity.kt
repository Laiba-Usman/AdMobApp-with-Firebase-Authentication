package com.example.admobapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.admobapp.ads.AdMobManager
import com.example.admobapp.ads.BannerAdView
import com.example.admobapp.ads.MediumBannerAdView
import com.example.admobapp.ui.theme.AdMobAppTheme
import android.content.Intent
import android.net.Uri
import com.example.admobapp.ThemePreference

class SettingsActivity : ComponentActivity() {
    private lateinit var adMobManager: AdMobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adMobManager = AdMobManager(this)

        // Load rewarded ad
        adMobManager.loadRewardedAd()

        setContent {
            var darkPref by remember { mutableStateOf(ThemePreference.getDarkMode(this) ?: true) }
            AdMobAppTheme(darkTheme = darkPref) {
                SettingsScreen(
                    onBackClick = { finish() },
                    onShowRewardedAd = {
                        adMobManager.showRewardedAd(this,
                            onUserEarnedReward = { amount ->
                                // Handle reward
                            }
                        )
                    },
                    onLogout = {
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                        startActivity(android.content.Intent(this@SettingsActivity, LoginActivity::class.java))
                        finish()
                    },
                    onDarkModeChanged = { enabled ->
                        darkPref = enabled
                        ThemePreference.setDarkMode(this, enabled)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onShowRewardedAd: () -> Unit,
    onLogout: () -> Unit,
    onDarkModeChanged: (Boolean) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(ThemePreference.getDarkMode(context) ?: true) }
    var autoPlayVideos by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0E13))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121826)
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    SettingsSection(title = "Preferences") {
                        SettingSwitchItem(
                            title = "Notifications",
                            subtitle = "Receive push notifications",
                            icon = Icons.Default.Notifications,
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )

                        SettingSwitchItem(
                            title = "Dark Mode",
                            subtitle = "Use dark theme",
                            icon = Icons.Default.DarkMode,
                            checked = darkModeEnabled,
                            onCheckedChange = {
                                darkModeEnabled = it
                                onDarkModeChanged(it)
                            }
                        )

                        SettingSwitchItem(
                            title = "Auto-play Videos",
                            subtitle = "Automatically play videos",
                            icon = Icons.Default.PlayArrow,
                            checked = autoPlayVideos,
                            onCheckedChange = { autoPlayVideos = it }
                        )
                    }
                }

                item {
                    SettingsSection(title = "Rewards") {
                        SettingsActionItem(
                            title = "Watch Ad for Rewards",
                            subtitle = "Earn coins by watching ads",
                            icon = Icons.Default.Paid,
                            onClick = onShowRewardedAd
                        )
                        SettingsActionItem(
                            title = "Ad Unit Settings",
                            subtitle = "Configure AdMob unit IDs",
                            icon = Icons.Default.Tune,
                            onClick = {
                                context.startActivity(android.content.Intent(context, AdminAdSettingsActivity::class.java))
                            }
                        )
                    }
                }

                item {
                    // Medium Rectangle Ad in the middle
                    MediumBannerAdView()
                }

                item {
                    SettingsSection(title = "Account") {
                        SettingsActionItem(
                            title = "Privacy Policy",
                            subtitle = "Read our privacy policy",
                            icon = Icons.Default.Policy,
                            onClick = {
                                val url = context.getString(R.string.privacy_policy_url)
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        )

                        SettingsActionItem(
                            title = "Terms of Service",
                            subtitle = "View terms and conditions",
                            icon = Icons.Default.Description,
                            onClick = {
                                val url = context.getString(R.string.terms_url)
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }
                        )

                        SettingsActionItem(
                            title = "Logout",
                            subtitle = "Sign out of your account",
                            icon = Icons.Default.Logout,
                            onClick = onLogout,
                            textColor = Color(0xFFFF6B6B)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp)) // Space for banner ad
                }
            }
        }

        // Banner Ad at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            BannerAdView()
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121826)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFFAAB4FF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun SettingSwitchItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFFAAB4FF),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = Color(0xFF8FA0FF),
                fontSize = 14.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF1B2A49),
                uncheckedThumbColor = Color(0xFF1B2A49),
                uncheckedTrackColor = Color(0xFF394367)
            )
        )
    }
}

@Composable
fun SettingsActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    textColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFFAAB4FF),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = Color(0xFF8FA0FF),
                fontSize = 14.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = Color(0xFFAAB4FF),
            modifier = Modifier.size(20.dp)
        )
    }
}

