package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "vip_dark_host_session")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_DISPLAY_NAME = stringPreferencesKey("display_name")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_PHOTO_URL = stringPreferencesKey("photo_url")
        private val KEY_ID_TOKEN = stringPreferencesKey("id_token")
    }

    val sessionFlow: Flow<UserSession?> = context.dataStore.data.map { preferences ->
        val userId = preferences[KEY_USER_ID]
        val displayName = preferences[KEY_DISPLAY_NAME]
        val email = preferences[KEY_EMAIL]
        val photoUrl = preferences[KEY_PHOTO_URL]

        if (!userId.isNullOrEmpty() && !email.isNullOrEmpty()) {
            UserSession(
                userId = userId,
                displayName = displayName ?: "VIP Developer",
                email = email,
                photoUrl = photoUrl
            )
        } else {
            null
        }
    }

    val idTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_ID_TOKEN]
    }

    suspend fun saveSession(session: UserSession, idToken: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = session.userId
            preferences[KEY_DISPLAY_NAME] = session.displayName
            preferences[KEY_EMAIL] = session.email
            if (session.photoUrl != null) {
                preferences[KEY_PHOTO_URL] = session.photoUrl
            } else {
                preferences.remove(KEY_PHOTO_URL)
            }
            if (idToken != null) {
                preferences[KEY_ID_TOKEN] = idToken
            }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_DISPLAY_NAME)
            preferences.remove(KEY_EMAIL)
            preferences.remove(KEY_PHOTO_URL)
            preferences.remove(KEY_ID_TOKEN)
        }
    }
}
