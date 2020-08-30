
package com.example.mvvmdemo.webservice

import android.util.Log
import com.example.mvvmdemo.struct.login.LoginDomain
import com.example.mvvmdemo.struct.login.LoginHeaderInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response


class DemoDomain(clz: Class<*>, env: Int, useHttps: Boolean) : LoginDomain(clz, env, useHttps) {
    override fun initLoginOkhttp(builder: OkHttpClient.Builder) {
        // 可以添加对应的拦截器
        builder.addInterceptor(LoginHeaderInterceptor())
        builder.addInterceptor(HttpLogInterceptor())
    }

    override fun initWithoutLoginOkhttp(builder: OkHttpClient.Builder) {
        builder.addInterceptor(HttpLogInterceptor())
    }

    inner class HttpLogInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val reqId = request.url().hashCode()
            Log.i(TAG, "[REQUEST-$reqId] ${request.url()}")
            return chain.proceed(request).apply {
                Log.i(TAG, "[REQUEST-$reqId] size: ${this.body()?.contentLength()}")
            }
        }

    }
}