package com.jamessc94.soundtrack.binding

import androidx.databinding.BindingAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

object ViewPagerBinding {

    @JvmStatic
    @BindingAdapter("tab", "adapter", "list")
    fun bindAdapter(view : ViewPager2, tab: TabLayout, adapter: FragmentStateAdapter, list: List<String>){
        view.adapter = adapter

        TabLayoutMediator(tab, view){ t, pos ->
            t.text = list[pos]

        }.attach()

    }

    @JvmStatic
    @BindingAdapter("tab", "adapter")
    fun bindAdapterW(view : ViewPager2, tab: TabLayout, adapter: FragmentStateAdapter){
        view.adapter = adapter

        TabLayoutMediator(tab, view){ _, _ ->

        }.attach()

    }

}