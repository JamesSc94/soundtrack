package com.jamessc94.soundtrack.network

import com.jamessc94.soundtrack.model.SArtistListResponse
import com.jamessc94.soundtrack.model.SChartListResponse
import retrofit2.Response
import retrofit2.http.*

interface SoundtrackService {

    @FormUrlEncoded
    @POST("2.0/")
    suspend fun fetchTrendingList(@Field("method") method : String,
                                  @Field("api_key") token : String,
                                  @Field("format") format: String,
                                  @Field("page") page : Int,
                                  @Field("limit") limit : Int): Response<SChartListResponse>

    @FormUrlEncoded
    @POST("2.0/")
    suspend fun fetchArtistList(@Field("method") method : String,
                                @Field("api_key") token : String,
                                @Field("format") format: String,
                                @Field("page") page : Int,
                                @Field("limit") limit : Int): Response<SArtistListResponse>



}