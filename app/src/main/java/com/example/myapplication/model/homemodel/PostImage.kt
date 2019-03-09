package com.example.myapplication.model.homemodel

import com.google.gson.annotations.SerializedName

data class PostImage(
    @SerializedName("__v")
    val v: Int,
    @SerializedName("_id")
    val idn: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("ext")
    val ext: String,
    @SerializedName("hash")
    val hash: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("mime")
    val mime: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("provider")
    val provider: String,
    @SerializedName("related")
    val related: List<String>,
    @SerializedName("sha256")
    val sha256: String,
    @SerializedName("size")
    val size: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("url")
    val url: String
)