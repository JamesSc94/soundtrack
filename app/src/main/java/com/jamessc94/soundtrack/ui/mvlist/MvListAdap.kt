package com.jamessc94.soundtrack.ui.mvlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jamessc94.soundtrack.databinding.CellMvListBinding
import com.jamessc94.soundtrack.model.MvideoAdapM

class MvListAdap(val clickListener: MvListAdapListener) : ListAdapter<MvideoAdapM, RecyclerView.ViewHolder>(diffUtil) {

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

    class ViewHolder private constructor(val binding: CellMvListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: MvListAdapListener, item: MvideoAdapM){
            binding.data = item
            binding.click = clickListener

            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(CellMvListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            }

        }

    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<MvideoAdapM>() {

            override fun areItemsTheSame(oldItem: MvideoAdapM, newItem: MvideoAdapM): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MvideoAdapM, newItem: MvideoAdapM): Boolean =
                oldItem == newItem
        }
    }

}

class MvListAdapListener(val clickListener: (item: MvideoAdapM) -> Unit) {
    fun onClick(item: MvideoAdapM) = clickListener(item)

}