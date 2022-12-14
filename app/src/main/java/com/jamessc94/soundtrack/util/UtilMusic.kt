package com.jamessc94.soundtrack.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.jamessc94.soundtrack.R
import java.io.FileNotFoundException

object UtilMusic {

    fun getSongUri(id: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
//        log("Querying $contentUri")
        return context.contentResolver.query(contentUri, projection, null, null, null)?.use {
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            if (it.moveToFirst()) {
                it.getString(dataIndex)
            } else {
                ""
            }
        } ?: throw IllegalStateException("Unable to query $contentUri, system returned null.")
    }

    fun getAlbumArtBitmap(context: Context, albumId: Long?): Bitmap? {
        if (albumId == null) return null
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, UtilMedia.getAlbumArtUri(albumId))
        } catch (e: FileNotFoundException) {
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_next)
        }
    }

}