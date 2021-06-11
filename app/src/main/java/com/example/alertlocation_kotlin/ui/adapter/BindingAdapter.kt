package com.example.alertlocation_kotlin.ui.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.alertlocation_kotlin.R


object BindingAdapter {
    @BindingAdapter( "changeIcon" )
    @JvmStatic
    fun ImageView.changeIcon(isExpanded: Boolean) {
        if(!isExpanded)
            this.setImageResource(R.drawable.ic_baseline_add_24)
        else
            this.setImageResource(R.drawable.ic_baseline_remove_24)
    }


 }
