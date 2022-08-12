package com.jamessc94.soundtrack.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jamessc94.soundtrack.model.Track

@Dao
interface TrackDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackList(vararg updates : Track)

    @Query("SELECT * FROM track WHERE aid =:aid")
    fun getTrack(aid: String) : List<Track>

}