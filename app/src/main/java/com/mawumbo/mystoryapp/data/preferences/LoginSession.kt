package com.mawumbo.mystoryapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_session")

@Singleton
class LoginSession @Inject constructor(@ApplicationContext private val context: Context) {

    val loginSessionFlow = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }.map { preference ->
            preference[PreferencesKeys.SESSION_TOKEN] ?: ""
        }

    suspend fun updateLoginSession(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SESSION_TOKEN] = token
        }
    }

    private object PreferencesKeys {
        val SESSION_TOKEN = stringPreferencesKey("session_token")
    }
}