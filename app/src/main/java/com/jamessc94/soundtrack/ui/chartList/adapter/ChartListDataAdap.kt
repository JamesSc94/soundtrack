package com.jamessc94.soundtrack.ui.chartList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jamessc94.soundtrack.databinding.CellChartlistDataBinding
import com.jamessc94.soundtrack.model.SArtist

class ChartListDataAdap(val clickListener: ChartListDataAdapListener) : PagingDataAdapter<SArtist, RecyclerView.ViewHolder>(diffUtil) {

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

    class ViewHolder private constructor(val binding: CellChartlistDataBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: ChartListDataAdapListener, item: SArtist, pos: Int){
            binding.seq = pos + 1
            binding.data = item
            binding.click = clickListener

            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(CellChartlistDataBinding.inflate(LayoutInflater.from(parent.context), parent, false))

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

class ChartListDataAdapListener(val clickListener: (item: SArtist) -> Unit) {
    fun onClick(item: SArtist) = clickListener(item)

}