package com.example.myapplication.model

import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.util.Log
import com.example.myapplication.R
import com.example.myapplication.mainscreen.BASE_URL
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.OnItemBind
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import me.tatarka.bindingcollectionadapter2.recyclerview.BR
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ViewModel {
    val items = ObservableArrayList<Result>()

    val mergeItems = MergeObservableList<Any>()
        .insertList(items)

    val multipleItem = OnItemBindClass<Any>()
        .map(String::class.java, ItemBinding.VAR_NONE, R.layout.item_loading)
        .map(Result::class.java, BR.itemResult, R.layout.item_row)

    fun getObservableResult(textSrc: String, page : Int): Observable<List<Result>> {

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
//            .doOnNext { mergeItems.insertItem(it) }
    }

}