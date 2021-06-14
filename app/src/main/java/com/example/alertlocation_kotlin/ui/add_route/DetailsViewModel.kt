package com.example.alertlocation_kotlin.ui.add_route

import android.content.Context
import android.text.BoringLayout
import androidx.lifecycle.*
import com.example.alertlocation_kotlin.RemoteRepository
import com.example.alertlocation_kotlin.data.model.Points
import com.example.alertlocation_kotlin.data.model.Route
import com.example.alertlocation_kotlin.data.model.User
import com.example.alertlocation_kotlin.data.repositories.mainRepository
import com.example.alertlocationkotlin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(var mainRepository: mainRepository, var remoteRepository: RemoteRepository, context: Context) : ViewModel() {


    var transitionToEnd= MutableLiveData<Boolean?> (null)
    var route_name: MutableLiveData<String>? = null
    var message: String =""
    var usersToSend = MutableLiveData<MutableList<User>>()
    var myCurrentLocation = MutableLiveData<String?>(null)
    var pointsList = MutableLiveData<MutableList<Points>>()
    var allRoutes: LiveData<MutableList<Route>>

    var groupNotificationKey=MutableLiveData<String?>(null)
    init{
        allRoutes = mainRepository.getAll().asLiveData()
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

    fun updateFriendlyName(id: Long, name: String) {
        viewModelScope.launch(Dispatchers.Default) {
            kotlin.runCatching {
                mainRepository.updateFriendlyName(id,name)
            }.onFailure {

            }.onSuccess {
                println("savedOrNot $it")
            }

        }
    }

    fun enableRoute(id: Long, isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            kotlin.runCatching {
                mainRepository.enableRoute(id,isEnabled)
            }.onFailure {

            }.onSuccess {
                println("savedOrNot $it")
            }

        }
    }

    fun getGroupId(users: MutableList<User>) {

        viewModelScope.launch(Dispatchers.Default) {
            kotlin.runCatching {
                remoteRepository.createGroupNotification(  Group(
                    "create", System.currentTimeMillis().toString(), users.map { it -> it.token }.toMutableList()
                ))
            }.onFailure {

            }.onSuccess { response ->

                val notificationGroupResponse: NotificationGroupResponse? = response.body()
                groupNotificationKey.postValue(notificationGroupResponse?.notification_key)

            }//https://firebase.google.com/docs/cloud-messaging/android/device-group?utm_source=studio

        }

    }

    fun sendPushNotification(route: Route) {
        viewModelScope.launch(Dispatchers.Default) {
            kotlin.runCatching {
                remoteRepository.postNotification(
                    PushNotification(NotificationData(route.message,route.RouteName),
                        route.notificationGroupId)
                )
            }.onFailure {

            }.onSuccess { response ->

            }//https://firebase.google.com/docs/cloud-messaging/android/device-group?utm_source=studio

        }
    }

    fun removeItem(route: Route) {
        viewModelScope.launch(Dispatchers.Default) {
            kotlin.runCatching {
                mainRepository.removeItem(route)
            }.onFailure {

            }.onSuccess {

            }

        }
    }


}