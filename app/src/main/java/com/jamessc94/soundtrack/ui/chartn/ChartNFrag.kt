package com.jamessc94.soundtrack.ui.chartn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.jamessc94.soundtrack.databinding.FragChartnBinding
import com.jamessc94.soundtrack.ui.chartList.adapter.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ChartNFrag : Fragment() {

    val vm : ChartNVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragChartnBinding.inflate(inflater, container, false)

//        val adapter = ChartNAdap(ChartNAdapListener {
//            if(it.isStreamable){
//
//
//            }
//
//        })


//        binding.fragChartnRv.adapter = adapter
//        binding.fragChartnPb.visibility = View.GONE

//        vm.temp.observe(this, Observer {
//            adapter.submitList(it)
//
//        })

        val adapter = ChartNAdap(ChartNAdapListener {

        })

        binding.fragChartnRv.setHasFixedSize(true)
//        binding.fragChartnRv.adapter = adapter
        val adapterwFooter = adapter.withLoadStateFooter(
            ChartNLoadAdap { adapter.retry() })
        binding.fragChartnRv.adapter = adapterwFooter

        lifecycleScope.launch {
            vm.fetchChart().collect { pagingData ->
                binding.fragChartnPb.visibility = View.GONE
                adapter.submitData(pagingData)

            }
        }

        return binding.root

    }

}