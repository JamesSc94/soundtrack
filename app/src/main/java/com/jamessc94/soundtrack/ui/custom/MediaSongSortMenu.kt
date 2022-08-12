package com.jamessc94.soundtrack.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.PopupMenu
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.media.callbacks.SortMenuListener

class MediaSongSortMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    private var sortMenuListener: SortMenuListener? = null

    init {
        setImageResource(R.drawable.ic_sort_black)
        setOnClickListener {
            val popupMenu = PopupMenu(context, this)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_sort_by_az -> sortMenuListener?.sortAZ()
                    R.id.menu_sort_by_za -> sortMenuListener?.sortZA()
                    R.id.menu_sort_by_year -> sortMenuListener?.sortYear()
                    R.id.menu_sort_by_duration -> sortMenuListener?.sortDuration()
                }
                true
            }
            popupMenu.inflate(R.menu.song_sort_by)
            popupMenu.show()
        }
    }

    fun setupMenu(listener: SortMenuListener?) {
        this.sortMenuListener = listener
    }

}