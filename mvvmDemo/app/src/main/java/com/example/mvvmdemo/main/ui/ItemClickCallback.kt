
package com.example.mvvmdemo.main.ui

import com.example.mvvmdemo.main.pojo.Gank


interface ItemClickCallback{
    fun onClick(gank:Gank)
}