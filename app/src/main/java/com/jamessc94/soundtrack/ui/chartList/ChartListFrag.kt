package com.jamessc94.soundtrack.ui.chartList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.databinding.FragChartlistPageBinding
import com.jamessc94.soundtrack.ui.chartList.adapter.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ChartListFrag : Fragment() {

    companion object {
        const val TYPE= "type"

        fun newInstance(type: String): ChartListFrag {
            val fragment = ChartListFrag().apply{

                arguments =  Bundle().apply {
                    putString(TYPE, type)
                }

            }

            return fragment
        }
    }

    val vm : ChartListFragVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragChartlistPageBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        when(arguments?.get(TYPE)){
            getString(R.string.tracks) -> {
                val adapter = ChartListTrackAdap(ChartListTrackAdapListener {

                })

                binding.fragChartlistPageRv.adapter = adapter.withLoadStateFooter(
                    ChartListLoadAdap { adapter.retry() })
                lifecycleScope.launch {
                    vm.fetchChart().collect { pagingData ->
                        binding.fragChartlistPagePb.visibility = View.GONE
                        adapter.submitData(pagingData)

                    }
                }

            }

            getString(R.string.artists) -> {
                val adapter = ChartListDataAdap(ChartListDataAdapListener {

                })

                binding.fragChartlistPageRv.adapter = adapter.withLoadStateFooter(
                    ChartListLoadAdap { adapter.retry() })
                lifecycleScope.launch {
                    vm.fetchArtist().collect { pagingData ->
                        binding.fragChartlistPagePb.visibility = View.GONE
                        adapter.submitData(pagingData)

                    }
                }

            }

        }

        return binding.root

    }
}