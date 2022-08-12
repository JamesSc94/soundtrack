package com.jamessc94.soundtrack.ui.chartList

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.jamessc94.soundtrack.model.ArtistRemoteKeys
import com.jamessc94.soundtrack.model.SArtist
import com.jamessc94.soundtrack.model.asDatabaseModel
import com.jamessc94.soundtrack.persistence.AppDatabase
import com.jamessc94.soundtrack.persistence.SArtistDAO
import com.jamessc94.soundtrack.persistence.SArtistRemoteKeysDAO
import com.jamessc94.soundtrack.persistence.SChartDAO
import com.jamessc94.soundtrack.repo.SChartRepo
import kotlinx.coroutines.flow.collect
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val ARTIST_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
@Singleton
class ChartListArtistRemoteMediator @Inject constructor(
    private val dao: SArtistDAO,
    private val daoAK: SArtistRemoteKeysDAO,
    private val db: AppDatabase,
    private val repo: SChartRepo,
) : RemoteMediator<Int, SArtist>() {

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, SArtist>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: ARTIST_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val apiResponse = repo.fetchArtistChartListPageList(
                method = "chart.gettopartists",
                page = page,
                limit = state.config.pageSize,
            )

            val models = apiResponse.body()!!.artists.artist
            val endOfPaginationReached = models.isEmpty()
            db.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    daoAK.clearArtistRemoteKeys()
//                    repoDatabase.reposDao().clearRepos()
                }
                val prevKey = if (page == ARTIST_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = models.map {
                    ArtistRemoteKeys(name = it.name, prevKey = prevKey, nextKey = nextKey)
                }

                daoAK.insertAll(keys)
                dao.insertSArtistList(*models.asDatabaseModel())
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, SArtist>): ArtistRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { artist ->
                // Get the remote keys of the last item retrieved
                daoAK.getArtistRemoteKeys(artist.name)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, SArtist>): ArtistRemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { artist ->
                // Get the remote keys of the first items retrieved
                daoAK.getArtistRemoteKeys(artist.name)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, SArtist>
    ): ArtistRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.name?.let { name ->
                daoAK.getArtistRemoteKeys(name)

            }
        }
    }


}