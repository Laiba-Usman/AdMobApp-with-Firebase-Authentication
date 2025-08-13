package com.example.admobapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
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
class AboutActivity : ComponentActivity() {
    private lateinit var adMobManager: AdMobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adMobManager = AdMobManager(this)

        setContent {
            AdMobAppTheme {
                AboutScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
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
                        text = "About",
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
                    AboutHeader()
                }

                item {
                    AboutDescription()
                }

                item {
                    FeaturesCard()
                }

                item {
                    TechnicalInfoCard()
                }

                item {
                    ContactCard()
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
fun AboutHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF121826)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFF1B2A49), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AA",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AdMob App",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Version 1.0.0",
                color = Color(0xFF8FA0FF),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun AboutDescription() {
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
                text = "About This App",
                color = Color(0xFFAAB4FF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "This is a demonstration app showcasing AdMob integration with Firebase Realtime Database. " +
                        "The app demonstrates various ad formats including App Open Ads, Banner Ads, Interstitial Ads, " +
                        "and Rewarded Ads implemented following Google's best practices.",
                color = Color(0xFF8FA0FF),
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun FeaturesCard() {
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
                text = "Features",
                color = Color(0xFFAAB4FF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            val features = listOf(
                "🚀 App Open Ads after splash screen",
                "📱 Banner Ads on every screen",
                "📺 Interstitial Ads between screens",
                "🎁 Rewarded Ads for user incentives",
                "🔥 Firebase Realtime Database integration",
                "🌙 Dark theme with maroon accents",
                "📡 Network connectivity handling",
                "🛡️ Proper error handling"
            )

            features.forEach { feature ->
                Text(
                    text = feature,
                    color = Color(0xFFD2B48C),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TechnicalInfoCard() {
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
                text = "Technical Details",
                color = Color(0xFF8B4513),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            TechnicalInfoItem("Framework", "Jetpack Compose")
            TechnicalInfoItem("Language", "Kotlin")
            TechnicalInfoItem("Ad Platform", "Google AdMob")
            TechnicalInfoItem("Database", "Firebase Realtime Database")
            TechnicalInfoItem("Architecture", "MVVM Pattern")
            TechnicalInfoItem("UI Theme", "Material Design 3")
        }
    }
}

@Composable
fun TechnicalInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFFD2B48C),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ContactCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D1B1B)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Contact & Support",
                color = Color(0xFF8B4513),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            ContactItem(
                icon = Icons.Default.Email,
                title = "Email",
                value = "support@admobapp.com"
            )

            ContactItem(
                icon = Icons.Default.Language,
                title = "Website",
                value = "www.admobapp.com"
            )

            ContactItem(
                icon = Icons.Default.Code,
                title = "GitHub",
                value = "github.com/admobapp"
            )
        }
    }
}

@Composable
fun ContactItem(
    icon: ImageVector,
    title: String,
    value: String
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
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = Color(0xFF8FA0FF),
                fontSize = 12.sp
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

