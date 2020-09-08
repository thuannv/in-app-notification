package com.github.thuannv.inappnotification.sample

import android.app.Application
import com.github.thuannv.inappnotification.NotificationManager

class SampleApp: Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationManager.init(this)
        Scheduler.schedule(applicationContext)
    }
}