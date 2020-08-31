package com.github.thuannv.inappnotification.sample

import android.app.Application

class SampleApp: Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationManager.init(this)
    }
}