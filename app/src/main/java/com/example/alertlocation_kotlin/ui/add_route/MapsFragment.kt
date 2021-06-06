package com.example.alertlocation_kotlin.ui.add_route

import android.Manifest
import android.animation.ObjectAnimator
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alertlocation_kotlin.Constants
import com.example.alertlocation_kotlin.Constants.Companion.MY_PERMISSIONS_REQUEST_LOCATION
import com.example.alertlocation_kotlin.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_maps.*
import java.util.*


class MapsFragment : Fragment(), OnMapReadyCallback {

    private var add_points_flag = true
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var permissionGranted = false
    private lateinit var mGoogleMap: GoogleMap
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    companion object {
        fun newInstance() = MapsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val anim = ObjectAnimator.ofFloat(actionsContainer, "translationX", 90f, 0f)
        anim.duration = 2000
        anim.start()

        findMe.setOnClickListener {
            find_Me()
        }
        add_points.setOnClickListener {
            if (add_points_flag) {
                add_points.alpha = 0.15f
                add_points_flag = false
            } else {
                add_points.alpha = 1f
                add_points_flag = true
            }

        }

        floatingActionButton.setOnClickListener {
            it.visibility = View.GONE
            mGoogleMap.clear()
        }

    }

    private fun find_Me() {



            mGoogleMap.isMyLocationEnabled = true
            mGoogleMap.uiSettings.isMyLocationButtonEnabled = false
            val builder = LocationSettingsRequest.Builder().addLocationRequest(createLocationRequest())
            val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            navigate_to_coordinates(LatLng(location.latitude, location.longitude))
                        }
                    }
            }
            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(
                            requireActivity(),
                            2
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = Constants.UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun navigate_to_coordinates(location: LatLng, addMarker: Boolean = false) {
        if (addMarker)
            mGoogleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(getLocationTitle(location))
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
                    .icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )


            )

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        mGoogleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), 15.0f
            )
        )
    }

    fun getLocationTitle(location: LatLng):String{
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)


        val address: String? = addresses[0]?.getAddressLine(0) ?: null// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city: String? = addresses[0]?.locality ?: null
        val state: String? = addresses[0]?.adminArea ?: null
        val country: String? = addresses[0]?.countryName ?: null
        val postalCode: String? = addresses[0]?.postalCode ?: null
        val thoroughfare: String? = addresses[0]?.thoroughfare ?: null
        val addressNumber: String? = addresses[0]?.featureName ?: null

        return  address ?: "$thoroughfare $addressNumber $city,$postalCode,$country".replace(
            "null",
            ""
        )

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap.setOnMapClickListener {
            if (!add_points_flag) {
                val title = getLocationTitle(it)

                mGoogleMap.addMarker(
                    MarkerOptions()
                        .position(it).title(title)
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
                        .icon(
                            BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        )
                )
                floatingActionButton.visibility = View.VISIBLE
            }
        }

    }

}