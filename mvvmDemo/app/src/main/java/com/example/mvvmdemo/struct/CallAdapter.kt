
package com.example.mvvmdemo.struct


/**
 * 用来标记使用哪种CallAdapter的, 只有两种情况, Default和RxJava, Default可以不写
 * 也就是你要返回RxJava相关Observer类时才需要加, 不过默认其实也加了
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class CallAdapter(
    vararg val value: CallAdapterType) {
    enum class CallAdapterType {
        @Deprecated("no need")
        Default,
        RxJava2
    }
}