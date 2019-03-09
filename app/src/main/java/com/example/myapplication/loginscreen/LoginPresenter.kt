package com.example.myapplication.loginscreen

import com.androidnetworking.common.Priority
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.example.myapplication.Utils.BaseViewState
import com.example.myapplication.mainscreen.MainViewState
import com.example.myapplication.model.loginmodel.LoginRequest
import com.example.myapplication.model.loginmodel.LoginResponse
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient

const val BASE_URL = "http://1d85e976.ngrok.io"

class LoginPresenter : MviBasePresenter<LoginView, LoginViewState>() {

    override fun bindIntents() {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .build()

        val authResponseData: Observable<LoginViewState> = intent(LoginView::legacyAuth)
            .switchMap { Rx2AndroidNetworking.post("$BASE_URL/auth/local")
                .addBodyParameter(it)
                .setPriority(Priority.MEDIUM)
                .setOkHttpClient(okHttpClient)
                .build()
                .getObjectObservable(LoginResponse::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map<LoginViewState> { LoginViewState.DataState(it) }
                .startWith(LoginViewState.LoadingState)
                .onErrorReturn { LoginViewState.ErrorState(it) }
                .observeOn(AndroidSchedulers.mainThread())
            }

        subscribeViewState(authResponseData.distinctUntilChanged(), LoginView::render)
    }

}