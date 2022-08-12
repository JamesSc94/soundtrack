package com.jamessc94.soundtrack.ui.track

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jamessc94.soundtrack.databinding.CellTrackBinding
import com.jamessc94.soundtrack.model.Track

class TrackAdap(val clickListener: TrackAdapListener) : ListAdapter<Track, RecyclerView.ViewHolder>(diffUtil) {

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

    class ViewHolder private constructor(val binding: CellTrackBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: TrackAdapListener, item: Track){
            binding.data = item
            binding.click = clickListener

            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(CellTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            }

        }

    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Track>() {

            override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean =
                oldItem == newItem
        }
    }

}

class TrackAdapListener(val clickListener: (item: Track) -> Unit) {
    fun onClick(item: Track) = clickListener(item)

}