package com.example.myapplication.mainscreen

import com.example.myapplication.model.RecipeModel
import com.example.myapplication.model.Result
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient

class MainPresenter : MviBasePresenter<MainView, MainViewState>() {

    var currentPage = 1

    fun getObservableResult(textSrc: String, page: Int): Observable<List<Result>> {

        val interceptor = com.androidnetworking.interceptors.HttpLoggingInterceptor()
        interceptor.level = com.androidnetworking.interceptors.HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .build()

        return Rx2AndroidNetworking
            .get(BASE_URL)
            .addQueryParameter("q", textSrc)
            .addQueryParameter("p", "$page")
            .setOkHttpClient(okHttpClient)
            .build()
            .getObjectObservable(RecipeModel::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.results }

    }

    override fun bindIntents() {
        val firstDataState: Observable<MainViewState> = intent(MainView::loadFirstData)
            .map { currentPage = 1; return@map true }
            .switchMap {
                getObservableResult("omelet", currentPage)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { MainViewState.DataState(it) as MainViewState }
                    .startWith(MainViewState.LoadingState)
                    .onErrorReturn { MainViewState.DataState(listOf()) }
                    .doAfterTerminate { currentPage++ }
            }

        val nextData: Observable<MainViewState> = intent(MainView::loadNextData)
            .subscribeOn(Schedulers.io())
            .switchMap {
                getObservableResult("omelet", currentPage)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { MainViewState.NextDataState(it) as MainViewState }
                    .onErrorReturn { MainViewState.DataState(listOf()) }
                    .doAfterTerminate { currentPage++ }
            }

        val allIntentObservable = Observable.merge(firstDataState, nextData)
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntentObservable.distinctUntilChanged(), MainView::render)
    }
}