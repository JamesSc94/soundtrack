package com.jamessc94.soundtrack.ui.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jamessc94.soundtrack.databinding.CellDiscoverBinding
import com.jamessc94.soundtrack.model.SArtist

class DiscoverAdap(val clickListener: DiscoverAdapListener) : ListAdapter<SArtist, RecyclerView.ViewHolder>(diffUtil) {

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

    class ViewHolder private constructor(val binding: CellDiscoverBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: DiscoverAdapListener, item: SArtist){
            binding.data = item
            binding.click = clickListener

            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(CellDiscoverBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            }

        }

    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<SArtist>() {

            override fun areItemsTheSame(oldItem: SArtist, newItem: SArtist): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: SArtist, newItem: SArtist): Boolean =
                oldItem == newItem
        }
    }

}

class DiscoverAdapListener(val clickListener: (item: SArtist) -> Unit) {
    fun onClick(item: SArtist) = clickListener(item)

}