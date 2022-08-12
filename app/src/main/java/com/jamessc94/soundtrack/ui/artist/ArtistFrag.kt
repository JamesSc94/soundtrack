package com.jamessc94.soundtrack.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.databinding.FragArtistBinding
import com.jamessc94.soundtrack.model.SArtistDetailsAdap
import com.jamessc94.soundtrack.ui.dashboard.DashboardFragDirections
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ArtistFrag : Fragment() {

    val vm : ArtistVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragArtistBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        vm.artistDetailsFlow.observe(this, Observer {
            it?.let {
                vm.aid = it.id
                binding.fragArtistName.text = it.name
                Glide.with(context!!).load(it.thumb).into(binding.fragArtistProfile)

                vm.adapter.submitList(listOf(
                    SArtistDetailsAdap(resources.getString(R.string.style), it.style),
                    SArtistDetailsAdap(resources.getString(R.string.company), it.company),
                    SArtistDetailsAdap(resources.getString(R.string.form_year), it.form_year),
                    SArtistDetailsAdap(resources.getString(R.string.biography), it.biography),

                ))

            }


        })

        binding.fragArtistMv.setOnClickListener {
            if(vm.aid.isNotEmpty()) {
                findNavController().navigate(ArtistFragDirections.actionArtistFragToMvListFrag(vm.aid))

            }

        }

        binding.fragArtistAlbum.setOnClickListener {
            if(vm.aid.isNotEmpty()) {
                findNavController().navigate(ArtistFragDirections.actionArtistFragToAlbumFrag(vm.aid))

            }

        }

        return binding.root

    }

}