package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.DarkPurple
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HighlightPurple
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBlack
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isSigningIn by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(24.dp)
            .testTag("login_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo (120dp circle, #1a0a2e fill, #7c3aed border 1.5dp)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(DarkPurple)
                    .border(1.5.dp, AccentPurple, CircleShape)
                    .testTag("app_logo_badge"),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vip_dark_host_icon_1789100637341),
                    contentDescription = "VIP Dark Host Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // "VIP" wordmark (28sp, bold, #a855f7, letter-spacing 3sp)
            Text(
                text = "VIP",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                color = HighlightPurple
            )

            // "DARK HOST" (18sp, bold, #e9d5ff, letter-spacing 6sp)
            Text(
                text = "DARK HOST",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline "PREMIUM BOT HOSTING" (11sp, 400, #9ca3af, letter-spacing 1sp)
            Text(
                text = "PREMIUM BOT HOSTING",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 1.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Divider: 1dp, #4c1d95 line, 140dp wide
            HorizontalDivider(
                color = RoyalPurple,
                thickness = 1.dp,
                modifier = Modifier.width(140.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Google Sign-In button: 280dp wide, #1a0a2e bg, #7c3aed border 1dp, radius 8dp
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkPurple)
                    .border(1.dp, AccentPurple, RoundedCornerShape(8.dp))
                    .clickable(enabled = !isSigningIn) {
                        isSigningIn = true
                        coroutineScope.launch {
                            viewModel.authRepository.signInWithGoogle()
                            isSigningIn = false
                        }
                    }
                    .testTag("google_signin_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isSigningIn) {
                    CircularProgressIndicator(
                        color = GoldAccent,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Google "G" representation
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF4285F4)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Continue with Google",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}
