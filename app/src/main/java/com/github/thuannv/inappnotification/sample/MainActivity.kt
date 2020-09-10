package com.github.thuannv.inappnotification.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var depth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        depth = intent.getIntExtra(DEPTH, 0)

        title = if (depth > 0) "Demo depth$depth" else "Demo"

        button_toggle_view.setOnClickListener {
            if (depth < MAX_DEPTH) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(DEPTH, depth + 1)
                startActivity(intent)
            } else {
                finish()
            }
        }
    }

    companion object {
        const val DEPTH = "depth"
        const val MAX_DEPTH = 5
    }
}