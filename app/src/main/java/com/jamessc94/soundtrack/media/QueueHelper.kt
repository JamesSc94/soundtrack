package com.jamessc94.soundtrack.media

import com.jamessc94.soundtrack.media.ext.equalsBy
import com.jamessc94.soundtrack.media.ext.toSongEntityList
import com.jamessc94.soundtrack.model.QueueEntity
import com.jamessc94.soundtrack.persistence.QueueDao
import com.jamessc94.soundtrack.repo.MediaRepo
import javax.inject.Singleton

interface QueueHelper {

    fun updateQueueSongs(
        queueSongs: LongArray?,
        currentSongId: Long?
    )

    fun updateQueueData(queueData: QueueEntity)
}

@Singleton
class RealQueueHelper(
    private val queueDao: QueueDao,
    private val songsRepository: MediaRepo
) : QueueHelper {

    override fun updateQueueSongs(queueSongs: LongArray?, currentSongId: Long?) {
        if (queueSongs == null || currentSongId == null) {
            return
        }

        val currentList = queueDao.getQueueSongsSync()
        val songListToSave = queueSongs.toSongEntityList(songsRepository)

        val listsEqual = currentList.equalsBy(songListToSave) { left, right ->
            left.id == right.id
        }

        if (queueSongs.isNotEmpty() && !listsEqual) {
            queueDao.clearQueueSongs()
            queueDao.insertAllSongs(songListToSave)
            setCurrentSongId(currentSongId)

        } else {
            setCurrentSongId(currentSongId)

        }
    }

    override fun updateQueueData(queueData: QueueEntity) = queueDao.insert(queueData)

    private fun setCurrentSongId(id: Long) = queueDao.setCurrentId(id)
}