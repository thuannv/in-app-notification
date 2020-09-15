[![](https://jitpack.io/v/thuannv/in-app-notification.svg)](https://jitpack.io/#thuannv/in-app-notification)

# In-app Notification 
Library for showing in-app notification in any activity. This library helps you overcome the requirement of show in drop down notification when user is using app since you might have to maintain the legacy application architecture, which is not a single Activity app as well as you do not want to ask your user permission `android.permission.SYSTEM_ALERT_WINDOW`.

## Demo
![in_app_notification_demo.gif](assets/in_app_notification_demo.gif)


## Getting started
Add it in your root `build.gradle` at the end of repositories:
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

In your app `build.gradle`:

```gradle
dependencies {
	implementation 'com.github.thuannv:in-app-notification:1.0.0'
}
```

## Sample usage to show notification

First, you must call `init()` on application onCreate()
```java
class SampleApp: Application() {

    override fun onCreate() {
        super.onCreate()
        ...
        InAppNotificationManager.init(this)
        ...
    }
}
```

Abstract notification information class
```java
abstract class InAppNotificationInfo(
    val context: Context? = null,
    val autoDismiss: Boolean = false,
    val autoDismissMillis: Long = 0
) {

    abstract fun getView(): View?
}
```

A discrete implementation notification information
```java
class NormalNotificationInfo(
    private val title: String,
    private val description: String,
    context: Context? = null,
    autoDismiss: Boolean = false,
    autoDismissMillis: Long = 0,
) : InAppNotificationInfo(context, autoDismiss, autoDismissMillis) {

    override fun getView(): View? {
        return context?.let { ctx ->
            ctx.layoutInflater()
                .inflate(R.layout.layout_notification, FrameLayout(ctx), false)
                .also { view ->
                    view.findViewById<TextView>(R.id.first_line_text)?.text = title
                    view.findViewById<TextView>(R.id.second_line_text)?.text = description
                    view.findViewById<ImageView>(R.id.icon)?.apply {
                        Glide.with(this)
                            .load(R.drawable.ic_account)
                            .override(dp(64f), dp(64f))
                            .into(this)
                    }
                    view.setOnTouchListener { _, event ->
                        event?.let { ev ->
                            when (ev.action) {
                                MotionEvent.ACTION_UP -> Log.e("NormalNotification", "up")
                                MotionEvent.ACTION_DOWN -> Log.e("NormalNotification", "down")
                            }
                            true
                        } ?: false
                    }
                }
        }
    }
}
```
Show the notification
```java
val notification = NormalNotificationInfo.Builder()
      .title("Notification")
      .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.")
      .context(context)
      .autoDismiss(true)
      .autoDismissMillis(15000L)
      .build()

InAppNotificationManager.notify(notification)
```

## License

    Copyright (C) 2020 thuannv

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
