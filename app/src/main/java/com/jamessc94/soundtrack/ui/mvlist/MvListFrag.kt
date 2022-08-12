package com.jamessc94.soundtrack.ui.mvlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jamessc94.soundtrack.databinding.FragAlbumBinding
import com.jamessc94.soundtrack.databinding.FragMvListBinding
import com.jamessc94.soundtrack.model.asAdapter
import com.jamessc94.soundtrack.ui.album.AlbumAdap
import com.jamessc94.soundtrack.ui.album.AlbumAdapListener
import com.jamessc94.soundtrack.ui.album.AlbumFragDirections
import com.jamessc94.soundtrack.ui.album.AlbumVM
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MvListFrag : Fragment() {

    val vm : MvListVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragMvListBinding.inflate(inflater, container, false)

        val adapter = MvListAdap(MvListAdapListener {
            findNavController().navigate(MvListFragDirections.actionMvListFragToMvideoFrag(it.vid))

        })

        binding.lifecycleOwner = this
        binding.vm = vm
        binding.adapter = adapter

        vm.mvFlow.observe(this, Observer {
            it?.let {
                adapter.submitList(it.asAdapter().toMutableList())

            }


        })

        return binding.root

    }

}