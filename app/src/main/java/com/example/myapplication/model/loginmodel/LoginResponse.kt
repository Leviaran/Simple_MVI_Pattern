package com.example.myapplication.model.loginmodel

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("jwt")
    val jwt: String,
    @SerializedName("user")
    val user: User
)