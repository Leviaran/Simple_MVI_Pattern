package com.example.myapplication.model.loginmodel

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("__v")
    val v: Int,
    @SerializedName("_id")
    val idn: String,
    @SerializedName("blocked")
    val blocked: Boolean,
    @SerializedName("confirmed")
    val confirmed: Boolean,
    @SerializedName("email")
    val email: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("photoProfile")
    val photoProfile: PhotoProfile,
    @SerializedName("provider")
    val provider: String,
    @SerializedName("role")
    val role: Role,
    @SerializedName("username")
    val username: String
)