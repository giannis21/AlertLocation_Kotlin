package com.example.alertlocation_kotlin.ui.adapter

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.example.alertlocation_kotlin.R


object BindingAdapter {
    @BindingAdapter( "changeIcon" )
    @JvmStatic
    fun ImageView.changeIcon(isExpanded: Boolean) {
        println("mpikee")
        if(!isExpanded)
            this.setImageResource(R.drawable.ic_baseline_add_24)
        else
            this.setImageResource(R.drawable.ic_baseline_remove_24)
    }

    @BindingAdapter( "setColor" )
    @JvmStatic
    fun ConstraintLayout.setColor(isEnabled: Boolean) {

        if(isEnabled)
            this.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
         else
            this.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark))

    }

    @BindingAdapter( "enableSwitch" )
    @JvmStatic
    fun Switch.enableSwitch(isEnabled: Boolean) {
        this.isChecked = isEnabled
    }


 }
