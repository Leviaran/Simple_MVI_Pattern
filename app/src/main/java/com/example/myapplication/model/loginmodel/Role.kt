package com.example.myapplication.model.loginmodel

import com.google.gson.annotations.SerializedName

data class Role(
    @SerializedName("__v")
    val v: Int,
    @SerializedName("_id")
    val id: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String
)