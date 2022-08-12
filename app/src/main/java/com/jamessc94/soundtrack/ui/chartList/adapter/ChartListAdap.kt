package com.jamessc94.soundtrack.ui.chartList.adapter

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.ui.chartList.ChartListFrag
import dagger.hilt.android.qualifiers.ActivityContext

class ChartListAdap constructor(
    @ActivityContext ctx : Context,
) : FragmentStateAdapter(ctx as AppCompatActivity) {

    private val typePage : Array<String> by lazy {
        ctx.resources.getStringArray(R.array.chart_type)

    }

    override fun getItemCount(): Int = typePage.size

    override fun createFragment(pos: Int): Fragment = ChartListFrag.newInstance(typePage[pos])

}