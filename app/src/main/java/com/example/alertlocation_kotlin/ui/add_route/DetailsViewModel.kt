package com.example.alertlocation_kotlin.ui.add_route

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alertlocation_kotlin.data.model.User

class DetailsViewModel : ViewModel() {


    var route_name: MutableLiveData<String>? = null
    var message: MutableLiveData<String>? = null
    var usersToSend = MutableLiveData<MutableList<User>>()

    init{
        usersToSend.value= mutableListOf()
    }
    fun removeUser(user: User) {
        usersToSend.value?.remove(user)
    }

    fun addUser(user: User) {
        usersToSend.value?.add(user)
    }

    fun addUsers(newUsers: MutableList<User>) {
        newUsers.forEachIndexed { index, user ->
             val userToBeAdded= usersToSend.value?.firstOrNull { it.username == user.username }
             if(userToBeAdded == null)
                 addUser(user)
        }

    }
}