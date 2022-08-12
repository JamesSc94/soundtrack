package com.jamessc94.soundtrack.repo

import androidx.annotation.WorkerThread
import com.jamessc94.soundtrack.model.asDatabaseModel
import com.jamessc94.soundtrack.network.Network
import com.jamessc94.soundtrack.persistence.AlbumDAO
import com.jamessc94.soundtrack.persistence.MvideoDAO
import com.jamessc94.soundtrack.persistence.SArtistDetailsDAO
import com.jamessc94.soundtrack.persistence.TrackDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class AudioDBRepo @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val daoA: SArtistDetailsDAO,
    private val daoAl: AlbumDAO,
    private val daoT: TrackDAO,
    private val daoMV: MvideoDAO,
) {

    @WorkerThread
    fun fetchArtistDetails(
        name: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ) = flow {
        val artist = daoA.getSArtistDetails(name = name)

        daoMV.clearMvideo()

        artist?.let {
            emit(artist)

        } ?: run{
            Network.audioDB.fetchArtistDetails(name).apply {
                if(this.isSuccessful){
                    daoA.insertSArtistDetailsList(*this.body()!!.artists.asDatabaseModel())
                    emit(daoA.getSArtistDetails(name = name))

                }else{
                    onError(this.errorBody()!!.toString())

                }

                emit(artist)

            }

        }

    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

    @WorkerThread
    fun fetchAlbum(
        aid: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ) = flow {
        val album = daoAl.getAlbum(aid = aid)

        if(album.isNotEmpty()){
            emit(album)

        }else{
            Network.audioDB.fetchAlbum(aid).apply {
                if(this.isSuccessful){
                    daoAl.insertAlbumList(*this.body()!!.album.asDatabaseModel(aid))
                    emit(daoAl.getAlbum(aid = aid))

                }else{
                    onError(this.errorBody()!!.toString())

                }

                emit(album)

            }

        }

    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

    @WorkerThread
    fun fetchTrack(
        aid: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ) = flow {
        val track = daoT.getTrack(aid = aid)

        if(track.isNotEmpty()){
            emit(track)

        }else{
            Network.audioDB.fetchTrack(aid).apply {
                if(this.isSuccessful){
                    this.body()!!.track?.let {
                        daoT.insertTrackList(*it.asDatabaseModel(aid))
                        emit(daoT.getTrack(aid = aid))

                    }

                }else{
                    onError(this.errorBody()!!.toString())

                }

                emit(track)

            }

        }

    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

    @WorkerThread
    fun fetchMV(
        aid: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit,
    ) = flow {
        val mv = daoMV.getMvideo(aid = aid)

        if(mv.isNotEmpty()){
            emit(mv)

        }else{
            Network.audioDB.fetchMV(aid).apply {
                if(this.isSuccessful){
                    daoMV.insertMvideoList(*this.body()!!.mvids.asDatabaseModel())
                    emit(daoMV.getMvideo(aid = aid))

                }else{
                    onError(this.errorBody()!!.toString())

                }

//                emit(mv)

            }

        }

    }.onStart{ onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

}