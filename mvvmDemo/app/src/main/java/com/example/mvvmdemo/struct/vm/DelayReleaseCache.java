package com.example.mvvmdemo.struct.vm;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.MainThread;

import java.util.HashMap;



@MainThread
class DelayReleaseCache<T> {
    private Handler mHandler=new Handler(Looper.getMainLooper());
    private HashMap<String,DelayRelease<T>> mCache=new HashMap<>();
    private class DelayRelease<TT> implements Runnable{

        long mBeginTime;
        long mDelayTime;
        TT mObject;
        Runnable mReleaser;

        DelayRelease(long beginTime,long delayTime,TT object,Runnable releaser){
            mBeginTime=beginTime;
            mDelayTime=delayTime;
            mObject=object;
            mReleaser=releaser;
        }
        @Override
        public void run() {
            mReleaser.run();
            mCache.values().remove(this);
        }
    }

    void add(String key,T t,Runnable releaser,long delayRun){
        DelayRelease<T> delayRelease=mCache.get(key);
        if(delayRelease==null){
            delayRelease=new DelayRelease<>(SystemClock.uptimeMillis(),delayRun,t,releaser);
            mCache.put(key,delayRelease);
            mHandler.postDelayed(delayRelease,delayRun);
        }else if(delayRelease.mObject==t){
            delayRelease.mReleaser=releaser;
            if(delayRun!=delayRelease.mDelayTime){
                long now =SystemClock.uptimeMillis();
                long newDelay=delayRelease.mBeginTime+delayRun-now;
                if(newDelay<=0){
                    mHandler.removeCallbacks(delayRelease);
                    mCache.remove(key);
                    releaser.run();;
                }else {
                    delayRelease.mDelayTime=delayRun;
                    mHandler.removeCallbacks(delayRelease);
                    mHandler.postDelayed(delayRelease,newDelay);
                }
            }
        }else {
            // 新对象要add，结束掉已存在的
            mHandler.removeCallbacks(delayRelease);
            delayRelease.run();

            delayRelease = new DelayRelease<>(SystemClock.uptimeMillis(), delayRun, t, releaser);
            mCache.put(key, delayRelease);
            mHandler.postDelayed(delayRelease, delayRun);
        }
    }

    @MainThread
    T get(String key){
        DelayRelease<T> delayRelease=mCache.remove(key);
        if(delayRelease!=null){
            mHandler.removeCallbacks(delayRelease);
            return delayRelease.mObject;
        }
        return null;
    }
}
