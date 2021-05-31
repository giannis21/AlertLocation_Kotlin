package com.example.alertlocationkotlin

import com.example.alertlocation_kotlin.Constants
import com.example.alertlocation_kotlin.Group_DataNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key=${Constants.SERVER_KEY}","Content-Type:${Constants.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun postNotification(@Body notification: PushNotification):Response<ResponseBody>

    @Headers("Authorization: key=${Constants.SERVER_KEY}","Content-Type:${Constants.CONTENT_TYPE}","project_id:686403281935")
    @POST("fcm/notification")
    suspend fun createGroupNotification(@Body notificationGroup: Group):Response<NotificationGroupResponse>

    @Headers("Authorization: key=${Constants.SERVER_KEY}","Content-Type:${Constants.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun createDataGroupNotification(@Body notificationGroup: Group_DataNotification):Response<DataGroupResponse>
}