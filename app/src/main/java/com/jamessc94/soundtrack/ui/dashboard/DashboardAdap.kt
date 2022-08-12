package com.jamessc94.soundtrack.ui.dashboard

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.ui.chartn.ChartNFrag
import com.jamessc94.soundtrack.ui.discover.DiscoverFrag
import com.jamessc94.soundtrack.ui.local.LocalFrag
import dagger.hilt.android.qualifiers.ActivityContext

class DashboardAdap constructor(
    @ActivityContext ctx : Context,
) : FragmentStateAdapter(ctx as AppCompatActivity) {

    private val typePage : Array<String> by lazy {
        ctx.resources.getStringArray(R.array.dashboard)

    }

    override fun getItemCount(): Int = typePage.size

    override fun createFragment(pos: Int): Fragment {
        return when(pos){
            0 -> DiscoverFrag.newInstance()
            1 -> ChartNFrag()
            2 -> LocalFrag()
            else -> Fragment()

        }

    }

}