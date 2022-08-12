package com.jamessc94.soundtrack.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.jamessc94.soundtrack.ArtworkSize
import com.jamessc94.soundtrack.util.UtilMedia.getAlbumArtUri
import timber.log.Timber

const val CROSS_FADE_DIRATION = 400

object ImageviewBinding {

    @JvmStatic
    @BindingAdapter("glideDirect")
    fun bindGlideDirect(view : ImageView, url : String){
        Glide.with(view.context).load(url).into(view)

    }

    @JvmStatic
    @BindingAdapter("glideCircular")
    fun bindGlideCircular(view : ImageView, url : String){
        Glide.with(view.context).load(url).apply(RequestOptions.circleCropTransform()).into(view)
//        Glide.with(view.context).load(R.drawable.profile_default).apply(RequestOptions.circleCropTransform()).into(view)

    }

    @JvmStatic
    @BindingAdapter("albumArtist", "albumName", "artworkSize", "albumId", requireAll = true)
    fun setLastFmAlbumImage(
        view: ImageView,
        albumArtist: String?,
        albumName: String?,
        artworkSize: ArtworkSize,
        albumId: Long?
    ) {

        if (albumArtist == null || albumName == null || albumId == null) return

        Glide.with(view).load(getAlbumArtUri(albumId))
            .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DIRATION)).into(view)

    }

}