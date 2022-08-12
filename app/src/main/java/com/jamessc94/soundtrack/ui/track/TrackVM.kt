package com.jamessc94.soundtrack.ui.track

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.jamessc94.soundtrack.repo.AudioDBRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TrackVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val adbRepo: AudioDBRepo,
    application : Application
) : AndroidViewModel(application) {

    var isLoading = MutableLiveData<Boolean>(false)
    var toastMessage = MutableLiveData<String>("")

    val stateType: MutableStateFlow<String> = MutableStateFlow(savedStateHandle.get<String>("aid")!!)
    val trackFlow = stateType.flatMapLatest { aid ->
        adbRepo.fetchTrack(
            aid = aid,
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