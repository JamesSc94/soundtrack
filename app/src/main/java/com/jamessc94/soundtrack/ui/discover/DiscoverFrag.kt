package com.jamessc94.soundtrack.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.databinding.FragDiscoverBinding
import com.jamessc94.soundtrack.ui.dashboard.DashboardFragDirections
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DiscoverFrag : Fragment() {

    companion object {

        fun newInstance(): DiscoverFrag {
            return DiscoverFrag()
        }
    }

    val vm : DiscoverVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragDiscoverBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm
        binding.adapterAdsVp = DiscoverAdsAdap(activity!!)
        binding.adapter = DiscoverAdap(DiscoverAdapListener {
            findNavController().navigate(DashboardFragDirections.actionDashboardFragToArtistFrag(it.name))

        })

        return binding.root

    }

}