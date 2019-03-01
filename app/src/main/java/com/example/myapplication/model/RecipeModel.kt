package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class RecipeModel(
    @SerializedName("href")
    val href: String,
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("title")
    val title: String,
    @SerializedName("version")
    val version: Double
)