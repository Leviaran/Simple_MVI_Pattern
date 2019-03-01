package com.example.myapplication.mainscreen

interface MainView {
    fun giveHello() : String

}

class MainViewRepository() : MainView {
    override fun giveHello(): String {
        return "Hello Randy"
    }
}