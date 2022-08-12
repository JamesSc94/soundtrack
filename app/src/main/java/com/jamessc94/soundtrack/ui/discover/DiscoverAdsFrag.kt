package com.jamessc94.soundtrack.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jamessc94.soundtrack.databinding.FragDiscoverAdsBinding
import com.jamessc94.soundtrack.ui.chartList.ChartListFragVM

class DiscoverAdsFrag : Fragment() {

    companion object {
        const val URLS = "urls"

        fun newInstance(url: String): DiscoverAdsFrag {
            val fragment = DiscoverAdsFrag().apply{

                arguments =  Bundle().apply {
                    putString(URLS, url)
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
        val binding = FragDiscoverAdsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        arguments?.getString(URLS).let {
            binding.url = it

        }

        return binding.root

    }
}