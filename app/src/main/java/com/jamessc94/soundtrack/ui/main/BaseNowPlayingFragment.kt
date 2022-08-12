package com.jamessc94.soundtrack.ui.main

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.jamessc94.soundtrack.media.ext.safeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseNowPlayingFragment : CoroutineFragment() {

    protected val mainViewModel : MainVM by viewModels()
    protected val nowPlayingViewModel : NowPlayingVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nowPlayingViewModel.currentData.observe(this) {
            showHideBottomSheet()

        }

    }

    override fun onPause() {
        showHideBottomSheet()
        super.onPause()
    }

    private fun showHideBottomSheet() {
        val activity = safeActivity as MainActivity

        nowPlayingViewModel.currentData.value?.let {
            if (!it.title.isNullOrEmpty()) {
                activity.showBottomSheet()

            } else {
                activity.hideBottomSheet()

            }
        }
    }

}