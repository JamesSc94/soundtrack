package com.jamessc94.soundtrack.network

import com.jamessc94.soundtrack.model.AlbumResponse
import com.jamessc94.soundtrack.model.MvideoResponse
import com.jamessc94.soundtrack.model.SArtistDetailsResponse
import com.jamessc94.soundtrack.model.TrackResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AudioDBService {

    @GET("search.php")
    suspend fun fetchArtistDetails(@Query("s") s: String) : Response<SArtistDetailsResponse>

    @GET("album.php")
    suspend fun fetchAlbum(@Query("i") i: String) : Response<AlbumResponse>

    @GET("track.php")
    suspend fun fetchTrack(@Query("h") h: String) : Response<TrackResponse>

    @GET("mvid.php")
    suspend fun fetchMV(@Query("i") i: String) : Response<MvideoResponse>

}