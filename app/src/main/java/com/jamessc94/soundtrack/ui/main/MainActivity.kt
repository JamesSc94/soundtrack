package com.jamessc94.soundtrack.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.databinding.ActivityMainBinding
import com.jamessc94.soundtrack.media.ext.hide
import com.jamessc94.soundtrack.media.ext.replaceFragment
import com.jamessc94.soundtrack.media.ext.show
import com.jamessc94.soundtrack.ui.bottom.BottomFrag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val vm : MainVM by viewModels()
    private lateinit var binding : ActivityMainBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        vm.rootMediaId.observe(this) {
            replaceFragment(
                R.id.activity_main_bottom_container,
                BottomFrag()
            )

        }

        NavigationUI.setupActionBarWithNavController(this, findNavController(R.id.activity_main_frag))

        bottomSheetBehavior = BottomSheetBehavior.from(binding.activityMainBottom)
        bottomSheetBehavior.apply {
            isHideable = true
            addBottomSheetCallback(BottomSheetCallback())

        }

        binding.activityMainDim.setOnClickListener { collapseBottomSheet() }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.activity_main_frag)
        val appBarConfiguration : AppBarConfiguration = AppBarConfiguration(navController.graph)

        return NavigationUI.navigateUp(navController, appBarConfiguration)

    }

    override fun onBackPressed() {
        bottomSheetBehavior.let {
            if (it.state == BottomSheetBehavior.STATE_EXPANDED) {
                collapseBottomSheet()
            } else {
                super.onBackPressed()
            }
        }
    }

    fun collapseBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun showBottomSheet() {
        if(bottomSheetBehavior.peekHeight == 0){
            bottomSheetBehavior.setPeekHeight(160, true)

        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

    }

    private inner class BottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.activityMainDim.show()

            } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                binding.activityMainDim.hide()

            }

            vm.bottomSheetListener?.onStateChanged(bottomSheet, newState)

        }

        override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
            if (slideOffset > 0) {
                binding.activityMainDim.alpha = slideOffset

            } else if (slideOffset == 0f) {
                binding.activityMainDim.hide()

            }

            vm.bottomSheetListener?.onSlide(bottomSheet, slideOffset)

        }

    }

}