package com.example.alertlocationkotlin

data class DataGroupResponse(
    val failed_registration_ids: List<String>,
    val failure: Int,
    val success: Int
)