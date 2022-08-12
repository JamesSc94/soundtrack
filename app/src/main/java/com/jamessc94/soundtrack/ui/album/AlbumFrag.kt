package com.jamessc94.soundtrack.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jamessc94.soundtrack.databinding.FragAlbumBinding
import com.jamessc94.soundtrack.model.asAdapter
import com.jamessc94.soundtrack.ui.artist.ArtistFragDirections
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AlbumFrag : Fragment() {

    val vm : AlbumVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragAlbumBinding.inflate(inflater, container, false)

        val adapter = AlbumAdap(AlbumAdapListener {
            findNavController().navigate(AlbumFragDirections.actionAlbumFragToTrackFrag(it.id))

        })

        binding.lifecycleOwner = this
        binding.vm = vm
        binding.adapter = adapter

        vm.albumFlow.observe(this, Observer {
            it?.let {
                adapter.submitList(it.asAdapter().toMutableList())

            }


        })

        return binding.root

    }

}