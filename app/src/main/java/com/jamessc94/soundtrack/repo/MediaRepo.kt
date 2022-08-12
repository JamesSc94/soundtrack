package com.jamessc94.soundtrack.repo

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import com.jamessc94.soundtrack.media.MediaID
import com.jamessc94.soundtrack.media.ext.mapList
import com.jamessc94.soundtrack.model.Songs
import java.io.File

interface MediaRepo {

    fun loadSongs(caller: String?): List<Songs>

    fun getSongForId(id: Long): Songs

    fun getSongsForIds(idList: LongArray): List<Songs>

    fun getSongFromPath(songPath: String): Songs

    fun searchSongs(searchString: String, limit: Int): List<Songs>

    fun deleteTracks(ids: LongArray): Int

}

class MediaRealRepo(
    private val contentResolver: ContentResolver,
) : MediaRepo {

    //======================================================  Callback  ====================================================================
    override fun loadSongs(caller: String?): List<Songs> {
        MediaID.currentCaller = caller
        return makeSongCursor(null, null)
            .mapList(true) { Songs.fromCursor(this) }

    }

    override fun getSongForId(id: Long): Songs {
        val songs = makeSongCursor("_id = $id", null)
            .mapList(true) { Songs.fromCursor(this) }
        return songs.firstOrNull() ?: Songs()

    }

    override fun getSongsForIds(idList: LongArray): List<Songs> {
        var selection = "_id IN ("
        for (id in idList) {
            selection += "$id,"
        }
        if (idList.isNotEmpty()) {
            selection = selection.substring(0, selection.length - 1)
        }
        selection += ")"

        return makeSongCursor(selection, null)
            .mapList(true) { Songs.fromCursor(this) }
    }

    override fun getSongFromPath(songPath: String): Songs {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.DATA
        val selectionArgs = arrayOf(songPath)
        val projection = arrayOf("_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id")
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        return contentResolver.query(uri, projection, "$selection=?", selectionArgs, sortOrder)?.use {
            if (it.moveToFirst() && it.count > 0) {
                Songs.fromCursor(it)
            } else {
                Songs()
            }
        } ?: throw IllegalStateException("Unable to query $uri, system returned null.")
    }

    override fun searchSongs(searchString: String, limit: Int): List<Songs> {
        val result = makeSongCursor("title LIKE ?", arrayOf("$searchString%"))
            .mapList(true) { Songs.fromCursor(this) }
        if (result.size < limit) {
            val moreSongs = makeSongCursor("title LIKE ?", arrayOf("%_$searchString%"))
                .mapList(true) { Songs.fromCursor(this) }
            result += moreSongs
        }
        return if (result.size < limit) {
            result
        } else {
            result.subList(0, limit)
        }
    }

    override fun deleteTracks(ids: LongArray): Int {
        val projection = arrayOf(
            BaseColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.Audio.AudioColumns.ALBUM_ID
        )
        val selection = StringBuilder().apply {
            append("${BaseColumns._ID} IN (")
            for (i in ids.indices) {
                append(ids[i])
                if (i < ids.size - 1) {
                    append(",")
                }
            }
            append(")")
        }

        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection.toString(),
            null,
            null
        )?.use {
            it.moveToFirst()
            // Step 2: Remove selected tracks from the database
            contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selection.toString(), null)

            // Step 3: Remove files from card
            it.moveToFirst()
            while (!it.isAfterLast) {
                val name = it.getString(1)
                val f = File(name)
                try { // File.delete can throw a security exception
                    if (!f.delete()) {
                        // I'm not sure if we'd ever get here (deletion would
                        // have to fail, but no exception thrown)
                        Log.i("Errors", "Failed to delete file: $name")
                    }
                } catch (_: SecurityException) {
                }
                it.moveToNext()
            }
        }

        return ids.size
    }

    private fun makeSongCursor(selection: String?, paramArrayOfString: Array<String>?): Cursor {
        return makeSongCursor(selection, paramArrayOfString, MediaStore.Audio.Media.DISPLAY_NAME)

    }

    @SuppressLint("Recycle")
    private fun makeSongCursor(selection: String?, paramArrayOfString: Array<String>?, sortOrder: String?): Cursor {
        val selectionStatement = StringBuilder("is_music=1 AND title != ''")
        if (!selection.isNullOrEmpty()) {
            selectionStatement.append(" AND $selection")
        }
        val projection = arrayOf("_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id")

        return contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionStatement.toString(),
            paramArrayOfString,
            sortOrder
        ) ?: throw IllegalStateException("Unable to query ${MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}, system returned null.")
    }

}