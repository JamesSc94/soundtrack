package com.jamessc94.soundtrack.ui.chartn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jamessc94.soundtrack.databinding.CellChartnBinding
import com.jamessc94.soundtrack.model.ChartN

class ChartNAdap(val clickListener: ChartNAdapListener) : PagingDataAdapter<ChartN, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = getItem(pos)
                holder.bind(clickListener, item!!, pos)
            }

        }
    }

    class ViewHolder private constructor(val binding: CellChartnBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: ChartNAdapListener, item: ChartN, pos: Int){
            binding.seq = pos + 1
            binding.data = item
            binding.click = clickListener

            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(CellChartnBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            }

        }

    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ChartN>() {

            override fun areItemsTheSame(oldItem: ChartN, newItem: ChartN): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: ChartN, newItem: ChartN): Boolean =
                oldItem == newItem

        }
    }

}

class ChartNAdapListener(val clickListener: (item: ChartN) -> Unit) {
    fun onClick(item: ChartN) = clickListener(item)

}