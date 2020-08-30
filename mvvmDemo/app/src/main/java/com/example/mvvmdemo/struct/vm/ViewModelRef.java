package com.example.mvvmdemo.struct.vm;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

public class ViewModelRef extends ViewModel {

    public interface Releaser {
        void onRelease(ViewModelCounter counter, ViewModel viewModel);
    }

    private ViewModelCounter mViewModelCounter;
    private boolean mIsClosed;

    private ViewModelRef(ViewModelCounter viewModelCounter) {
        mViewModelCounter = viewModelCounter;
        mViewModelCounter.addRef();
    }

    private ViewModelRef(ViewModel viewModel, Releaser releaser) {
        mViewModelCounter = new ViewModelCounter(viewModel, releaser);
    }

    public synchronized boolean isValid() {
        return !mIsClosed;
    }

    @Override
    protected void onCleared() {
        synchronized (this) {
            if (mIsClosed) {
                return;
            }
            mIsClosed = true;
        }
        mViewModelCounter.defRef();
    }

    ViewModel get() {
        return mViewModelCounter.get();
    }

    ViewModelCounter getCounter() {
        return mViewModelCounter;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            // We put synchronized here so that lint doesn't warn about accessing mIsClosed, which is
            // guarded by this. Lint isn't aware of finalize semantics.
            synchronized (this) {
                if (mIsClosed) {
                    return;
                }
            }
            // 理论上并不应该走到这里
//            Log.w(TAG, String.format("Finalized without closing: %x %x (type = %s)",
//                    System.identityHashCode(this),
//                    System.identityHashCode(mViewModelCounter),
//                    mViewModelCounter.get().getClass().getName()));
//            onCleared();
            throw new IllegalStateException("ViewModelRef: " + this + " not closed");
        } finally {
            super.finalize();
        }
    }

    private synchronized @Nullable
    ViewModelRef cloneOrNull() {
        if (isValid()) {
            return new ViewModelRef(mViewModelCounter);
        }
        return null;
    }

    static ViewModelRef create(ViewModel viewModel,Releaser releaser){
        return new ViewModelRef(viewModel,releaser);
    }

    public static ViewModelRef of(ViewModelCounter viewModelCounter){
        if(viewModelCounter==null){
            return null;
        }
        return new ViewModelRef(viewModelCounter);
    }

    static class ViewModelCounter extends ViewModel {
        private ViewModel mViewModel;
        private int mRefCount;
        private Releaser mReleaser;

        ViewModelCounter(ViewModel viewModel, Releaser releaser) {
            this.mViewModel = viewModel;
            this.mReleaser = releaser;
            mRefCount = 1;
        }

        private synchronized void addRef() {
            if (mViewModel != null) {
                mRefCount++;
            }
        }

        private synchronized ViewModel defRef() {
            ViewModel result = null;
            if (mViewModel != null && mRefCount > 0) {
                mRefCount--;
                if (mRefCount == 0 && mReleaser != null) {
                    mReleaser.onRelease(this, mViewModel);
                    result = mViewModel;
                    mViewModel = null;
                }
            }
            return result;
        }

        public synchronized ViewModel get() {
            return mViewModel;
        }
    }
}
