package com.jamessc94.soundtrack.persistence

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jamessc94.soundtrack.model.*

@Dao
interface SChartDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSChartList(vararg updates : SChart)

    @Query("SELECT * FROM chart")
    fun getSChartAll() : List<SChart>

    @Query("SELECT * FROM chart")
    fun getSChartAllFlow() : PagingSource<Int, SChart>

}

@Dao
interface SArtistDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSArtistList(vararg updates : SArtist)

    @Query("SELECT * FROM artist")
    fun getSArtistAll() : LiveData<List<SArtist>>

    @Query("SELECT * FROM artist")
    fun getSArtistFlow() : PagingSource<Int, SArtist>

}

@Dao
interface SArtistDetailsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSArtistDetailsList(vararg updates : SArtistDetails)

    @Query("SELECT * FROM artistdetails WHERE name =:name")
    fun getSArtistDetails(name: String) : SArtistDetails?

    @Query("SELECT * FROM artistdetails")
    fun getSArtistFlow() : PagingSource<Int, SArtistDetails>

}

@Dao
interface SChartRemoteKeysDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(updates : List<ChartRemoteKeys>)

    @Query("SELECT * FROM chartRemoteKeys WHERE name = :name")
    suspend fun getChartRemoteKeys(name: String) : ChartRemoteKeys?

    @Query("DELETE FROM chartRemoteKeys")
    fun clearChartRemoteKeys()

}

@Dao
interface SArtistRemoteKeysDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(updates : List<ArtistRemoteKeys>)

    @Query("SELECT * FROM artistRemoteKeys WHERE name = :name")
    suspend fun getArtistRemoteKeys(name: String) : ArtistRemoteKeys?

    @Query("DELETE FROM artistRemoteKeys")
    fun clearArtistRemoteKeys()

}