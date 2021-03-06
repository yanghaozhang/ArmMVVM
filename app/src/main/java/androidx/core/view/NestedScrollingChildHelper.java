/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package androidx.core.view;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat.NestedScrollType;
import androidx.core.view.ViewCompat.ScrollAxis;

import static androidx.core.view.ViewCompat.TYPE_NON_TOUCH;
import static androidx.core.view.ViewCompat.TYPE_TOUCH;

/**
 *
 * ChildView调用NestedScrollingChild接口方法->调用NestedScrollingChildHelper->ViewParentCompat->NestedScrollingParent接口实现的ParentView
 *
 * NestedScrollingChild接口方法与NestedScrollingChildHelper一一对应
 * 实现接口时,不需要自己编写接口方法,只需要调用NestedScrollingChildHelper的对应方法
 *
 * NestedScrollingChildHelper->NestedScrollingParent的调用对应方法如下:
 *      startNestedScroll
 *      	    onStartNestedScroll
 *      	    如果onStartNestedScroll返回true,调用onNestedScrollAccepted
 *      	    onNestedScrollAccepted
 *      dispatchNestedPreScroll
 *      	    onNestedPreScroll
 *      dispatchNestedScroll
 *      	    onNestedScroll
 *      dispatchNestedPreFling
 *      	    onNestedPreFling
 *      dispatchNestedFling
 *      	    onNestedFling
 *      stopNestedScroll
 *      	    onStopNestedScroll
 *
 * 上述的对应方法,组成了滚动事件的传递顺序,并在方法中传递的consumed[]参数中对滚动距离进行了消费分配
 *
 * ViewGroup往往不仅仅是NestedScrollingParent还是NestedScrollingChild,比如NestedScrollView
 * 所以会出现不断上传滚动事件的情况,且对于这样的ViewGroup,它的NestedScrollingParent接口实现与NestedScrollingChildHelper一一对应
 *      NestedScrollingChild->
 *          NestedScrollingChildHelper->
 *              NestedScrollingParent->
 *                  NestedScrollingParentHelper调用
 *                  NestedScrollingChild调用->
 *                      NestedScrollingChildHelper->
 *                          NestedScrollingParent
 *
 *
 * Helper class for implementing nested scrolling child views compatible with Android platform
 * versions earlier than Android 5.0 Lollipop (API 21).
 *
 * <p>{@link View View} subclasses should instantiate a final instance of this
 * class as a field at construction. For each <code>View</code> method that has a matching
 * method signature in this class, delegate the operation to the helper instance in an overridden
 * method implementation. This implements the standard framework policy for nested scrolling.</p>
 *
 * <p>Views invoking nested scrolling functionality should always do so from the relevant
 * {@link ViewCompat}, {@link ViewGroupCompat} or
 * {@link ViewParentCompat} compatibility
 * shim static methods. This ensures interoperability with nested scrolling views on Android
 * 5.0 Lollipop and newer.</p>
 */
public class NestedScrollingChildHelper {
    /** 用户操作的父布局 */
    private ViewParent mNestedScrollingParentTouch;
    /** 非用户操作,惯性运动的父布局 */
    private ViewParent mNestedScrollingParentNonTouch;
    /** 子布局 */
    private final View mView;
    /** 子布局是否能嵌套滚动 */
    private boolean mIsNestedScrollingEnabled;
    /** 临时变量:保存xy方向的累积偏移量 */
    private int[] mTempNestedScrollConsumed;

    /**
     * Construct a new helper for a given view.
     */
    public NestedScrollingChildHelper(@NonNull View view) {
        mView = view;
    }

    /**
     * Enable nested scrolling.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @param enabled true to enable nested scrolling dispatch from this view, false otherwise
     */
    public void setNestedScrollingEnabled(boolean enabled) {
        if (mIsNestedScrollingEnabled) {
            ViewCompat.stopNestedScroll(mView);
        }
        mIsNestedScrollingEnabled = enabled;
    }

    /**
     * Check if nested scrolling is enabled for this view.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @return true if nested scrolling is enabled for this view
     */
    public boolean isNestedScrollingEnabled() {
        return mIsNestedScrollingEnabled;
    }

    /**
     * Check if this view has a nested scrolling parent view currently receiving events for
     * a nested scroll in progress with the type of touch.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @return true if this view has a nested scrolling parent, false otherwise
     */
    public boolean hasNestedScrollingParent() {
        return hasNestedScrollingParent(TYPE_TOUCH);
    }

    /**
     * Check if this view has a nested scrolling parent view currently receiving events for
     * a nested scroll in progress with the given type.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @return true if this view has a nested scrolling parent, false otherwise
     */
    public boolean hasNestedScrollingParent(@NestedScrollType int type) {
        return getNestedScrollingParentForType(type) != null;
    }

    /**
     * Start a new nested scroll for this view.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @param axes Supported nested scroll axes.
     *             See {@link NestedScrollingChild#startNestedScroll(int)}.
     * @return true if a cooperating parent view was found and nested scrolling started successfully
     */
    public boolean startNestedScroll(@ScrollAxis int axes) {
        return startNestedScroll(axes, TYPE_TOUCH);
    }

    /**
     * Start a new nested scroll for this view.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild2} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @param axes Supported nested scroll axes.
     *             See {@link NestedScrollingChild2#startNestedScroll(int,
     *             int)}.
     * @return true if a cooperating parent view was found and nested scrolling started successfully
     */
    public boolean startNestedScroll(@ScrollAxis int axes, @NestedScrollType int type) {
        // 父布局正处于滚动状态:已持有处于滚动的父类
        if (hasNestedScrollingParent(type)) {
            // Already in progress
            return true;
        }
        // 子布局支持嵌套滚动
        if (isNestedScrollingEnabled()) {
            ViewParent p = mView.getParent();
            View child = mView;
            // 不断向上查找父布局,直到出现消费滚动事件的父布局或到顶层
            while (p != null) {
                // 调用父布局的onNestedScrollAccepted,如果父布局接收内部的滚动,返回true
                if (ViewParentCompat.onStartNestedScroll(p, child, mView, axes, type)) {
                    // 持有父布局
                    setNestedScrollingParentForType(type, p);
                    // 调用父布局的onNestedScrollAccepted
                    ViewParentCompat.onNestedScrollAccepted(p, child, mView, axes, type);
                    return true;
                }
                if (p instanceof View) {
                    child = (View) p;
                }
                p = p.getParent();
            }
        }
        return false;
    }

    /**
     * Stop a nested scroll in progress.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     */
    public void stopNestedScroll() {
        stopNestedScroll(TYPE_TOUCH);
    }

    /**
     * Stop a nested scroll in progress.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild2} interface method with the same
     * signature to implement the standard policy.</p>
     */
    public void stopNestedScroll(@NestedScrollType int type) {
        ViewParent parent = getNestedScrollingParentForType(type);
        if (parent != null) {
            // 调用父布局的onStopNestedScroll
            ViewParentCompat.onStopNestedScroll(parent, mView, type);
            // 重置
            setNestedScrollingParentForType(type, null);
        }
    }

    /**
     * Dispatch one step of a nested scrolling operation to the current nested scrolling parent.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @return <code>true</code> if the parent consumed any of the nested scroll distance
     */
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
            int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return dispatchNestedScrollInternal(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, TYPE_TOUCH, null);
    }

    /**
     * 子视图消费完滚动事件后,将剩余未消费的滚动距离交由嵌套层父布局处理
     *
     * Dispatch one step of a nested scrolling operation to the current nested scrolling parent.
     *
     * <p>This is a delegate method. Call it from your {@link NestedScrollingChild2} interface
     * method with the same signature to implement the standard policy.
     *
     * @return <code>true</code> if the parent consumed any of the nested scroll distance
     */
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
            int dyUnconsumed, @Nullable int[] offsetInWindow, @NestedScrollType int type) {
        return dispatchNestedScrollInternal(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type, null);
    }

    /**
     * Dispatch one step of a nested scrolling operation to the current nested scrolling parent.
     *
     * <p>This is a delegate method. Call it from your {@link NestedScrollingChild3} interface
     * method with the same signature to implement the standard policy.
     */
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
            int dyUnconsumed, @Nullable int[] offsetInWindow, @NestedScrollType int type,
            @Nullable int[] consumed) {
        dispatchNestedScrollInternal(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type, consumed);
    }


    /**
     * 分发滚动事件给父布局
     *
     * @param dxConsumed    x方向消费分辨率
     * @param dyConsumed    y方向消费分辨率
     * @param dxUnconsumed  x方向未消费分辨率
     * @param dyUnconsumed  y方向未消费分辨率
     * @param offsetInWindow
     *          此次滚动前后View在window上的偏移量
     * @param type  滚动类型(是否用户操作类型)
     * @param consumed      父布局需要处理的消费内容
     * @return
     */
    private boolean dispatchNestedScrollInternal(int dxConsumed, int dyConsumed,
            int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow,
            @NestedScrollType int type, @Nullable int[] consumed) {
        // 支持嵌套滚动
        if (isNestedScrollingEnabled()) {
            final ViewParent parent = getNestedScrollingParentForType(type);
            if (parent == null) {
                return false;
            }

            if (dxConsumed != 0 || dyConsumed != 0 || dxUnconsumed != 0 || dyUnconsumed != 0) {
                int startX = 0;
                int startY = 0;
                // 记录滚动开始时View在window上的坐标
                if (offsetInWindow != null) {
                    mView.getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }

                // 初始化消费的滚动
                if (consumed == null) {
                    consumed = getTempNestedScrollConsumed();
                    consumed[0] = 0;
                    consumed[1] = 0;
                }

                // 调用父布局的onNestedScroll
                ViewParentCompat.onNestedScroll(parent, mView,
                        dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);

                // 减去开始滚动时的坐标,offsetInWindow为滚动结束后的View滚动了的偏移量
                if (offsetInWindow != null) {
                    mView.getLocationInWindow(offsetInWindow);
                    offsetInWindow[0] -= startX;
                    offsetInWindow[1] -= startY;
                }
                return true;
            }
            // xy不存在滚动距离,将累积偏移量置为0
            else if (offsetInWindow != null) {
                // No motion, no dispatch. Keep offsetInWindow up to date.
                offsetInWindow[0] = 0;
                offsetInWindow[1] = 0;
            }
        }
        return false;
    }

    /**
     * 当子视图滚动前调用该方法,允许嵌套层父布局预先消费滚动事件,然后子视图再滚动
     *
     *
     * Dispatch one step of a nested pre-scrolling operation to the current nested scrolling parent.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @return true if the parent consumed any of the nested scroll
     */
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed,
            @Nullable int[] offsetInWindow) {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, TYPE_TOUCH);
    }

    /**
     * 当子视图滚动前调用该方法,允许嵌套层父布局预先消费滚动事件,然后子视图再滚动
     *
     * Dispatch one step of a nested pre-scrolling operation to the current nested scrolling parent.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild2} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @return true if the parent consumed any of the nested scroll
     */
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed,
            @Nullable int[] offsetInWindow, @NestedScrollType int type) {
        if (isNestedScrollingEnabled()) {
            final ViewParent parent = getNestedScrollingParentForType(type);
            if (parent == null) {
                return false;
            }

            if (dx != 0 || dy != 0) {
                int startX = 0;
                int startY = 0;
                // 记录滚动开始时View在window上的坐标
                if (offsetInWindow != null) {
                    mView.getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }

                // 初始化消费的滚动
                if (consumed == null) {
                    consumed = getTempNestedScrollConsumed();
                }
                consumed[0] = 0;
                consumed[1] = 0;

                // 调用父布局的onNestedScroll
                ViewParentCompat.onNestedPreScroll(parent, mView, dx, dy, consumed, type);

                // 减去开始滚动时的坐标,offsetInWindow为滚动结束后的View滚动了的偏移量
                if (offsetInWindow != null) {
                    mView.getLocationInWindow(offsetInWindow);
                    offsetInWindow[0] -= startX;
                    offsetInWindow[1] -= startY;
                }
                // 如果父布局接收了滚动,则返回true
                return consumed[0] != 0 || consumed[1] != 0;
            }
            // xy不存在滚动距离,将累积偏移量置为0
            else if (offsetInWindow != null) {
                offsetInWindow[0] = 0;
                offsetInWindow[1] = 0;
            }
        }
        return false;
    }

    /**
     * Dispatch a nested fling operation to the current nested scrolling parent.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @return true if the parent consumed the nested fling
     */
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        if (isNestedScrollingEnabled()) {
            ViewParent parent = getNestedScrollingParentForType(TYPE_TOUCH);
            if (parent != null) {
                // 调用父布局的onNestedFling
                return ViewParentCompat.onNestedFling(parent, mView, velocityX,
                        velocityY, consumed);
            }
        }
        return false;
    }

    /**
     * Dispatch a nested pre-fling operation to the current nested scrolling parent.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @return true if the parent consumed the nested fling
     */
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (isNestedScrollingEnabled()) {
            ViewParent parent = getNestedScrollingParentForType(TYPE_TOUCH);
            if (parent != null) {
                // 调用父布局的onNestedPreFling
                return ViewParentCompat.onNestedPreFling(parent, mView, velocityX,
                        velocityY);
            }
        }
        return false;
    }

    /**
     * View subclasses should always call this method on their
     * <code>NestedScrollingChildHelper</code> when detached from a window.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     */
    public void onDetachedFromWindow() {
        ViewCompat.stopNestedScroll(mView);
    }

    /**
     * 代码调动停止滚动
     *
     * Called when a nested scrolling child stops its current nested scroll operation.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @param child Child view stopping its nested scroll. This may not be a direct child view.
     */
    public void onStopNestedScroll(@NonNull View child) {
        ViewCompat.stopNestedScroll(mView);
    }

    private ViewParent getNestedScrollingParentForType(@NestedScrollType int type) {
        switch (type) {
            case TYPE_TOUCH:
                return mNestedScrollingParentTouch;
            case TYPE_NON_TOUCH:
                return mNestedScrollingParentNonTouch;
        }
        return null;
    }

    private void setNestedScrollingParentForType(@NestedScrollType int type, ViewParent p) {
        switch (type) {
            case TYPE_TOUCH:
                mNestedScrollingParentTouch = p;
                break;
            case TYPE_NON_TOUCH:
                mNestedScrollingParentNonTouch = p;
                break;
        }
    }

    private int[] getTempNestedScrollConsumed() {
        if (mTempNestedScrollConsumed == null) {
            mTempNestedScrollConsumed = new int[2];
        }
        return mTempNestedScrollConsumed;
    }
}
