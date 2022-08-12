package com.jamessc94.soundtrack.binding

import android.graphics.Bitmap
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.cast.framework.media.MediaUtils
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.util.UtilMedia
import com.jamessc94.soundtrack.util.UtilMedia.getAlbumArtUri
import com.jamessc94.soundtrack.util.UtilMedia.makeShortTimeString

val IMAGE_ROUND_CORNERS_TRANSFORMER: Transformation<Bitmap>
    get() = RoundedCorners(2)

val LARGE_IMAGE_ROUND_CORNERS_TRANSFORMER: Transformation<Bitmap>
    get() = RoundedCorners(5)

val EXTRA_LARGE_IMAGE_ROUND_CORNERS_TRANSFORMER: Transformation<Bitmap>
    get() = RoundedCorners(8)

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, albumId: Long) {
    val size = view.resources.getDimensionPixelSize(R.dimen.album_art)
    val options = RequestOptions()
        .centerCrop()
        .override(size, size)
        .transform(IMAGE_ROUND_CORNERS_TRANSFORMER)
        .placeholder(R.drawable.ic_music_note)
    Glide.with(view)
        .load(getAlbumArtUri(albumId))
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(options)
        .into(view)
}

@BindingAdapter("playState")
fun setPlayState(view: ImageView, state: Int) {
    if (state == PlaybackStateCompat.STATE_PLAYING) {
        view.setImageResource(android.R.drawable.ic_media_play)
    } else {
        view.setImageResource(android.R.drawable.ic_media_pause)
    }
}

@BindingAdapter("repeatMode")
fun setRepeatMode(view: ImageView, mode: Int) {
    when (mode) {
        PlaybackStateCompat.REPEAT_MODE_NONE -> view.setImageResource(R.drawable.ic_repeat_none)
        PlaybackStateCompat.REPEAT_MODE_ONE -> view.setImageResource(R.drawable.ic_repeat_one)
        PlaybackStateCompat.REPEAT_MODE_ALL -> view.setImageResource(R.drawable.ic_repeat_all)
        else -> view.setImageResource(R.drawable.ic_repeat_none)
    }
}

@BindingAdapter("shuffleMode")
fun setShuffleMode(view: ImageView, mode: Int) {
    when (mode) {
        PlaybackStateCompat.SHUFFLE_MODE_NONE -> view.setImageResource(R.drawable.ic_shuffle_none)
        PlaybackStateCompat.SHUFFLE_MODE_ALL -> view.setImageResource(R.drawable.ic_shuffle_all)
        else -> view.setImageResource(R.drawable.ic_shuffle_none)
    }
}

@BindingAdapter("duration")
fun setDuration(view: TextView, duration: Int) {
    view.text = UtilMedia.makeShortTimeString(view.context, duration.toLong() / 1000)
}