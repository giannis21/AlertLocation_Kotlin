package com.example.alertlocation_kotlin.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.example.alertlocation_kotlin.*
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.ui.add_route.DetailsViewModel
import com.example.alertlocationkotlin.FirebaseService

import kotlinx.android.synthetic.main.fragment_username.*

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

        viewModel = (activity as MainActivity).viewModel

        FirebaseService.sharedPref = context?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)


        uniqueUsername.editText?.doAfterTextChanged {
            errorMsg.visibility=View.GONE
        }
        (activity as MainActivity).getFirebaseToken(false)
        if(FirebaseService.token.toString().isNotEmpty()){
            findNavController().navigate(R.id.action_usernameFragment_to_mainScreenFragment)
            return
        }
        btnSend.setOnClickListener {
            viewModel.updateUniqueId(uniqueUsername.editText?.text.toString(),FirebaseService.token.toString(),false){
                try
                {
                    if(it) {
                    FirebaseService.uniqueId= uniqueUsername.editText?.text.toString()

                    findNavController().navigate(R.id.action_usernameFragment_to_mainScreenFragment)
                }else
                    errorMsg.visibility= View.VISIBLE
                }catch (e:Exception){}

            }

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


