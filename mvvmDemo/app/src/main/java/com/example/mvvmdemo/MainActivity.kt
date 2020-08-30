package com.example.mvvmdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mvvmdemo.main.ui.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) { // 这一行不能丢
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MainFragment())
                .commitAllowingStateLoss()
        }
    }
}
