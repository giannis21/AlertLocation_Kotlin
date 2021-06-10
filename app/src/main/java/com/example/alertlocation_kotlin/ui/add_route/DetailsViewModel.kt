package com.example.alertlocation_kotlin.ui.add_route

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertlocation_kotlin.data.model.Points
import com.example.alertlocation_kotlin.data.model.Route
import com.example.alertlocation_kotlin.data.model.User
import com.example.alertlocation_kotlin.data.repositories.mainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(var mainRepository: mainRepository,context: Context) : ViewModel() {


    var route_name: MutableLiveData<String>? = null
    var message: String =""
    var usersToSend = MutableLiveData<MutableList<User>>()
    var myCurrentLocation = MutableLiveData<String?>(null)
    var pointsList = MutableLiveData<MutableList<Points>>()
    var allRoutes: LiveData<MutableList<Route>>
    init{
        allRoutes = mainRepository.getAll()
        usersToSend.value= mutableListOf()
        pointsList.value= mutableListOf()
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
    fun addPoint(point: Points) {
        pointsList.value?.add(point)
    }

    fun clearPoints(){
        pointsList.value?.clear()
    }

    fun addRouteToDatabase(route: Route){
        viewModelScope.launch(Dispatchers.Default) {
            kotlin.runCatching {
                mainRepository.addRoute(route)
            }.onFailure {

            }.onSuccess {
               println("savedOrNot $it")
            }

        }

    }


}