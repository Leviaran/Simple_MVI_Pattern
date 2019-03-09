package com.example.myapplication.mainscreen

import android.annotation.SuppressLint
import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.Utils.hideKeyboard
import com.example.myapplication.Utils.setError
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.Result
import com.example.myapplication.model.ViewModel
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.jakewharton.rxrelay2.PublishRelay
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit


const val BASE_URL = "https://2f0243cd.ngrok.io/"

class MainActivity : MviActivity<MainView, MainPresenter>(), MainView, ViewModel.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val unsubs = CompositeDisposable()
    private val onClickPublishSubject = PublishRelay.create<Result>()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = ViewModel()
        binding.state = State.NEUTRAL
        binding.viewModel?.onItemClickListener = this

        initPublishSubject()
    }

    private fun initPublishSubject() {
        val onClickSubscription = onClickPublishSubject
            .debounce(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({Log.e("hasilnya",it.toString())},
                {Log.e("erorr", it.message)})

        unsubs.add(onClickSubscription)
    }

    override fun loadNextData(): Observable<Boolean> {
        return RxRecyclerView.scrollStateChanges(binding.rvRecipeItem)
            .filter { event -> event == RecyclerView.SCROLL_STATE_IDLE }
            .filter { getRecyclerLayoutManager(binding.rvRecipeItem)
                    .findLastCompletelyVisibleItemPosition() == getTotalDataInReyclerViewAdapter(binding.rvRecipeItem) - 1
            }
            .map { true }
    }

    @SuppressLint("CheckResult")
    override fun loadSearchData() : Observable<String> {
        return RxSearchView.queryTextChanges(binding.svSearchRecipe)
            .skip(2)
            .debounce(1000, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .filter { !TextUtils.isEmpty(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .filter { if (it.length < 3) { binding.svSearchRecipe.setError("Your keyword less than 3"); return@filter false}
            else return@filter true}
            .map(CharSequence::toString)
    }

    override fun loadFirstData(): Observable<Boolean> {
        return Observable.just(true)
    }

    override fun render(state: MainViewState) {
        when (state) {
            is MainViewState.LoadingState -> renderLoadingState()
            is MainViewState.DataState -> renderDataState(state.result, state)
            is MainViewState.ErrorState -> renderErrorState(state.error)
            is MainViewState.NextDataState -> renderDataState(state.result, state)
            is MainViewState.SearchState -> renderSearchState(state.result)
        }
    }

    override fun onItemClick(result: Result) {
        onClickPublishSubject.accept(result)
    }

    override fun createPresenter(): MainPresenter = MainPresenter()

    private fun renderLoadingState() {
        resetAllValue()
        setIsBotLoading(false)
    }

    private fun renderDataState(dataState: List<Result>, state: MainViewState) {
        if (state is MainViewState.DataState) binding.viewModel?.mergeItems?.insertItem("footer")
        binding.viewModel?.items?.addAll(dataState)
        binding.executePendingBindings()
        binding.state = State.SUCCESS
    }

    private fun renderSearchState(dataState: List<Result>) {
        binding.viewModel?.mergeItems?.insertItem("footer")
        binding.viewModel?.items?.addAll(dataState)
        binding.executePendingBindings()
        binding.state = State.SUCCESS
        baseContext.hideKeyboard(binding.root)
    }

    private fun renderErrorState(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_LONG).show()
    }

    private fun getTotalDataInReyclerViewAdapter(recyclerView: RecyclerView): Int {
        return recyclerView.adapter?.itemCount ?: 0
    }

    private fun getRecyclerLayoutManager(recyclerView: RecyclerView): LinearLayoutManager {
        return (recyclerView.layoutManager as LinearLayoutManager)
    }

    private fun resetAllValue() {
        binding.viewModel?.apply {
            items.clear()
            mergeItems?.removeItem("footer")
        }
    }

    private fun setIsBotLoading(isBottomLoading: Boolean) {
        when {
            isBottomLoading -> binding.viewModel?.mergeItems?.insertItem("footer")
            else -> binding.state = State.LOADING
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!unsubs.isDisposed) unsubs.dispose()
    }
}

sealed class State {
    object LOADING : State()
    object SUCCESS : State()
    object FAILED : State()
    object NEUTRAL : State()
}