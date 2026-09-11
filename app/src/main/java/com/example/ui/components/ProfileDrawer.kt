package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.UserSession
import com.example.ui.theme.DarkPurple
import com.example.ui.theme.HighlightPurple
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.StopRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProfileDrawerContent(
    userSession: UserSession?,
    onNavigateHome: () -> Unit,
    onNavigateResources: () -> Unit,
    onNavigateGitHub: () -> Unit,
    onNavigateTerms: () -> Unit,
    onNavigatePrivacy: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(DarkPurple)
            .testTag("profile_drawer")
    ) {
        // Top section: #4c1d95 bg, 120dp height
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(RoyalPurple)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar (56dp circle)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(DarkPurple)
                    .border(1.5.dp, HighlightPurple, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!userSession?.photoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = userSession!!.photoUrl,
                        contentDescription = "User Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.vip_dark_host_icon_1789100637341),
                        contentDescription = "Logo Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userSession?.displayName ?: "The Gsm Work",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = userSession?.email ?: "thegsmwork@gmail.com",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Menu items
        DrawerMenuItem(
            label = "Home",
            icon = Icons.Default.Home,
            iconTint = HighlightPurple,
            onClick = onNavigateHome
        )

        DrawerMenuItem(
            label = "Resources",
            icon = Icons.Default.BarChart,
            iconTint = HighlightPurple,
            onClick = onNavigateResources
        )

        DrawerMenuItem(
            label = "GitHub",
            icon = Icons.Default.CloudDownload,
            iconTint = HighlightPurple,
            onClick = onNavigateGitHub
        )

        HorizontalDivider(
            color = RoyalPurple,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        DrawerMenuItem(
            label = "Terms of Use",
            icon = Icons.Default.Description,
            iconTint = TextMuted,
            onClick = onNavigateTerms
        )

        DrawerMenuItem(
            label = "Privacy Policy",
            icon = Icons.Default.Security,
            iconTint = TextMuted,
            onClick = onNavigatePrivacy
        )

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            color = RoyalPurple,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Logout
        DrawerMenuItem(
            label = "Logout",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            iconTint = StopRed,
            labelColor = StopRed,
            onClick = onLogout
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    labelColor: Color = TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = labelColor
        )
    }
}
