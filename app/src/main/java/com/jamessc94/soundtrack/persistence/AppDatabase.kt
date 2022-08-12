package com.jamessc94.soundtrack.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jamessc94.soundtrack.model.*

@Database(entities = [SChart::class, SArtist::class, SArtistDetails::class, Album::class, Track::class,
    Mvideo::class, ChartN::class, ChartRemoteKeys::class, ArtistRemoteKeys::class,
    ChartNRemoteKeys::class, QueueEntity::class, SongEntity::class], version = 13, exportSchema = true)

abstract class AppDatabase : RoomDatabase() {

    abstract fun schartDAO(): SChartDAO
    abstract fun sartistDAO(): SArtistDAO
    abstract fun sartistDetailsDAO() : SArtistDetailsDAO
    abstract fun albumDAO(): AlbumDAO
    abstract fun trackDAO(): TrackDAO
    abstract fun mvideoDAO(): MvideoDAO
    abstract fun chartnDAO(): ChartNDAO
    abstract fun schartRemoteKeysDAO(): SChartRemoteKeysDAO
    abstract fun sartistRemoteKeysDAO(): SArtistRemoteKeysDAO
    abstract fun chartnRemoteDAO(): ChartNRemoteKeysDAO
    abstract fun queueEntityDao(): QueueDao

    companion object {
        private var INSTANCE :AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase {
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "soundtrack_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance

                }

                return instance

            }

        }

    }

}