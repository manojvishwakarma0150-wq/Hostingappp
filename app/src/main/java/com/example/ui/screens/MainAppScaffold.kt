package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.ui.components.AdRewardDialog
import com.example.ui.components.ProfileDrawerContent
import com.example.ui.components.VipBottomNavBar
import com.example.ui.components.VipNavTab
import com.example.ui.theme.DarkPurple
import com.example.ui.theme.VoidBlack
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class ScreenRoute {
    object Home : ScreenRoute()
    object Resources : ScreenRoute()
    object GitHub : ScreenRoute()
    object Terms : ScreenRoute()
    object Privacy : ScreenRoute()
}

@Composable
fun MainAppScaffold(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userSession by viewModel.userSession.collectAsState()
    val showAdRewardDialog by viewModel.showAdRewardDialog.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    var currentRoute by remember { mutableStateOf<ScreenRoute>(ScreenRoute.Home) }

    // Listen for snackbar events
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (userSession == null) {
        // SCREEN 1: Splash / Login
        LoginScreen(viewModel = viewModel)
    } else {
        // Authenticated App Shell with Navigation Drawer
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = DarkPurple
                ) {
                    ProfileDrawerContent(
                        userSession = userSession,
                        onNavigateHome = {
                            currentRoute = ScreenRoute.Home
                            coroutineScope.launch { drawerState.close() }
                        },
                        onNavigateResources = {
                            currentRoute = ScreenRoute.Resources
                            coroutineScope.launch { drawerState.close() }
                        },
                        onNavigateGitHub = {
                            currentRoute = ScreenRoute.GitHub
                            coroutineScope.launch { drawerState.close() }
                        },
                        onNavigateTerms = {
                            currentRoute = ScreenRoute.Terms
                            coroutineScope.launch { drawerState.close() }
                        },
                        onNavigatePrivacy = {
                            currentRoute = ScreenRoute.Privacy
                            coroutineScope.launch { drawerState.close() }
                        },
                        onLogout = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.logout()
                        }
                    )
                }
            }
        ) {
            val showBottomBar = currentRoute is ScreenRoute.Home ||
                    currentRoute is ScreenRoute.Resources ||
                    currentRoute is ScreenRoute.GitHub

            Scaffold(
                modifier = modifier
                    .fillMaxSize()
                    .background(VoidBlack)
                    .testTag("main_app_scaffold"),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (showBottomBar) {
                        val activeTab = when (currentRoute) {
                            ScreenRoute.Home -> VipNavTab.HOME
                            ScreenRoute.Resources -> VipNavTab.RESOURCES
                            ScreenRoute.GitHub -> VipNavTab.GITHUB
                            else -> VipNavTab.HOME
                        }
                        VipBottomNavBar(
                            selectedTab = activeTab,
                            onTabSelected = { tab ->
                                currentRoute = when (tab) {
                                    VipNavTab.HOME -> ScreenRoute.Home
                                    VipNavTab.RESOURCES -> ScreenRoute.Resources
                                    VipNavTab.GITHUB -> ScreenRoute.GitHub
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentRoute) {
                        ScreenRoute.Home -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                            )
                        }

                        ScreenRoute.Resources -> {
                            BackHandler { currentRoute = ScreenRoute.Home }
                            ResourcesScreen(
                                viewModel = viewModel,
                                onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                            )
                        }

                        ScreenRoute.GitHub -> {
                            BackHandler { currentRoute = ScreenRoute.Home }
                            GitHubScreen(
                                viewModel = viewModel,
                                onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                            )
                        }

                        ScreenRoute.Terms -> {
                            BackHandler { currentRoute = ScreenRoute.Home }
                            WebViewScreen(
                                title = "Terms of Use",
                                contentHtml = LegalContent.TERMS_OF_USE_HTML,
                                onBack = { currentRoute = ScreenRoute.Home }
                            )
                        }

                        ScreenRoute.Privacy -> {
                            BackHandler { currentRoute = ScreenRoute.Home }
                            WebViewScreen(
                                title = "Privacy Policy",
                                contentHtml = LegalContent.PRIVACY_POLICY_HTML,
                                onBack = { currentRoute = ScreenRoute.Home }
                            )
                        }
                    }
                }
            }
        }
    }

    // Ad Reward Dialog / Simulation Fallback
    if (showAdRewardDialog) {
        AdRewardDialog(
            onDismiss = { viewModel.setShowAdRewardDialog(false) },
            onRewardEarned = {
                viewModel.onRewardEarned()
                Toast.makeText(context, "+24 Hours Added!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
