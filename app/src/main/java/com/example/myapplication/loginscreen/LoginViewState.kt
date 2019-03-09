package com.example.myapplication.loginscreen

import com.example.myapplication.Utils.BaseViewState
import com.example.myapplication.model.Result
import com.example.myapplication.model.loginmodel.LoginResponse

sealed class LoginViewState {
    object LoadingState : LoginViewState()
    data class DataState(val result: LoginResponse) : LoginViewState()
    data class ErrorState(val error : Throwable) : LoginViewState()
}