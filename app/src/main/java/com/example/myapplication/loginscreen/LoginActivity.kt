package com.example.myapplication.loginscreen

import android.app.ProgressDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import com.example.myapplication.R
import com.example.myapplication.Utils.hideKeyboard
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.mainscreen.MainActivity
import com.example.myapplication.model.loginmodel.LoginRequest
import com.example.myapplication.model.loginmodel.LoginResponse
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class LoginActivity : MviActivity<LoginView, LoginPresenter>(), LoginView {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var progressDialog : ProgressDialog
    private lateinit var authTextInputRequest: Observable<LoginRequest>
    private var rxRelay = PublishRelay.create<LoginRequest>()

    private val verifyEmailPattern = ObservableTransformer<String, String> {
            observable -> observable.flatMap { Observable.just(it).map { it.trim() }
            .filter { Patterns.EMAIL_ADDRESS.matcher(it).matches() }
            .singleOrError()
            .onErrorResumeNext {
                if (it is NoSuchElementException) {
                    Single.error(Exception("Email Not valid"))
                } else {
                    Single.error(it)
                }
            }.toObservable()
    } }

    private val verifyTextLengt = ObservableTransformer<String, String> {
            observable -> observable.flatMap { Observable.just(it).map { it.trim() }
            .filter { it.length > 6 }
            .singleOrError()
            .onErrorResumeNext {
                if (it is NoSuchElementException) {
                    Single.error(Exception("Lengt must be greater than six"))
                } else {
                    Single.error(it)
                }
            }.toObservable()
    } }

    override fun legacyAuth(): Observable<LoginRequest> {
        return rxRelay.observeOn(AndroidSchedulers.mainThread())
    }

    private inline fun retryWhenError(crossinline onError : (ex : Throwable) -> Unit) : ObservableTransformer<String, String> =
        ObservableTransformer { observable -> observable.retryWhen {
            error -> error.flatMap {
            onError(it)
            Observable.just("") }
        }}

    override fun render(state: LoginViewState) {
        when (state) {
            is LoginViewState.SuccessAuthState -> authenticationSuccess()
            is LoginViewState.LoadingState -> showLoading()
            is LoginViewState.ErrorState -> showError(state.error)
        }
    }

    private fun authenticationSuccess() {
        progressDialog.dismiss()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showLoading() {
        progressDialog.setMessage("Loading")
        progressDialog.show()
        baseContext.hideKeyboard(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun showError(throwable: Throwable) {
        Snackbar.make(binding.root, throwable.message.toString(), Snackbar.LENGTH_SHORT).show()
        progressDialog.dismiss()
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun createPresenter(): LoginPresenter = LoginPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        progressDialog = ProgressDialog(this)

        RxTextView.afterTextChangeEvents(binding.tilUsername)
            .skipInitialValue()
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map { binding.tilUsername.error = null; it.view().text.toString() }
            .compose(verifyTextLengt)
            .compose(verifyEmailPattern)
            .compose(retryWhenError { binding.tilUsername.error = it.message })
            .switchMap { userName -> RxTextView.afterTextChangeEvents(binding.tilPassword)
                .skipInitialValue()
                .debounce(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { binding.tilPassword.error = null; it.view().text.toString() }
                .compose(verifyTextLengt)
                .compose(retryWhenError { binding.tilPassword.error = it.message })
                .map { password -> LoginRequest(userName, password) } }
            .switchMap { loginrequest -> RxView.clicks(binding.btnSignin)
                .map { rxRelay.accept(loginrequest) }}
            .subscribe()
    }
}