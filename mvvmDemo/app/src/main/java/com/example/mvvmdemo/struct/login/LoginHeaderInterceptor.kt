
package com.example.mvvmdemo.struct.login

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class LoginHeaderInterceptor :Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        // 同步获取登录态
        val loginInfo = blockingGetUserInfo()
        val origin = chain.request()
        updateRequest(loginInfo, origin)
        return chain.proceed(updateRequest(loginInfo, origin))
    }

    private fun updateRequest(loginInfo: String, request: Request): Request {
        // 自己根据业务修改http请求头, 这里只是演示
        val url = request.url().newBuilder()
            .addQueryParameter("session", loginInfo) // 自己加一些对应的登录态
            .build()
        return request /*request.newBuilder().url(url).build()*/
    }

    // 同步获取登录态
    private fun blockingGetUserInfo() : String {
        // TODO 自己根据业务实现
        return "Hahahaa"
    }

}