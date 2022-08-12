package com.jamessc94.soundtrack.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.provider.Settings
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatTextView
import com.jamessc94.soundtrack.util.UtilMedia

class MediaProgressTv @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    private var mMediaController: MediaControllerCompat? = null
    private var mControllerCallback: MediaProgressTv.ControllerCallback? = null

    private var duration: Int = 0

    private var mProgressAnimator: ValueAnimator? = null

    //get the global duration scale for animators, user may chane the duration scale from developer options
    //need to make sure our value animator doesn't change the duration scale
    private val mDurationScale = Settings.Global.getFloat(context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE, 1f)

    fun setMediaController(mediaController: MediaControllerCompat?) {
        if (mediaController != null) {
            mControllerCallback = ControllerCallback()
            mediaController.registerCallback(mControllerCallback!!)
            mControllerCallback!!.onMetadataChanged(mediaController.metadata)
            mControllerCallback!!.onPlaybackStateChanged(mediaController.playbackState)
        } else if (mMediaController != null) {
            mMediaController!!.unregisterCallback(mControllerCallback!!)
            mControllerCallback = null
        }
        mMediaController = mediaController
    }

    fun disconnectController() {
        if (mMediaController != null) {
            mMediaController!!.unregisterCallback(mControllerCallback!!)
            mControllerCallback = null
            mMediaController = null
        }
    }

    private inner class ControllerCallback : MediaControllerCompat.Callback(), ValueAnimator.AnimatorUpdateListener {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            state ?: return

            // If there's an ongoing animation, stop it now.
            if (mProgressAnimator != null) {
                mProgressAnimator!!.cancel()
                mProgressAnimator = null
            }

            val progress = state.position.toInt()

            text = UtilMedia.makeShortTimeString(context, (progress / 1000).toLong())

            if (state.state == PlaybackStateCompat.STATE_PLAYING) {
                val timeToEnd = ((duration - progress) / state.playbackSpeed).toInt()

                if (timeToEnd > 0) {
                    mProgressAnimator?.cancel()
                    mProgressAnimator = ValueAnimator.ofInt(progress, duration)
                        .setDuration((timeToEnd / mDurationScale).toLong())
                    mProgressAnimator!!.interpolator = LinearInterpolator()
                    mProgressAnimator!!.addUpdateListener(this)
                    mProgressAnimator!!.start()
                }
            } else {

                text = UtilMedia.makeShortTimeString(context, state.position / 1000)
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)

            val max = metadata?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.toInt() ?: 0
            duration = max
            onPlaybackStateChanged(mMediaController?.playbackState)
        }

        override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
            val animatedIntValue = valueAnimator.animatedValue as Int
            text = UtilMedia.makeShortTimeString(context, (animatedIntValue / 1000).toLong())
        }
    }
}