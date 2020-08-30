
package com.example.mvvmdemo.struct


import okhttp3.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


internal object WebServiceFactory{
    const val NORMAL = 0 // 正式环境(默认)
    const val DEV = 1 // 开发环境
    const val TEST = 2 // 测试环境
    const val PRE_RLS = 3 // 测试环境

    fun <T> create(clz: Class<T>, env: Int): T {
        return createRetrofit(clz, null, env, true).create(clz)
    }

    fun <T> createRetrofit(clz:Class<T>, callFactory: Call.Factory?=null,env:Int= NORMAL,useHttps:Boolean):Retrofit{
        val baseUrlA=clz.getAnnotation(BaseUrl::class.java)
        val callAdapterA=clz.getAnnotation(CallAdapter::class.java)
        val convertFactoryA=clz.getAnnotation(ConvertFactory::class.java)
        return Retrofit.Builder()
            .baseUrl(getBaseUrl(baseUrlA, env, useHttps))
            .addAdapterFactoryByAnnotation(callAdapterA)
            .addConvertFactoryByAnnotation(convertFactoryA)
            .apply { if (callFactory !== null) callFactory(callFactory) }
            .build()
    }

    private fun getBaseUrl(baseUrlAnnotation:BaseUrl?,env:Int,useHttps:Boolean):String{
        // 保证annotation存在
        if(baseUrlAnnotation === null) {
            throw IllegalArgumentException("class need Annotation @BaseUrl")
        }
        // 保证baseUrl默认值不为空
        if (baseUrlAnnotation.value.isEmpty()) {
            throw IllegalArgumentException("class need Annotation @BaseUrl value")
        }

        var config:Array<out String> =when(env){
            TEST -> baseUrlAnnotation.test
            PRE_RLS->baseUrlAnnotation.pre_rls
            DEV->baseUrlAnnotation.dev
            else -> baseUrlAnnotation.value
        }

        config =if(config.isEmpty()) baseUrlAnnotation.value else config
        return when(config.size){
            0 -> throw IllegalArgumentException("class need Annotation @BaseUrl value") // 不可能出现
            1 -> config[0] // 只定义了一个
            else -> if (useHttps) config[0] else config[1]
        }
    }

    // call adapters
    private val RXJAVA2_CALL_ADAPTER_FACTORY by lazy {
        RxJava2CallAdapterFactory.create()
    }


    private fun Retrofit.Builder.addAdapterFactoryByAnnotation(callAdapterAnnotation:CallAdapter?):Retrofit.Builder{
        callAdapterAnnotation?.value?.forEach {
            when(it){
                CallAdapter.CallAdapterType.RxJava2 ->addCallAdapterFactory(RXJAVA2_CALL_ADAPTER_FACTORY)
                else ->{}
            }
        }
        return this
    }

    // converters
    private val SCALARS_CONVERTER_FACTORY by lazy {
        ScalarsConverterFactory.create()
    }

    private val GSON_CONVERTER_FACTORY by lazy {
        GsonConverterFactory.create()
    }

    private val PROTO_CONVERTER_FACTORY by lazy {
        ProtoConverterFactory.create()
    }
    /**
     * 添加对应的ConvertFactory
     */
    private fun Retrofit.Builder.addConvertFactoryByAnnotation(
        convertFactoryAnnotation: ConvertFactory?
    ): Retrofit.Builder {
        convertFactoryAnnotation?.value?.forEach {
            when (it) {
                ConvertFactory.DataType.String -> addConverterFactory(SCALARS_CONVERTER_FACTORY)
                ConvertFactory.DataType.Gson -> addConverterFactory(GSON_CONVERTER_FACTORY)
                ConvertFactory.DataType.ProtoBuf -> addConverterFactory(PROTO_CONVERTER_FACTORY)
            }
        }
        return this
    }

}