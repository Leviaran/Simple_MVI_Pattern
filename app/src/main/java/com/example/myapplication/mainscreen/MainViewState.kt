package com.example.myapplication.mainscreen

import com.example.myapplication.model.Result
import com.example.myapplication.model.homemodel.ActivityFeedModel

sealed class MainViewState {
    object LoadingState : MainViewState()
    data class DataState(val result: List<ActivityFeedModel>) : MainViewState()
    data class NextDataState(val result: List<ActivityFeedModel>) : MainViewState()
    data class PullRefreshState(val result: List<ActivityFeedModel>) : MainViewState()
    data class ErrorState(val error: Throwable) : MainViewState()
    data class SearchState(val result: List<ActivityFeedModel>) : MainViewState()
}