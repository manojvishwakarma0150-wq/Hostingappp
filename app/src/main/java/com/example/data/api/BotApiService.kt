package com.example.data.api

import com.example.data.model.BotStatusResponse
import com.example.data.model.DeployResponse
import com.example.data.model.LogsResponse
import com.example.data.model.ResetTimerRequest
import com.example.data.model.ResetTimerResponse
import com.example.data.model.RunGithubRequest
import com.example.data.model.RunGithubResponse
import com.example.data.model.StopRequest
import com.example.data.model.StopResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface BotApiService {

    @Multipart
    @POST("bot/deploy")
    suspend fun deployBot(
        @Part bot_file: MultipartBody.Part,
        @Part requirements_file: MultipartBody.Part,
        @Part("user_id") user_id: RequestBody
    ): Response<DeployResponse>

    @POST("bot/stop")
    suspend fun stopBot(
        @Body request: StopRequest
    ): Response<StopResponse>

    @GET("bot/status")
    suspend fun getBotStatus(
        @Query("user_id") userId: String
    ): Response<BotStatusResponse>

    @GET("bot/logs")
    suspend fun getBotLogs(
        @Query("user_id") userId: String
    ): Response<LogsResponse>

    @POST("bot/reset-timer")
    suspend fun resetTimer(
        @Body request: ResetTimerRequest
    ): Response<ResetTimerResponse>

    @POST("bot/run-github")
    suspend fun runGithub(
        @Body request: RunGithubRequest
    ): Response<RunGithubResponse>
}
