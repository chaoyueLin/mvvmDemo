package com.example.mvvmdemo.struct.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import java.util.HashMap;
import java.util.Map;

public class HeytapViewModelProvider extends ViewModelProvider {

    static class CachedFactory implements Factory {

        private static final HashMap<String, ViewModel> sGlobalStore = new HashMap<>();
        private static DelayReleaseCache<ViewModel> sDelayReleaseCache = new DelayReleaseCache<>();

        private static ViewModelRef.Releaser sGloableReleaser = (counter, viewModel) -> {
            String key = null;
            for (Map.Entry<String, ViewModel> entry : sGlobalStore.entrySet()) {
                if (entry.getValue() == counter) {
                    key = entry.getKey();
                    break;
                }
            }

            if (key != null) {
                sGlobalStore.remove(key);
                Shared annotion = viewModel.getClass().getAnnotation(Shared.class);
                sDelayReleaseCache.add(key, viewModel, () -> {
                    if (viewModel instanceof BaseViewModel) {
                        ((BaseViewModel) viewModel).onCleared();
                    } else if (viewModel instanceof BaseAndroidViewModel) {
                        ((BaseAndroidViewModel) viewModel).onCleared();
                    } else {
                        throw new IllegalStateException("ViewModel must extend BaseViewModel or BaseAndroidViewModel");
                    }
                }, annotion == null ? 0 : annotion.maintenanceTime());
            }
        };

        // ViewModel实际类型
        private Class<? extends ViewModel> mWillCreateClass;
        private String mWillCreateKey;
        private Shared.SharedType mWillCreateType;

        // 被代理的Factory
        private final Factory mBase;

        // 默认苟傲
        CachedFactory(Factory base) {
            mBase = base;
        }

        void setWillCreateInfo(@NonNull Class<? extends ViewModel> clazz,
                               @NonNull String key,
                               @NonNull Shared.SharedType type) {
            this.mWillCreateClass = clazz;
            this.mWillCreateKey = key;
            this.mWillCreateType = type;
        }


        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            // Model class有两种：
            // ViewModelRef或者其他
            ViewModel viewModel = null;
            if (!ViewModelRef.class.equals(modelClass) // 非Shared
                    && !SingletonViewModelRef.class.equals(modelClass)) { // 非singleton
                // 没有添加Annotation
                viewModel = mBase.create(modelClass);
            } else if (mWillCreateKey == null || mWillCreateClass == null || mWillCreateType == null) {
                // 没有调setWillCreateInfo
                throw new IllegalArgumentException("must call setWillCreateInfo.");
            } else if (SingletonViewModelRef.class.equals(modelClass)) {
                viewModel = sGlobalStore.get(mWillCreateKey);
                if (viewModel == null) {
                    viewModel = mBase.create(mWillCreateClass);
                    sGlobalStore.put(mWillCreateKey, viewModel); // 永久缓存
                }
                // 包一层, 避免被调用clear方法
                viewModel = new SingletonViewModelRef(viewModel);
            } else {
                switch (mWillCreateType) {
                    case NOT_SHARE:
                        viewModel = mBase.create(modelClass);
                        break;
//                    case SINGLETON: {
//                        // 单例，直接存到map中
//                        viewModel = sGlobalStore.get(mWillCreateKey);
//                        if (viewModel == null) {
//                            viewModel = mBase.create(modelClass);
//                            sGlobalStore.put(mWillCreateKey, viewModel);
//                        }
//                        break;
//                    }
                    case SHARED: {
                        viewModel = sGlobalStore.get(mWillCreateKey);
                        if (viewModel instanceof ViewModelRef.ViewModelCounter
                                && mWillCreateClass.isInstance(((ViewModelRef.ViewModelCounter) viewModel).get())) {
                            // ViewModel存在
                            viewModel = ViewModelRef.of((ViewModelRef.ViewModelCounter) viewModel); // 引用计数+1并返回智能指针
                        } else {
                            viewModel = sDelayReleaseCache.get(mWillCreateKey); // 看是否在等待销毁
                            if (viewModel == null) {
                                // ViewModel不存在
                                viewModel = mBase.create(mWillCreateClass); // 创建viewmodel
                            }
                            ViewModelRef ref = ViewModelRef.create(viewModel, sGloableReleaser);// 转为智能指针
                            sGlobalStore.put(mWillCreateKey, ref.getCounter()); // 缓存
                            viewModel = ref; // 返回智能指针
                        }
                        break;
                    }
                }

            }
            return(T)viewModel;
        }

    }

    private final CachedFactory mFactory;

    HeytapViewModelProvider(@NonNull ViewModelStoreOwner owner, @NonNull CachedFactory factory) {
        super(owner, factory);
        this.mFactory = factory;
    }

    HeytapViewModelProvider(@NonNull ViewModelStore store, @NonNull CachedFactory factory) {
        super(store, factory);
        this.mFactory = factory;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T get(@NonNull String key, @NonNull Class<T> modelClass) {
        Shared annotation = modelClass.getAnnotation(Shared.class);
        Shared.SharedType type = annotation == null ? Shared.SharedType.NOT_SHARE : annotation.type();
        mFactory.setWillCreateInfo(modelClass, key, type);
        switch (type) {
            case NOT_SHARE:
            case SINGLETON:
                if(BaseViewModel.class.isAssignableFrom(modelClass) ||
                        BaseAndroidViewModel.class.isAssignableFrom(modelClass)) {
                    // super.get后会将ViewModelRef存到ViewModelStories中
                    // 使用ViewModelRef作为特殊标记, 传给自己的Factory
                    SingletonViewModelRef viewModelRef = super.get(key, SingletonViewModelRef.class);
                    return (T) viewModelRef.get();
                } else {
                    throw new IllegalArgumentException("model class must extends BaseViewModel or BaseAndroidViewModel");
                }
            case SHARED:
                if(BaseViewModel.class.isAssignableFrom(modelClass) ||
                        BaseAndroidViewModel.class.isAssignableFrom(modelClass)) {
                    // super.get后会将ViewModelRef存到ViewModelStories中
                    // 使用ViewModelRef作为特殊标记, 传给自己的Factory
                    ViewModelRef viewModelRef = super.get(key, ViewModelRef.class);
                    return (T) viewModelRef.get();
                } else {
                    throw new IllegalArgumentException("model class must extends BaseViewModel or BaseAndroidViewModel");
                }
            default:
                return super.get(key, modelClass);

        }
    }
}



