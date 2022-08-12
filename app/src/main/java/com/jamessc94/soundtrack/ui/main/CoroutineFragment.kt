package com.jamessc94.soundtrack.ui.main

import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class CoroutineFragment : Fragment(){

    protected val mainDispatcher: CoroutineDispatcher get() = Dispatchers.Main

    private val job = Job()
    protected val scope = CoroutineScope(job + mainDispatcher)

    protected fun launch(
        context: CoroutineContext = mainDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
    }

}