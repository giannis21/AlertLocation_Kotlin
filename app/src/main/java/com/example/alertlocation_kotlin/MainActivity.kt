package com.example.alertlocation_kotlin


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.alertlocation_kotlin.data.database.RouteRoomDatabase
import com.example.alertlocation_kotlin.data.network.NotificationApi
import com.example.alertlocation_kotlin.data.repositories.mainRepository
import com.example.alertlocation_kotlin.ui.add_route.DetailsViewModel
import com.example.alertlocationkotlin.*
import com.example.alertlocationkotlin.FirebaseService.Companion.isRinging
import com.example.alertlocationkotlin.FirebaseService.Companion.isVibrate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.banner_layout.view.*


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: DetailsViewModel
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

        val deepLink= intent.extras
        deepLink?.let {
            if(deepLink["senderId"] !=null){
                viewModel.notificationDeepLink.value=true
                viewModel.notificationBundle.value= it

            }
        }



       vibrate_checked.isChecked = isVibrate!!

//------------------------------------------------ενεργο η οχι το SOUND notification state-----------------------------------------------//

       sound_switch.isChecked = isRinging!!

//------------------------------------------------αλλαγη καταστασης του vibration και αποθηκευση ----------------------------------//
        vibrate_checked.setOnClickListener {
            vibrate_checked.isChecked = vibrate_checked.isChecked != true
            isVibrate = vibrate_checked.isChecked
        }

        sound_switch.setOnClickListener {
            isRinging = sound_switch.isChecked
        }
        getFirebaseToken(false)
        uniqueId.text = FirebaseService.uniqueId
        uniqueId.setOnClickListener {
            showDialog()
        }

        ///when app is in foreground it works


        //FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        viewModel.uniqueIdRetrieved.observe(this, Observer {

        })
    }

    fun showSettings(isShown:Boolean){
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
        if(id==null) {
            editText?.hint = "Type a new unique id.."
        }else{
            editText?.hint = "Type a friendly name.."
        }


        dialog.findViewById<TextView>(R.id.okayBtn)?.setOnClickListener {
            if(editText?.text?.isNotBlank()!!) {
                id?.let {
                    viewModel.updateFriendlyName(id, editText.text.toString())
                    dialog.dismiss()
                    showBanner("Route name updated succesfully!",true)
                } ?:kotlin.run {
                     getFirebaseToken(true){
                         viewModel.updateUniqueId(editText.text.toString(),FirebaseService.token ?: "",true,FirebaseService.uniqueId){
                             if(it){
                                 FirebaseService.uniqueId= editText?.text.toString()
                                 uniqueId.text = FirebaseService.uniqueId
                             }

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
                cLayout.imageView.setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY)

                Handler(Looper.getMainLooper()).postDelayed({
                    cLayout.removeView(view)
                }, 4000)
            }
        }
    }

    fun getFirebaseToken(changeToken: Boolean,callback:(() -> Unit)?=null) {
        FirebaseInstallations.getInstance().getToken(changeToken)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    FirebaseService.token = task.result?.token
                    if(changeToken){
                        callback?.invoke()
                    }
                } else {
                    Log.e("Installations", "Unable to get Installation auth token")
                }
            }
    }

}