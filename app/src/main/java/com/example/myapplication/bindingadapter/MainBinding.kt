package com.example.myapplication.bindingadapter

import android.databinding.BindingAdapter
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.mainscreen.State

object MainBinding {

    @BindingAdapter("app:set_image")
    @JvmStatic
    fun setImage(imageView: ImageView, urlStr : String) {
        Glide.with(imageView.context)
            .load(urlStr)
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

}