package com.jamessc94.soundtrack.media

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.net.toUri
import com.jamessc94.soundtrack.MediaConst.ACTION_REPEAT_QUEUE
import com.jamessc94.soundtrack.MediaConst.ACTION_REPEAT_SONG
import com.jamessc94.soundtrack.MediaConst.REPEAT_MODE
import com.jamessc94.soundtrack.MediaConst.SHUFFLE_MODE
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.media.ext.isPlaying
import com.jamessc94.soundtrack.media.ext.position
import com.jamessc94.soundtrack.media.ext.toSongIDs
import com.jamessc94.soundtrack.model.QueueEntity
import com.jamessc94.soundtrack.model.Songs
import com.jamessc94.soundtrack.persistence.QueueDao
import com.jamessc94.soundtrack.repo.MediaRepo
import com.jamessc94.soundtrack.util.UtilMusic
import javax.inject.Inject
import javax.inject.Singleton

typealias OnIsPlaying = SongPlayer.(playing: Boolean) -> Unit

interface SongPlayer {

    fun setQueue(
        data: LongArray = LongArray(0),
        title: String = ""
    )

    fun getSession(): MediaSessionCompat

    fun playSong()

    fun playSong(id: Long)

    fun playSong(song: Songs)

    fun seekTo(position: Int)

    fun pause()

    fun nextSong()

    fun repeatSong()

    fun repeatQueue()

    fun previousSong()

    fun playNext(id: Long)

    fun swapQueueSongs(from: Int, to: Int)

    fun removeFromQueue(id: Long)

    fun stop()

    fun release()

    fun onPlayingState(playing: OnIsPlaying)

    fun onPrepared(prepared: OnPrepared<SongPlayer>)

    fun onError(error: OnError<SongPlayer>)

    fun onCompletion(completion: OnCompletion<SongPlayer>)

    fun updatePlaybackState(applier: PlaybackStateCompat.Builder.() -> Unit)

    fun setPlaybackState(state: PlaybackStateCompat)

    fun restoreFromQueueData(queueData: QueueEntity)
}

@Singleton
class RealSongPlayer @Inject constructor(
    private val context: Application,
    private val musicPlayer: MusicPlayer,
    private val songsRepository: MediaRepo,
    private val queueDao: QueueDao,
    private val queue: Queue
) : SongPlayer, AudioManager.OnAudioFocusChangeListener {

    private var isInitialized: Boolean = false

    private var isPlayingCallback: OnIsPlaying = {}
    private var preparedCallback: OnPrepared<SongPlayer> = {}
    private var errorCallback: OnError<SongPlayer> = {}
    private var completionCallback: OnCompletion<SongPlayer> = {}

    private var metadataBuilder = MediaMetadataCompat.Builder()
    private var stateBuilder = createDefaultPlaybackState()

    private lateinit var audioManager: AudioManager
    private lateinit var focusRequest: AudioFocusRequest

    private var mediaSession = MediaSessionCompat(context, context.getString(R.string.app_name)).apply {
        setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        setCallback(MediaSessionCallback(this, this@RealSongPlayer, songsRepository, queueDao))
        setPlaybackState(stateBuilder.build())
        setSessionActivity(
            PendingIntent.getActivity(context, 0,
                context.packageManager.getLaunchIntentForPackage(context.packageName), 0)
        )
        isActive = true

    }

    init {
        queue.setMediaSession(mediaSession)

        musicPlayer.onPrepared {
            preparedCallback(this@RealSongPlayer)

            playSong()
            seekTo(getSession().position().toInt())
        }

        musicPlayer.onCompletion {
            completionCallback(this@RealSongPlayer)

            val controller = getSession().controller
            when (controller.repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_ONE -> {
                    controller.transportControls.sendCustomAction(ACTION_REPEAT_SONG, null)
                }
                PlaybackStateCompat.REPEAT_MODE_ALL -> {
                    controller.transportControls.sendCustomAction(ACTION_REPEAT_QUEUE, null)
                }
                else -> controller.transportControls.skipToNext()
            }
        }

        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    build()
                })
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(this@RealSongPlayer, Handler(Looper.getMainLooper()))
                build()
            }
        }

    }

    override fun setQueue(
        data: LongArray,
        title: String
    ) {
        this.queue.ids = data
        this.queue.title = title
    }

    override fun getSession(): MediaSessionCompat = mediaSession

    override fun playSong() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(focusRequest)
        } else {
            audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
        queue.ensureCurrentId()

        if (isInitialized) {
            updatePlaybackState {
                setState(PlaybackStateCompat.STATE_PLAYING, mediaSession.position(), 1F)
            }
            musicPlayer.play()
            return
        }

        musicPlayer.reset()

        val path = UtilMusic.getSongUri(queue.currentSongId).toString()
        val isSourceSet = if (path.startsWith("content://")) {
            musicPlayer.setSource(path.toUri())
        } else {
            musicPlayer.setSource(path)
        }

        if (isSourceSet) {
            isInitialized = true
            musicPlayer.prepare()
        }
    }

    override fun playSong(id: Long) {
        val song = songsRepository.getSongForId(id)
        playSong(song)
    }

    override fun playSong(song: Songs) {
        if (queue.currentSongId != song.id) {
            queue.currentSongId = song.id
            isInitialized = false
            updatePlaybackState {
                setState(PlaybackStateCompat.STATE_STOPPED, 0, 1F)
            }
        }
        setMetaData(song)
        playSong()
    }

    override fun seekTo(position: Int) {
        if (isInitialized) {
            musicPlayer.seekTo(position)
            updatePlaybackState {
                setState(
                    mediaSession.controller.playbackState.state,
                    position.toLong(),
                    1F
                )
            }
        }
    }

    override fun pause() {
        if (musicPlayer.isPlaying() && isInitialized) {
            musicPlayer.pause()
            updatePlaybackState {
                setState(PlaybackStateCompat.STATE_PAUSED, mediaSession.position(), 1F)
            }
        }
    }

    override fun nextSong() {
        queue.nextSongId?.let {
            playSong(it)
        } ?: pause()
    }

    override fun repeatSong() {
        updatePlaybackState {
            setState(PlaybackStateCompat.STATE_STOPPED, 0, 1F)
        }
        playSong(queue.currentSong())
    }

    override fun repeatQueue() {
        if (queue.currentSongId == queue.lastId())
            playSong(queue.firstId())
        else {
            nextSong()
        }
    }

    override fun previousSong() {
        queue.previousSongId?.let(::playSong)
    }

    override fun playNext(id: Long) {
        queue.moveToNext(id)
    }

    override fun swapQueueSongs(from: Int, to: Int) {
        queue.swap(from, to)
    }

    override fun removeFromQueue(id: Long) {
        queue.remove(id)
    }

    override fun stop() {
        musicPlayer.stop()
        updatePlaybackState {
            setState(PlaybackStateCompat.STATE_NONE, 0, 1F)
        }
    }

    override fun release() {
        mediaSession.apply {
            isActive = false
            release()
        }
        musicPlayer.release()
        queue.reset()
    }

    override fun onPlayingState(playing: OnIsPlaying) {
        this.isPlayingCallback = playing
    }

    override fun onPrepared(prepared: OnPrepared<SongPlayer>) {
        this.preparedCallback = prepared
    }

    override fun onError(error: OnError<SongPlayer>) {
        this.errorCallback = error
        musicPlayer.onError { throwable ->
            errorCallback(this@RealSongPlayer, throwable)
        }
    }

    override fun onCompletion(completion: OnCompletion<SongPlayer>) {
        this.completionCallback = completion
    }

    override fun updatePlaybackState(applier: PlaybackStateCompat.Builder.() -> Unit) {
        applier(stateBuilder)
        setPlaybackState(stateBuilder.build())
    }

    override fun setPlaybackState(state: PlaybackStateCompat) {
        mediaSession.setPlaybackState(state)
        state.extras?.let { bundle ->
            mediaSession.setRepeatMode(bundle.getInt(REPEAT_MODE))
            mediaSession.setShuffleMode(bundle.getInt(SHUFFLE_MODE))
        }
        if (state.isPlaying) {
            isPlayingCallback(this, true)
        } else {
            isPlayingCallback(this, false)
        }
    }

    override fun restoreFromQueueData(queueData: QueueEntity) {
        queue.currentSongId = queueData.currentId ?: -1
        val playbackState = queueData.playState ?: PlaybackStateCompat.STATE_NONE
        val currentPos = queueData.currentSeekPos ?: 0
        val repeatMode = queueData.repeatMode ?: PlaybackStateCompat.REPEAT_MODE_NONE
        val shuffleMode = queueData.shuffleMode ?: PlaybackStateCompat.SHUFFLE_MODE_NONE

        val queueIds = queueDao.getQueueSongsSync().toSongIDs()
        setQueue(queueIds, queueData.queueTitle)
        setMetaData(queue.currentSong())

        val extras = Bundle().apply {
            putInt(REPEAT_MODE, repeatMode)
            putInt(SHUFFLE_MODE, shuffleMode)
        }
        updatePlaybackState {
            setState(playbackState, currentPos, 1F)
            setExtras(extras)
        }
    }

    private fun setMetaData(song: Songs) {
        val artwork = UtilMusic.getAlbumArtBitmap(context, song.albumId)
        val mediaMetadata = metadataBuilder.apply {
            putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album)
            putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
            putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
            putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, song.albumId.toString())
            putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, artwork)
            putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
            putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration.toLong())
        }.build()
        mediaSession.setMetadata(mediaMetadata)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                pause()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                playSong()
            }
        }
    }
}

private fun createDefaultPlaybackState(): PlaybackStateCompat.Builder {
    return PlaybackStateCompat.Builder().setActions(
        PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                or PlaybackStateCompat.ACTION_SET_REPEAT_MODE
    )
        .setState(PlaybackStateCompat.STATE_NONE, 0, 1f)
}