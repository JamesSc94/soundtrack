package com.jamessc94.soundtrack.di

import com.jamessc94.soundtrack.media.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaInterfaceModule {

    @Singleton
    @Binds
    abstract fun bindMediaSessionInterface(realMediaSessionConnection: RealMediaSessionConnection): MediaSessionConnection

    @Singleton
    @Binds
    abstract fun bindNotificationInterface(notifications: RealNotifications): Notifications

    @Singleton
    @Binds
    abstract fun bindQueueHelperInterface(queueHelper: RealQueueHelper): QueueHelper

    @Singleton
    @Binds
    abstract fun bindSongPlayerInterface(realSongPlayer: RealSongPlayer): SongPlayer

}