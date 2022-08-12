package com.jamessc94.soundtrack.ui.chartList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ChartListVM @Inject constructor(
    application : Application
) : AndroidViewModel(application) {

    val tlList = listOf("TOP Track", "TOP Artists")

}