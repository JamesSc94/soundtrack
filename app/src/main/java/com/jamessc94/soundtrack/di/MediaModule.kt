package com.jamessc94.soundtrack.di

import android.app.Application
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import com.jamessc94.soundtrack.media.*
import com.jamessc94.soundtrack.persistence.QueueDao
import com.jamessc94.soundtrack.repo.MediaRealRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideMediaSession(@ApplicationContext appContext: Context): RealMediaSessionConnection {
        return RealMediaSessionConnection(appContext, ComponentName(appContext, MediaServices::class.java))

    }

    @Singleton
    @Provides
    fun provideMediaTORepo(@ApplicationContext appContext: Context): MediaRealRepo{
        return MediaRealRepo(appContext.contentResolver)

    }

    @Singleton
    @Provides
    fun provideQueueHelper(queueDao: QueueDao, @ApplicationContext appContext: Context): RealQueueHelper {
        return RealQueueHelper(queueDao, MediaRealRepo(appContext.contentResolver))

    }

    @Singleton
    @Provides
    fun provideMusicPlayer(@ApplicationContext appContext: Context): RealMusicPlayer {
        return RealMusicPlayer(appContext as Application)

    }

    @Provides
    @Singleton
    fun provideSongPlayer(@ApplicationContext appContext: Context, mp : RealMusicPlayer,
                          repo: MediaRealRepo, dao: QueueDao, queue: RealQueue): RealSongPlayer {
        return RealSongPlayer(appContext as Application, mp, repo, dao, queue)

    }

    @Singleton
    @Provides
    fun provideQueue(@ApplicationContext appContext: Context,  repo: MediaRealRepo, dao: QueueDao): RealQueue {
        return RealQueue(appContext as Application, repo, dao)

    }

    @Provides
    @Singleton
    fun provideNotification(@ApplicationContext appContext: Context): RealNotifications {
        return RealNotifications(appContext as Application, ContextCompat.getSystemService(
            appContext,
            NotificationManager::class.java
        ) as NotificationManager
        )

    }

}