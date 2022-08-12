package com.jamessc94.soundtrack.ui.chartList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jamessc94.soundtrack.databinding.CellChartTrackBinding
import com.jamessc94.soundtrack.model.SChart

class ChartListTrackAdap(val clickListener: ChartListTrackAdapListener) : PagingDataAdapter<SChart, RecyclerView.ViewHolder>(diffUtil) {

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

    class ViewHolder private constructor(val binding: CellChartTrackBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: ChartListTrackAdapListener, item: SChart, pos: Int){
            binding.seq = pos + 1
            binding.data = item
            binding.click = clickListener

            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(CellChartTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            }

        }

    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<SChart>() {

            override fun areItemsTheSame(oldItem: SChart, newItem: SChart): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: SChart, newItem: SChart): Boolean =
                oldItem == newItem
        }
    }

}

class ChartListTrackAdapListener(val clickListener: (item: SChart) -> Unit) {
    fun onClick(item: SChart) = clickListener(item)

}