package com.example.myapplication.Utils

import android.widget.EditText
import android.widget.SearchView


fun SearchView.setError(errorMessage : String) {
    val id = this.context.resources.getIdentifier("android:id/search_src_text", null, null)
    val editText = this.findViewById<EditText>(id)
    editText.setError(errorMessage)
}