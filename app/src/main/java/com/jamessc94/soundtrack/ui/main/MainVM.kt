package com.jamessc94.soundtrack.ui.main

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.jamessc94.soundtrack.MediaConst.ACTION_PLAY_NEXT
import com.jamessc94.soundtrack.MediaConst.ACTION_SONG_DELETED
import com.jamessc94.soundtrack.MediaConst.SONG
import com.jamessc94.soundtrack.media.callbacks.BottomSheetListener
import com.jamessc94.soundtrack.media.Event
import com.jamessc94.soundtrack.media.MediaID
import com.jamessc94.soundtrack.media.MediaSessionConnection
import com.jamessc94.soundtrack.media.callbacks.PopupMenuListener
import com.jamessc94.soundtrack.media.ext.id
import com.jamessc94.soundtrack.media.ext.isPlayEnabled
import com.jamessc94.soundtrack.media.ext.isPlaying
import com.jamessc94.soundtrack.media.ext.isPrepared
import com.jamessc94.soundtrack.model.Audio
import com.jamessc94.soundtrack.model.CastStatus
import com.jamessc94.soundtrack.model.Songs
import com.jamessc94.soundtrack.repo.MediaRealRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val applica: Application,
    val repo : MediaRealRepo,
    private val mediaSessionConnection: MediaSessionConnection,
) : AndroidViewModel(applica){

    lateinit var bottomSheetListener: BottomSheetListener

    var queue: MutableList<MediaSessionCompat.QueueItem>? = null
    var nowPlaying = MutableLiveData<Audio>()

    val customAction: LiveData<Event<String>> get() = _customAction
    private val _customAction = MutableLiveData<Event<String>>()

    val navigateToMediaItem: LiveData<Event<MediaID>> get() = _navigateToMediaItem
    private val _navigateToMediaItem = MutableLiveData<Event<MediaID>>()

    val rootMediaId: LiveData<MediaID?> =
        mediaSessionConnection.isConnected.map { isConnected ->
            if (isConnected) {
                MediaID().fromString(mediaSessionConnection.rootMediaId)

            } else {
                null

            }
        }

    val mediaControllerBottom: LiveData<MediaControllerCompat?> =
        mediaSessionConnection.isConnected.map { isConnected ->
            if (isConnected) {
                mediaSessionConnection.mediaController
            } else {
                null
            }
        }

    val castLiveData: LiveData<CastStatus> get() = _castLiveData
    private val _castLiveData = MutableLiveData<CastStatus>()

    val castProgressLiveData: LiveData<Pair<Long, Long>> get() = _castProgressLiveData
    private val _castProgressLiveData = MutableLiveData<Pair<Long, Long>>()

    val popupMenuListener = object : PopupMenuListener {

        override fun play(song: Songs) {
            mediaItemClicked(song, null)
        }

        override fun goToAlbum(song: Songs) {
//            browseToItem(albumRepository.getAlbum(song.albumId))
        }

        override fun goToArtist(song: Songs) {
//            browseToItem(artistRepository.getArtist(song.artistId))
        }

        override fun addToPlaylist(context: Context, song: Songs) {
//            AddToPlaylistDialog.show(context as AppCompatActivity, song)
        }

        override fun removeFromPlaylist(song: Songs, playlistId: Long) {
//            playlistsRepository.removeFromPlaylist(playlistId, song.id)
//            _customAction.postValue(Event(ACTION_REMOVED_FROM_PLAYLIST))
        }

        override fun deleteSong(context: Context, song: Songs) {
//            DeleteSongDialog.show(context as MediaTOActv, song)
        }

        override fun playNext(song: Songs) {
            mediaSessionConnection.transportControls.sendCustomAction(ACTION_PLAY_NEXT,
                Bundle().apply { putLong(SONG, song.id) }
            )
        }
    }

    fun transportControls() = mediaSessionConnection.transportControls

    fun mediaItemClicked(clickedItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
        if (clickedItem.isBrowsable) {
            browseToItem(clickedItem)

        } else {
            playMedia(clickedItem, extras)

        }

    }

    private fun browseToItem(mediaItem: MediaBrowserCompat.MediaItem) {
        _navigateToMediaItem.value = Event(MediaID().fromString(mediaItem.mediaId!!).apply {
            this.mediaItem = mediaItem
        })
    }

    fun playMedia(mediaItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && MediaID().fromString(mediaItem.mediaId!!).mediaId == nowPlaying?.id) {
            mediaSessionConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        Log.i("Errors","midmid failed ${mediaItem.mediaId}")

                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaItem.mediaId, extras)

        }

    }

    fun onSongDeleted(id: Long) {
        _customAction.postValue(Event(ACTION_SONG_DELETED))
        // also need to remove the deleted song from the current playing queue
        mediaSessionConnection.transportControls.sendCustomAction(ACTION_SONG_DELETED,
            Bundle().apply {
                // sending parceleable data through media session custom action bundle is not working currently
                putLong(SONG, id)
            })
    }

}