package com.jamessc94.soundtrack.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jamessc94.soundtrack.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class DashboardVM @Inject constructor(
    application : Application
) : AndroidViewModel(application) {

    val tlList = ArrayList<String>(listOf(*application.resources.getStringArray(R.array.dashboard)))

}