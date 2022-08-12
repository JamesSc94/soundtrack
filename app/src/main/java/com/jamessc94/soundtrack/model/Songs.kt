package com.jamessc94.soundtrack.model

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.jamessc94.soundtrack.media.MediaID
import com.jamessc94.soundtrack.media.ext.value
import com.jamessc94.soundtrack.media.ext.valueOrDefault
import com.jamessc94.soundtrack.media.ext.valueOrEmpty
import com.jamessc94.soundtrack.util.UtilMedia.getAlbumArtUri

data class Songs(
    var id: Long = 0,
    var albumId: Long = 0,
    var artistId: Long = 0,
    var title: String = "",
    var artist: String = "",
    var album: String = "",
    var duration: Int = 0,
    var trackNumber: Int = 0,
    var bundle : Bundle = Bundle(),
) : MediaBrowserCompat.MediaItem(
    MediaDescriptionCompat.Builder()
        .setMediaId(MediaID("9", "$id").asString())
        .setTitle(title)
        .setIconUri(getAlbumArtUri(albumId))
        .setSubtitle(artist)
        .setExtras(bundle)
        .build(), FLAG_PLAYABLE
) {
    companion object {
        const val SONGS_MEDIA_ID = "SONGS_MEDIA_ID"
        const val SONGS_ALBUM_ID = "SONGS_ALBUM_ID"
        const val SONGS_ARTIST_ID = "SONGS_ARTIST_ID"
        const val SONGS_TITLE = "SONGS_TITLE"
        const val SONGS_ARTIST = "SONGS_ARTIST"
        const val SONGS_ALBUM = "SONGS_ALBUM"
        const val SONGS_DURATION = "SONGS_DURATION"
        const val SONGS_TRACKNUMBER = "SONGS_TRACKNUMBER"

        fun fromCursor(cursor: Cursor, albumId: Long = -1, artistId: Long = -1): Songs {
            val b = Bundle()
            b.putLong(SONGS_MEDIA_ID, cursor.value(MediaStore.Audio.Media._ID))
            b.putLong(SONGS_ALBUM_ID, cursor.valueOrDefault(MediaStore.Audio.Media.ALBUM_ID, albumId))
            b.putLong(SONGS_ARTIST_ID, cursor.valueOrDefault(MediaStore.Audio.Media.ARTIST_ID, artistId))
            b.putString(SONGS_TITLE, cursor.valueOrEmpty(MediaStore.Audio.Media.TITLE))
            b.putString(SONGS_ARTIST,  cursor.valueOrEmpty(MediaStore.Audio.Media.ARTIST))
            b.putString(SONGS_ALBUM, cursor.valueOrEmpty(MediaStore.Audio.Media.ALBUM))
            b.putInt(SONGS_DURATION, cursor.value(MediaStore.Audio.Media.DURATION))
            b.putInt(SONGS_TRACKNUMBER, cursor.value<Int>(MediaStore.Audio.Media.TRACK).normalizeTrackNumber())

            return Songs(
                id = cursor.value(MediaStore.Audio.Media._ID),
                albumId = cursor.valueOrDefault(MediaStore.Audio.Media.ALBUM_ID, albumId),
                artistId = cursor.valueOrDefault(MediaStore.Audio.Media.ARTIST_ID, artistId),
                title = cursor.valueOrEmpty(MediaStore.Audio.Media.TITLE),
                artist = cursor.valueOrEmpty(MediaStore.Audio.Media.ARTIST),
                album = cursor.valueOrEmpty(MediaStore.Audio.Media.ALBUM),
                duration = cursor.value(MediaStore.Audio.Media.DURATION),
                trackNumber = cursor.value<Int>(MediaStore.Audio.Media.TRACK).normalizeTrackNumber(),
                bundle = b
            )
        }

    }
}

private fun Int.normalizeTrackNumber(): Int {
    var returnValue = this

    while (returnValue >= 1000) {
        returnValue -= 1000
    }
    return returnValue
}