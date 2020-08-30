
package com.example.mvvmdemo.main.db

import androidx.room.*
import com.example.mvvmdemo.main.pojo.Gank
import io.reactivex.Maybe
import io.reactivex.Single


@Dao
abstract class GankDao {

    // 插入数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGank(ganks: List<Gank>): List<Long>

    // 清空整个表
    @Query("DELETE FROM Gank")
    abstract fun nukeTable()

    /**
     * 清空整个表, 再插入数据
     */
    @Transaction
    open fun nukeThenInsert(ganks: List<Gank>) {
        nukeTable()
        insertGank(ganks)
    }

    /**
     * 如果结果为空则直接complete
     */
    @Query("SELECT * FROM Gank WHERE gankId = :id")
    abstract fun getGank(id: String): Maybe<Gank>

    /**
     * 如果未空则抛异常
     */
    @Query("SELECT * FROM Gank WHERE gankId = :id")
    abstract fun getGank1(id: String): Single<Gank>

    /**
     * 返回全部数据
     */
    @Query("SELECT * FROM Gank")
    abstract fun getAll(): Single<List<Gank>>
}