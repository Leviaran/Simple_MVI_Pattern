package com.example.myapplication.mainscreen

import android.util.Log
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.example.myapplication.loginscreen.USER_PREF
import com.example.myapplication.model.RecipeModel
import com.example.myapplication.model.Result
import com.example.myapplication.model.homemodel.ActivityFeedModel
import com.example.myapplication.model.loginmodel.LoginResponse
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.paperdb.Book
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

const val AUTH = "Authorization"

class MainPresenter : MviBasePresenter<MainView, MainViewState>(), KoinComponent {

    var currentPage = 1
    val FIRST_SEARCH = "omelet"

    val bookPref : Book by inject()

    fun getUserLocalData() : Observable<LoginResponse> {
        return Observable.just(bookPref)
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.read<LoginResponse>(USER_PREF) }
    }

    fun getObservableResult(textSrc: String, page: Int): Observable<List<ActivityFeedModel>> {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .build()

        return  getUserLocalData()
            .switchMap { localData ->
                Rx2AndroidNetworking
                .get("$BASE_URL/feedposts")
                .addHeaders(AUTH, "Bearer ${localData.jwt}")
                .setOkHttpClient(okHttpClient)
                .build()
                .getObjectListObservable(ActivityFeedModel::class.java)
                    .doOnError { Log.e("error", it.message) }
                    .doOnNext { Log.e("resul", it.toString()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) }
    }

    override fun bindIntents() {
        val firstDataState: Observable<MainViewState> = intent(MainView::loadFirstData)
            .map { currentPage = 1; return@map true }
            .switchMap {
                getObservableResult(FIRST_SEARCH, currentPage)
                    .doOnNext { Log.e("hasil",it.toString())}
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