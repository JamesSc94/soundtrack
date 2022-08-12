package com.jamessc94.soundtrack.repo

import com.jamessc94.soundtrack.model.*
import com.jamessc94.soundtrack.network.Network
import retrofit2.Response
import javax.inject.Inject


class SChartRepo @Inject constructor() {

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }

    suspend fun fetchChartListPageList(
        method: String,
        page: Int,
        limit: Int,
    ) : Response<SChartListResponse> {
        Network.retroSoundtrack.fetchTrendingList(method, Network.token, Network.type, page, limit).apply {
            return this

        }

    }

    suspend fun fetchArtistChartListPageList(
        method: String,
        page: Int,
        limit: Int,
    ) : Response<SArtistListResponse> {
        Network.retroSoundtrack.fetchArtistList(method, Network.token, Network.type, page, limit).apply {
            return this

        }

    }

    suspend fun fetchDiscoverArtistList(
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
        method: String,
        page: Int,
        limit: Int,
    ) : Response<SArtistListResponse> {

        onStart()

        Network.retroSoundtrack.fetchArtistList(method, Network.token, Network.type, page, limit).apply {
            if(this.isSuccessful){
                onComplete()

            }else{
                onError(this.errorBody()!!.string())

            }

            return this

        }

    }

}