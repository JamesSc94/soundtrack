package com.jamessc94.soundtrack.ui.chartn

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.jamessc94.soundtrack.model.ChartN
import com.jamessc94.soundtrack.model.SChart
import com.jamessc94.soundtrack.persistence.ChartNDAO
import com.jamessc94.soundtrack.repo.NapsterRepo
import com.jamessc94.soundtrack.repo.SChartRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChartNVM @Inject constructor(
    private val dao: ChartNDAO,
    private val cnRM: ChartNRemoteMediator,
    application : Application
) : AndroidViewModel(application) {

    var isLoading = MutableLiveData<Boolean>(false)
    var toastMessage = MutableLiveData<String>("")
    val temp = dao.getChartNLD()

    fun fetchChart(): Flow<PagingData<ChartN>> {
        return getChartNPagingData().cachedIn(viewModelScope)

    }

    @OptIn(ExperimentalPagingApi::class)
    fun getChartNPagingData(): Flow<PagingData<ChartN>> {
        return Pager(
            config = PagingConfig(pageSize = NapsterRepo.NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = cnRM,
            pagingSourceFactory = { dao.getChartN() }
        ).flow
    }

}