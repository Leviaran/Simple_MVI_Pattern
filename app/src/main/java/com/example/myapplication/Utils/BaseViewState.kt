package com.example.myapplication.Utils

import com.example.myapplication.loginscreen.LoginViewState
import com.example.myapplication.model.loginmodel.LoginResponse

sealed class BaseViewState {
    object LoadingState : BaseViewState()
    data class DataState<T>(val result: T) : BaseViewState()
    data class ErrorState(val error : Throwable) : BaseViewState()
}