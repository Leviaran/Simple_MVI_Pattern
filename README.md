![Made in](https://img.shields.io/badge/made%20in-kotlin-blue.svg)
[![Build Status](https://travis-ci.org/Leviaran/Simple_MVI_Pattern.svg?branch=master)](https://travis-ci.org/Leviaran/Simple_MVI_Pattern)

# Simple MVI Pattern
Simple Model View Intent (MVI) architecture app with reactive paradigm. This is pattern based on [cycle.js](https://youtu.be/1zj7M1LnJV4), the workflow is undirectional and circular data flow, each flow have state from user interaction(intent). This new state is rendered on view and trigger new update view to show the user.

> EVENT IS DATA, DATA IS EVENT

![cycle.js](http://hannesdorfmann.com/images/mvi/mvi-cicle.png)

# Motivation
Make simple state and avoid side effect so developer can debug and track easely. Side effect can avoided if that project use reactive paradigm, this way of creating an immutable model will ensure thread safety. 
No Callback, Usually views are updated via callbacks after the asynchronous call. These checks and callbacks are handled automatically in MVI using reactive programming that supports observer.
Independent UI, Using observable components observe the same model then get the notification if the model gets updated.

# Dependencies
The core libraries used are:
* RxAndroid
* RxJava2
* RxKotlin
* RxBinding-kotlin
* RxRelay
* RxAndroid-networking
* Binding-Collection-adapter
* Mosby-MVI
* Glide
* Koin-Android
* Lottie

# Todo
what we will going to do
* Fixing loadmore animation
* Fixing the design
* Fixing Search keyword
* Fixing Pull Refresh layout
* Adding detail

