package com.jamessc94.soundtrack.ui.stream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jamessc94.soundtrack.databinding.FragChartnBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StreamFrag : Fragment() {

    val vm : StreamVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragChartnBinding.inflate(inflater, container, false)

        return binding.root

    }

}