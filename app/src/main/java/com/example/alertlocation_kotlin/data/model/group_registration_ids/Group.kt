package com.example.alertlocationkotlin

data class Group(
   var operation:String ="create",
   var notification_key_name:String,
   var registration_ids:MutableList<String>
)
data class NotificationGroupResponse(
    var notification_key:String
)

