package com.example.myapplication.loginscreen

import android.util.Log
import com.androidnetworking.common.Priority
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.example.myapplication.mainscreen.BASE_URL
import com.example.myapplication.model.loginmodel.LoginResponse
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.paperdb.Book
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

const val USER_PREF = "user_pref"
const val USER_BOOK = "user_book"

class LoginPresenter : MviBasePresenter<LoginView, LoginViewState>(), KoinComponent {

    val paperBook : Book by inject<Book>()

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
                .switchMap { loginResponse -> Observable.just(paperBook)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.write(USER_PREF, loginResponse) }
                    .doOnError { Log.e("error",it.message) }
                    .doOnNext { Log.e("result","success") }}
                .map<LoginViewState> { LoginViewState.SuccessAuthState }
                .startWith(LoginViewState.LoadingState)
                .onErrorReturn { LoginViewState.ErrorState(it) }
                .doOnNext { Log.e("trigger",it.toString()) }

            }

        subscribeViewState(authResponseData.distinctUntilChanged(), LoginView::render)
    }

}