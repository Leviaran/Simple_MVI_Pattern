package com.example.myapplication.loginscreen

import com.example.myapplication.model.loginmodel.LoginRequest
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface LoginView : MvpView {
    fun legacyAuth() : Observable<LoginRequest>
    fun render(state: LoginViewState)
}