package com.example.myapplication.mainscreen

import android.annotation.SuppressLint
import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.Result
import com.example.myapplication.model.ViewModel
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import io.reactivex.Observable


const val BASE_URL = "http://www.recipepuppy.com/api/"

class MainActivity : MviActivity<MainView, MainPresenter>(), MainView {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = ViewModel()
        binding.state = State.NEUTRAL
    }

    override fun loadNextData(): Observable<Boolean> {
        return RxRecyclerView.scrollStateChanges(binding.rvRecipeItem)
            .filter { event -> event == RecyclerView.SCROLL_STATE_IDLE }
            .filter { event ->
                getRecyclerLayoutManager(binding.rvRecipeItem)
                    .findLastCompletelyVisibleItemPosition() == getTotalDataInReyclerViewAdapter(binding.rvRecipeItem) - 1
            }
            .map { true }
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
        }
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

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

sealed class State {
    object LOADING : State()
    object SUCCESS : State()
    object FAILED : State()
    object NEUTRAL : State()
}