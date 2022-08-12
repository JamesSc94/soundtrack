package com.jamessc94.soundtrack.network

import com.jamessc94.soundtrack.model.ChartNResponse
import com.jamessc94.soundtrack.model.SArtistDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NapsterService {

    @GET("tracks/top")
    suspend fun fetchTracksTopList(@Query("apikey") apikey: String,
                                   @Query("offset") offset: Int,
                                   @Query("limit") limit: Int) : Response<ChartNResponse>

}