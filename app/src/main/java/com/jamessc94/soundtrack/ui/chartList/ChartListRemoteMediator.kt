package com.jamessc94.soundtrack.ui.chartList

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.jamessc94.soundtrack.model.ChartRemoteKeys
import com.jamessc94.soundtrack.model.SChart
import com.jamessc94.soundtrack.model.asDatabaseModel
import com.jamessc94.soundtrack.persistence.AppDatabase
import com.jamessc94.soundtrack.persistence.SChartDAO
import com.jamessc94.soundtrack.persistence.SChartRemoteKeysDAO
import com.jamessc94.soundtrack.repo.SChartRepo
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val CHART_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
@Singleton
class ChartListRemoteMediator @Inject constructor(
    private val dao: SChartDAO,
    private val daoCK: SChartRemoteKeysDAO,
    private val db: AppDatabase,
    private val repo: SChartRepo,
) : RemoteMediator<Int, SChart>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH

    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, SChart>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: CHART_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val apiResponse = repo.fetchChartListPageList(
                method = "chart.gettoptracks",
                page = page,
                limit = state.config.pageSize,
            )

            val models = apiResponse.body()!!.tracks.track
            val endOfPaginationReached = models.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    daoCK.clearChartRemoteKeys()

                }
                val prevKey = if (page == CHART_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = models.map {
                    ChartRemoteKeys(name = it.name, prevKey = prevKey, nextKey = nextKey)
                }

                daoCK.insertAll(keys)
                dao.insertSChartList(*models.asDatabaseModel())
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, SChart>): ChartRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { chart ->
                // Get the remote keys of the last item retrieved
                daoCK.getChartRemoteKeys(chart.name)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, SChart>): ChartRemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { chart ->
                // Get the remote keys of the first items retrieved
                daoCK.getChartRemoteKeys(chart.name)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, SChart>
    ): ChartRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.name?.let { name ->
                daoCK.getChartRemoteKeys(name)

            }
        }
    }

}