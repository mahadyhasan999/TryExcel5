package com.example.tryexcel5

data class User(
    val username: String,
    val userId: String,
    val password: String,
    val role: String,
    val buyer: String,
    val workOrder: String,
    val styleCode: String,
    val stepName: String,
    val stepNumber: String,
    val totalQty: String,
    val startTime: String = "",
    val endTime: String = ""
)
