
package com.example.mvvmdemo.struct


interface IDomain {

    fun <T> service(clz:Class<T>):T
}