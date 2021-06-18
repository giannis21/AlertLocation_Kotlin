package com.example.alertlocation_kotlin


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.alertlocation_kotlin.data.database.RouteRoomDatabase
import com.example.alertlocation_kotlin.data.repositories.mainRepository
import com.example.alertlocation_kotlin.ui.adapter.RoutesAdapter
import com.example.alertlocation_kotlin.ui.add_route.DetailsViewModel
import com.example.alertlocationkotlin.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
 import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.banner_layout.view.*
import kotlinx.android.synthetic.main.fragment_username.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var viewModelFactory: ViewmodelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseService.sharedPref = this?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        val networkConnectionIncterceptor = this.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = NotificationApi(networkConnectionIncterceptor!!)
        val remoteRepository = RemoteRepository(webService)
        val routeDao = RouteRoomDatabase.getDatabase(this).routeDao()
        viewModelFactory = ViewmodelFactory(mainRepository(routeDao), remoteRepository,this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DetailsViewModel::class.java)

        //vibrate_checked.isChecked = PreferenceUtils.get_vibration_state(requireContext())!!

//------------------------------------------------ενεργο η οχι το SOUND notification state-----------------------------------------------//

       // sound_switch.isChecked = PreferenceUtils.get_sound_state(requireContext())!!

//------------------------------------------------αλλαγη καταστασης του vibration και αποθηκευση ----------------------------------//
        vibrate_checked.setOnClickListener {
            vibrate_checked.isChecked = vibrate_checked.isChecked != true

            if (vibrate_checked.isChecked) {
               // PreferenceUtils.set_vibration_state(true, this)
            } else {
              //  PreferenceUtils.set_vibration_state(false, this)
            }
        }

        sound_switch.setOnClickListener {
            if (sound_switch.isChecked) {
             //   PreferenceUtils.set_sound_state(true, requireContext())
            } else {
             //   PreferenceUtils.set_sound_state(false, requireContext())
            }
        }
        uniqueId.setText(FirebaseService.uniqueId)
        uniqueId.setOnClickListener {
            showDialog()
        }

        ///when app is in foreground it works


        //FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

    }
    open fun showSettings(isShown:Boolean){
        if(isShown)
            settingsContainer.visibility =View.VISIBLE
        else
            settingsContainer.visibility =View.GONE
    }


    fun showDialog(id: Long ?=null) {
        val dialog = BottomSheetDialog(this)
        val inflater = this?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)
        val editText  =dialog.findViewById<TextInputLayout>(R.id.textInputLayout)?.editText
        dialog.findViewById<TextView>(R.id.okayBtn)?.setOnClickListener {
            if(editText?.text?.isNotBlank()!!) {
                id?.let {
                    viewModel.updateFriendlyName(id, editText.text.toString())
                    dialog.dismiss()
                    showBanner("Route name updated succesfully!")
                } ?:kotlin.run {
                     viewModel.updateUniqueId(editText.text.toString(),FirebaseService.token ?: "",true,FirebaseService.uniqueId){
                         if(it){
                             FirebaseService.uniqueId= uniqueUsername.editText?.text.toString()
                             uniqueId.text = FirebaseService.uniqueId
                         }

                     }
                }

            }
        }

        dialog.show()
    }

    fun showBanner(value: String, success: Boolean = false) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.banner_layout, null)

        runOnUiThread {
            frameLayout_main?.let { cLayout ->
                cLayout.addView(view, 0)
                cLayout.bringToFront()
                cLayout.redBannerTxtV.text = value

                if(!success){
                    cLayout.cardView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.LightRed)
                    cLayout.imageView.background = ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24)

                }else{
                    cLayout.cardView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.success_color)
                    cLayout.imageView.background = ContextCompat.getDrawable(this, R.drawable.ic_save)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    cLayout.removeView(view)
                }, 4000)
            }
        }
    }

}