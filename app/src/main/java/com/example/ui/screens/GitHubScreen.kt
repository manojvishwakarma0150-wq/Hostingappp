package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BotStatusCard
import com.example.ui.components.ConsolePanel
import com.example.ui.components.OfflineBanner
import com.example.ui.components.SessionTimerCard
import com.example.ui.components.VipTopAppBar
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.DarkPurple
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HighlightPurple
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBlack
import com.example.ui.viewmodel.MainViewModel

@Composable
fun GitHubScreen(
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val focusManager = LocalFocusManager.current

    val userSession by viewModel.userSession.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val isBotRunning by viewModel.isBotRunning.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val isStopping by viewModel.isStopping.collectAsState()
    val consoleLogs by viewModel.consoleLogs.collectAsState()

    val repoUrl by viewModel.repoUrl.collectAsState()
    val isCloning by viewModel.isCloning.collectAsState()
    val clonedPyFiles by viewModel.clonedPyFiles.collectAsState()
    val selectedPyFile by viewModel.selectedPyFile.collectAsState()
    val cloneSuccessMessage by viewModel.cloneSuccessMessage.collectAsState()
    val isRunningGithub by viewModel.isRunningGithub.collectAsState()

    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .testTag("github_screen")
    ) {
        // AppBar
        VipTopAppBar(
            userSession = userSession,
            onAvatarClick = onOpenDrawer,
            onLogoutClick = { viewModel.logout() }
        )

        // Offline Banner
        OfflineBanner(isOnline = isOnline)

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Show Timer and Status Card if bot is currently running
            if (isBotRunning) {
                SessionTimerCard(
                    formattedTimer = viewModel.formatCountdown(remainingSeconds),
                    onResetClick = {
                        if (activity != null && viewModel.adMobManager.isAdReady()) {
                            viewModel.adMobManager.showRewardedAd(
                                activity = activity,
                                onRewardEarned = { _, _ -> viewModel.onRewardEarned() },
                                onAdClosed = {},
                                onAdUnavailableFallback = { viewModel.setShowAdRewardDialog(true) }
                            )
                        } else {
                            viewModel.setShowAdRewardDialog(true)
                        }
                    },
                    modifier = Modifier.padding(top = 12.dp)
                )

                BotStatusCard(
                    onStopClick = { viewModel.stopBot() },
                    isStopping = isStopping,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Clone Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("github_clone_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Clone a GitHub Repository",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Field (height 52dp, bg #0a0015, border 1dp #4c1d95, radius 8dp)
                    BasicTextField(
                        value = repoUrl,
                        onValueChange = { viewModel.setRepoUrl(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VoidBlack)
                            .border(1.dp, RoyalPurple, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp)
                            .testTag("github_repo_input"),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(HighlightPurple),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        decorationBox = { innerTextField ->
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = "Link",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (repoUrl.isEmpty()) {
                                        Text(
                                            text = "GitHub Repository URL (e.g. https://github.com/...)",
                                            fontSize = 14.sp,
                                            color = TextMuted
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Clone Button (full width, #7c3aed bg, radius 8dp, height 48dp)
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.cloneGithubRepo()
                        },
                        enabled = isOnline && !isCloning && repoUrl.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentPurple,
                            contentColor = Color.White,
                            disabledContainerColor = DarkPurple,
                            disabledContentColor = RoyalPurple
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("clone_repo_button")
                    ) {
                        if (isCloning) {
                            CircularProgressIndicator(
                                color = GoldAccent,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = "Clone",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Clone",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Post-Clone State
                    if (!cloneSuccessMessage.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Success banner (#00aa44 bg, white text)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessGreen)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("clone_success_banner")
                        ) {
                            Text(
                                text = cloneSuccessMessage!!,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    if (clonedPyFiles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Select Python file to run:",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // File Selector Dropdown
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(VoidBlack)
                                .border(1.dp, RoyalPurple, RoundedCornerShape(8.dp))
                                .clickable { dropdownExpanded = !dropdownExpanded }
                                .padding(horizontal = 14.dp)
                                .testTag("select_py_file_dropdown")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedPyFile ?: "Choose file",
                                    fontSize = 14.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Open dropdown",
                                    tint = HighlightPurple
                                )
                            }

                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier
                                    .background(DarkPurple)
                                    .border(1.dp, RoyalPurple)
                            ) {
                                clonedPyFiles.forEach { file ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = file,
                                                color = if (file == selectedPyFile) HighlightPurple else TextPrimary,
                                                fontWeight = if (file == selectedPyFile) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            viewModel.selectPyFile(file)
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Run Selected File Button (full width, #00aa44 bg, radius 8dp, height 48dp)
                        Button(
                            onClick = { viewModel.runSelectedGithubFile() },
                            enabled = isOnline && !isRunningGithub && selectedPyFile != null,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SuccessGreen,
                                contentColor = Color.White,
                                disabledContainerColor = DarkPurple,
                                disabledContentColor = RoyalPurple
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_selected_file_button")
                        ) {
                            if (isRunningGithub) {
                                CircularProgressIndicator(
                                    color = GoldAccent,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Run",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Run Selected File",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Shared Console Log Panel
            ConsolePanel(
                logs = consoleLogs,
                onRefresh = { viewModel.fetchLogs() },
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        }
    }
}
