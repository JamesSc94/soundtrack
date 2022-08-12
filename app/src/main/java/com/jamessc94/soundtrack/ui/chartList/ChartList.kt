package com.jamessc94.soundtrack.ui.chartList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jamessc94.soundtrack.databinding.FragChartlistBinding
import com.jamessc94.soundtrack.ui.chartList.adapter.ChartListAdap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChartList : Fragment() {

    val vm : ChartListVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragChartlistBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm
        binding.adapterVp = ChartListAdap(activity!!)

        return binding.root

    }

}