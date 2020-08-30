package com.example.mvvmdemo.struct.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


public class BaseAndroidViewModel extends AndroidViewModel {

    public BaseAndroidViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
