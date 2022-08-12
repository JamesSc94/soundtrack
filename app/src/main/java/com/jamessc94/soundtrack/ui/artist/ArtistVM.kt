package com.jamessc94.soundtrack.ui.artist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.jamessc94.soundtrack.repo.AudioDBRepo
import com.jamessc94.soundtrack.repo.SChartRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ArtistVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val adbRepo: AudioDBRepo,
    application : Application
) : AndroidViewModel(application) {

    var isLoading = MutableLiveData(false)
    var toastMessage = MutableLiveData("")
    val aname = savedStateHandle.get<String>("name")!!
    var aid = ""
    val adapter = ArtistAdap()

    val stateType: MutableStateFlow<String> = MutableStateFlow(aname)
    val artistDetailsFlow = stateType.flatMapLatest { name ->
        adbRepo.fetchArtistDetails(
            name = name,
            onStart = { isLoading.postValue(true) },
            onComplete = { isLoading.postValue(false)  },
            onError = {
                toastMessage.postValue(it)
                isLoading.postValue(false)

            }
        )

    }.catch { c ->
        c.printStackTrace()

    }.asLiveData()

}