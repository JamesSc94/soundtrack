package com.jamessc94.soundtrack.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "album")
@JsonClass(generateAdapter = true)
data class Album(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id:String = "",

    @ColumnInfo(name = "aid")
    var aid:String = "",

    @ColumnInfo(name = "name")
    var name:String = "",

    @ColumnInfo(name = "company")
    var company:String = "",

    @ColumnInfo(name = "released_year")
    var released_year:String = "",

    @ColumnInfo(name = "style")
    var style:String = "",

    @ColumnInfo(name = "description")
    var description:String = "",

    @ColumnInfo(name = "thumb")
    var thumb:String = ""

)

data class AlbumAdapM(
    var id:String = "",
    var name:String = "",
    var company:String = "",
    var released_year:String = "",
    var style:String = "",
    var description:String = "",
    var thumb:String = ""

)

@JsonClass(generateAdapter = true)
data class AlbumResponse(val album : List<AlbumResponseAlbum>) {

    data class AlbumResponseAlbum(
        var idAlbum: String,
        var strAlbum: String,
        var intYearReleased: String,
        var strStyle: String?,
        var strLabel: String?,
        var strAlbumThumb: String,
        var strDescriptionEN: String?,
    )

}

fun List<AlbumResponse.AlbumResponseAlbum>.asDatabaseModel(aid: String): Array<Album> {
    return this.map {
        Album(
            id = it.idAlbum,
            aid = aid,
            name = it.strAlbum,
            company = it.strLabel?:"",
            released_year = it.intYearReleased,
            style = it.strStyle?:"",
            description = it.strDescriptionEN?:"",
            thumb = it.strAlbumThumb
        )
    }.toTypedArray()

}

fun List<Album>.asAdapter(): Array<AlbumAdapM> {
    return this.map {
        AlbumAdapM(
            id = it.id,
            name = it.name,
            company = it.company,
            released_year = it.released_year,
            style = it.style,
            description = it.description,
            thumb = it.thumb
        )
    }.toTypedArray()

}