package com.jamessc94.soundtrack.ui.chartList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.jamessc94.soundtrack.model.SArtist
import com.jamessc94.soundtrack.model.SChart
import com.jamessc94.soundtrack.persistence.SArtistDAO
import com.jamessc94.soundtrack.persistence.SChartDAO
import com.jamessc94.soundtrack.repo.SChartRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChartListFragVM @Inject constructor(
    private val repo: SChartRepo,
    private val daoC: SChartDAO,
    private val daoA: SArtistDAO,
    private val cRM: ChartListRemoteMediator,
    private val aRM: ChartListArtistRemoteMediator,
) : ViewModel() {

    var isLoading = MutableLiveData<Boolean>(false)

    fun fetchChart(): Flow<PagingData<SChart>> {
        return getChartPagingData().cachedIn(viewModelScope)

    }

    fun fetchArtist(): Flow<PagingData<SArtist>> {
        return getChartArtistPagingData().cachedIn(viewModelScope)

    }

    @OptIn(ExperimentalPagingApi::class)
    fun getChartPagingData(): Flow<PagingData<SChart>> {
        return Pager(
            config = PagingConfig(pageSize = SChartRepo.NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = cRM,
            pagingSourceFactory = { daoC.getSChartAllFlow() }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getChartArtistPagingData(): Flow<PagingData<SArtist>> {
        return Pager(
            config = PagingConfig(pageSize = SChartRepo.NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = aRM,
            pagingSourceFactory = { daoA.getSArtistFlow() }
        ).flow
    }

}