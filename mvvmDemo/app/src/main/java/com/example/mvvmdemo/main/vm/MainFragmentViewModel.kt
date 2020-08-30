
package com.example.mvvmdemo.main.vm

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import com.example.mvvmdemo.main.model.GankRepository
import com.example.mvvmdemo.main.pojo.TodayGanks
import com.example.mvvmdemo.struct.vm.BaseAndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers


class MainFragmentViewModel : BaseAndroidViewModel {

    val todayGanks = MutableLiveData<TodayGanks>() // ui 数据
    val error = MutableLiveData<Throwable>() // ui 提示
    val isLoading = MutableLiveData<Boolean>() // 下拉刷新

    // repository
    private val repo : GankRepository

    constructor(app: Application) : super(app){
        repo = GankRepository()
        initLoad()
    }

    @VisibleForTesting
    constructor(app: Application, repository: GankRepository) : super(app) {
        repo = repository
        initLoad()
    }

    /**
     * 首次加载
     */
    @SuppressLint("CheckResult")
    @MainThread
    @VisibleForTesting
    private fun initLoad() {
        isLoading.postValue(true)
        repo.getTodayGanks()
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                isLoading.postValue(false)
                todayGanks.postValue(it)
            }, {
                isLoading.postValue(false)
                error.postValue(it)
            })
    }

    /**
     * 更新
     */
    @SuppressLint("CheckResult")
    @MainThread
    fun update() {
        if(isLoading.value == false) { // 避免重入
            isLoading.postValue(true)
            repo.refreshTodayGanks()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    isLoading.postValue(false)
                    todayGanks.postValue(it)
                }, {
                    isLoading.postValue(false)
                    error.postValue(it)
                })
        }

    }



    override fun onCleared() {
        Log.e(TAG, "${javaClass.simpleName} is Cleared")
    }

    companion object {
        private const val TAG = "DemoViewModel"
    }
}