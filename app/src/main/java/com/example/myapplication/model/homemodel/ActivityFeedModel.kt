package com.example.myapplication.model.homemodel

import com.google.gson.annotations.SerializedName

data class ActivityFeedModel(
    @SerializedName("__v")
    val v: Int,
    @SerializedName("_id")
    val idn: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("isLiked")
    val isLiked: Boolean,
    @SerializedName("isSaved")
    val isSaved: Boolean,
    @SerializedName("linkUrl")
    val linkUrl: String,
    @SerializedName("postDescription")
    val postDescription: String,
    @SerializedName("postImage")
    val postImage: PostImage,
    @SerializedName("titlePost")
    val titlePost: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("user")
    val user: User
)