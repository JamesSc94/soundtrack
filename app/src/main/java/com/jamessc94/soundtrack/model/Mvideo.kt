package com.jamessc94.soundtrack.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "mvideo")
@JsonClass(generateAdapter = true)
data class Mvideo(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id:String = "",

    @ColumnInfo(name = "artist_id")
    var artist_id:String = "",

    @ColumnInfo(name = "album_id")
    var album_id:String = "",

    @ColumnInfo(name = "name")
    var name:String = "",

    @ColumnInfo(name = "vid")
    var vid:String = "",

    @ColumnInfo(name = "thumb")
    var thumb:String = "",

)

data class MvideoAdapM(

    var id:String = "",
    var artist_id:String = "",
    var album_id:String = "",
    var name:String = "",
    var vid:String = "",
    var thumb:String = ""
)

@JsonClass(generateAdapter = true)
data class MvideoResponse(val mvids : List<MvideoResponseMvids>) {

    data class MvideoResponseMvids(
        var idTrack: String,
        var idArtist: String,
        var idAlbum: String,
        var strTrack: String,
        var strTrackThumb: String?,
        var strMusicVid: String)

}

fun List<MvideoResponse.MvideoResponseMvids>.asDatabaseModel(): Array<Mvideo> {
    return this.map {
        Mvideo(
            id = it.idTrack,
            artist_id = it.idArtist,
            album_id = it.idAlbum,
            name = it.strTrack,
            vid = it.strMusicVid,
            thumb = it.strTrackThumb?: "",
        )
    }.toTypedArray()

}

fun List<Mvideo>.asAdapter(): Array<MvideoAdapM> {
    return this.map {
        MvideoAdapM(
            id = it.id,
            artist_id = it.artist_id,
            album_id = it.album_id,
            name = it.name,
            vid = it.vid,
            thumb = it.thumb,
        )
    }.toTypedArray()

}