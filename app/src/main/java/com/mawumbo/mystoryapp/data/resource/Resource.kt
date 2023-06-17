package com.mawumbo.mystoryapp.data.resource

sealed class Resource<out T>() {

    data class Success<T>(val data: T) : Resource<T>()

    data class Failure(
        val exception: Throwable?,
        val message: String = exception?.message ?: "Unexpected error occurred"
    ) : Resource<Nothing>()

}