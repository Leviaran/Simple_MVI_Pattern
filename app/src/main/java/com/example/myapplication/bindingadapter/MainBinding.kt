package com.example.myapplication.bindingadapter

import android.databinding.BindingAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.mainscreen.BASE_URL
import com.example.myapplication.mainscreen.State

object MainBinding {

    @BindingAdapter("app:set_circ_image")
    @JvmStatic
    fun setImage(imageView: ImageView, urlStr : String) {
        Glide.with(imageView.context)
            .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
//            .load("http://localhost:1337/uploads/cfb2f5c3525d45828976bcb8a99a3668.jpeg")
            .load("https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg?cs=srgb&dl=face-facial-hair-fine-looking-614810.jpg&fm=jpg")
//            .load("$BASE_URL$urlStr")
            .into(imageView)
    }

    @BindingAdapter("app:set_image")
    @JvmStatic
    fun setStandarImage(imageView: ImageView, urlStr: String) {
        Glide.with(imageView.context)
            .applyDefaultRequestOptions(RequestOptions().centerCrop())
//            .load("http://localhost:1337/uploads/50abb249462f4cddb2cc29af73e8b788.jpg")
            .load("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?cs=srgb&dl=background-calm-clouds-747964.jpg&fm=jpg")
//            .load("$BASE_URL$urlStr")
            .into(imageView)
    }

    @BindingAdapter("app:start_lottie_anim")
    @JvmStatic
    fun startAnim(lottieAnimationView: LottieAnimationView, state: State) {
        when(state) {
            State.LOADING -> lottieAnimationView.apply {
                setAnimation("loading.json")
                playAnimation()
                visibility = View.VISIBLE
            }

            State.SUCCESS -> lottieAnimationView.apply {
                cancelAnimation()
                clearAnimation()
                visibility = View.GONE
            }

            State.FAILED -> lottieAnimationView.apply {
                setAnimation("error.json")
                playAnimation()
                visibility = View.VISIBLE
            }

            State.NEUTRAL -> lottieAnimationView.apply {
                cancelAnimation()
                clearAnimation()
                visibility = View.GONE
            }
        }
    }

    @BindingAdapter("app:state_swipe_refresh")
    @JvmStatic
    fun setState(swipeRefreshLayout: SwipeRefreshLayout, state: State) {
        if (state is State.SUCCESS || state is State.FAILED)
            swipeRefreshLayout.isRefreshing = false
    }

}