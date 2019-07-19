package com.zuluft

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity :
        AppCompatActivity(),
        ContainerWithTitle {

    override fun setTitle(title: String) {
        tvTitle.text = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (supportFragmentManager.findFragmentById(R.id.flContent) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(
                            R.id.flContent,
                            MatchConfirmationFragment()
                    )
                    .commit()
        }
    }
}
