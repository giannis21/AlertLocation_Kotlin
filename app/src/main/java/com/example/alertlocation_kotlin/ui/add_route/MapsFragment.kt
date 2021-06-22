package com.example.alertlocation_kotlin.ui.add_route

import android.animation.ObjectAnimator
import android.content.IntentSender
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.example.alertlocation_kotlin.*
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.data.model.Points
import com.example.alertlocation_kotlin.ext.SharedFunctions.getAddress
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


class MapsFragment : Fragment(), OnMapReadyCallback {

    private var add_points_flag = true
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var permissionGranted = false
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var viewModel: DetailsViewModel

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

        viewModel = (activity as MainActivity).viewModel

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
            viewModel.clearPoints()
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
        if (addMarker){
            addMarker(location, getAddress(requireContext(),location.latitude,location.longitude))
        }

        animateCamera(LatLng(location.latitude, location.longitude))

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap.setOnMapClickListener {
            if (!add_points_flag) {
                val title = getAddress(requireContext(),it.latitude,it.longitude)
                if(viewModel.pointsList.value?.size == 3)
                {
                    Toast.makeText(requireContext(),"You cannot add more than three points!",Toast.LENGTH_SHORT).show()
                    return@setOnMapClickListener
                }

                viewModel.addPoint(Points(it.latitude,it.longitude,title))
                addMarker(it, title)
                floatingActionButton.visibility = View.VISIBLE
            }
        }
        if(viewModel.addressSelected.value !=null ){

            actionsContainer.visibility=View.GONE
            val latLng=LatLng(viewModel.addressSelected.value!!.latitude,viewModel.addressSelected.value!!.longitude)
            addMarker(latLng, viewModel.addressSelected.value!!.address)
            animateCamera(latLng)

        }else if(viewModel.notificationDeepLink.value==true){
            val latLng=getDeeplinkLocation()
            addMarker(latLng,color="red")
            animateCamera(latLng)

            banner.visibility=View.VISIBLE
            val bannerTxt= banner.findViewById<TextView>(R.id.redBannerTxtV)
            bannerTxt.text= HtmlCompat.fromHtml(
                getString(R.string.user_passed_time, viewModel.notificationBundle.value?.get("senderId").toString(),viewModel.notificationBundle.value?.get("timeSent").toString()),HtmlCompat.FROM_HTML_MODE_LEGACY)

        }


        else{

            val anim = ObjectAnimator.ofFloat(actionsContainer, "translationX", 90f, 0f)
            anim.duration = 2000
            anim.start()
        }

    }

    private fun animateCamera(latLng: LatLng) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mGoogleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, 15.0f)
        )
    }

    private fun addMarker(location: LatLng, title: String?=null,color: String ="blue") {

        mGoogleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title(title ?: "" )
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
                .icon(
                    if(color == "blue")
                     BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE) else BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)
                )
        )
    }

    private fun getDeeplinkLocation(): LatLng {
        val tempLocation= viewModel.notificationBundle.value?.get("userLocation").toString()
        var a =tempLocation
        val lat= tempLocation.substring(1,tempLocation.indexOf(","))
        val long= tempLocation.substring(tempLocation.indexOf(",")+1,tempLocation.length-1)

        return LatLng(lat.toDouble(),long.toDouble())
    }

}