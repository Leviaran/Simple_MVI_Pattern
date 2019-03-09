package com.example.myapplication.model.loginmodel

import com.google.gson.annotations.SerializedName

data class LoginRequest (
    @SerializedName("identifier")
    val identifier : String,
    @SerializedName("password")
    val password : String
)