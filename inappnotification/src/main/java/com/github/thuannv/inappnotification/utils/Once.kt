package com.github.thuannv.inappnotification.utils

import java.util.concurrent.atomic.AtomicBoolean

class Once {

    private val isExecuted = AtomicBoolean(false)

    fun execute(block: () -> Unit) {
        if (isExecuted.compareAndSet(false, true)) {
            return block()
        }
    }
}