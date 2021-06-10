package com.example.alertlocation_kotlin.ui

import android.Manifest
import android.content.*
import android.content.ClipData
import android.content.Context.CLIPBOARD_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alertlocation_kotlin.Constants
import com.example.alertlocation_kotlin.Constants.Companion.ACTION_BROADCAST
import com.example.alertlocation_kotlin.Constants.Companion.EXTRA_LOCATION
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.data.database.RouteRoomDatabase
import com.example.alertlocation_kotlin.data.model.Route
import com.example.alertlocation_kotlin.data.repositories.mainRepository
import com.example.alertlocation_kotlin.ui.adapter.RoutesAdapter
import com.example.alertlocation_kotlin.ui.add_route.ConfigurationBottomSheetDialogFragment
import com.example.alertlocation_kotlin.ui.add_route.DetailsViewModel
import com.example.tvshows.ui.nowplaying.ViewmodelFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_main_screen.*
import java.io.IOException
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainScreenFragment : Fragment() {

    private var mService: LocationUpdatesService? = null
    private var myReceiver:  MyReceiver? = null
    private var mBound = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var permissionGranted = false
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null
    private lateinit var viewModel: DetailsViewModel
    private lateinit var viewModelFactory: ViewmodelFactory
    private lateinit var routesAdapter: RoutesAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: LocationUpdatesService.LocalBinder = service as LocationUpdatesService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mBound = false
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myReceiver=MyReceiver()

        val routeDao = RouteRoomDatabase.getDatabase(requireContext()).routeDao()
        viewModelFactory = ViewmodelFactory(mainRepository(routeDao), requireContext())
        viewModel = ViewModelProvider(requireActivity(),viewModelFactory).get(DetailsViewModel::class.java)

        routesAdapter= RoutesAdapter(mutableListOf(),requireContext())
        routesRecyclerview.adapter=routesAdapter
        linearLayoutManager = LinearLayoutManager(requireContext())
        routesRecyclerview.layoutManager = linearLayoutManager

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        floatingActionButton.setOnClickListener {

            if(checkLocationPermission()){
                val dialogFragment: ConfigurationBottomSheetDialogFragment = ConfigurationBottomSheetDialogFragment.newInstance()
                dialogFragment.show(requireActivity().supportFragmentManager, "Bottomsheet")
            }

        }

        copy_address.setOnClickListener {
            find_Me()
        }

        viewModel.allRoutes.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it.isNullOrEmpty())
                return@Observer

            routesAdapter.updateData(it)

        })


    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = Constants.UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun find_Me() {

        if (checkLocationPermission()) {

            val builder = LocationSettingsRequest.Builder().addLocationRequest(createLocationRequest())
            val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            viewModel.myCurrentLocation.postValue(getAddress(it.latitude, it.longitude))
                            viewModel.myCurrentLocation.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                                //  viewModel.myCurrentLocation.removeObserver()
                                if(!it.isNullOrEmpty())
                                {
                                    myClipboard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                                    myClip = ClipData.newPlainText("text", it)
                                    myClip?.let { _ ->
                                        myClipboard!!.setPrimaryClip(myClip!!)
                                        Toast.makeText(requireContext(), "$it Copied", Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(requireContext(), "Something went wrong, try again!", Toast.LENGTH_SHORT).show()
                                }
                            })
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
    }


    private fun getAddress(lat: Double, lon: Double): String {
        var errorMessage = ""
        var addresses: List<Address> = emptyList()
        val geocoder = Geocoder(activity?.applicationContext, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(
                lat,
                lon,

                1)
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
    private class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(EXTRA_LOCATION)
            if (location != null) {

                System.out.println("dddddddddd" + Utils.getLocationText(location))
            }
        }
    }

    override fun onStart() {
        super.onStart()


        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        requireActivity().bindService(
            Intent(requireContext(), LocationUpdatesService::class.java),
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            requireActivity().unbindService(mServiceConnection)
            mBound = false
        }
    }
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            myReceiver!!,
            IntentFilter(ACTION_BROADCAST)
        )
    }



    private fun checkLocationPermission(): Boolean { /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        // TODO: check if device's gps is enabled
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.MY_PERMISSIONS_REQUEST_LOCATION
            )
            false
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    find_Me()
                } else {
                    permissionGranted = false
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }


}