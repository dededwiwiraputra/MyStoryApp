package com.mawumbo.mystoryapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mawumbo.mystoryapp.data.preferences.LoginSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val loginSession: LoginSession
) : ViewModel() {

    val uiState: SharedFlow<MainActivityUiState> = loginSession.loginSessionFlow.map { token ->
        Log.d("Token Cek", ": $token")
        MainActivityUiState(
            navigateToLogin = if (token.isBlank()) true else null
        )
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
    )

}

data class MainActivityUiState(
    val navigateToLogin: Boolean? = null
)