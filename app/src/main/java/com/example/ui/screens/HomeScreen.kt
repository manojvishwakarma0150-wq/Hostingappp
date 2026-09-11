package com.example.ui.screens

import android.app.Activity
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BotStatusCard
import com.example.ui.components.ConsolePanel
import com.example.ui.components.OfflineBanner
import com.example.ui.components.SessionTimerCard
import com.example.ui.components.UserGreetingCard
import com.example.ui.components.VipTopAppBar
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
fun HomeScreen(
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val userSession by viewModel.userSession.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val isBotRunning by viewModel.isBotRunning.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val botFileName by viewModel.botFileName.collectAsState()
    val reqFileName by viewModel.reqFileName.collectAsState()
    val isDeploying by viewModel.isDeploying.collectAsState()
    val isStopping by viewModel.isStopping.collectAsState()
    val consoleLogs by viewModel.consoleLogs.collectAsState()

    // Activity Result Launchers for file picking
    val botPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val contentResolver = context.contentResolver
            var name = "bot.py"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) {
                    name = cursor.getString(nameIndex)
                }
            }
            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: "print('Telegram Bot running')".toByteArray()
            viewModel.setBotFile(name, bytes)
        }
    }

    val reqPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val contentResolver = context.contentResolver
            var name = "requirements.txt"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) {
                    name = cursor.getString(nameIndex)
                }
            }
            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: "python-telegram-bot\naiohttp".toByteArray()
            viewModel.setRequirementsFile(name, bytes)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .testTag("home_screen")
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
            if (isBotRunning) {
                // STATE B: Session Timer Card
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

                // STATE B: Bot Status Card
                BotStatusCard(
                    onStopClick = { viewModel.stopBot() },
                    isStopping = isStopping,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                // STATE A: User Greeting Section
                UserGreetingCard(
                    userSession = userSession,
                    onAvatarClick = onOpenDrawer,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // STATE A: Upload Files Card
                UploadFilesCard(
                    botFileName = botFileName,
                    reqFileName = reqFileName,
                    isDeploying = isDeploying,
                    isOnline = isOnline,
                    onPickBotFile = {
                        // Attempt system file picker, fallback if on emulator without files
                        try {
                            botPickerLauncher.launch("*/*")
                        } catch (_: Exception) {
                            viewModel.setBotFile("bot.py", "import telegram\nprint('Bot Active')".toByteArray())
                        }
                    },
                    onPickReqFile = {
                        try {
                            reqPickerLauncher.launch("*/*")
                        } catch (_: Exception) {
                            viewModel.setRequirementsFile("requirements.txt", "python-telegram-bot\naiohttp".toByteArray())
                        }
                    },
                    onDeploy = { viewModel.deployBot() }
                )
            }

            // Shared Console Log Panel (always below action cards)
            ConsolePanel(
                logs = consoleLogs,
                onRefresh = { viewModel.fetchLogs() },
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        }
    }
}

/**
 * Upload Files Card (State A)
 */
@Composable
private fun UploadFilesCard(
    botFileName: String?,
    reqFileName: String?,
    isDeploying: Boolean,
    isOnline: Boolean,
    onPickBotFile: () -> Unit,
    onPickReqFile: () -> Unit,
    onDeploy: () -> Unit
) {
    val canDeploy = !botFileName.isNullOrEmpty() && !reqFileName.isNullOrEmpty() && isOnline && !isDeploying

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("upload_files_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row: File icon #a855f7 20dp + "Upload Files" 16sp bold #e9d5ff
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    tint = HighlightPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Upload Files",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button 1: Choose bot.py
            UploadFileSelectorButton(
                icon = Icons.Default.Code,
                defaultLabel = "Choose bot.py",
                selectedName = botFileName,
                onClick = onPickBotFile,
                testTag = "choose_bot_py_button"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Button 2: Choose requirements.txt
            UploadFileSelectorButton(
                icon = Icons.Default.FormatListBulleted,
                defaultLabel = "Choose requirements.txt",
                selectedName = reqFileName,
                onClick = onPickReqFile,
                testTag = "choose_requirements_button"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Row: Deploy Button + Stop Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Deploy Button
                Button(
                    onClick = onDeploy,
                    enabled = canDeploy,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HighlightPurple,
                        contentColor = VoidBlack,
                        disabledContainerColor = DarkPurple,
                        disabledContentColor = RoyalPurple
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .then(
                            if (!canDeploy) {
                                Modifier.border(1.dp, RoyalPurple.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            } else Modifier
                        )
                        .testTag("deploy_bot_button")
                ) {
                    if (isDeploying) {
                        CircularProgressIndicator(
                            color = GoldAccent,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Deploy",
                            fontSize = 15.sp,
                            fontWeight = if (canDeploy) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // Stop Button (Disabled in State A)
                Button(
                    onClick = {},
                    enabled = false,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = DarkPurple,
                        disabledContentColor = RoyalPurple
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(1.dp, RoyalPurple.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = "Stop",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = RoyalPurple
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadFileSelectorButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    defaultLabel: String,
    selectedName: String?,
    onClick: () -> Unit,
    testTag: String
) {
    val isSelected = !selectedName.isNullOrEmpty()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(VoidBlack)
            .border(1.dp, RoyalPurple, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp)
            .testTag(testTag),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HighlightPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isSelected) selectedName!! else defaultLabel,
                    fontSize = 14.sp,
                    color = if (isSelected) HighlightPurple else TextPrimary,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = SuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
