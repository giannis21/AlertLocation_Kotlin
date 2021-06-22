package com.example.alertlocation_kotlin.ext

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import java.io.IOException
import java.util.*

object SharedFunctions {
     fun getAddress(context: Context,lat: Double, lon: Double): String {
        var errorMessage = ""
        var addresses: List<Address> = emptyList()
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(
                lat,
                lon,

                1
            )
        } catch (ioException: IOException) {
            return ""
        } catch (illegalArgumentException: IllegalArgumentException) {
            return ""
        }

        // Handle case where no address was found.
        return if (addresses.isEmpty()) {
            ""
        } else {
            val address = addresses[0]

            val addressFragments = with(address) {
                (0..maxAddressLineIndex).map { getAddressLine(it) }
            }
            addressFragments.joinToString(separator = "\n")
        }

    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    fun View.slideInLeft(): AnimatorSet {
        val animatorSet = AnimatorSet()
        val parent = parent as ViewGroup
        val distance = (parent.width - left).toFloat()

        val object1: ObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 1f)
        val object2: ObjectAnimator = ObjectAnimator.ofFloat(this, "translationX", -distance, 0f)

        animatorSet.playTogether(object1, object2)
        return animatorSet
    }

    fun View.slideOutLeft(): AnimatorSet {
        val animatorSet = AnimatorSet()
        val right = right.toFloat()

        val object1: ObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
        val object2: ObjectAnimator = ObjectAnimator.ofFloat(this, "translationX", 0f, -right)

        animatorSet.playTogether(object1, object2)
        return animatorSet
    }
}