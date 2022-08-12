package com.jamessc94.soundtrack.ui.discover

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jamessc94.soundtrack.R
import dagger.hilt.android.qualifiers.ActivityContext

class DiscoverAdsAdap constructor(
    @ActivityContext ctx : Context,
) : FragmentStateAdapter(ctx as AppCompatActivity) {

    private val typePage : Array<String> by lazy {
        ctx.resources.getStringArray(R.array.dashboard)

    }

    override fun getItemCount(): Int = typePage.size

    override fun createFragment(pos: Int): Fragment = DiscoverAdsFrag.newInstance("")

}