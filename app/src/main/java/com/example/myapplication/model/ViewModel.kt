package com.example.myapplication.model

import android.databinding.ObservableArrayList
import com.example.myapplication.R
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import me.tatarka.bindingcollectionadapter2.recyclerview.BR

class ViewModel {
    val items = ObservableArrayList<Result>()

    val mergeItems = MergeObservableList<Any>()
        .insertList(items)

    val multipleItem = OnItemBindClass<Any>()
        .map(String::class.java, ItemBinding.VAR_NONE, R.layout.item_loading)
        .map(Result::class.java, BR.itemResult, R.layout.item_row)

}