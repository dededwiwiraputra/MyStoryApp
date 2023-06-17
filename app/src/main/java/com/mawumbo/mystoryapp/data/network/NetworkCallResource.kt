package com.mawumbo.mystoryapp.data.network

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mawumbo.mystoryapp.data.resource.Resource
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkCallResource<T>(private val call: Call<T>) : Call<Resource<T>> {

    override fun enqueue(callback: Callback<Resource<T>>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val networkResult = safeParseResponse(response)
                callback.onResponse(this@NetworkCallResource, Response.success(networkResult))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val networkResult = Resource.Failure(t)
                callback.onResponse(this@NetworkCallResource, Response.success(networkResult))
            }
        })
    }

    override fun execute(): Response<Resource<T>> = throw NotImplementedError()
    override fun clone(): Call<Resource<T>> = NetworkCallResource(call.clone())
    override fun request(): Request = call.request()
    override fun timeout(): Timeout = call.timeout()
    override fun isExecuted(): Boolean = call.isExecuted
    override fun isCanceled(): Boolean = call.isCanceled
    override fun cancel() {
        call.cancel()
    }

    private fun <T> safeParseResponse(response: Response<T>): Resource<T> {
        return try {
            val body = response.body() // response body
            if (response.isSuccessful && body != null) {
                Resource.Success(body) // success fetching from network
            } else { // error fetching from network
                val gson = Gson()
                val message = gson.fromJson<JsonObject>(
                    response.errorBody()?.string() ?: "", JsonElement::class.java
                )
                Resource.Failure(
                    null,
                    message = message.get("message").asString // get error message
                )
            }
        } catch (exception: Exception) {
            Resource.Failure(exception)
        }
    }

}