package com.example.myapplication.mainscreen

import com.example.myapplication.model.Result

sealed class MainViewState {
    object LoadingState : MainViewState()
    data class DataState(val result: List<Result>) : MainViewState()
    data class NextDataState(val result: List<Result>) : MainViewState()
    data class PullRefreshState(val result: List<Result>) : MainViewState()
    data class ErrorState(val error: Throwable) : MainViewState()
}