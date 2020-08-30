package com.example.mvvmdemo.main.webservice;

import com.example.mvvmdemo.struct.login.Login;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;


@Login(false)
public interface GankService {
    String CATEGORY_FULI = "福利";
    String CATEGORY_ANDROID = "Android";
    String CATEGORY_VIDEO = "休息视频";
    String CATEGORY_FRONT_END = "前端";

    /**
     * 获取某一个分类下的数据, 可以返回Observable, Flowable, Maybe, 建议返回Single
     * @param category 分类名
     * @param count 请求数量
     * @param page 请求的页面序号
     */
    @GET("data/{category}/{count}/{page}")
    Single<ResultJson> getByCategory(
            @Path("category") String category,
            @Path("count") int count,
            @Path("page") int page);
}
