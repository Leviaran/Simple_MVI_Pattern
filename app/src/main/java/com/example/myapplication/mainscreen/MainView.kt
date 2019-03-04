package com.example.myapplication.mainscreen

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable

interface MainView : MvpView {
    fun loadFirstData(): Observable<Boolean>
    fun loadNextData(): Observable<Boolean>
    fun render(state: MainViewState)
}