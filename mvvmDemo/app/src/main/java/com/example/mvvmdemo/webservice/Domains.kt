package com.example.mvvmdemo.webservice

import com.example.mvvmdemo.struct.BaseUrl
import com.example.mvvmdemo.struct.CallAdapter
import com.example.mvvmdemo.struct.ConvertFactory
import com.example.mvvmdemo.struct.WebServiceFactory



object Domains {
    @Volatile
    var ENV =WebServiceFactory.NORMAL
    set(value) {
        field=value
    }

    var USE_HTTPS =false
    set(value) {
        field =value
    }

    private const val RELEASE_HOST_HTTPS = "https://gank.io/api/"
    private const val RELEASE_HOST_HTTP  = "http://gank.io/api/"
    private const val PRE_RELEASE_HOST_HTTPS = "https://gank.io/api/"
    private const val PRE_RELEASE_HOST_HTTP  = "http://gank.io/api/"
    private const val DEV_HOST_HTTPS = "https://gank.io/api/"
    private const val DEV_HOST_HTTP  = "http://gank.io/api/"
    private const val TEST_HOST_HTTPS = "https://gank.io/api/"
    private const val TEST_HOST_HTTP  = "http://gank.io/api/"


    //主url配置
    @BaseUrl(value   = [RELEASE_HOST_HTTPS, RELEASE_HOST_HTTP],        // 默认环境
        pre_rls = [PRE_RELEASE_HOST_HTTPS, PRE_RELEASE_HOST_HTTP],// 预发布环境
        dev     = [DEV_HOST_HTTPS, DEV_HOST_HTTP],// 开发环境
        test    = [TEST_HOST_HTTPS, TEST_HOST_HTTP]) // 测试环境
    @CallAdapter(CallAdapter.CallAdapterType.RxJava2)
    @ConvertFactory(ConvertFactory.DataType.Gson)
    internal class MainURL

    /**
     * 主域名
     */
    val MAIN by lazy {
        DemoDomain(
            Domains.MainURL::class.java,
            ENV,
            USE_HTTPS
        )
    }
}