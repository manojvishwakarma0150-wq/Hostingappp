package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdMobManager
import com.example.data.api.ApiClient
import com.example.data.local.SessionManager
import com.example.data.model.UserSession
import com.example.data.repository.AuthRepository
import com.example.data.repository.BotRepository
import com.example.util.NetworkMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val sessionManager = SessionManager(application)
    val authRepository = AuthRepository(application, sessionManager)
    val botRepository = BotRepository(ApiClient.apiService)
    val adMobManager = AdMobManager(application)
    private val networkMonitor = NetworkMonitor(application)

    // User session
    val userSession: StateFlow<UserSession?> = sessionManager.sessionFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Network connectivity
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.Eagerly, networkMonitor.checkCurrentNetwork())

    // Bot execution status
    private val _isBotRunning = MutableStateFlow(false)
    val isBotRunning: StateFlow<Boolean> = _isBotRunning.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0L)
    val remainingSeconds: StateFlow<Long> = _remainingSeconds.asStateFlow()

    // File uploads
    private val _botFileName = MutableStateFlow<String?>(null)
    val botFileName: StateFlow<String?> = _botFileName.asStateFlow()
    private var botFileBytes: ByteArray? = null

    private val _reqFileName = MutableStateFlow<String?>(null)
    val reqFileName: StateFlow<String?> = _reqFileName.asStateFlow()
    private var reqFileBytes: ByteArray? = null

    // UI Loading indicators
    private val _isDeploying = MutableStateFlow(false)
    val isDeploying: StateFlow<Boolean> = _isDeploying.asStateFlow()

    private val _isStopping = MutableStateFlow(false)
    val isStopping: StateFlow<Boolean> = _isStopping.asStateFlow()

    // Console logs
    private val _consoleLogs = MutableStateFlow<List<String>>(emptyList())
    val consoleLogs: StateFlow<List<String>> = _consoleLogs.asStateFlow()

    // GitHub flow
    private val _repoUrl = MutableStateFlow("")
    val repoUrl: StateFlow<String> = _repoUrl.asStateFlow()

    private val _isCloning = MutableStateFlow(false)
    val isCloning: StateFlow<Boolean> = _isCloning.asStateFlow()

    private val _clonedPyFiles = MutableStateFlow<List<String>>(emptyList())
    val clonedPyFiles: StateFlow<List<String>> = _clonedPyFiles.asStateFlow()

    private val _selectedPyFile = MutableStateFlow<String?>(null)
    val selectedPyFile: StateFlow<String?> = _selectedPyFile.asStateFlow()

    private val _cloneSuccessMessage = MutableStateFlow<String?>(null)
    val cloneSuccessMessage: StateFlow<String?> = _cloneSuccessMessage.asStateFlow()

    private val _isRunningGithub = MutableStateFlow(false)
    val isRunningGithub: StateFlow<Boolean> = _isRunningGithub.asStateFlow()

    // Fallback Ad dialog when AdMob can't show in emulator
    private val _showAdRewardDialog = MutableStateFlow(false)
    val showAdRewardDialog: StateFlow<Boolean> = _showAdRewardDialog.asStateFlow()

    // Toast/Snackbar events
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    // Background jobs
    private var timerJob: Job? = null
    private var pollingJob: Job? = null

    init {
        // Wire token provider for ApiClient
        ApiClient.setTokenProvider {
            authRepository.getIdToken(false)
        }

        // Initialize AdMob
        adMobManager.initialize()

        // Sync initial bot status if user already logged in
        viewModelScope.launch {
            userSession.collect { session ->
                if (session != null) {
                    refreshBotStatus()
                }
            }
        }
    }

    fun onResume() {
        refreshBotStatus()
        adMobManager.loadRewardedAd()
    }

    fun setBotFile(name: String, bytes: ByteArray) {
        _botFileName.value = name
        botFileBytes = bytes
    }

    fun setRequirementsFile(name: String, bytes: ByteArray) {
        _reqFileName.value = name
        reqFileBytes = bytes
    }

    fun setRepoUrl(url: String) {
        _repoUrl.value = url
    }

    fun selectPyFile(file: String) {
        _selectedPyFile.value = file
    }

    fun clearCloneSuccessMessage() {
        _cloneSuccessMessage.value = null
    }

    fun setShowAdRewardDialog(show: Boolean) {
        _showAdRewardDialog.value = show
    }

    fun refreshBotStatus() {
        val user = userSession.value ?: return
        viewModelScope.launch {
            val result = botRepository.getBotStatus(user.userId)
            result.onSuccess { status ->
                _isBotRunning.value = status.running
                _remainingSeconds.value = status.remaining_seconds
                if (status.running) {
                    startLocalTimer()
                    startLogPolling()
                } else {
                    stopLocalTimer()
                    stopLogPolling()
                }
            }
        }
    }

    private fun startLocalTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isBotRunning.value && _remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value = (_remainingSeconds.value - 1).coerceAtLeast(0)
                if (_remainingSeconds.value == 0L) {
                    _isBotRunning.value = false
                    stopLogPolling()
                }
            }
        }
    }

    private fun stopLocalTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun startLogPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (_isBotRunning.value) {
                fetchLogs()
                delay(5000)
            }
        }
    }

    fun stopLogPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun fetchLogs() {
        val user = userSession.value ?: return
        viewModelScope.launch {
            val result = botRepository.getBotLogs(user.userId)
            result.onSuccess { logsResponse ->
                _consoleLogs.value = logsResponse.logs
            }
        }
    }

    fun deployBot() {
        val user = userSession.value ?: return
        val botName = _botFileName.value ?: "bot.py"
        val reqName = _reqFileName.value ?: "requirements.txt"
        val botBytes = botFileBytes ?: "print('VIP Bot Started')".toByteArray()
        val reqBytes = reqFileBytes ?: "python-telegram-bot".toByteArray()

        val botPart = MultipartBody.Part.createFormData(
            "bot_file",
            botName,
            botBytes.toRequestBody("text/x-python".toMediaTypeOrNull())
        )
        val reqPart = MultipartBody.Part.createFormData(
            "requirements_file",
            reqName,
            reqBytes.toRequestBody("text/plain".toMediaTypeOrNull())
        )

        viewModelScope.launch {
            _isDeploying.value = true
            val result = botRepository.deployBot(botPart, reqPart, user.userId)
            _isDeploying.value = false

            result.onSuccess {
                _isBotRunning.value = true
                _remainingSeconds.value = 24 * 3600L
                startLocalTimer()
                startLogPolling()
                fetchLogs()
                _snackbarEvent.emit("Bot deployed successfully!")
            }.onFailure { e ->
                _snackbarEvent.emit(e.message ?: "Failed to deploy bot")
            }
        }
    }

    fun stopBot() {
        val user = userSession.value ?: return
        viewModelScope.launch {
            _isStopping.value = true
            val result = botRepository.stopBot(user.userId)
            _isStopping.value = false

            result.onSuccess {
                _isBotRunning.value = false
                stopLocalTimer()
                stopLogPolling()
                fetchLogs()
                _snackbarEvent.emit("Bot stopped successfully")
            }.onFailure { e ->
                _snackbarEvent.emit(e.message ?: "Failed to stop bot")
            }
        }
    }

    fun onRewardEarned() {
        val user = userSession.value ?: return
        viewModelScope.launch {
            val result = botRepository.resetTimer(user.userId)
            result.onSuccess {
                _remainingSeconds.value = 24 * 3600L
                _snackbarEvent.emit("Session extended! 24 hours added.")
                startLocalTimer()
                fetchLogs()
            }.onFailure { e ->
                _snackbarEvent.emit(e.message ?: "Failed to reset timer")
            }
        }
    }

    fun cloneGithubRepo() {
        val url = _repoUrl.value.trim()
        if (url.isEmpty()) {
            viewModelScope.launch { _snackbarEvent.emit("Please enter a GitHub repository URL") }
            return
        }

        viewModelScope.launch {
            _isCloning.value = true
            delay(1200) // Realistic cloning delay
            val files = botRepository.mockClonedRepoFiles(url)
            _clonedPyFiles.value = files
            _selectedPyFile.value = files.firstOrNull()
            _isCloning.value = false
            _cloneSuccessMessage.value = "Repository cloned successfully. Found ${files.size} Python files."
        }
    }

    fun runSelectedGithubFile() {
        val user = userSession.value ?: return
        val url = _repoUrl.value.trim()
        val file = _selectedPyFile.value ?: return

        viewModelScope.launch {
            _isRunningGithub.value = true
            val result = botRepository.runGithub(url, file, user.userId)
            _isRunningGithub.value = false

            result.onSuccess {
                _isBotRunning.value = true
                _remainingSeconds.value = 24 * 3600L
                startLocalTimer()
                startLogPolling()
                fetchLogs()
                _snackbarEvent.emit("Running $file")
            }.onFailure { e ->
                _snackbarEvent.emit(e.message ?: "Failed to run $file")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            stopLocalTimer()
            stopLogPolling()
            authRepository.logout()
            _botFileName.value = null
            _reqFileName.value = null
            _isBotRunning.value = false
            _consoleLogs.value = emptyList()
        }
    }

    fun formatCountdown(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
    }
}
