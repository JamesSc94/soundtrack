package com.jamessc94.soundtrack.media

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.Nullable
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.jamessc94.soundtrack.MediaConst
import com.jamessc94.soundtrack.MediaConst.ACTION_NEXT
import com.jamessc94.soundtrack.MediaConst.ACTION_PREVIOUS
import com.jamessc94.soundtrack.MediaConst.APP_PACKAGE_NAME
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.media.MediaID.Companion.CALLER_OTHER
import com.jamessc94.soundtrack.media.MediaID.Companion.CALLER_SELF
import com.jamessc94.soundtrack.media.ext.*
import com.jamessc94.soundtrack.model.QueueEntity
import com.jamessc94.soundtrack.permission.Permission
import com.jamessc94.soundtrack.permission.PermissionManager
import com.jamessc94.soundtrack.permission.granted
import com.jamessc94.soundtrack.repo.MediaRealRepo
import com.jamessc94.soundtrack.repo.MediaRepo
import com.jamessc94.soundtrack.util.UtilMedia.EMPTY_ALBUM_ART_URI
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MediaServices : MediaBrowserServiceCompat(), LifecycleOwner {

    companion object {
        const val MEDIA_ID_ARG = "MEDIA_ID"
        const val MEDIA_TYPE_ARG = "MEDIA_TYPE"
        const val MEDIA_CALLER = "MEDIA_CALLER"
        const val MEDIA_ID_ROOT = -1
        const val TYPE_ALL_SONGS = 2
        const val TYPE_SONG = 9

        const val NOTIFICATION_ID = 888
    }

    private val lifecycle = LifecycleRegistry(this)
        private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver

    @Inject
    lateinit var repo : MediaRealRepo
    @Inject
    lateinit var player: SongPlayer
    @Inject
    lateinit var queueHelper : QueueHelper
    @Inject
    lateinit var notifications: Notifications

    override fun getLifecycle() = lifecycle

    override fun onCreate() {
        super.onCreate()
        lifecycle.currentState = Lifecycle.State.RESUMED

        sessionToken = player.getSession().sessionToken
        becomingNoisyReceiver = BecomingNoisyReceiver(this, sessionToken!!)

        player.onPlayingState { isPlaying ->
            if (isPlaying) {
                becomingNoisyReceiver.register()
                startForeground(NOTIFICATION_ID, notifications.buildNotification(getSession()))

            } else {
                becomingNoisyReceiver.unregister()
                stopForeground(false)
                saveCurrentData()

            }

            notifications.updateNotification(player.getSession())

        }

        player.onCompletion {
            notifications.updateNotification(player.getSession())

        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_STICKY

        }

        val mediaSession = player.getSession()
        val controller = mediaSession.controller

        when (intent.action) {
            MediaConst.ACTION_PLAY_PAUSE -> {
                controller.playbackState?.let { playbackState ->
                    when {
                        playbackState.isPlaying -> controller.transportControls.pause()
                        playbackState.isPlayEnabled -> controller.transportControls.play()
                    }
                }
            }
            ACTION_NEXT -> {
                controller.transportControls.skipToNext()

            }
            ACTION_PREVIOUS -> {
                controller.transportControls.skipToPrevious()

            }
        }

        MediaButtonReceiver.handleIntent(mediaSession, intent)

        return START_STICKY

    }

    override fun onDestroy() {
        lifecycle.currentState = Lifecycle.State.DESTROYED
        saveCurrentData()
        player.release()

        super.onDestroy()

    }

    override fun onLoadChildren(parentId: String, result: Result<List<MediaBrowserCompat.MediaItem>>) {
        result.detach()

        granted.observeOnce(this, Observer {
            if(it){
                GlobalScope.launch(Dispatchers.Main) {
                    val mediaItems = withContext(Dispatchers.IO) {
                        loadChildren(parentId)
                    }

                    result.sendResult(mediaItems)
                }

            }

        })

    }

    @Nullable
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): MediaBrowserServiceCompat.BrowserRoot? {
        val caller = if (clientPackageName == APP_PACKAGE_NAME) {
            CALLER_SELF

        } else {
            CALLER_OTHER

        }

        return MediaBrowserServiceCompat.BrowserRoot(MediaID(MEDIA_ID_ROOT.toString(), null, caller).asString(), null)
    }

    private fun addMediaRoots(mMediaRoot: MutableList<MediaBrowserCompat.MediaItem>, caller: String) {
        mMediaRoot.add(
            MediaBrowserCompat.MediaItem(
            MediaDescriptionCompat.Builder().apply {
                setMediaId(MediaID(TYPE_ALL_SONGS.toString(), null, caller).asString())
                setTitle(getString(R.string.songs))
                setIconUri(EMPTY_ALBUM_ART_URI.toUri())
                setSubtitle(getString(R.string.songs))
            }.build(), MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        ))

    }

    private fun loadChildren(parentId: String): ArrayList<MediaBrowserCompat.MediaItem> {
        val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
        val mediaIdParent = MediaID().fromString(parentId)

        val mediaType = mediaIdParent.type
        val mediaId = mediaIdParent.mediaId
        val caller = mediaIdParent.caller

        if(mediaType == MEDIA_ID_ROOT.toString()) {
            addMediaRoots(mediaItems, caller!!)

        } else {
            mediaItems.addAll(repo.loadSongs(caller))

        }

        return if (caller == CALLER_SELF) {
            mediaItems
        } else {
            mediaItems.toRawMediaItems()
        }
    }

    private fun saveCurrentData() {
        GlobalScope.launch(Dispatchers.IO) {
            val mediaSession = player.getSession()
            val controller = mediaSession.controller

            if (controller == null ||
                controller.playbackState == null ||
                controller.playbackState.state == PlaybackStateCompat.STATE_NONE
            ) {
                return@launch
            }

            val queue = controller.queue
            val currentId = controller.metadata?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
            queueHelper.updateQueueSongs(queue?.toIDList(), currentId?.toLong())

            val queueEntity = QueueEntity().apply {
                this.currentId = currentId?.toLong()
                currentSeekPos = controller.playbackState?.position
                repeatMode = controller.repeatMode
                shuffleMode = controller.shuffleMode
                playState = controller.playbackState?.state
                queueTitle = controller.queueTitle?.toString() ?: getString(R.string.all_songs)
            }
            queueHelper.updateQueueData(queueEntity)
        }
    }

}