package com.example.alertlocation_kotlin.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.alertlocation_kotlin.NetworkConnectionIncterceptor
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.RemoteRepository
import com.example.alertlocation_kotlin.ViewmodelFactory
import com.example.alertlocation_kotlin.data.database.RouteRoomDatabase
import com.example.alertlocation_kotlin.data.model.User
import com.example.alertlocation_kotlin.data.repositories.mainRepository
import com.example.alertlocation_kotlin.ui.add_route.DetailsViewModel
import com.example.alertlocationkotlin.FirebaseService
import com.example.alertlocationkotlin.NotificationApi

import com.google.firebase.database.*
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.fragment_username.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UsernameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UsernameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: DetailsViewModel
    private lateinit var viewModelFactory: ViewmodelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_username, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val networkConnectionIncterceptor = this.context?.applicationContext?.let { NetworkConnectionIncterceptor(it) }
        val webService = NotificationApi(networkConnectionIncterceptor!!)
        var remoteRepository = RemoteRepository(webService)
        val routeDao = RouteRoomDatabase.getDatabase(requireContext()).routeDao()
        viewModelFactory = ViewmodelFactory(mainRepository(routeDao), remoteRepository,requireContext())
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(DetailsViewModel::class.java)

        FirebaseService.sharedPref = context?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        if(FirebaseService.token.toString().isNotEmpty()){
            findNavController().navigate(R.id.action_usernameFragment_to_mainScreenFragment)
            return
        }

        uniqueUsername.editText?.doAfterTextChanged {
            errorMsg.visibility=View.GONE
        }
        FirebaseInstallations.getInstance().getToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Installations", "Installation auth token: " + task.result?.token)
                    FirebaseService.token = task.result?.token
                } else {
                    Log.e("Installations", "Unable to get Installation auth token")
                }
            }

        btnSend.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Users")
            val firebaseSearchQuery: Query = myRef.child(uniqueUsername.editText?.text.toString())

            firebaseSearchQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        myRef.child(uniqueUsername.editText?.text.toString()).setValue(User(uniqueUsername.editText?.text.toString(),FirebaseService.token.toString()))
                        findNavController().navigate(R.id.action_usernameFragment_to_mainScreenFragment)
                    }else
                        errorMsg.visibility=View.VISIBLE

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    throw databaseError.toException()
                }
            })
        }
    }


}
/*
            myRef.child("kostas2").setValue(User("kostas2","token1"))
            myRef.child("maria4").setValue(User("maria4","token1"))
            myRef.child("gianni23s").setValue(User("gianni23s","token1"))
            myRef.child("xristos23").setValue(User("xristos23","token1"))
            myRef.child("elppida43").setValue(User("elppida43","token1"))
            myRef.child("fan23").setValue(User("fan23","token1"))
            myRef.child("cristo23s").setValue(User("cristo23s","token1"))
            myRef.child("baggel43is").setValue(User("baggel43is","token1"))
            myRef.child("xristin43a").setValue(User("xristin43a","token1"))

 */


