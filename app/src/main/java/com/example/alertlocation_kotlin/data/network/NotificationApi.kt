package com.example.alertlocation_kotlin.data.network

import com.example.alertlocation_kotlin.Constants
import com.example.alertlocation_kotlin.Constants.Companion.BASE_URL
import com.example.alertlocation_kotlin.data.model.notification_to_group_id.Group_DataNotification
import com.example.alertlocation_kotlin.NetworkConnectionIncterceptor
import com.example.alertlocation_kotlin.data.model.pushNotification.PushNotification
import com.example.alertlocationkotlin.DataGroupResponse
import com.example.alertlocationkotlin.Group
import com.example.alertlocationkotlin.NotificationGroupResponse
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key=${Constants.SERVER_KEY}","Content-Type:${Constants.CONTENT_TYPE}")
    @POST("/fcm/send")
    suspend fun postNotification(@Body notification: PushNotification):Response<ResponseBody>

    @Headers("Authorization: key=${Constants.SERVER_KEY}","Content-Type:${Constants.CONTENT_TYPE}","project_id:939355020609")
    @POST("fcm/notification")
    suspend fun createGroupNotification(@Body notificationGroup: Group):Response<NotificationGroupResponse>  //it returns a group id in order to send message in all registration ids

    @Headers("Authorization: key=${Constants.SERVER_KEY}","Content-Type:${Constants.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun createDataGroupNotification(@Body notificationGroup: Group_DataNotification):Response<DataGroupResponse>

    companion object {

        operator fun invoke(networkConnectionIncterceptor: NetworkConnectionIncterceptor): NotificationApi {
            val logging = HttpLoggingInterceptor()
            logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }

            val okHttpClient1 = OkHttpClient.Builder()
                .addInterceptor(networkConnectionIncterceptor)
                .addInterceptor(logging)

            return Retrofit.Builder().client(okHttpClient1.build())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NotificationApi::class.java)
        }
    }
}