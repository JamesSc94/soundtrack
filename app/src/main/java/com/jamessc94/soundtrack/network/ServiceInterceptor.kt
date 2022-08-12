package com.jamessc94.soundtrack.network

import okhttp3.Interceptor
import okhttp3.Response

class ServiceInterceptor : Interceptor {

//    companion object {
//        init {
//            System.loadLibrary("keys");
//        }
//    }
//
//    private external fun getAPIKey(): String
//
//    val token : String by lazy {
//        getAPIKey()
//
//    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if(request.header("No-Authentication")==null){
            //val token = getTokenFromSharedPreference();
            //or use Token Function
//            if(!token.isNullOrEmpty())
//            {
//                val finalToken =  "METToken $token"
//                request = request.newBuilder()
//                    .addHeader("Authorization", finalToken)
//                    .build()
//            }

            request = request.newBuilder()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build()

        }

        return chain.proceed(request)

    }
}