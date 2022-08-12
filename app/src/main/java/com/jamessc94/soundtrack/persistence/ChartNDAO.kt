package com.jamessc94.soundtrack.persistence

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jamessc94.soundtrack.model.ChartN
import com.jamessc94.soundtrack.model.ChartRemoteKeys

@Dao
interface ChartNDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChartNList(vararg updates : ChartN)

    @Query("SELECT * FROM chartn")
    fun getChartNLD() : LiveData<List<ChartN>>

    @Query("SELECT * FROM chartn")
    fun getChartN() : PagingSource<Int, ChartN>

}

@Dao
interface ChartNRemoteKeysDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(updates : List<ChartRemoteKeys>)

    @Query("SELECT * FROM chartRemoteKeys WHERE name = :name")
    suspend fun getChartRemoteKeys(name: String) : ChartRemoteKeys?

    @Query("DELETE FROM chartRemoteKeys")
    fun clearChartRemoteKeys()

}