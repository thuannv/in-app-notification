package com.github.thuannv.inappnotification.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

object Controller {

    private const val MAX_DEPTH = 5

    var depth: Int = 1

    var direction: Int = 1

    fun op() {
        depth += direction
        if (depth >= MAX_DEPTH) {
            direction = -1
        } else if (depth == 1) {
            direction = 1
        }
    }
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Demo activity count = ${Controller.depth}"

        button_toggle_view.setOnClickListener {
            if (Controller.direction > 0) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                finish()
            }
            Controller.op()
        }
    }

    override fun onResume() {
        super.onResume()
        button_toggle_view.text = if (Controller.direction > 0) "New Activity" else "Kill Activity"
    }
}