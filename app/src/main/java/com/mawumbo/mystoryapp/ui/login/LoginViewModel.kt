package com.mawumbo.mystoryapp.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mawumbo.mystoryapp.data.network.ApiService
import com.mawumbo.mystoryapp.data.preferences.LoginSession
import com.mawumbo.mystoryapp.model.LoginBody
import com.mawumbo.mystoryapp.model.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: ApiService,
    private val loginSession: LoginSession
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val body = LoginBody(email, password)

            val response = try {
                api.login(body)
            } catch (exception: HttpException) {
                Log.d("Login", "login: $exception")
                LoginResponse(true, exception.message(), null)
            }

            if (!response.error) {
                response.user?.token?.let {
                    loginSession.updateLoginSession(it)
                    navigateToHome()
                }
            } else {
                showError(response.message)
            }

        }
    }

    fun navigateToHome() {
        _uiState.update {
            it.copy(navigateToHome = true)
        }
    }

    fun navigatedToHome() {
        _uiState.update {
            it.copy(navigateToHome = null)
        }
    }

    fun showError(msg: String) {
        _uiState.update {
            it.copy(error = "Login Failed, $msg")
        }
    }

    fun errorShown() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}

data class LoginUiState(
    val navigateToHome: Boolean? = null,
    val error: String? = null
)