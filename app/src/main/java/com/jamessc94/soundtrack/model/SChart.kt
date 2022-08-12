package com.jamessc94.soundtrack.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass


@Entity(tableName = "chart")
@JsonClass(generateAdapter = true)
data class SChart(

    @PrimaryKey
    @ColumnInfo(name = "name")
    var name:String = "",

    @ColumnInfo(name = "playcount")
    var playcount:String = "",

    @ColumnInfo(name = "listeners")
    var listeners:String = "",

    @ColumnInfo(name = "url")
    var url:String = "",

    @ColumnInfo(name = "artist")
    var artist:String = "",

    @ColumnInfo(name = "image")
    var image:String = "",

)

@Entity(tableName = "artist")
@JsonClass(generateAdapter = true)
data class SArtist(

    @PrimaryKey
    @ColumnInfo(name = "name")
    var name:String = "",

    @ColumnInfo(name = "playcount")
    var playcount:String = "",

    @ColumnInfo(name = "listeners")
    var listeners:String = "",

    @ColumnInfo(name = "url")
    var url:String = "",

    @ColumnInfo(name = "image")
    var image:String = ""

)

@Entity(tableName = "artistdetails")
@JsonClass(generateAdapter = true)
data class SArtistDetails(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id:String = "",

    @ColumnInfo(name = "name")
    var name:String = "",

    @ColumnInfo(name = "company")
    var company:String = "",

    @ColumnInfo(name = "form_year")
    var form_year:String = "",

    @ColumnInfo(name = "style")
    var style:String = "",

    @ColumnInfo(name = "biography")
    var biography:String = "",

    @ColumnInfo(name = "thumb")
    var thumb:String = ""

)

data class SArtistDetailsAdap(
    var title:String = "",
    var content:String = "",

)

@Entity(tableName = "chartRemoteKeys")
data class ChartRemoteKeys(
    @PrimaryKey val name: String,
    val prevKey: Int?,
    val nextKey: Int?
)


@Entity(tableName = "artistRemoteKeys")
data class ArtistRemoteKeys(
    @PrimaryKey val name: String,
    val prevKey: Int?,
    val nextKey: Int?
)

////////////////////////////////////////////////////////////

@JsonClass(generateAdapter = true)
data class SChartListResponse(val tracks : SChartListResponseBodyTracks) {

    data class SChartListResponseBodyTracks(
        var track: List<SChartListResponseBodyTrack>

    )

    data class SChartListResponseBodyTrack(
        var name: String,
        var playcount: String,
        var listeners: String,
        var url: String,
        var artist: SChartListResponseBodyTrackArtist,
//        var image: List<SChartListResponseBodyTrackImage>?,

    )

    data class SChartListResponseBodyTrackArtist(
        var name: String,

    )

    data class SChartListResponseBodyTrackImage(
        var text: String,
        var size: String
    )

}

fun List<SChartListResponse.SChartListResponseBodyTrack>.asDatabaseModel(): Array<SChart> {
    return this.map {
        SChart(
            name = it.name,
            playcount = it.playcount,
            listeners = it.listeners,
            url = it.url,
            image = "",
        )

    }.toTypedArray()

}

@JsonClass(generateAdapter = true)
data class SArtistListResponse(val artists : SArtistListResponseBodyArtists) {

    data class SArtistListResponseBodyArtists(
        var artist: List<SArtistListResponseBodyArtist>

    )

    data class SArtistListResponseBodyArtist(
        var name: String,
        var playcount: String,
        var listeners: String,
        var url: String,
//        var image: List<SChartListResponseBodyTrackImage>?,

    )

    data class SChartListResponseBodyTrackImage(
        var text: String,
        var size: String
    )

}

fun List<SArtistListResponse.SArtistListResponseBodyArtist>.asDatabaseModel(): Array<SArtist> {
    return this.map {
        SArtist(
            name = it.name,
            playcount = it.playcount,
            listeners = it.listeners,
            url = it.url,
            image = "",
        )

    }.toTypedArray()

}

@JsonClass(generateAdapter = true)
data class SArtistDetailsResponse(val artists : List<SArtistDetailsResponseArtists>) {

    data class SArtistDetailsResponseArtists(
        var idArtist: String,
        var strArtist: String,
        var strLabel: String,
        var intFormedYear: String,
        var strStyle: String,
        var strBiographyEN: String,
        var strArtistThumb: String,

    )

}

fun List<SArtistDetailsResponse.SArtistDetailsResponseArtists>.asDatabaseModel(): Array<SArtistDetails> {
    return this.map {
        SArtistDetails(
            id = it.idArtist,
            name = it.strArtist,
            company = it.strLabel,
            form_year = it.intFormedYear,
            style = it.strStyle,
            biography = it.strBiographyEN,
            thumb = it.strArtistThumb
        )
    }.toTypedArray()

}