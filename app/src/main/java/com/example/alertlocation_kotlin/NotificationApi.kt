package com.example.alertlocationkotlin

import com.example.alertlocation_kotlin.Constants
import com.example.alertlocation_kotlin.Constants.Companion.BASE_URL
import com.example.alertlocation_kotlin.Group_DataNotification
import com.example.alertlocation_kotlin.NetworkConnectionIncterceptor
 import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    companion object {

        operator fun invoke(networkConnectionIncterceptor: NetworkConnectionIncterceptor): NotificationApi {

            val okHttpClient1 = OkHttpClient.Builder()
                .addInterceptor(networkConnectionIncterceptor)


            return Retrofit.Builder().client(okHttpClient1.build())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NotificationApi::class.java)
        }
    }
}