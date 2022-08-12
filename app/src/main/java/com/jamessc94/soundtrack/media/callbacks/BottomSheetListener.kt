package com.jamessc94.soundtrack.media.callbacks

import android.view.View
import androidx.annotation.NonNull

interface BottomSheetListener {
    fun onStateChanged(@NonNull bottomSheet: View, newState: Int)
    fun onSlide(@NonNull bottomSheet: View, slideOffset: Float)

}