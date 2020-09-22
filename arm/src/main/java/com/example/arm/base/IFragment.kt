package com.example.arm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.arm.integration.cache.Cache

interface IFragment {
    /**
     * 提供在 [Fragment] 生命周期内的缓存容器, 可向此 [Fragment] 存取一些必要的数据
     * 此缓存容器和 [Fragment] 的生命周期绑定, 如果 [Fragment] 在屏幕旋转或者配置更改的情况下
     * 重新创建, 那此缓存容器中的数据也会被清空, 如果你想避免此种情况请使用 [LifecycleModel](https://github.com/JessYanCoding/LifecycleModel)
     *
     * @return like [LruCache]
     */
    val mCache: Cache<String, Any>

    /**
     * 是否使用 EventBus
     * Arms 核心库现在并不会依赖某个 EventBus, 要想使用 EventBus, 还请在项目中自行依赖对应的 EventBus
     * 现在支持两种 EventBus, greenrobot 的 EventBus 和畅销书 《Android源码设计模式解析与实战》的作者 何红辉 所作的 AndroidEventBus
     * 确保依赖后, 将此方法返回 true, Arms 会自动检测您依赖的 EventBus, 并自动注册
     * 这种做法可以让使用者有自行选择三方库的权利, 并且还可以减轻 Arms 的体积
     *
     * @return 返回 `true`, Arms 会自动注册 EventBus
     */
    fun useEventBus(): Boolean

    /**
     * 初始化 View
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

    /**
     * 初始化数据
     *
     * @param savedInstanceState
     */
    fun initData(savedInstanceState: Bundle?)
}