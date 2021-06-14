package com.example.alertlocation_kotlin

import com.example.alertlocationkotlin.*
import okhttp3.ResponseBody
import retrofit2.Response


class RemoteRepository(private val my_Api: NotificationApi) {

    suspend fun fetch_genres(notification: PushNotification): Response<ResponseBody> {
        return my_Api.postNotification(notification)
    }

    suspend fun createGroupNotification(notificationGroup: Group): Response<NotificationGroupResponse> {
        return my_Api.createGroupNotification(notificationGroup)
    }

    suspend fun createDataGroupNotification(notificationGroup: Group_DataNotification) :Response<DataGroupResponse> {
        return my_Api.createDataGroupNotification(notificationGroup)
    }

}