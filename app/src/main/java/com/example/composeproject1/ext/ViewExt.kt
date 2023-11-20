package com.example.composeproject1.ext

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("ClickableViewAccessibility")
fun View.longSeriesClickListener(
    timeSpace: Int = 300,
    scope: CoroutineScope,
    clickFunc: () -> Unit
) {
    var isDown = false
    var isFirstDeal = false
    var curJob: Job? = null
    setOnTouchListener { _, event ->
        val isMove = event.action == MotionEvent.ACTION_MOVE
        if (!isMove) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                isDown = true
            }
            isFirstDeal = false
            curJob?.cancel()
        }
        if (!isFirstDeal && isDown && (isMove || event.action == MotionEvent.ACTION_DOWN)) {
            clickFunc.invoke()
            isFirstDeal = true
            curJob?.cancel()
            curJob = scope.launch(Dispatchers.Main.immediate) {
                while (isDown) {
                    delay(timeSpace.toLong())
                    clickFunc.invoke()
                }
            }
        }
        true
    }
}
