package com.jamessc94.soundtrack.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jamessc94.soundtrack.model.Album

@Dao
interface AlbumDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbumList(vararg updates : Album)

    @Query("SELECT * FROM album WHERE aid =:aid")
    fun getAlbum(aid: String) : List<Album>

}