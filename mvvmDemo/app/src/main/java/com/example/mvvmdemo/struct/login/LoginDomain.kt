
package com.example.mvvmdemo.struct.login

import com.example.mvvmdemo.struct.IDomain
import com.example.mvvmdemo.struct.WebServiceFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.lang.reflect.Proxy

abstract class LoginDomain(clz:Class<*>,env:Int,useHttps:Boolean):IDomain {

    /**
     * 初始化带登录态的okhttp
     */
    open fun initLoginOkhttp(builder: OkHttpClient.Builder) {

    }

    /**
     * 初始化不带登录态的okhttp
     */
    open fun initWithoutLoginOkhttp(builder: OkHttpClient.Builder) {

    }

    /**
     * 需要登录态的Retrofit对象, 通过此对象发出的http请求都会自动带上登录态
     */
    private val retrofitWithLogin: Retrofit by lazy {
        WebServiceFactory.createRetrofit(
            clz,
            OkHttpClient.Builder().apply { initLoginOkhttp(this) }.build(),
            env,
            useHttps
        )
    }

    /**
     * 不需要登录态的Retrofit对象, 通过此对象发出的http请求不会自动带上登录态
     */
    private val retrofitWithoutLogin: Retrofit by lazy {
        WebServiceFactory.createRetrofit(
            clz,
            OkHttpClient.Builder().apply { initWithoutLoginOkhttp(this) }.build(),
            env,
            useHttps
        )
    }
    override fun <T> service(clz: Class<T>): T {
       val needLogin=clz.getAnnotation(Login::class.java)?.value?:true
        return if (!needLogin){
            retrofitWithoutLogin.create(clz)
        }else{
            retrofitWithLogin.create(clz)
//        // 如果有登录态, 需要拦截
//            Proxy.newProxyInstance(
//                clz.classLoader,
//                arrayOf<Class<*>>(clz),
//                ReLoginAspect(retrofitWithLogin.create(clz))
//            ) as T
        }

    }

    companion object {
        private const val DEFAULT_MILLISECONDS = 20000L       //默认的超时时间
        const val TAG: String = "LoginDomain"
    }
}