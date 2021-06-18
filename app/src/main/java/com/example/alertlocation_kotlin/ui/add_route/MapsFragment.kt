package com.example.alertlocation_kotlin.ui.add_route

import android.animation.ObjectAnimator
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.alertlocation_kotlin.*
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.data.database.RouteRoomDatabase
import com.example.alertlocation_kotlin.data.model.Points
import com.example.alertlocation_kotlin.data.repositories.mainRepository
import com.example.alertlocationkotlin.NotificationApi
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
import java.io.IOException
import java.util.*


class MapsFragment : Fragment(), OnMapReadyCallback {

    private var add_points_flag = true
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var permissionGranted = false
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var viewModel: DetailsViewModel
    private lateinit var viewModelFactory: ViewmodelFactory
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
        val networkConnectionIncterceptor = this.context?.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = NotificationApi(networkConnectionIncterceptor!!)
        var remoteRepository = RemoteRepository(webService)
        val routeDao = RouteRoomDatabase.getDatabase(requireContext()).routeDao()
        viewModelFactory = ViewmodelFactory(mainRepository(routeDao), remoteRepository,requireContext())
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(DetailsViewModel::class.java)




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

            val title=getAddress(location.latitude,location.longitude)
            mGoogleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(title)
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
                    .icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
            )
        }


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
    private fun getAddress(lat: Double, lon: Double): String {

        var addresses: List<Address> = emptyList()
        val geocoder = Geocoder(activity?.applicationContext, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1)
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

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap.setOnMapClickListener {
            if (!add_points_flag) {
                val title = getAddress(it.latitude,it.longitude)
                if(viewModel.pointsList.value?.size == 3)
                {
                    Toast.makeText(requireContext(),"You cannot add more than three points!",Toast.LENGTH_SHORT).show()
                    return@setOnMapClickListener
                }

                viewModel.addPoint(Points(it.latitude,it.longitude,title))
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
        if(viewModel.addressSelected.value !=null){
            actionsContainer.visibility=View.GONE
            val lat=viewModel.addressSelected.value!!.latitude
            val long= viewModel.addressSelected!!.value!!.longitude
            val address= viewModel.addressSelected.value!!.address
            val latLng=LatLng(lat,long)
            mGoogleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(address)
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
                    .icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
            )
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        lat,
                        long
                    ), 15.0f
                )
            )
        }else{

            val anim = ObjectAnimator.ofFloat(actionsContainer, "translationX", 90f, 0f)
            anim.duration = 2000
            anim.start()
        }

    }

}