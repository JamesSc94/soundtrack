package com.jamessc94.soundtrack.ui.local

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.session.PlaybackState
import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.jamessc94.soundtrack.media.MediaID
import com.jamessc94.soundtrack.media.MediaServices
import com.jamessc94.soundtrack.media.MediaSessionConnection
import com.jamessc94.soundtrack.repo.AudioDBRepo
import com.jamessc94.soundtrack.repo.MediaRealRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LocalVM @Inject constructor(
    applica : Application,
    val repo : MediaRealRepo,
    mediaSessionConnection: MediaSessionConnection,
) : AndroidViewModel(applica) {

    var sortOrderPref : SharedPreferences = applica.getSharedPreferences("soundtrack", Context.MODE_PRIVATE)
    var mediaId : MediaID = MediaID(MediaServices.TYPE_ALL_SONGS.toString(), null)
    private val _mediaItems = MutableLiveData<List<MediaBrowserCompat.MediaItem>>()
        .apply { postValue(emptyList()) }

    val mediaItems: LiveData<List<MediaBrowserCompat.MediaItem>> = _mediaItems

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {
            _mediaItems.postValue(children)

        }

    }

    val mediaSessionConnection = mediaSessionConnection.also {
        it.subscribe(mediaId.asString(), subscriptionCallback)

    }

    fun reloadMediaItems() {
        mediaSessionConnection.unsubscribe(mediaId.asString(), subscriptionCallback)
        mediaSessionConnection.subscribe(mediaId.asString(), subscriptionCallback)
    }

    override fun onCleared() {
        super.onCleared()
        // And then, finally, unsubscribe the media ID that was being watched.
        mediaSessionConnection.unsubscribe(mediaId.asString(), subscriptionCallback)
    }

}

