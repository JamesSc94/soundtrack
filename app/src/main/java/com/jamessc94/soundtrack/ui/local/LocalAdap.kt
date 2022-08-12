package com.jamessc94.soundtrack.ui.local

import android.support.v4.media.session.PlaybackStateCompat
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.databinding.CellLocalHeaderBinding
import com.jamessc94.soundtrack.databinding.CellLocalSongBinding
import com.jamessc94.soundtrack.media.callbacks.PopupMenuListener
import com.jamessc94.soundtrack.media.callbacks.SortMenuListener
import com.jamessc94.soundtrack.media.ext.*
import com.jamessc94.soundtrack.model.Songs
import com.jamessc94.soundtrack.ui.main.NowPlayingVM
import timber.log.Timber

private const val PLAYLIST_ID_NOT_IN_PLAYLIST = -1L
private const val TYPE_SONG_HEADER = 0
private const val TYPE_SONG_ITEM = 1
private const val INVALID_POSITION = -1

class LocalAdap(val lifecycleOwner: LifecycleOwner,
                nowPlayingViewModel : NowPlayingVM,
                val songsAdapListener : SongsAdapListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var songs: List<Songs> = emptyList()
    var showHeader = false
    var isQueue = false

    var popupMenuListener: PopupMenuListener? = null
    var sortMenuListener: SortMenuListener? = null

    var playlistId: Long = PLAYLIST_ID_NOT_IN_PLAYLIST
    private var nowPlayingPosition = INVALID_POSITION

    init {
        // attach observer for updating now playing indicator on songs
        nowPlayingViewModel.currentData.observe(lifecycleOwner) {
            val previousPlayingPosition = nowPlayingPosition

            if (!it.mediaId.isNullOrEmpty()
                && it.state == PlaybackStateCompat.STATE_PLAYING) {
                nowPlayingPosition = getPositionForSong(it.mediaId!!.toLong())
            } else {
                nowPlayingPosition = INVALID_POSITION
            }

            // reset playing indicator on previous playing position
            if (previousPlayingPosition != INVALID_POSITION)
                notifyItemChanged(previousPlayingPosition)

            // show playing indicator on now playing position
            if (nowPlayingPosition != INVALID_POSITION)
                notifyItemChanged(nowPlayingPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SONG_HEADER -> {
                val viewBinding = parent.inflateWithBinding<CellLocalHeaderBinding>(R.layout.cell_local_header)
                HeaderViewHolder(viewBinding, sortMenuListener)
            }
            TYPE_SONG_ITEM -> {
                val viewBinding = parent.inflateWithBinding<CellLocalSongBinding>(R.layout.cell_local_song)
                ViewHolder(viewBinding, popupMenuListener, playlistId, isQueue)
            }
            else -> {
                val viewBinding = parent.inflateWithBinding<CellLocalSongBinding>(R.layout.cell_local_song)
                ViewHolder(viewBinding, popupMenuListener)

            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_SONG_HEADER -> {
                (holder as HeaderViewHolder).bind(songs.size)
            }
            TYPE_SONG_ITEM -> {
                val song = songs[position + if (showHeader) -1 else 0]
                (holder as ViewHolder).bind(song, position, nowPlayingPosition, songsAdapListener)

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showHeader && position == 0) {
            TYPE_SONG_HEADER
        } else {
            TYPE_SONG_ITEM
        }
    }

    override fun getItemCount() = if (showHeader) {
        songs.size + 1
    } else {
        songs.size
    }

    class HeaderViewHolder constructor(var binding: CellLocalHeaderBinding, private val sortMenuListener: SortMenuListener?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(count: Int) {
            binding.songCount = count
            binding.executePendingBindings()

            binding.cellLocalHeaderShuffle.setOnClickListener { sortMenuListener?.shuffleAll() }
            binding.cellLocalHeaderSort.setupMenu(sortMenuListener)

        }
    }

    class ViewHolder constructor(
        private val binding: CellLocalSongBinding,
        private val popupMenuListener: PopupMenuListener?,
        private val playlistId: Long = -1,
        private val isQueue: Boolean = false
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Songs, pos: Int, nowPlayingPosition: Int, songsAdapListener : SongsAdapListener) {
            binding.song = song
            binding.cellLocalSongAlbumart.clipToOutline = true
            binding.executePendingBindings()

            binding.root.setOnClickListener {
                songsAdapListener.onClick(pos)

            }

            binding.cellLocalSongPopupmenu.run {
                playlistId = this@ViewHolder.playlistId
                setupMenu(popupMenuListener) { song }
            }

            if (bindingAdapterPosition == nowPlayingPosition) {
                binding.cellLocalSongVisualizer.show()
            } else binding.cellLocalSongVisualizer.hide()

            binding.cellLocalSongReorder.showOrHide(isQueue)
        }
    }

    fun updateData(songs: List<Songs>) {
        this.songs = songs
        notifyDataSetChanged()

    }

    fun reorderSong(from: Int, to: Int) {
        songs.moveElement(from, to)
        notifyItemMoved(from, to)

    }

    fun getSongForPosition(position: Int): Songs? {
        return if (showHeader) {
            if (position == 0) {
                null
            } else {
                songs[position - 1]
            }
        } else {
            songs[position]
        }
    }

    private fun getPositionForSong(songId: Long): Int {
        val songIndex = songs.indexOf(songs.find { it.id == songId })
        return if (showHeader) {
            songIndex + 1
        } else songIndex
    }
}

class SongsAdapListener(val clickListener: (pos: Int) -> Unit) {
    fun onClick(pos: Int) = clickListener(pos)

}