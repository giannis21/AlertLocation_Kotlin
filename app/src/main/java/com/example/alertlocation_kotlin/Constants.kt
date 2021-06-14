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



        const val RUNNING_DATABASE_NAME = "running_db"

        const val REQUEST_CODE_LOCATION_PERMISSION = 0

        const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
        const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

        const val LOCATION_UPDATE_INTERVAL = 5000L
        const val FASTEST_LOCATION_INTERVAL = 2000L

        const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Tracking"
        const val NOTIFICATION_ID = 1
    }
}