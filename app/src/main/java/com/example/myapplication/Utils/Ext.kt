package com.example.myapplication.Utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView


fun SearchView.setError(errorMessage : String) {
    val id = this.context.resources.getIdentifier("android:id/search_src_text", null, null)
    val editText = this.findViewById<EditText>(id)
    editText.setError(errorMessage)
}

fun Context.hideKeyboard(view : View) {
    val inputMethod = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethod.hideSoftInputFromWindow(view.windowToken, 0)
}