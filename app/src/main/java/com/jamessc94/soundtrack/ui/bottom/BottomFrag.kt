package com.jamessc94.soundtrack.ui.bottom

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jamessc94.soundtrack.MediaConst.ACTION_CAST_CONNECTED
import com.jamessc94.soundtrack.MediaConst.ACTION_CAST_DISCONNECTED
import com.jamessc94.soundtrack.MediaConst.ACTION_RESTORE_MEDIA_SESSION
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.binding.setPlayState
import com.jamessc94.soundtrack.databinding.FragBottomBinding
import com.jamessc94.soundtrack.media.callbacks.BottomSheetListener
import com.jamessc94.soundtrack.media.ext.hide
import com.jamessc94.soundtrack.media.ext.show
import com.jamessc94.soundtrack.model.CastStatus
import com.jamessc94.soundtrack.model.CastStatus.Companion.STATUS_PLAYING
import com.jamessc94.soundtrack.ui.main.BaseNowPlayingFragment
import com.jamessc94.soundtrack.ui.main.MainActivity

class BottomFrag : BaseNowPlayingFragment(), BottomSheetListener {

    lateinit var binding : FragBottomBinding
    private var isCasting = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragBottomBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = nowPlayingViewModel
        binding.lifecycleOwner = this

        setupUI()
        setupCast()

    }

    private fun setupUI() {
        val layoutParams = binding.fragBottomPb.layoutParams as LinearLayout.LayoutParams
        binding.fragBottomPb.measure(0, 0)
        layoutParams.setMargins(0, -(binding.fragBottomPb.measuredHeight / 2), 0, 0)
        binding.fragBottomPb.layoutParams = layoutParams
        binding.fragBottomTitle.isSelected = true

        binding.fragBottomPpToggle.setOnClickListener {
            nowPlayingViewModel.currentData.value?.let { mediaData ->
                mainViewModel.mediaItemClicked(mediaData.toDummySong(), null)
            }
        }

        binding.fragBottomPause.setOnClickListener {
            nowPlayingViewModel.currentData.value?.let { mediaData ->
                mainViewModel.
                mediaItemClicked(mediaData.toDummySong(), null)
            }
        }

        binding.fragBottomNext.setOnClickListener {
            mainViewModel.transportControls().skipToNext()
        }

        binding.fragBottomPrevious.setOnClickListener {
            mainViewModel.transportControls().skipToPrevious()
        }

        binding.fragBottomRepeat.setOnClickListener {
            when (nowPlayingViewModel.currentData.value?.repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_NONE -> mainViewModel.transportControls().setRepeatMode(
                    PlaybackStateCompat.REPEAT_MODE_ONE
                )
                PlaybackStateCompat.REPEAT_MODE_ONE -> mainViewModel.transportControls().setRepeatMode(
                    PlaybackStateCompat.REPEAT_MODE_ALL
                )
                PlaybackStateCompat.REPEAT_MODE_ALL -> mainViewModel.transportControls().setRepeatMode(
                    PlaybackStateCompat.REPEAT_MODE_NONE
                )
            }
        }

        binding.fragBottomShuffle.setOnClickListener {
            when (nowPlayingViewModel.currentData.value?.shuffleMode) {
                PlaybackStateCompat.SHUFFLE_MODE_NONE -> mainViewModel.transportControls().setShuffleMode(
                    PlaybackStateCompat.SHUFFLE_MODE_ALL
                )
                PlaybackStateCompat.SHUFFLE_MODE_ALL -> mainViewModel.transportControls().setShuffleMode(
                    PlaybackStateCompat.SHUFFLE_MODE_NONE
                )
            }
        }

        (activity as? MainActivity)?.let { mainActivity ->
            binding.fragBottomCollapse.setOnClickListener { mainActivity.collapseBottomSheet() }
            mainActivity.vm.bottomSheetListener = this
        }

//        buildUIControls()
    }

    private fun buildUIControls() {
//        binding.btnLyrics.setOnClickListener {
//            val currentSong = nowPlayingViewModel.currentData.value
//            val artist = currentSong?.artist
//            val title = currentSong?.title
//            val mainActivity = activity as? MediaTOActv
//            if (artist != null && title != null && mainActivity != null) {
//                mainActivity.collapseBottomSheet()
//                Handler(Looper.getMainLooper()).postDelayed({
//                    mainActivity.addFragment(fragment = LyricsFragment.newInstance(artist, title))
//                }, 200)
//            }
//        }
    }

    private fun setupCast() {
        //display cast data directly if casting instead of databinding
        val castProgressObserver = Observer<Pair<Long, Long>> {
            binding.fragBottomPb.progress = it.first.toInt()
            if (binding.fragBottomPb.max != it.second.toInt())
                binding.fragBottomPb.max = it.second.toInt()

            binding.fragBottomSb.progress = it.first.toInt()
            if (binding.fragBottomSb.max != it.second.toInt())
                binding.fragBottomSb.max = it.second.toInt()
        }

        val castStatusObserver = Observer<CastStatus> {
            it ?: return@Observer
            if (it.isCasting) {
                isCasting = true

                mainViewModel.castProgressLiveData.observe(this, castProgressObserver)
//                setLastFmAlbumImage(binding.bottomContolsAlbumart, it.castSongArtist, it.castSongAlbum, ArtworkSize.SMALL, it.castAlbumId.toLong())

                binding.fragBottomArtist.text = getString(R.string.casting_to_x, it.castDeviceName)
                if (it.castSongId == -1) {
                    binding.fragBottomTitle.text = getString(R.string.nothing_playing)
                } else {
                    binding.fragBottomTitle.text =
                        getString(R.string.now_playing_format, it.castSongTitle, it.castSongArtist)
                }

                if (it.state == STATUS_PLAYING) {
                    setPlayState(binding.fragBottomPpToggle, PlaybackStateCompat.STATE_PLAYING)
                    setPlayState(binding.fragBottomPause, PlaybackStateCompat.STATE_PLAYING)
                } else {
                    setPlayState(binding.fragBottomPpToggle, PlaybackStateCompat.STATE_PAUSED)
                    setPlayState(binding.fragBottomPause, PlaybackStateCompat.STATE_PAUSED)
                }
            } else {
                isCasting = false
                mainViewModel.castProgressLiveData.removeObserver(castProgressObserver)
            }
        }

        mainViewModel.customAction
            .map { it.peekContent() }
            .observe(this) {
                when (it) {
                    ACTION_CAST_CONNECTED -> {
                        mainViewModel.castLiveData.observe(this, castStatusObserver)
                    }
                    ACTION_CAST_DISCONNECTED -> {
                        isCasting = false
                        mainViewModel.castLiveData.removeObserver(castStatusObserver)
                        mainViewModel.transportControls().sendCustomAction(ACTION_RESTORE_MEDIA_SESSION, null)
                    }
                }
            }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        if (slideOffset > 0) {
            binding.fragBottomPause.hide()
            binding.fragBottomPb.hide()
            binding.fragBottomCollapse.show()
        } else {
            binding.fragBottomPb.show()
        }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_EXPANDED) {
            binding.fragBottomPause.hide()
            binding.fragBottomCollapse.show()
            //disable expanded controls when casting as we don't support next/previous yet
            if (isCasting) {
                (activity as MainActivity).collapseBottomSheet()
            }
        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            binding.fragBottomPause.show()
            binding.fragBottomCollapse.hide()
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.mediaControllerBottom.observe(this) { mediaController ->
            binding.fragBottomPb.setMediaController(mediaController)
            binding.fragBottomProgressTv.setMediaController(mediaController)
            binding.fragBottomSb.setMediaController(mediaController)
        }
    }

    override fun onStop() {
        binding.fragBottomPb.disconnectController()
        binding.fragBottomProgressTv.disconnectController()
        binding.fragBottomSb.disconnectController()
        super.onStop()
    }
}