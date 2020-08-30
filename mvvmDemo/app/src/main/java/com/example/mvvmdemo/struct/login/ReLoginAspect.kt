
package com.example.mvvmdemo.struct.login

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method


class ReLoginAspect<T>(private val webService: T):InvocationHandler{
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}