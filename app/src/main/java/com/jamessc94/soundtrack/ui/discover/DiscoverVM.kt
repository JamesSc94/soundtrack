package com.jamessc94.soundtrack.ui.discover

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.model.asDatabaseModel
import com.jamessc94.soundtrack.persistence.SArtistDAO
import com.jamessc94.soundtrack.persistence.SChartDAO
import com.jamessc94.soundtrack.repo.SChartRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverVM @Inject constructor(
    private val repo: SChartRepo,
    daoA: SArtistDAO,
    application : Application
) : AndroidViewModel(application) {

    var isLoading = MutableLiveData<Boolean>(false)
    var toastMessage = MutableLiveData<String>("")

    val artists = daoA.getSArtistAll()

//    init {
//        viewModelScope.launch {
//            try{
//                val response = repo.fetchDiscoverArtistList(
//                    onStart = { isLoading.value = true },
//                    onComplete = { isLoading.value = false },
//                    onError = { toastMessage.value = it
//                        isLoading.value = false},
//                    "chart.gettopartists",
//                    1,
//                    20
//
//                )
//
//                if(response.isSuccessful){
//                    daoA.insertSArtistList(*response.body()!!.artists.artist.asDatabaseModel())
//
//                }
//
//            }catch (e : Exception){
//                isLoading.value = false
//                e.printStackTrace()
//
//            }
//
//        }
//
//    }

}