package com.example.alertlocation_kotlin.ui

import android.content.*
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.alertlocation_kotlin.Constants.Companion.ACTION_BROADCAST
import com.example.alertlocation_kotlin.Constants.Companion.EXTRA_LOCATION
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.ui.add_route.ConfigurationBottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_main_screen.*

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
        floatingActionButton.setOnClickListener {
            // mService?.requestLocationUpdates()
             val dialogFragment: ConfigurationBottomSheetDialogFragment = ConfigurationBottomSheetDialogFragment.newInstance()
             dialogFragment.show(requireActivity().supportFragmentManager, "Bottomsheet")
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainScreenFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainScreenFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}