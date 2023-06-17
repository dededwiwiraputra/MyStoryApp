package com.mawumbo.mystoryapp.data.network

import com.mawumbo.mystoryapp.data.resource.Resource
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ResourceCallAdapter(private val type: Type) : CallAdapter<Type, Call<Resource<Type>>> {
    override fun responseType(): Type = type

    override fun adapt(call: Call<Type>): Call<Resource<Type>> {
        return NetworkCallResource(call)
    }
}