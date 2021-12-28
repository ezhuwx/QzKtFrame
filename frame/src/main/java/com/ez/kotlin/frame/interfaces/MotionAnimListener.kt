package com.ez.kotlin.frame.interfaces

import androidx.constraintlayout.motion.widget.MotionLayout

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/12/16
 * E-mail : ezhuwx@163.com
 * Update on 13:55 by ezhuwx
 */
 interface MotionAnimListener : MotionLayout.TransitionListener {
    override fun onTransitionStarted(motion: MotionLayout?, startId: Int, endId: Int) {
    }

    override fun onTransitionChange(
        motion: MotionLayout?,
        startId: Int,
        endId: Int,
        position: Float
    ) {
    }

    override fun onTransitionCompleted(motion: MotionLayout?, currentId: Int) {
    }

    override fun onTransitionTrigger(
        motion: MotionLayout?,
        triggerId: Int,
        positive: Boolean,
        progress: Float
    ) {
    }
}