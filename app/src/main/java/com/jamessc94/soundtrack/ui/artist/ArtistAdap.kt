package com.jamessc94.soundtrack.ui.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jamessc94.soundtrack.databinding.CellArtistBinding
import com.jamessc94.soundtrack.model.SArtistDetailsAdap

class ArtistAdap : ListAdapter<SArtistDetailsAdap, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(pos)
                holder.bind(item!!)
            }

        }
    }

    class ViewHolder private constructor(val binding: CellArtistBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: SArtistDetailsAdap){
            binding.title = item.title
            binding.content = item.content

            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(CellArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            }

        }

    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<SArtistDetailsAdap>() {

            override fun areItemsTheSame(oldItem: SArtistDetailsAdap, newItem: SArtistDetailsAdap): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: SArtistDetailsAdap, newItem: SArtistDetailsAdap): Boolean =
                oldItem == newItem
        }
    }

}