package com.example.myapplication.di

import com.androidnetworking.AndroidNetworking
import org.koin.dsl.module.applicationContext
import org.koin.dsl.module.module

const val BASE_URL = "http://www.recipepuppy.com"

//inline fun <reified T> createWebServer () : T {
//    return AndroidNetworking.get(BASE_URL)
//}