package com.jamessc94.soundtrack.ui.stream

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StreamVM @Inject constructor(
    application : Application
) : AndroidViewModel(application) {
}