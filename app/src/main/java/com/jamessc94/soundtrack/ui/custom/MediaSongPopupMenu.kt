package com.jamessc94.soundtrack.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.PopupMenu
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.media.callbacks.PopupMenuListener
import com.jamessc94.soundtrack.model.Songs

class MediaSongPopupMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    private var popupMenuListener: PopupMenuListener? = null
    private var adapterSong: () -> Songs? = {
        null
    }

    //specific for playlist song, need to show remove from playlist
    var playlistId: Long = -1

    init {
        setImageResource(R.drawable.ic_more_vert)
        setOnClickListener {
            val popupMenu = PopupMenu(context, this)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popup_song_play -> popupMenuListener?.play(adapterSong()!!)
                    R.id.popup_song_goto_album -> popupMenuListener?.goToAlbum(adapterSong()!!)
                    R.id.popup_song_goto_artist -> popupMenuListener?.goToArtist(adapterSong()!!)
                    R.id.popup_song_play_next -> popupMenuListener?.playNext(adapterSong()!!)
                    R.id.popup_song_addto_playlist -> popupMenuListener?.addToPlaylist(context, adapterSong()!!)
                    R.id.popup_song_delete -> popupMenuListener?.deleteSong(context, adapterSong()!!)
                    R.id.popup_song_remove_playlist -> popupMenuListener?.removeFromPlaylist(adapterSong()!!, playlistId)
                }
                true
            }

            popupMenu.inflate(R.menu.menu_popup_song)

            if (playlistId.toInt() != -1)
                popupMenu.menu.findItem(R.id.popup_song_remove_playlist).isVisible = true

            popupMenu.show()
        }
    }

    fun setupMenu(listener: PopupMenuListener?, adapterSong: () -> Songs) {
        this.popupMenuListener = listener
        this.adapterSong = adapterSong
    }
}