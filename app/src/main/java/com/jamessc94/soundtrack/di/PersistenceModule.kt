package com.jamessc94.soundtrack.di

import android.content.Context
import com.jamessc94.soundtrack.persistence.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideSoundtrackDatabase(@ApplicationContext context: Context) = AppDatabase.getInstance(context)

    @Provides
    fun provideChartDAO(appDatabase: AppDatabase): SChartDAO {
        return appDatabase.schartDAO()

    }

    @Provides
    fun provideArtistDAO(appDatabase: AppDatabase): SArtistDAO {
        return appDatabase.sartistDAO()

    }

    @Provides
    fun provideArtistDetailsDAO(appDatabase: AppDatabase): SArtistDetailsDAO {
        return appDatabase.sartistDetailsDAO()

    }

    @Provides
    fun provideAlbumDAO(appDatabase: AppDatabase): AlbumDAO {
        return appDatabase.albumDAO()

    }

    @Provides
    fun provideTrackDAO(appDatabase: AppDatabase): TrackDAO {
        return appDatabase.trackDAO()

    }

    @Provides
    fun provideMvideoDAO(appDatabase: AppDatabase): MvideoDAO {
        return appDatabase.mvideoDAO()

    }

    @Provides
    fun provideChartNDAO(appDatabase: AppDatabase): ChartNDAO {
        return appDatabase.chartnDAO()

    }

    @Provides
    fun provideChartRemoteKeysDAO(appDatabase: AppDatabase): SChartRemoteKeysDAO {
        return appDatabase.schartRemoteKeysDAO()

    }

    @Provides
    fun provideArtistRemoteKeysDAO(appDatabase: AppDatabase): SArtistRemoteKeysDAO {
        return appDatabase.sartistRemoteKeysDAO()

    }

    @Provides
    fun provideChartNRemoteKeysDAO(appDatabase: AppDatabase): ChartNRemoteKeysDAO {
        return appDatabase.chartnRemoteDAO()

    }

    @Provides
    fun provideQueueDAO(appDatabase: AppDatabase): QueueDao{
        return appDatabase.queueEntityDao()

    }
}