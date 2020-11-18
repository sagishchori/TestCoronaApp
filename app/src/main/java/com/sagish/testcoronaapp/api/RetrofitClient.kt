package com.sagish.testcoronaapp.api

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL : String = "https://api.covid19api.com/"

    // Is used to log the API calls
    private val logger = HttpLoggingInterceptor.Logger { Log.d("API", it) }

    // Is used to intercept the API calls. I set the intercepting level to
    // HttpLoggingInterceptor.Level.BODY in order to get all API call
    // data (calls, responses, headers and bodies)
    private val interceptor = HttpLoggingInterceptor(logger)
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    // Create a HttpClient with pre-defined interceptor to be used in my
    // Retrofit client
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    private val retrofit: Retrofit? = Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(HttpUrl.parse(BASE_URL)!!)
        .build()

    fun<T> buildService(service : Class<T>) : T? {
        return retrofit?.create(service)
    }
}