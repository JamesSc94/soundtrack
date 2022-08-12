package com.jamessc94.soundtrack.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jamessc94.soundtrack.model.Mvideo

@Dao
interface MvideoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMvideoList(vararg updates : Mvideo)

    @Query("SELECT * FROM mvideo WHERE artist_id =:aid")
    fun getMvideo(aid: String) : List<Mvideo>

    @Query("DELETE FROM mvideo")
    fun clearMvideo()

}