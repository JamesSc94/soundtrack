package com.jamessc94.soundtrack.ui.mvideo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragmentX
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.databinding.FragMvideoBinding
import com.jamessc94.soundtrack.network.Network
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MvideoFrag : Fragment(), YouTubePlayer.OnInitializedListener {

    val vm : MvideoVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragMvideoBinding.inflate(inflater, container, false)

        val ft = childFragmentManager.beginTransaction()
        val frag = YouTubePlayerFragmentX.newInstance()
        ft.replace(R.id.frag_mvideo_fl, frag)
        ft.commit()

        frag.initialize(Network.gKey, this)

        return binding.root

    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        youTubePlayer: YouTubePlayer?,
        wasRestored: Boolean
    ) {
        youTubePlayer?.setPlayerStateChangeListener(playerStateChangeListener)
        youTubePlayer?.setPlaybackEventListener(playbackEventListener)

        if (!wasRestored) {
            youTubePlayer?.cueVideo(vm.youtubeID)
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider?,
        youTubeInitializationResult: YouTubeInitializationResult?
    ) {
        if (youTubeInitializationResult?.isUserRecoverableError == true) {
            youTubeInitializationResult.getErrorDialog(activity, 0).show()

        } else {
            val errorMessage = "There was an error initializing the YoutubePlayer ($youTubeInitializationResult)"

        }

    }

    private val playerStateChangeListener = object: YouTubePlayer.PlayerStateChangeListener {
        override fun onAdStarted() {
        }

        override fun onLoading() {
        }

        override fun onVideoStarted() {
        }

        override fun onLoaded(p0: String?) {
        }

        override fun onVideoEnded() {

        }

        override fun onError(p0: YouTubePlayer.ErrorReason?) {

        }
    }

    private val playbackEventListener = object: YouTubePlayer.PlaybackEventListener {
        override fun onSeekTo(p0: Int) {
        }

        override fun onBuffering(p0: Boolean) {
        }

        override fun onPlaying() {

        }

        override fun onStopped() {

        }

        override fun onPaused() {

        }
    }

}