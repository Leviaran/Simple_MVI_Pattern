package com.example.myapplication.mainscreen

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.Result
import com.example.myapplication.model.ViewModel
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

const val BASE_URL = "http://www.recipepuppy.com/api/"

class MainActivity : RxAppCompatActivity() {

    val viewModel : ViewModel by inject<ViewModel>()
    private var currentPage = 1
    private lateinit var binding : ActivityMainBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewModel = ViewModel()
        binding.state = State.NEUTRAL


        val observer = Observable.defer<List<Result>> {
            viewModel.getObservableResult("omelet",1)
                .doOnSubscribe { setIsBotLoading(false) }
                .doAfterTerminate { currentPage++; binding.executePendingBindings() }}
            .map { binding.viewModel?.apply {
                items.addAll(it)
                mergeItems?.insertItem("footer")
            } }
            .compose(RxLifecycle.bindUntilEvent(lifecycle(), ActivityEvent.DESTROY))
            .share()

        observer.subscribe({ binding.state = State.SUCCESS}, {binding.state = State.FAILED})

        RxSearchView.queryTextChangeEvents(binding.svSearchRecipe)
            .throttleLast(100, TimeUnit.MILLISECONDS)
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .filter { !TextUtils.isEmpty(it.queryText()) }
            .doOnNext { binding.viewModel?.mergeItems?.removeAll(); currentPage = 0 }
            .flatMap {
                viewModel.getObservableResult(it.queryText().toString(),1)
                    .doOnSubscribe{setIsBotLoading(false)}
                    .doAfterTerminate { currentPage++; binding.executePendingBindings() } }
            .map {
                binding.viewModel?.apply {
                    items.addAll(it)
                    mergeItems?.insertItem("footer") }
                binding.executePendingBindings() }
            .compose(RxLifecycle.bindUntilEvent(lifecycle(), ActivityEvent.DESTROY))
            .subscribe({ binding.state = State.SUCCESS}, {binding.state = State.FAILED})

        binding.srlMainSwipe.setOnRefreshListener {
            currentPage = 1
            binding.viewModel?.items?.clear()
            binding.viewModel?.mergeItems?.removeItem("footer")
            observer.subscribe({ binding.state = State.SUCCESS; }, {binding.state = State.FAILED})
        }

        RxRecyclerView.scrollEvents(binding.rvRecipeItem)
            .filter {
                getTotalItemCount(it.view()) - 1 <= getLastVisiblePosition(it.view()) }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .flatMap {
                viewModel.getObservableResult("omelet", currentPage)
                    .doAfterTerminate { currentPage++; binding.executePendingBindings() } }
            .map { binding.viewModel?.items?.addAll(it) }
            .compose(RxLifecycle.bindUntilEvent(lifecycle(), ActivityEvent.DESTROY))
            .subscribe({binding.state = State.SUCCESS}, { binding.state = State.FAILED })
    }

    private fun getTotalItemCount(recyclerView: RecyclerView) : Int {
        return (recyclerView.layoutManager as LinearLayoutManager).itemCount
    }

    private fun getLastVisiblePosition(recyclerView: RecyclerView) : Int {
        return (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
    }

    private fun setIsBotLoading(isBottomLoading : Boolean) {
        when {
            isBottomLoading -> binding.viewModel?.mergeItems?.insertItem("footer")
            else -> binding.state = State.LOADING
        }
    }
}

sealed class State {
    object LOADING : State()
    object SUCCESS : State()
    object FAILED : State()
    object NEUTRAL : State()
}