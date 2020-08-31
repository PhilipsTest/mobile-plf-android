package com.philips.cdp.registration.ads.helper

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class RestHandler {
    /**
     * Class to create Retrofit service
     */
    fun getServiceCountry(baseUrl: String,context: Context)= Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(GetServiceCountry::class.java)



    fun create(): GetServiceCountry = Retrofit.Builder()
            .baseUrl("https://lz-houseads.firebaseapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(GetServiceCountry::class.java)


}