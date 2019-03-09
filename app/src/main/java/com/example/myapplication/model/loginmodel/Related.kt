package com.example.myapplication.model.loginmodel

import com.google.gson.annotations.SerializedName

data class Related(
    @SerializedName("_id")
    val id: String,
    @SerializedName("field")
    val `field`: String,
    @SerializedName("kind")
    val kind: String,
    @SerializedName("ref")
    val ref: String
)