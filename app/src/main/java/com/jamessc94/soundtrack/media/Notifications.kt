package com.jamessc94.soundtrack.media

import android.app.*
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.IdRes
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.jamessc94.soundtrack.MediaConst.ACTION_NEXT
import com.jamessc94.soundtrack.MediaConst.ACTION_PLAY_PAUSE
import com.jamessc94.soundtrack.MediaConst.ACTION_PREVIOUS
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.media.ext.isPlaying
import com.jamessc94.soundtrack.ui.main.MainActivity
import com.jamessc94.soundtrack.util.UtilMedia.isOreo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CHANNEL_ID = "soundtracks"
private const val NOTIFICATION_ID = 888

interface Notifications {

    fun updateNotification(mediaSession: MediaSessionCompat)

    fun buildNotification(mediaSession: MediaSessionCompat): Notification
}

class RealNotifications @Inject constructor(
    private val context: Application,
    private val notificationManager: NotificationManager
) : Notifications {

    private var postTime: Long = -1

    override fun updateNotification(mediaSession: MediaSessionCompat) {
        GlobalScope.launch {
            notificationManager.notify(NOTIFICATION_ID, buildNotification(mediaSession))
        }
    }

    override fun buildNotification(mediaSession: MediaSessionCompat): Notification {
        if (mediaSession.controller.metadata == null || mediaSession.controller.playbackState == null) {
            return getEmptyNotification()
        }

        val albumName = mediaSession.controller.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
        val artistName = mediaSession.controller.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        val trackName = mediaSession.controller.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        val artwork = mediaSession.controller.metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)
        val isPlaying = mediaSession.isPlaying()

        val playButtonResId = if (isPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }

        val nowPlayingIntent = Intent(context, MainActivity::class.java)
        val clickIntent = PendingIntent.getActivity(context, 0, nowPlayingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (postTime == -1L) {
            postTime = System.currentTimeMillis()

        }

        createNotificationChannel()

        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
            .setShowCancelButton(true)
            .setShowActionsInCompactView(0, 1, 2)
            .setCancelButtonIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setStyle(style)
            setSmallIcon(R.drawable.ic_not)
            setLargeIcon(artwork)
            setContentIntent(clickIntent)
            setContentTitle(trackName)
            setContentText(artistName)
            setSubText(albumName)
            setColorized(true)
            setShowWhen(false)
            setWhen(postTime)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            addAction(getPreviousAction(context))
            addAction(getPlayPauseAction(context, playButtonResId))
            addAction(getNextAction(context))
        }

//        if (artwork != null) {
//            builder.color = Palette.from(artwork)
//                .generate()
//                .getVibrantColor("#403f4d".toColorInt())
//        }

        return builder.build()
    }

    private fun getPreviousAction(context: Context): NotificationCompat.Action {
        val actionIntent = Intent(context, MediaServices::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val pendingIntent = PendingIntent.getService(context, 0, actionIntent, 0)
        return NotificationCompat.Action(R.drawable.ic_previous, "", pendingIntent)
    }

    private fun getPlayPauseAction(context: Context, @IdRes playButtonResId: Int): NotificationCompat.Action {
        val actionIntent = Intent(context, MediaServices::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val pendingIntent = PendingIntent.getService(context, 0, actionIntent, 0)
        return NotificationCompat.Action(playButtonResId, "", pendingIntent)
    }

    private fun getNextAction(context: Context): NotificationCompat.Action {
        val actionIntent = Intent(context, MediaServices::class.java).apply {
            action = ACTION_NEXT
        }
        val pendingIntent = PendingIntent.getService(context, 0, actionIntent, 0)
        return NotificationCompat.Action(R.drawable.ic_next, "", pendingIntent)
    }

    private fun getEmptyNotification(): Notification {
        createNotificationChannel()
        return NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_not)
            setContentTitle("Soundtrack")
            setColorized(true)
            setShowWhen(false)
            setWhen(postTime)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }.build()
    }

    private fun createNotificationChannel() {
        if (!isOreo()) return
        val name = context.getString(R.string.media_playback)
        val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW).apply {
            description = context.getString(R.string.media_playback_controls)
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }
}