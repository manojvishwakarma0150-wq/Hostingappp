package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class UserSession(
    val userId: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class DeployResponse(
    @Json(name = "status") val status: String,
    @Json(name = "bot_id") val bot_id: String? = null,
    @Json(name = "expires_at") val expires_at: Long? = null
)

@JsonClass(generateAdapter = true)
data class StopRequest(
    @Json(name = "user_id") val user_id: String
)

@JsonClass(generateAdapter = true)
data class StopResponse(
    @Json(name = "status") val status: String
)

@JsonClass(generateAdapter = true)
data class BotStatusResponse(
    @Json(name = "running") val running: Boolean,
    @Json(name = "remaining_seconds") val remaining_seconds: Long
)

@JsonClass(generateAdapter = true)
data class LogsResponse(
    @Json(name = "logs") val logs: List<String>
)

@JsonClass(generateAdapter = true)
data class ResetTimerRequest(
    @Json(name = "user_id") val user_id: String
)

@JsonClass(generateAdapter = true)
data class ResetTimerResponse(
    @Json(name = "expires_at") val expires_at: Long
)

@JsonClass(generateAdapter = true)
data class RunGithubRequest(
    @Json(name = "repo_url") val repo_url: String,
    @Json(name = "selected_file") val selected_file: String,
    @Json(name = "user_id") val user_id: String
)

@JsonClass(generateAdapter = true)
data class RunGithubResponse(
    @Json(name = "status") val status: String,
    @Json(name = "bot_id") val bot_id: String? = null
)
