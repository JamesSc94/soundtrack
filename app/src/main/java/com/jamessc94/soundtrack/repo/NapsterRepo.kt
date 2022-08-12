package com.jamessc94.soundtrack.repo

import com.jamessc94.soundtrack.model.ChartNResponse
import com.jamessc94.soundtrack.model.SArtistListResponse
import com.jamessc94.soundtrack.model.SChartListResponse
import com.jamessc94.soundtrack.network.Network
import retrofit2.Response
import javax.inject.Inject

class NapsterRepo @Inject constructor() {

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }

    suspend fun fetchChartNTrackList(
        offset: Int,
        limit: Int,
    ) : Response<ChartNResponse> {
        Network.napster.fetchTracksTopList(Network.npKey, offset, limit).apply {
            return this

        }

    }

}