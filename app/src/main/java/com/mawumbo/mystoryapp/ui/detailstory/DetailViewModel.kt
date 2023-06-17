package com.mawumbo.mystoryapp.ui.detailstory

import android.util.Log
import androidx.lifecycle.*
import com.mawumbo.mystoryapp.data.network.ApiService
import com.mawumbo.mystoryapp.data.preferences.LoginSession
import com.mawumbo.mystoryapp.data.resource.Resource
import com.mawumbo.mystoryapp.model.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apiService: ApiService,
    private val session: LoginSession

) : ViewModel() {
    private val storyId: String? = savedStateHandle["storyId"]

    private val _uiState = MutableLiveData<DetailStoryUiState>()
    val uiState: LiveData<DetailStoryUiState> = _uiState

    init {
        if (storyId != null) {
            getStory(storyId)
        }
    }

    fun getStory(storyId: String) {
        viewModelScope.launch {
            _uiState.postValue(
                DetailStoryUiState.Loading
            )
            val token = "Bearer ${
                session.loginSessionFlow.first {
                    it.isNotEmpty()
                }
            }"
            Log.d("DetailViewModel", "Success Jalan")
            try {
                val resource = apiService.getStoryDetail(storyId, token)
                when (resource) {
                    is Resource.Success -> {
                        Log.d("DetailViewModel", "Success")
                        _uiState.postValue(DetailStoryUiState.Success(resource.data.story))
                    }
                    is Resource.Failure -> {
                        Log.d("DetailViewModel", "Failed")
                        val errorMessage = resource.exception?.message ?: "Error occurred"
                        _uiState.postValue(DetailStoryUiState.Error(errorMessage))
                    }
                }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Exception occurred: ${e.message}", e)
                _uiState.postValue(DetailStoryUiState.Error("An error occurred"))
            }
        }

    }
}

sealed interface DetailStoryUiState {
    object Loading : DetailStoryUiState
    data class Success(val story: Story) : DetailStoryUiState
    data class Error(val message: String) : DetailStoryUiState
}