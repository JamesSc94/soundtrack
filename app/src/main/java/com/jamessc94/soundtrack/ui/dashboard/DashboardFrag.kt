package com.jamessc94.soundtrack.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jamessc94.soundtrack.databinding.FragDashboardBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DashboardFrag : Fragment() {

    val vm : DashboardVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragDashboardBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm
        binding.adapterVp = DashboardAdap(activity!!)

        return binding.root

    }


}