package com.jamessc94.soundtrack.ui.main

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.jamessc94.soundtrack.MediaConst.ACTION_SET_MEDIA_STATE
import com.jamessc94.soundtrack.media.MediaSessionConnection
import com.jamessc94.soundtrack.model.MediaData
import com.jamessc94.soundtrack.model.QueueData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@HiltViewModel
class NowPlayingVM @Inject constructor(
    mediaSessionConnection: MediaSessionConnection
) : ViewModel() {

    private var albumArtDisposable: Disposable? = null

    private val _currentData = MutableLiveData<MediaData>()
    val currentData: LiveData<MediaData> = _currentData

    private val _queueData = MutableLiveData<QueueData>()
    val queueData: LiveData<QueueData> = _queueData

    private val playbackStateObserver = Observer<PlaybackStateCompat> { playbackState ->
        playbackState?.let {
            _currentData.postValue(_currentData.value?.pullPlaybackState(it)
                ?: MediaData().pullPlaybackState(it))
        }
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> { mediaMetaData ->
        mediaMetaData?.let {
            val newValue = _currentData.value?.pullMediaMetadata(it)
                ?: MediaData().pullMediaMetadata(it)
            _currentData.postValue(newValue)
        }
    }

    private val queueDataObserver = Observer<QueueData> { queueData ->
        queueData?.let {
            _queueData.postValue(queueData)
        }
    }

    private val mediaSessionConnection = mediaSessionConnection.also {
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
        it.queueData.observeForever(queueDataObserver)

        //set media data and state saved in db to the media session when connected
        it.isConnected.observeForever { connected ->
            if (connected) {
                it.transportControls.sendCustomAction(ACTION_SET_MEDIA_STATE, null)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaSessionConnection.playbackState.removeObserver(playbackStateObserver)
        mediaSessionConnection.nowPlaying.removeObserver(mediaMetadataObserver)
        albumArtDisposable?.dispose()
    }
}