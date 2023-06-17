package com.mawumbo.mystoryapp.ui.addstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mawumbo.mystoryapp.data.network.ApiService
import com.mawumbo.mystoryapp.data.preferences.LoginSession
import com.mawumbo.mystoryapp.model.FileUploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val api: ApiService,
    private val session: LoginSession
) : ViewModel() {

    private val _uiState = MutableStateFlow<UploadUiState>(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState


    fun uploadImage(photo: File, description: String) {
        val desc = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = photo.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            requestImageFile
        )

        viewModelScope.launch {
            val token = "Bearer ${
                session.loginSessionFlow.first {
                    it.isNotEmpty()
                }
            }"
            val response = try {
                api.uploadImage(imageMultipart, desc, token)
            } catch (e: HttpException) {
                FileUploadResponse(true, e.message())
            }

            if (!response.error) {
                toHome()
            } else {
                showError(response.message)
            }
        }
    }

    fun toHome() {
        _uiState.update {
            it.copy(navigateToHome = true)
        }
    }

    fun allreadyHome() {
        _uiState.update {
            it.copy(navigateToHome = null)
        }
    }

    fun showError(msg: String) {
        _uiState.update {
            it.copy(error = "Upload Failed, $msg")
        }
    }

    fun errorShown() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}

data class UploadUiState(
    val navigateToHome: Boolean? = null,
    val error: String? = null
)