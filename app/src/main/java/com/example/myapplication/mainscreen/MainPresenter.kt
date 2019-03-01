package com.example.myapplication.mainscreen

class MainPresenter(val repo : MainViewRepository) {
    fun sayHello() = "${repo.giveHello()} from ${this}"

}