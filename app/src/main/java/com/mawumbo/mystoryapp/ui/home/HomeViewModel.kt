package com.mawumbo.mystoryapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mawumbo.mystoryapp.data.network.ApiService
import com.mawumbo.mystoryapp.data.preferences.LoginSession
import com.mawumbo.mystoryapp.model.AllStoriesResponse
import com.mawumbo.mystoryapp.model.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: ApiService,
    private val session: LoginSession
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _allStory = MutableLiveData<List<Story>>()
    val allStory: LiveData<List<Story>> = _allStory


    fun refreshStories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = "Bearer ${
                    session.loginSessionFlow.first {
                        it.isNotEmpty()
                    }
                }"

                _allStory.postValue(api.getStories(token).asStories())
            } catch (e: HttpException) {
                Log.d("Home", "Error :$e ")
            }

            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            session.updateLoginSession("")
        }
    }

    private fun AllStoriesResponse.asStories() = this.stories

}