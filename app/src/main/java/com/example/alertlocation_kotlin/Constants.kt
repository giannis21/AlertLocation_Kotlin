package com.example.alertlocation_kotlin

class Constants {
    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        const val BASE_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAAn9DS5A8:APA91bEiM503MLqGDLC37iVmVkosYMb-DtraN9LhOy_lSSzpGpNQ6R36hj8qQPuz3E_o3l0DQix3nSUMH9xCV6DOVSNlqWfKfMmGa4zl0PfSGojf8U7a8VWoOxM1anGsqW9HRdJH3R96"
        const val CONTENT_TYPE = "application/json"
        val PACKAGE_NAME = "com.example.alertlocation_kotlin"

        /**
         * The name of the channel for notifications.
         */
        val CHANNEL_ID = "channel_01"

        val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"

        val EXTRA_LOCATION = "$PACKAGE_NAME.location"
        val EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
                ".started_from_notification"
        val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1500

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

        /**
         * The identifier for the notification displayed for the foreground service.
         */
        val NOTIFICATION_ID = 12345678
    }
}