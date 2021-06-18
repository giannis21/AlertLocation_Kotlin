package com.example.alertlocation_kotlin.ext

import android.content.Context
import android.location.Address
import android.location.Geocoder
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
}