package com.jamessc94.soundtrack.media

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import javax.inject.Singleton

typealias OnPrepared<T> = T.() -> Unit
typealias OnError<T> = T.(error: Throwable) -> Unit
typealias OnCompletion<T> = T.() -> Unit

interface MusicPlayer {
    fun play()

    fun setSource(path: String): Boolean

    fun setSource(uri: Uri): Boolean

    fun prepare()

    fun seekTo(position: Int)

    fun isPrepared(): Boolean

    fun isPlaying(): Boolean

    fun position(): Int

    fun pause()

    fun stop()

    fun reset()

    fun release()

    fun onPrepared(prepared: OnPrepared<MusicPlayer>)

    fun onError(error: OnError<MusicPlayer>)

    fun onCompletion(completion: OnCompletion<MusicPlayer>)
}

@Singleton
class RealMusicPlayer(internal val context: Application) : MusicPlayer,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    private var _player: MediaPlayer? = null
    private val player: MediaPlayer
        get() {
            if (_player == null) {
                _player = createPlayer(this)
            }
            return _player ?: throw IllegalStateException("Impossible")
        }

    private var didPrepare = false
    private var onPrepared: OnPrepared<MusicPlayer> = {}
    private var onError: OnError<MusicPlayer> = {}
    private var onCompletion: OnCompletion<MusicPlayer> = {}

    override fun play() {
        player.start()

    }

    override fun setSource(path: String): Boolean {
        try {
            player.setDataSource(path)
        } catch (e: Exception) {
            onError(this, e)
            return false

        }

        return true

    }

    override fun setSource(uri: Uri): Boolean {
        try {
            player.setDataSource(context, uri)
        } catch (e: Exception) {
            onError(this, e)
            return false

        }
        return true
    }

    override fun prepare() {
        player.prepareAsync()

    }

    override fun seekTo(position: Int) {
        player.seekTo(position)

    }

    override fun isPrepared() = didPrepare

    override fun isPlaying() = player.isPlaying

    override fun position() = player.currentPosition

    override fun pause() {
        player.pause()

    }

    override fun stop() {
        player.stop()

    }

    override fun reset() {
        player.reset()

    }

    override fun release() {
        player.release()
        _player = null

    }

    override fun onPrepared(prepared: OnPrepared<MusicPlayer>) {
        this.onPrepared = prepared
    }

    override fun onError(error: OnError<MusicPlayer>) {
        this.onError = error
    }

    override fun onCompletion(completion: OnCompletion<MusicPlayer>) {
        this.onCompletion = completion
    }

    // Callbacks from stock MediaPlayer...

    override fun onPrepared(mp: MediaPlayer?) {
        didPrepare = true
        onPrepared(this)

    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        didPrepare = false

        return false

    }

    override fun onCompletion(mp: MediaPlayer?) {
        onCompletion(this)

    }

}

private fun createPlayer(owner: RealMusicPlayer): MediaPlayer {
    return MediaPlayer().apply {
        setWakeMode(owner.context, PowerManager.PARTIAL_WAKE_LOCK)
        val attr = AudioAttributes.Builder().apply {
            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            setUsage(AudioAttributes.USAGE_MEDIA)
        }.build()
        setAudioAttributes(attr)
        setOnPreparedListener(owner)
        setOnCompletionListener(owner)
        setOnErrorListener(owner)
    }
}