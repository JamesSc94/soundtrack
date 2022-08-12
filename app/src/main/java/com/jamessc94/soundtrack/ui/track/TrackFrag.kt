package com.jamessc94.soundtrack.ui.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jamessc94.soundtrack.databinding.FragTrackBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackFrag : Fragment() {

    val vm : TrackVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragTrackBinding.inflate(inflater, container, false)

        val adapter = TrackAdap(TrackAdapListener {

        })

        binding.lifecycleOwner = this
        binding.vm = vm
        binding.adapter = adapter

        vm.trackFlow.observe(this, Observer {
            it?.let {
                adapter.submitList(it.toMutableList())

            }


        })

        return binding.root

    }

}