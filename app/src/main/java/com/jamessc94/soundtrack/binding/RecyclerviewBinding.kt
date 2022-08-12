package com.jamessc94.soundtrack.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.*

object RecyclerviewBinding {

    @JvmStatic
    @BindingAdapter("adapter")
    fun bindAdapter(view : RecyclerView, adapter: RecyclerView.Adapter<*>){
        view.adapter = adapter

    }

    @JvmStatic
    @BindingAdapter("gridlm")
    fun bindGridlm(view : RecyclerView, spanCount: Int){
        view.layoutManager = GridLayoutManager(view.context, spanCount)

    }

    @JvmStatic
    @BindingAdapter("deco")
    fun bindDeco(view : RecyclerView, b: Boolean){
        if(b){
            view.addItemDecoration(
                DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL)

            )

        }

    }

    @JvmStatic
    @BindingAdapter("submitList")
    fun bindSubmitList(view: RecyclerView, itemList: List<Any>?){
        (view.adapter as ListAdapter<Any, *>).let {
            it.submitList(itemList)

        }

    }

}