package com.mawumbo.mystoryapp.data.network

import com.mawumbo.mystoryapp.data.resource.Resource
import com.mawumbo.mystoryapp.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @GET("stories")
    suspend fun getStories(@Header("Authorization") token: String): AllStoriesResponse

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Path("id") id: String, @Header("Authorization") token: String
    ): Resource<DetailStoryResponse>

    @POST("login")
    suspend fun login(@Body body: LoginBody): LoginResponse

    @POST("register")
    suspend fun register(@Body body: RegisterBody): RegisterResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ): FileUploadResponse
}