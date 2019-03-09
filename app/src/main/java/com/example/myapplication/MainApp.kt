package com.example.myapplication

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.example.myapplication.model.ViewModel
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Paper
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.module

class MainApp : Application() {
    val appModule = module (override = true) {
        single { ViewModel() }
        factory { RxPaperBook.with() }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
        RxPaperBook.init(this)
        AndroidNetworking.initialize(this)
    }
}