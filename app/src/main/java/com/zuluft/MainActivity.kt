package com.zuluft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.zuluft.lib.ExplosionView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val explosionView = findViewById<ExplosionView>(R.id.explosionView)
        findViewById<View>(R.id.btnExplode)
                .setOnClickListener {
                    explosionView.explode()
                }
        findViewById<View>(R.id.btnCollapse)
                .setOnClickListener {
                    explosionView.collapse()
                }
    }
}
