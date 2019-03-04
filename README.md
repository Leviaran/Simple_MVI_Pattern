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

# Recommended Reading
* [Multi-part series on MVI by Hannes Dorfmann](http://hannesdorfmann.com/android/mosby3-mvi-1)
* [Mosby library](https://github.com/sockeqwe/mosby)
* [Managing state with RxJava talk by Jake Wharton](https://www.youtube.com/watch?v=0IKHxjkgop4)
* [Cycle.js by André Staltz](https://cycle.js.org/)
* [Introduce Reactive programming](http://blog.danlew.net/2017/07/27/an-introduction-to-functional-reactive-programming/)
* [Get to know MVI](http://thenewstack.io/developers-need-know-mvi-player-view-intent/)
* [Anothe MV band](https://proandroiddev.com/mvi-a-new-member-of-the-mv-band-6f7f0d23bc8a)
