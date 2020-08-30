package com.example.mvvmdemo.struct.vm;

import androidx.lifecycle.ViewModel;


public class SingletonViewModelRef extends ViewModel {

    private final ViewModel mViewModel;


    public SingletonViewModelRef(ViewModel mViewModel) {
        this.mViewModel = mViewModel;
    }

    @Override
    protected void onCleared() {
        // 不对onClear方法进行代理
    }

    public ViewModel get() {
        return mViewModel;
    }
}
