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
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat.NestedScrollType;
import androidx.core.view.ViewCompat.ScrollAxis;

/**
 * ChildView调用NestedScrollingChildHelper->ViewParentCompat->NestedScrollingParent接口实现的ParentView->ParentView调用NestedScrollingParentHelper
 *
 * 实现NestedScrollingParent接口时,一般只需要特别处理onStopNestedScroll/onStartNestedScroll/onNestedScrollAccepted对滚动进行处理
 *
 * NestedScrollingParent->NestedScrollingParentHelper的调用对应方法如下:
 * onNestedScrollAccepted->onNestedScrollAccepted
 * onStopNestedScroll->onStopNestedScroll
 *
 * 当前仅仅是对ParentView的NestedScrollingParent接口的滚动前/后方法,保存滚动的方向
 * 并没有太大的作用
 *
 *
 * Helper class for implementing nested scrolling parent views compatible with Android platform
 * versions earlier than Android 5.0 Lollipop (API 21).
 *
 * <p>{@link ViewGroup ViewGroup} subclasses should instantiate a final instance
 * of this class as a field at construction. For each <code>ViewGroup</code> method that has
 * a matching method signature in this class, delegate the operation to the helper instance
 * in an overridden method implementation. This implements the standard framework policy
 * for nested scrolling.</p>
 *
 * <p>Views invoking nested scrolling functionality should always do so from the relevant
 * {@link ViewCompat}, {@link ViewGroupCompat} or
 * {@link ViewParentCompat} compatibility
 * shim static methods. This ensures interoperability with nested scrolling views on Android
 * 5.0 Lollipop and newer.</p>
 */
public class NestedScrollingParentHelper {
    /** type == TYPE_TOUCH (值为0)，用户触摸操作类型的方向(横向/纵向) */
    private int mNestedScrollAxesTouch;
    /** type == TYPE_NON_TOUCH (值为1)，非用户触摸操作类型，主要用于代码中的惯性操作比如Fling(横向/纵向) */
    private int mNestedScrollAxesNonTouch;

    /**
     * Construct a new helper for a given ViewGroup
     */
    public NestedScrollingParentHelper(@NonNull ViewGroup viewGroup) {
    }

    /**
     * Called when a nested scrolling operation initiated by a descendant view is accepted
     * by this ViewGroup.
     *
     * <p>This is a delegate method. Call it from your {@link ViewGroup ViewGroup}
     * subclass method/{@link NestedScrollingParent} interface method with
     * the same signature to implement the standard policy.</p>
     */
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target,
            @ScrollAxis int axes) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    /**
     * Called when a nested scrolling operation initiated by a descendant view is accepted
     * by this ViewGroup.
     *
     * <p>This is a delegate method. Call it from your {@link ViewGroup ViewGroup}
     * subclass method/{@link NestedScrollingParent2} interface method with
     * the same signature to implement the standard policy.</p>
     */
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target,
            @ScrollAxis int axes, @NestedScrollType int type) {
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            mNestedScrollAxesNonTouch = axes;
        } else {
            mNestedScrollAxesTouch = axes;
        }
    }

    /**
     * Return the current axes of nested scrolling for this ViewGroup.
     *
     * <p>This is a delegate method. Call it from your {@link ViewGroup ViewGroup}
     * subclass method/{@link NestedScrollingParent} interface method with
     * the same signature to implement the standard policy.</p>
     */
    @ScrollAxis
    public int getNestedScrollAxes() {
        return mNestedScrollAxesTouch | mNestedScrollAxesNonTouch;
    }

    /**
     * React to a nested scroll operation ending.
     *
     * <p>This is a delegate method. Call it from your {@link ViewGroup ViewGroup}
     * subclass method/{@link NestedScrollingParent} interface method with
     * the same signature to implement the standard policy.</p>
     */
    public void onStopNestedScroll(@NonNull View target) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH);
    }

    /**
     * React to a nested scroll operation ending.
     *
     * <p>This is a delegate method. Call it from your {@link ViewGroup ViewGroup}
     * subclass method/{@link NestedScrollingParent2} interface method with
     * the same signature to implement the standard policy.</p>
     */
    public void onStopNestedScroll(@NonNull View target, @NestedScrollType int type) {
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            mNestedScrollAxesNonTouch = ViewGroup.SCROLL_AXIS_NONE;
        } else {
            mNestedScrollAxesTouch = ViewGroup.SCROLL_AXIS_NONE;
        }
    }
}
