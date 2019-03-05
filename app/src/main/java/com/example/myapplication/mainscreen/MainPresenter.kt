package com.example.myapplication.mainscreen

import com.androidnetworking.interceptors.HttpLoggingInterceptor
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
    val FIRST_SEARCH = "omelet"

    fun getObservableResult(textSrc: String, page: Int): Observable<List<Result>> {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

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
                getObservableResult(FIRST_SEARCH, currentPage)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { MainViewState.DataState(it) as MainViewState }
                    .startWith(MainViewState.LoadingState)
                    .onErrorReturn { MainViewState.DataState(listOf()) }
                    .doAfterTerminate { currentPage++ }
            }

        val nextData: Observable<MainViewState> = intent(MainView::loadNextData)
            .subscribeOn(Schedulers.io())
            .switchMap {
                getObservableResult(FIRST_SEARCH, currentPage)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { MainViewState.NextDataState(it) as MainViewState }
                    .onErrorReturn { MainViewState.DataState(listOf()) }
                    .doAfterTerminate { currentPage++ }
            }

        val searchData: Observable<MainViewState> = intent(MainView::loadSearchData)
            .map { currentPage = 1; return@map it }
            .subscribeOn(Schedulers.io())
            .switchMap { getObservableResult(it, currentPage)
                .observeOn(AndroidSchedulers.mainThread())
                .map { MainViewState.SearchState(it) as MainViewState }
                .startWith(MainViewState.LoadingState)
                .onErrorReturn { MainViewState.DataState(listOf()) }
                .doAfterTerminate { currentPage++ }
            }

        val allIntentObservable = Observable.merge(firstDataState, nextData, searchData)
            .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(allIntentObservable.distinctUntilChanged(), MainView::render)
    }
}