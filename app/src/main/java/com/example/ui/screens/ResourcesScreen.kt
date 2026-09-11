package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.VipTopAppBar
import com.example.ui.theme.BandwidthBadgeBg
import com.example.ui.theme.BandwidthBadgeText
import com.example.ui.theme.CpuBadgeBg
import com.example.ui.theme.CpuBadgeText
import com.example.ui.theme.HighlightPurple
import com.example.ui.theme.RamBadgeBg
import com.example.ui.theme.RamBadgeText
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.StorageBadgeBg
import com.example.ui.theme.StorageBadgeText
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBlack
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ResourcesScreen(
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userSession by viewModel.userSession.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .testTag("resources_screen")
    ) {
        // AppBar
        VipTopAppBar(
            userSession = userSession,
            onAvatarClick = onOpenDrawer,
            onLogoutClick = { viewModel.logout() }
        )

        // Center Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("resources_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header: bar-chart icon #a855f7 + "Your Resources" 18sp bold #e9d5ff
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = HighlightPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Your Resources",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Divider: 1dp #4c1d95
                    HorizontalDivider(
                        color = RoyalPurple,
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. CPU
                    ResourceItemRow(
                        icon = Icons.Default.Speed,
                        label = "CPU (vCPU)",
                        value = "0.2",
                        badgeBg = CpuBadgeBg,
                        badgeTextColor = CpuBadgeText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Storage
                    ResourceItemRow(
                        icon = Icons.Default.SdStorage,
                        label = "Storage (GB)",
                        value = "2",
                        badgeBg = StorageBadgeBg,
                        badgeTextColor = StorageBadgeText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. RAM
                    ResourceItemRow(
                        icon = Icons.Default.Memory,
                        label = "RAM (MB)",
                        value = "512",
                        badgeBg = RamBadgeBg,
                        badgeTextColor = RamBadgeText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Bandwidth
                    ResourceItemRow(
                        icon = Icons.Default.Wifi,
                        label = "Bandwidth",
                        value = "Unmetered",
                        badgeBg = BandwidthBadgeBg,
                        badgeTextColor = BandwidthBadgeText
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Footer note: info icon + "Resources are per bot container." 12sp #6b7280
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Resources are per bot container.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResourceItemRow(
    icon: ImageVector,
    label: String,
    value: String,
    badgeBg: Color,
    badgeTextColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = HighlightPurple,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }

        // Value Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(badgeBg)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = badgeTextColor
            )
        }
    }
}
