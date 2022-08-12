package com.jamessc94.soundtrack.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "track")
@JsonClass(generateAdapter = true)
data class Track(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id:String = "",

    @ColumnInfo(name = "aid")
    var aid:String = "",

    @ColumnInfo(name = "name")
    var name:String = "",

    @ColumnInfo(name = "duration")
    var duration:String = "",

)

@JsonClass(generateAdapter = true)
data class TrackResponse(val track : List<TrackResponseTrack>?) {

    data class TrackResponseTrack(
        var idTrack: String,
        var strTrack: String,
        var intDuration: String,

    )

}

fun List<TrackResponse.TrackResponseTrack>.asDatabaseModel(aid: String): Array<Track> {
    return this.map {
        Track(
            id = it.idTrack,
            aid = aid,
            name = it.strTrack,
            duration = it.intDuration,
        )
    }.toTypedArray()

}