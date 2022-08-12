package com.jamessc94.soundtrack.media.callbacks

import android.content.Context
import com.jamessc94.soundtrack.model.Songs

interface PopupMenuListener {

    fun play(song: Songs)

    fun goToAlbum(song: Songs)

    fun goToArtist(song: Songs)

    fun addToPlaylist(context: Context, song: Songs)

    fun deleteSong(context: Context, song: Songs)

    fun removeFromPlaylist(song: Songs, playlistId: Long)

    fun playNext(song: Songs)

}