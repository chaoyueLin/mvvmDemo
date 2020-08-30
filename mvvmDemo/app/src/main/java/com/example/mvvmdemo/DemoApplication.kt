
package com.example.mvvmdemo

import android.app.Application


class DemoApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object{
        var INSTANCE:DemoApplication?=null
    }
}