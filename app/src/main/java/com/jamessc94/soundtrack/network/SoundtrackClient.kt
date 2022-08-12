package com.jamessc94.soundtrack.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {

    init {
        System.loadLibrary("keys");
    }

    var client = OkHttpClient.Builder()
        .addInterceptor(ServiceInterceptor())
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val retrofitRS = Retrofit.Builder()
        .baseUrl("http://ws.audioscrobbler.com/")
        .client(client).addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val retrofitAudioDB = Retrofit.Builder()
        .baseUrl("https://www.theaudiodb.com/api/v1/json/2/")
        .client(client).addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val retrofitNapster = Retrofit.Builder()
        .baseUrl("https://api.napster.com/v2.2/")
        .client(client).addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private external fun getAPIKey(): String

    private external fun getGoogleKey(): String

    private external fun getNapsterKey(): String

    val token : String by lazy {
        getAPIKey()

    }

    val gKey : String by lazy {
        getGoogleKey()

    }

    val npKey : String by lazy {
        getNapsterKey()

    }

    val type = "json"

    val retroSoundtrack : SoundtrackService = retrofitRS.create(SoundtrackService::class.java)
    val audioDB : AudioDBService = retrofitAudioDB.create(AudioDBService::class.java)
    val napster : NapsterService = retrofitNapster.create(NapsterService::class.java)

}