package com.philips.platform.ccbdemo

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // AnimatedDotsView red = (AnimatedDotsView) findViewById(R.id.adv_1);
        adv_1.startAnimation();

        //final AnimatedDotsView yellow = (AnimatedDotsView) findViewById(R.id.adv_2);
        adv_2.startAnimation();

//        val animators = arrayOfNulls<ObjectAnimator>(3)

        var animators = arrayOf(ivanim1, ivanim2, ivanim3)

        val animatorSet = AnimatorSet()

        val alphaInAnim1 = ObjectAnimator.ofObject(ivanim1, "color",ArgbEvaluator(), Color.parseColor("#777777"), Color.parseColor("#FF00FF00"))

        alphaInAnim1.duration = 100
        alphaInAnim1.startDelay = 100
        //alphaInAnim.interpolator = ACCELERATE_INTERPOLATOR

        val alphaInAnim2 = ObjectAnimator.ofObject(ivanim2, "color",ArgbEvaluator(), Color.parseColor("#777777"), Color.parseColor("#FF00FF00"))
        alphaInAnim2.duration = 100
        alphaInAnim2.startDelay = 100

        val alphaInAnim3 = ObjectAnimator.ofObject(ivanim3, "color",ArgbEvaluator(), Color.parseColor("#777777"), Color.parseColor("#FF00FF00"))
        alphaInAnim3.duration = 100
        alphaInAnim3.startDelay = 100

        animatorSet.playSequentially(alphaInAnim1,alphaInAnim2,alphaInAnim3)
        animatorSet.interpolator = AccelerateInterpolator(2.0f)
        animatorSet.start()
    }
}
