package com.jamessc94.soundtrack.ui.mvideo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.jamessc94.soundtrack.repo.AudioDBRepo
import com.jamessc94.soundtrack.util.getYoutubeID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class MvideoVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    application : Application
) : AndroidViewModel(application) {

    var isLoading = MutableLiveData<Boolean>(false)
    var toastMessage = MutableLiveData<String>("")
    val youtubeID = savedStateHandle.get<String>("ytlink")!!.getYoutubeID()
//    val tempID = "http://www.youtube.com/watch?v=vPp5Ty9EE6k".getYoutubeID()
}