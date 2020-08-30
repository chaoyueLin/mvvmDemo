
package com.example.mvvmdemo.main.model

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvvmdemo.db.AppDatabase
import com.example.mvvmdemo.main.db.GankDao
import com.example.mvvmdemo.main.pojo.Gank
import com.example.mvvmdemo.main.pojo.TodayGanks
import com.example.mvvmdemo.main.webservice.GankJson
import com.example.mvvmdemo.main.webservice.GankService
import com.example.mvvmdemo.main.webservice.GankService.*
import com.example.mvvmdemo.main.webservice.ResultJson
import com.example.mvvmdemo.webservice.Domains
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import java.io.IOException


class GankRepository {
    private val dao: GankDao
    private val service: GankService

    constructor() {
        dao = AppDatabase.INSTANCE.gankDao()
        service  = Domains.MAIN.service(GankService::class.java)
    }

    @VisibleForTesting
    constructor(dao:GankDao, service: GankService) {
        this.dao = dao
        this.service = service
    }

    // to Gank struct
    fun convertToGank(list: List<GankJson>?): List<Gank> {
        TODO()
    }

    /**
     * 获取某一个分组的数据
     * @param category 分组名
     * @param count 需要的数据条数
     * @return Single
     */
    fun getByCategory(
        category: String?,
        count: Int
    ): Single<List<Gank>> { // 转成list形式
        // 成功的话, 需要插入数据库
        return service.getByCategory(category, count, 1)
            .subscribeOn(Schedulers.io()) // IO(网络线程)线程执行
            .observeOn(Schedulers.single()) // db线程执行下面的
            .map{ convertToGank(it.results) }
            .doOnSuccess { dao.nukeThenInsert(it) }
    }

    /**
     * 从网络获取今日数据
     */
    @SuppressLint("CheckResult")
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getTodayGanksFromWeb(): Single<TodayGanks> {
        return Single.zip( // 这里是一次性发所有请求, 也可以一条一条发
            service.getByCategory(CATEGORY_FULI, 1, 1).subscribeOn(Schedulers.io()), // 福利
            service.getByCategory(CATEGORY_ANDROID, 20, 1).subscribeOn(Schedulers.io()), // Android
            service.getByCategory(CATEGORY_FRONT_END, 20, 1).subscribeOn(Schedulers.io()), // 前端
            service.getByCategory(CATEGORY_VIDEO, 1, 1).subscribeOn(Schedulers.io()), // 视频
            Function4<ResultJson, ResultJson, ResultJson, ResultJson, TodayGanks> { t1, t2, t3, t4 -> // 结果合并
                // 合并数据
                if (t1.error || t2.error || t3.error || t4.error) {
                    throw IOException("Get Error From Server")
                }
                return@Function4 TodayGanks(
                    t1.results?.map { gankJsonToPojo(it, Gank.FULI) } ?: emptyList(),
                    t2.results?.map { gankJsonToPojo(it, Gank.ANDROID) } ?: emptyList(),
                    t3.results?.map { gankJsonToPojo(it, Gank.FRONT_END) } ?: emptyList(),
                    t4.results?.map { gankJsonToPojo(it, Gank.VIDEO) } ?: emptyList()
                )
            })
//            .subscribeOn(Schedulers.io()) // 网络线程执行上面的(没必要)
            .observeOn(Schedulers.single()) // db线程执行下面的
            .doOnSuccess {
                val willInsert = mutableListOf<Gank>().apply {
                    addAll(it.fuli)
                    addAll(it.android)
                    addAll(it.frondEnd)
                    addAll(it.video)
                }
                dao.nukeThenInsert(willInsert) // 执行成功, 写入db
            }
    }



    // 数据转换
    @VisibleForTesting
    fun gankJsonToPojo(json: GankJson, type:Int) : Gank {
        return Gank(
            gankId = json._id,
            typeId = type,
            createdAt = json.createdAt,
            desc = json.desc,
            images = json.images,
            publishedAt = json.publishedAt,
            source = json.source,
            type = json.type,
            url = json.url,
            used = json.used,
            who = json.who
        )
    }

    /**
     * 从db获取缓存数据
     */
    private fun getTodayGankFromDb() : Single<TodayGanks> {
        return dao.getAll()
            .map { listToTodayGanks(it)}
            .subscribeOn(Schedulers.single()) // db线程处理
    }

    /**
     * 将db中的数据分组后转成今日数据
     */
    private fun listToTodayGanks(list: List<Gank>): TodayGanks {
        return if(list.isEmpty()) {
            TodayGanks()
        } else {
            list
                .groupBy { it.typeId }
                .let {
                    TodayGanks(
                        it[Gank.FULI] ?: emptyList(),
                        it[Gank.ANDROID] ?: emptyList(),
                        it[Gank.FRONT_END] ?: emptyList(),
                        it[Gank.VIDEO] ?: emptyList()
                    )
                }
        }
    }

    // 对外接口, 下面的这些都ok

    /**
     * 优先从从db获取, 如果失败则从网络获取
     */
    fun getTodayGanks() : Single<TodayGanks> {
        return getTodayGankFromDb()
            .flatMap { if(it.isEmpty()) getTodayGanksFromWeb() else Single.just(it) } // 如果数据为空则总网络获取
    }

    @SuppressLint("CheckResult")
    fun getTodayGanks(to : MutableLiveData<TodayGanks>, onError: Consumer<Throwable>) {
        getTodayGanks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(to::postValue, onError::accept)
    }

    // 如果使用paging加载, 可以将此对象设计成有状态的并返回LiveData
    // 此类设计成无状态建议用上面那种方式
    // 不过要注意livedata不能处理异常
    @SuppressLint("CheckResult")
    fun getTodayGanks(onError: Consumer<Throwable>): LiveData<TodayGanks> {
        val result = MutableLiveData<TodayGanks>()
        getTodayGanks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(result::postValue, onError::accept)
        return result
    }

    /**
     * 不推荐
     */
    @SuppressLint("CheckResult")
    fun getTodayGanks(onSuccess: Consumer<TodayGanks>, onError: Consumer<Throwable>) {
        getTodayGanks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess, onError)
    }

    /**
     * 更新
     */
    fun refreshTodayGanks() : Single<TodayGanks>{
        return getTodayGanksFromWeb()
    }
}