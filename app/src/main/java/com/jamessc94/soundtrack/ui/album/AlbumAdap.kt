package com.jamessc94.soundtrack.ui.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jamessc94.soundtrack.databinding.CellAlbumBinding
import com.jamessc94.soundtrack.model.AlbumAdapM

class AlbumAdap(val clickListener: AlbumAdapListener) : ListAdapter<AlbumAdapM, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(pos)
                holder.bind(clickListener, item!!)
            }

        }
    }

    class ViewHolder private constructor(val binding: CellAlbumBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: AlbumAdapListener, item: AlbumAdapM){
            binding.data = item
            binding.click = clickListener

            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(CellAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            }

        }

    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<AlbumAdapM>() {

            override fun areItemsTheSame(oldItem: AlbumAdapM, newItem: AlbumAdapM): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: AlbumAdapM, newItem: AlbumAdapM): Boolean =
                oldItem == newItem
        }
    }

}

class AlbumAdapListener(val clickListener: (item: AlbumAdapM) -> Unit) {
    fun onClick(item: AlbumAdapM) = clickListener(item)

}