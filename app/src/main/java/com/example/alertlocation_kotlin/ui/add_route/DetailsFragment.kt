package com.example.alertlocation_kotlin.ui.add_route

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alertlocation_kotlin.R
import com.example.alertlocation_kotlin.data.model.User
import com.example.alertlocation_kotlin.ui.adapter.UsersAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.details_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DetailsFragment : Fragment() {

    companion object {
        fun newInstance() = DetailsFragment()
        var clearValuesListener: (() -> Unit)?=null
    }
    val handler = Handler(Looper.getMainLooper())
    private lateinit var viewModel: DetailsViewModel
    lateinit var usersAdapter : UsersAdapter
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.details_fragment, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(DetailsViewModel::class.java)
        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerview)
        usersAdapter = UsersAdapter(requireContext(), mutableListOf(),viewModel)
        message_id1.afterTextChanged {
            viewModel.message?.postValue(it)
        }
        clearValuesListener ={
            usersAdapter.tempListOfSelectedUsers.clear()
            viewModel.usersToSend.value?.clear()
        }

        val okay1 = view.findViewById<TextView>(R.id.okay1)
        saved_icon.setOnClickListener {
            text_input_layout?.visibility = View.GONE
        }

        UsersAdapter.changeSelectionListener = { isEmpty ->
            if (!isEmpty) {
                okay1.visibility = View.GONE
            } else {
                okay1.visibility = View.VISIBLE
            }
        }

        text_input_layout.editText?.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (hasFocus) {
                val animation = recyclerview.slideInLeft()
                animation.doOnEnd {
                    recyclerview.visibility = View.VISIBLE
                }
                animation.start()
            }
        }

        okay1.setOnClickListener {
            text_input_layout.editText?.setText("")
            text_input_layout.clearFocus()
            addChips(usersAdapter.tempListOfSelectedUsers)
            val animation = recyclerview.slideOutLeft()
            animation.doOnEnd {
                recyclerview.visibility = View.GONE
                okay1.visibility = View.GONE
                chip_group.visibility = View.VISIBLE
                usersAdapter.tempListOfSelectedUsers.clear()
                usersAdapter.clear()
            }
            animation.start()

        }
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")
        var user = User("giannis", "fcm1")

        var user3 = User("giannis", "fcm1")
        var user4 = User("kostas", "fcm1")


        recyclerview?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = usersAdapter
        }

        motionId.setTransitionListener(
                object : MotionLayout.TransitionListener {
                    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
                    override fun onTransitionChange(motionLayout: MotionLayout?, p1: Int, p2: Int, p3: Float) {

                        lifecycleScope.launch(Dispatchers.Main) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                linearLayout2.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_style)
                                saved_icon.alpha = 0f
                            }, 100)
                        }

                    }

                    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}
                    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

                })

        message_id1.afterTextChanged { searchText ->
            viewModel.message?.postValue(searchText)
//            saved_icon.alpha = 0f
//            linearLayout2.background = ContextCompat.getDrawable(requireContext(), R.drawable.edittext_style)
//            lifecycleScope.launch(Dispatchers.Main) {
//                handler.removeCallbacksAndMessages(null)
//                handler.postDelayed({
//                    if (searchText.length == message_id1.text.length) {
//                        viewModel.message?.postValue(searchText)
//                        animateSaveIcon()
//                    }
//                }, 1500)
//
//            }

        }


        searchHereEdittext.afterTextChanged { searchText ->

            okay1.visibility = View.GONE
            chip_group.visibility = View.GONE

            if (searchText.isEmpty()) {
                usersAdapter.clear()
                chip_group.visibility = View.VISIBLE
                return@afterTextChanged
            }
            val firebaseSearchQuery: Query = myRef.orderByChild("username").startAt(searchText).endAt(searchText + "\uf8ff")

            val tempList = mutableListOf<User>()
            firebaseSearchQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {

                        val name = userSnapshot.child("username").getValue(String::class.java)
                        val token = userSnapshot.child("token").getValue(String::class.java)
                        val user1 = User(name!!, token!!)
                        tempList.add(user1)
                        if (usersAdapter.tempListOfSelectedUsers.contains(user1)) {
                            okay1.visibility = View.VISIBLE
                        }
                    }

                    usersAdapter.swap(tempList)
                    tempList.clear()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    throw databaseError.toException()
                }
            })

        }
    }

    private fun animateSaveIcon() {
        linearLayout2.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_message_saved)
        linearLayout2.animate()
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 1000
        valueAnimator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            saved_icon.alpha = alpha
        }
        valueAnimator.doOnEnd {
            linearLayout2.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_message_saved)
        }
        valueAnimator.start()
    }

    private fun addChips(users: MutableList<User>) {


        chip_group.removeAllViews()
        viewModel.addUsers(users)
        viewModel.usersToSend.value?.forEach { user ->
            val chip = Chip(context)
            chip.text = user.username
            chip.tag = user.token
            chip.textSize=14f
            chip.chipIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_save)

            chip.setChipBackgroundColorResource(R.color.teal_200)
            chip.isCloseIconVisible = true
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            chip.setOnCloseIconClickListener {
                chip_group.removeView(it)
                val name= (it as Chip).text.toString()
                val token= (it as Chip).tag.toString()
                viewModel.removeUser(User(name, token))
            }

            chip_group.addView(chip)
        }

    }


    fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    fun View.slideInLeft(): AnimatorSet {
        val animatorSet = AnimatorSet()
        val parent = parent as ViewGroup
        val distance = (parent.width - left).toFloat()

        val object1: ObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 1f)
        val object2: ObjectAnimator = ObjectAnimator.ofFloat(this, "translationX", -distance, 0f)

        animatorSet.playTogether(object1, object2)
        return animatorSet
    }

    fun View.slideOutLeft(): AnimatorSet {
        val animatorSet = AnimatorSet()
        val right = right.toFloat()

        val object1: ObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
        val object2: ObjectAnimator = ObjectAnimator.ofFloat(this, "translationX", 0f, -right)

        animatorSet.playTogether(object1, object2)
        return animatorSet
    }


}