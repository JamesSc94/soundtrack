package com.jamessc94.soundtrack

import android.content.SharedPreferences
import android.provider.MediaStore

enum class ArtworkSize(val apiValue: String) {
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large"),
    EXTRA_LARGE("extralarge"),
    MEGA("mega")
}

const val PREF_APP_THEME = "theme_preference"
const val PREF_SONG_SORT_ORDER = "song_sort_order"
const val PREF_ALBUM_SORT_ORDER = "album_sort_order"
const val PREF_START_PAGE = "start_page_preference"
const val PREF_LAST_FOLDER = "last_folder"

enum class SongSortOrder(val rawValue: String) {

    SONG_A_Z(MediaStore.Audio.Media.DEFAULT_SORT_ORDER),
    SONG_Z_A(MediaStore.Audio.Media.DEFAULT_SORT_ORDER + " DESC"),
    SONG_YEAR(MediaStore.Audio.Media.YEAR + " DESC"),
    SONG_DURATION(MediaStore.Audio.Media.DURATION + " DESC");

    companion object {
        fun fromString(raw: String): SongSortOrder {
            return SongSortOrder.values().single { it.rawValue == raw }
        }

        fun toString(value: SongSortOrder): String = value.rawValue
    }
}

fun SharedPreferences.save(key: String, value : SongSortOrder) {
    val editor = edit()
    editor.putString(key, value.rawValue)
    editor.apply()

}