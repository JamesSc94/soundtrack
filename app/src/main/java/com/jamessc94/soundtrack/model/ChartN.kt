package com.jamessc94.soundtrack.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "chartn")
@JsonClass(generateAdapter = true)
data class ChartN(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id:String = "",

    @ColumnInfo(name = "artistId")
    var artistId:String = "",

    @ColumnInfo(name = "albumId")
    var albumId:String = "",

    @ColumnInfo(name = "name")
    var name:String = "",

    @ColumnInfo(name = "artistName")
    var artistName:String = "",

    @ColumnInfo(name = "albumName")
    var albumName:String = "",

    @ColumnInfo(name = "previewURL")
    var previewURL:String = "",

    @ColumnInfo(name = "playbackSeconds")
    var playbackSeconds:Int = 0,

    @ColumnInfo(name = "isStreamable")
    var isStreamable:Boolean = false

)

@Entity(tableName = "chartnRemoteKeys")
data class ChartNRemoteKeys(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)

@JsonClass(generateAdapter = true)
data class ChartNResponse(val tracks : List<ChartNResponseTracks>) {

    data class ChartNResponseTracks(
        var id: String,
        var artistId: String,
        var albumId: String,
        var name: String,
        var artistName: String,
        var albumName: String,
        var playbackSeconds: Int,
        var isStreamable: Boolean,
        var previewURL: String,
    )

}

fun List<ChartNResponse.ChartNResponseTracks>.asDatabaseModel(): Array<ChartN> {
    return this.map {
        ChartN(
            id = it.id,
            artistId = it.artistId,
            albumId = it.albumId,
            name = it.name,
            artistName = it.artistName,
            albumName = it.albumName,
            playbackSeconds = it.playbackSeconds,
            isStreamable = it.isStreamable,
            previewURL = it.previewURL,
        )
    }.toTypedArray()

}