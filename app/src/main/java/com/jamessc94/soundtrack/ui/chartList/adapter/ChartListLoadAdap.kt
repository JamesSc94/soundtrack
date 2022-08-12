package com.jamessc94.soundtrack.ui.chartList.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jamessc94.soundtrack.databinding.CellChartPagingFooterBinding
import com.jamessc94.soundtrack.databinding.CellChartlistLoadBinding

class ChartListLoadAdap(val retry: () -> Unit) : LoadStateAdapter<ChartListLoadAdap.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(retry, loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: CellChartlistLoadBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(retry: () -> Unit, loadState: LoadState){
            binding.show = if(loadState is LoadState.Loading) {
                View.VISIBLE} else {
                View.GONE}

            binding.executePendingBindings()

            binding.cellChartlistLoadRetry.setOnClickListener {
                retry()

            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(CellChartlistLoadBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            }

        }

    }
}