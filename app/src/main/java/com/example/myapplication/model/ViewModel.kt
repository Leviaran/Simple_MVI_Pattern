package com.example.myapplication.model

import android.databinding.ObservableArrayList
import com.example.myapplication.R
import com.example.myapplication.model.homemodel.ActivityFeedModel
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import me.tatarka.bindingcollectionadapter2.recyclerview.BR

class ViewModel {

    interface OnItemClickListener {
        fun onItemClick(result : ActivityFeedModel)
    }

    val items = ObservableArrayList<ActivityFeedModel>()
    lateinit var onItemClickListener: OnItemClickListener

    val mergeItems = MergeObservableList<Any>()
        .insertList(items)

    val multipleItem = OnItemBindClass<Any>()
        .map(String::class.java, ItemBinding.VAR_NONE, R.layout.item_loading)
        .map(ActivityFeedModel::class.java, object : OnItemBindClass<ActivityFeedModel>() {

            override fun onItemBind(itemBinding: ItemBinding<*>?, position: Int, item: ActivityFeedModel?) {
                itemBinding?.let {
                    it.clearExtras()
                        .set(BR.itemResult, R.layout.item_row)
                        .bindExtra(BR.listener, onItemClickListener)
                }
            }
        })
}