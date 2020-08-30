
package com.example.mvvmdemo.struct


@Target(AnnotationTarget.CLASS,AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrl(
    /**
     * 基本url
     */
    vararg val value: String,
    /**
     * 开发环境
     */
    val dev: Array<String> = [""],
    /**
     * 测试环境
     */
    val test: Array<String> = [""],
    /**
     * 预发布环境
     */
    val pre_rls: Array<String> = [""]
)