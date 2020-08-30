package com.example.mvvmdemo.struct.vm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Shared {
    SharedType type() default SharedType.SHARED;

    long maintenanceTime() default 0; // 对象无人引用时，残留多久，仅对SHARED有效



    public enum SharedType {

        NOT_SHARE,       // 不共享, 生命周期同创建的Activity/Fragment, 和标准ViewModel逻辑相同
        SHARED,          // 多个Activity/Fragment共享，如果没有Activity/Fragment引用，则会销毁
        SINGLETON,       // 单例，永远保存
    }
}
