
package com.example.mvvmdemo.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.mvvmdemo.DemoApplication
import com.example.mvvmdemo.main.db.GankDao
import com.example.mvvmdemo.main.pojo.Gank

@Database(entities = [Gank::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    /**
     * 获取gank表DAO
     */
    abstract fun gankDao() : GankDao

    companion object {
        // 单例
        val INSTANCE: AppDatabase by lazy {
            Room.databaseBuilder(DemoApplication.INSTANCE!!, AppDatabase::class.java, "demo.db").build()
        }
    }
}