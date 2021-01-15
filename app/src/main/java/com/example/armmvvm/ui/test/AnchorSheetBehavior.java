/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.example.armmvvm.ui.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.VelocityTrackerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import androidx.customview.widget.ViewDragHelper;
import androidx.viewpager.widget.ViewPager;

import com.example.armmvvm.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;


/**
 * 以下Bottomsheet实现的功能是：
 * （1）可以通过{@link #setAllowDragdown(boolean)}进行设置当展开状态下是否允许向下拖动，true表示可以向下拖动，false表示不可以；
 * （2）可以通过{@link #setAnchorHeight(int)}}进行设置中间状态的高度,
 * （3）该Behavior默认优先响应Bottomsheet的拖动事件，再响应内部可滑动view的滑动事件；
 * @author 创建人 ：xuciluan
 * @package 包名 ：com.augurit.am.cmpt.widget.bottomsheet
 * @createTime 创建时间 ：2017-05-16
 * @modifyBy 修改人 ：xuciluan
 * @modifyTime 修改时间 ：2017-05-16
 * @modifyMemo 修改备注：
 * @version 1.0
 */
public class AnchorSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> implements IBottomSheetBehavior{

    private static final String TAG = "AnchorSheetBehavior";
    /**
     * Callback for monitoring events about bottom sheets.
     */
    public abstract static class BottomSheetCallback {

        /**
         * Called when the bottom sheet changes its state.
         *
         * @param bottomSheet The bottom sheet view.
         * @param newState    The new state. This will be one of {@link #STATE_DRAGGING},
         *                    {@link #STATE_SETTLING}, {@link #STATE_EXPANDED},
         *                    {@link #STATE_COLLAPSED}, or {@link #STATE_HIDDEN}.
         */
        public abstract void onStateChanged(@NonNull View bottomSheet, @AnchorSheetBehavior.State int oldState, @AnchorSheetBehavior.State int newState);

        /**
         * Called when the bottom sheet is being dragged.
         *
         * @param bottomSheet The bottom sheet view.
         * @param slideOffset The new offset of this bottom sheet within its range, from 0 to 1
         *                    when it is moving upward, and from 0 to -1 when it moving downward.
         */
        public abstract void onSlide(@NonNull View bottomSheet, float slideOffset);
    }


    public static float HIDE_THRESHOLD = 0.5f;

    public static float HIDE_FRICTION = 0.1f;

    public float mMaximumVelocity;

    public int mPeekHeight;

    public int mMinOffset;

    public int mMaxOffset;

    public boolean mHideable;

    //锚点高度
    public int mAnchorHeight;
    //锚点相对父组件顶端的偏移
    public int anchorOffect;

    public boolean isAllowDragdown = true;

    public boolean isAllowPullUp = true;

    @AnchorSheetBehavior.State
    public int mState = STATE_COLLAPSED;

    public ViewDragHelper mViewDragHelper;

    public boolean mIgnoreEvents;

    public int mLastNestedScrollDy;

    public boolean mNestedScrolled;

    public int mParentHeight;

    public WeakReference<V> mViewRef;

    public WeakReference<View> mNestedScrollingChildRef;

    public boolean mNestedScrollingChildAssigned = false;

    public AnchorSheetBehavior.BottomSheetCallback mCallback;

    public VelocityTracker mVelocityTracker;

    public int mActivePointerId;

    public int mInitialY;

    public boolean mTouchingScrollingChild;

    /**
     * Default constructor for instantiating AnchorSheetBehaviors.
     */
    public AnchorSheetBehavior() {
    }

    /**
     * Default constructor for inflating AnchorSheetBehaviors from layout.
     *
     * @param context The {@link Context}.
     * @param attrs   The {@link AttributeSet}.
     */
    public AnchorSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BottomSheetBehavior_Layout);
        setPeekHeight(a.getDimensionPixelSize(
                R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight, 0));
        setHideable(a.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_hideable, false));
        a.recycle();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        return new AnchorSheetBehavior.SavedState(super.onSaveInstanceState(parent, child), mState);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, V child, Parcelable state) {
        AnchorSheetBehavior.SavedState ss = (AnchorSheetBehavior.SavedState) state;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        // Intermediate states are restored as collapsed state
        if (ss.state == STATE_DRAGGING || ss.state == STATE_SETTLING) {
            mState = STATE_COLLAPSED;
        } else {
            mState = ss.state;
        }
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
       /* // First let the parent lay it out
        if (mState != STATE_DRAGGING && mState != STATE_SETTLING) {
            if (ViewCompat.getFitsSystemWindows(parent) &&
                    !ViewCompat.getFitsSystemWindows(child)) {
                ViewCompat.setFitsSystemWindows(child, true);
            }
//            parent.onLayoutChild(child, layoutDirection);
        }
        int savedTop = child.getTop();
        // First let the parent lay it out
        parent.onLayoutChild(child, layoutDirection);
        // Offset the bottom sheet
        mParentHeight = parent.getHeight();
        mMinOffset = Math.max(0, mParentHeight - child.getHeight());
        mMaxOffset = Math.max(mParentHeight - mPeekHeight, mMinOffset);
        if (mState == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, mMinOffset);
        } else if (mHideable && mState == STATE_HIDDEN) {
            ViewCompat.offsetTopAndBottom(child, mParentHeight);
        } else if (mState == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, mMaxOffset);
        } else if (mState == STATE_ANCHOR){
            ViewCompat.offsetTopAndBottom(child, mParentHeight - mAnchorHeight);
        } else if (mState == STATE_DRAGGING || mState == STATE_SETTLING) {
            ViewCompat.offsetTopAndBottom(child, savedTop - child.getTop());
        }
        if (mViewDragHelper == null) {
            mViewDragHelper = ViewDragHelper.create(parent, mDragCallback);
        }
        mViewRef = new WeakReference<>(child);
        if (!mNestedScrollingChildAssigned) {
            mNestedScrollingChildRef = new WeakReference<>(findScrollingChild(child));
        }
        return true;*/

        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            child.setFitsSystemWindows(true);
        }

        if (mViewRef == null) {
            // First layout with this behavior.
            mViewRef = new WeakReference<>(child);
            updateAccessibilityActions();
            if (ViewCompat.getImportantForAccessibility(child)
                    == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
                ViewCompat.setImportantForAccessibility(child, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
            }
        }
        if (mViewDragHelper == null) {
            mViewDragHelper = ViewDragHelper.create(parent, mDragCallback);
        }

        int savedTop = child.getTop();
        // First let the parent lay it out
        parent.onLayoutChild(child, layoutDirection);
        // Offset the bottom sheet
        mParentHeight = parent.getHeight();
        mMinOffset = Math.max(0, mParentHeight - child.getHeight());
        mMaxOffset = Math.max(mParentHeight - mPeekHeight, mMinOffset);

        if (mState == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, mMinOffset);
        } else if (mHideable && mState == STATE_HIDDEN) {
            ViewCompat.offsetTopAndBottom(child, mParentHeight);
        } else if (mState == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, mMaxOffset);
        } else if (mState == STATE_ANCHOR){
            ViewCompat.offsetTopAndBottom(child, mParentHeight - mAnchorHeight);
        } else if (mState == STATE_DRAGGING || mState == STATE_SETTLING) {
            ViewCompat.offsetTopAndBottom(child, savedTop - child.getTop());
        }

        if (!mNestedScrollingChildAssigned) {
            mNestedScrollingChildRef = new WeakReference<>(findScrollingChild(child));
        }
        return true;
    }

    private void updateAccessibilityActions() {
        if (mViewRef == null) {
            return;
        }
        V child = mViewRef.get();
        if (child == null) {
            return;
        }
        ViewCompat.removeAccessibilityAction(child, AccessibilityNodeInfoCompat.ACTION_COLLAPSE);
        ViewCompat.removeAccessibilityAction(child, AccessibilityNodeInfoCompat.ACTION_EXPAND);
        ViewCompat.removeAccessibilityAction(child, AccessibilityNodeInfoCompat.ACTION_DISMISS);

        switch (mState) {
            case STATE_EXPANDED:
            {
                int nextState = STATE_ANCHOR;
                addAccessibilityActionForState(
                        child, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_COLLAPSE, nextState);
                break;
            }
            case STATE_ANCHOR:
            {
                addAccessibilityActionForState(
                        child, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_COLLAPSE, STATE_COLLAPSED);
                addAccessibilityActionForState(
                        child, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_EXPAND, STATE_EXPANDED);
                break;
            }
            case STATE_COLLAPSED:
            {
                int nextState = STATE_ANCHOR;
                addAccessibilityActionForState(child, AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_EXPAND, nextState);
                break;
            }
            default: // fall out
        }
    }

    private void addAccessibilityActionForState(
            V child, AccessibilityNodeInfoCompat.AccessibilityActionCompat action, final int state) {
        ViewCompat.replaceAccessibilityAction(
                child,
                action,
                null,
                new AccessibilityViewCommand() {
                    @Override
                    public boolean perform(@NonNull View view, @Nullable CommandArguments arguments) {
                        setState(state);
                        return true;
                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown() || (!isAllowDragdown && mState == STATE_EXPANDED) || (!isAllowPullUp && mState == STATE_COLLAPSED)) {
            return false;
        }
        Log.d(TAG, "onInterceptTouchEvent() called with: parent = [" + parent + "], child = [" + child + "], event = [" + event + "]");
        int action = MotionEventCompat.getActionMasked(event);
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchingScrollingChild = false;
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                // Reset the ignore flag
                if (mIgnoreEvents) {
                    mIgnoreEvents = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                int initialX = (int) event.getX();
                mInitialY = (int) event.getY();
                View scroll = mNestedScrollingChildRef.get();
                if (scroll != null && parent.isPointInChildBounds(scroll, initialX, mInitialY)) {
                    mActivePointerId = event.getPointerId(event.getActionIndex());
                    mTouchingScrollingChild = true;
                }
                mIgnoreEvents = mActivePointerId == MotionEvent.INVALID_POINTER_ID &&
                        !parent.isPointInChildBounds(child, initialX, mInitialY);
                break;
        }
        if (!mIgnoreEvents && mViewDragHelper.shouldInterceptTouchEvent(event)) {
            return true;
        }
        // We have to handle cases that the ViewDragHelper does not capture the bottom sheet because
        // it is not the top most view of its parent. This is not necessary when the touch event is
        // happening over the scrolling content as nested scrolling logic handles that case.
        //todo 在冲突检测中，由于parent.isPointInChildBounds(scroll, (int) event.getX(), (int) event.getY())返回了false，导致拦截了事件的分发，子view收到MotionEvent.CANCEL,所以在冲突界面中的
        //的结果列表中滑动失灵
        View scroll = mNestedScrollingChildRef.get();
        /*return*/boolean flag =  action == MotionEvent.ACTION_MOVE && scroll != null &&
                !mIgnoreEvents && mState != STATE_DRAGGING &&
                !parent.isPointInChildBounds(scroll, (int) event.getX(), (int) event.getY())/* !test(parent,scroll, (int) event.getX(), (int) event.getY())*/&&
                Math.abs(mInitialY - event.getY()) > mViewDragHelper.getTouchSlop() && isAllowDragdown;

        return flag;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        Log.d(TAG, "onTouchEvent() called with: parent = [" + parent + "], child = [" + child + "], event = [" + event + "]");
        if (!child.isShown() ||  (!isAllowDragdown && mState == STATE_EXPANDED) || (!isAllowPullUp && mState == STATE_COLLAPSED)) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(event);
        if (mState == STATE_DRAGGING && action == MotionEvent.ACTION_DOWN) {
            return true;
        }
        mViewDragHelper.processTouchEvent(event);
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        // The ViewDragHelper tries to capture only the top-most View. We have to explicitly tell it
        // to capture the bottom sheet in case it is not captured and the touch slop is passed.
        if (action == MotionEvent.ACTION_MOVE && !mIgnoreEvents) {
            if (Math.abs(mInitialY - event.getY()) > mViewDragHelper.getTouchSlop()) {
                mViewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
            }
        }
        return !mIgnoreEvents;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        Log.d(TAG, "onStartNestedScroll() called with: coordinatorLayout = [" + coordinatorLayout + "], child = [" + child + "], directTargetChild = [" + directTargetChild + "], target = [" + target + "], nestedScrollAxes = [" + nestedScrollAxes + "]");
        mLastNestedScrollDy = 0;
        mNestedScrolled = false;
        return ((nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0) && isAllowDragdown;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx,
                                  int dy, int[] consumed) {
        View scrollingChild = mNestedScrollingChildRef.get();
        if (target != scrollingChild) {
            return;
        }
        Log.d(TAG, "onNestedPreScroll() called with: coordinatorLayout = [" + coordinatorLayout + "], child = [" + child + "], target = [" + target + "], dx = [" + dx + "], dy = [" + dy + "], consumed = [" + consumed + "]");
        int currentTop = child.getTop();
        int newTop = currentTop - dy;
        if (dy > 0) { // Upward
            if (newTop < mMinOffset) {
                consumed[1] = currentTop - mMinOffset;
                ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                setStateInternal(STATE_EXPANDED);
            } else {
                consumed[1] = dy;
                ViewCompat.offsetTopAndBottom(child, -dy);
                setStateInternal(STATE_DRAGGING);
            }
        } else if (dy < 0) { // Downward
            if (!ViewCompat.canScrollVertically(target, -1)) {
                if (newTop <= mMaxOffset || mHideable) {
                    consumed[1] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(STATE_DRAGGING);
                } else {
                    consumed[1] = currentTop - mMaxOffset;
                    ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                    setStateInternal(STATE_COLLAPSED);
                }
            }
        }
        dispatchOnSlide(child.getTop());
        mLastNestedScrollDy = dy;
        mNestedScrolled = true;
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        Log.d(TAG, "onStopNestedScroll() called with: coordinatorLayout = [" + coordinatorLayout + "], child = [" + child + "], target = [" + target + "]");
        if (child.getTop() == mMinOffset) {
            setStateInternal(STATE_EXPANDED);
            return;
        }
        if (target != mNestedScrollingChildRef.get() || !mNestedScrolled) {
            return;
        }
        int top;
        int targetState;
        if (mHideable && shouldHide(child, getYVelocity())) {
            top = mParentHeight;
            targetState = STATE_HIDDEN;
        } else if (mLastNestedScrollDy >= 0) {
            int currentTop = child.getTop();
            if (Math.abs(currentTop - mAnchorHeight) < Math.abs(currentTop - mMinOffset)) {
                top = mAnchorHeight;
                targetState = STATE_ANCHOR;
            } else if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - mMaxOffset)) {
                top = mMinOffset;
                targetState = STATE_EXPANDED;
            } else {
                top = mMaxOffset;
                targetState = STATE_COLLAPSED;
            }
        } else {
            top = mMaxOffset;
            targetState = STATE_COLLAPSED;
        }
        if (mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
            setStateInternal(STATE_SETTLING);
            ViewCompat.postOnAnimation(child, new AnchorSheetBehavior.SettleRunnable(child, targetState));
        } else {
            setStateInternal(targetState);
        }
        mNestedScrolled = false;
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target,
                                    float velocityX, float velocityY) {

        return target == mNestedScrollingChildRef.get() &&
                (mState != STATE_EXPANDED ||
                        super.onNestedPreFling(coordinatorLayout, child, target,
                                velocityX, velocityY));
    }


    /**
     * Sets the height of the bottom sheet when it is collapsed.
     *
     * @param peekHeight The height of the collapsed bottom sheet in pixels.
     * @attr ref android.support.design.R.styleable#AnchorSheetBehavior_Params_behavior_peekHeight
     */
    public void setPeekHeight(int peekHeight) {
        mPeekHeight = Math.max(0, peekHeight);
        mMaxOffset = mParentHeight - peekHeight;
        if (mState == STATE_COLLAPSED && mViewRef != null) {
            V view = mViewRef.get();
            if (view != null) {
                view.requestLayout();
            }
        }
    }

    /**
     * Gets the height of the bottom sheet when it is collapsed.
     *
     * @return The height of the collapsed bottom sheet.
     * @attr ref android.support.design.R.styleable#AnchorSheetBehavior_Params_behavior_peekHeight
     */
    public int getPeekHeight() {
        return mPeekHeight;
    }

    /**
     * Sets whether this bottom sheet can hide when it is swiped down.
     *
     * @param hideable {@code true} to make this bottom sheet hideable.
     * @attr ref android.support.design.R.styleable#AnchorSheetBehavior_Params_behavior_hideable
     */
    public void setHideable(boolean hideable) {
        mHideable = hideable;
    }

    /**
     * Gets whether this bottom sheet can hide when it is swiped down.
     *
     * @return {@code true} if this bottom sheet can hide.
     * @attr ref android.support.design.R.styleable#AnchorSheetBehavior_Params_behavior_hideable
     */
    public boolean isHideable() {
        return mHideable;
    }

    /**
     * Sets a callback to be notified of bottom sheet events.
     *
     * @param callback The callback to notify when bottom sheet events occur.
     */
    public void setBottomSheetCallback(AnchorSheetBehavior.BottomSheetCallback callback) {
        mCallback = callback;
    }

    /**
     * Sets the state of the bottom sheet. The bottom sheet will transition to that state with
     * animation.
     *
     * @param state One of {@link #STATE_COLLAPSED}, {@link #STATE_EXPANDED}, or
     *              {@link #STATE_HIDDEN}.
     */
    public void setState(@AnchorSheetBehavior.State int state) {
        if (state == mState) {
            return;
        }
        if (mViewRef == null) {
            // The view is not laid out yet; modify mState and let onLayoutChild handle it later
            if (state == STATE_COLLAPSED || state == STATE_EXPANDED ||
                    (mHideable && state == STATE_HIDDEN)) {
                mState = state;
            }
            return;
        }
        V child = mViewRef.get();
        if (child == null) {
            return;
        }
        int top;
        if (state == STATE_COLLAPSED) {
            top = mMaxOffset;
        } else if (state == STATE_EXPANDED) {
            top = mMinOffset;
        } else if (mHideable && state == STATE_HIDDEN) {
            top = mParentHeight;
        } else if (state == STATE_ANCHOR){
            top = mParentHeight - mAnchorHeight;
        } else {
            throw new IllegalArgumentException("Illegal state argument: " + state);
        }
        setStateInternal(STATE_SETTLING);
        if (mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
            ViewCompat.postOnAnimation(child, new AnchorSheetBehavior.SettleRunnable(child, state));
        }
    }

    /**
     * Gets the current state of the bottom sheet.
     *
     * @return One of {@link #STATE_EXPANDED}, {@link #STATE_COLLAPSED}, {@link #STATE_DRAGGING},
     * and {@link #STATE_SETTLING}.
     */
    @AnchorSheetBehavior.State
    public int getState() {
        return mState;
    }

    public void setStateInternal(@AnchorSheetBehavior.State int state) {
        if (mState == state) {
            return;
        }
        int oldState = mState;
        mState = state;
        View bottomSheet = mViewRef.get();
        if (bottomSheet != null && mCallback != null) {
            mCallback.onStateChanged(bottomSheet, oldState,state);
        }
    }

    public int getAnchorHeight() {
        return mAnchorHeight;
    }

    public void setAnchorHeight(int mAnchorHeight) {
        this.mAnchorHeight = mAnchorHeight;
    }

    public void reset() {
        mActivePointerId = ViewDragHelper.INVALID_POINTER;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public boolean shouldHide(View child, float yvel) {
        if (child.getTop() < mMaxOffset) {
            // It should not hide, but collapse.
            return false;
        }
        final float newTop = child.getTop() + yvel * HIDE_FRICTION;
        return Math.abs(newTop - mMaxOffset) / (float) mPeekHeight > HIDE_THRESHOLD;
    }
//
//    private View findScrollingChild(View view) {
//        if (view instanceof NestedScrollingChild) {
//            return view;
//        }
//        if (view instanceof ViewGroup) {
//            ViewGroup group = (ViewGroup) view;
//            for (int i = 0, count = group.getChildCount(); i < count; i++) {
//                View scrollingChild = findScrollingChild(group.getChildAt(i));
//                if (scrollingChild != null) {
//                    return scrollingChild;
//                }
//            }
//        }
//        return null;
//    }

    public float getYVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        return VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId);
    }

    public ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (mState == STATE_DRAGGING) {
                return false;
            }
            if (mTouchingScrollingChild) {
                return false;
            }
            if (mState == STATE_EXPANDED && mActivePointerId == pointerId) {
                View scroll = mNestedScrollingChildRef.get();
                if (scroll != null && ViewCompat.canScrollVertically(scroll, -1)) {
                    // Let the content scroll up
                    return false;
                }
            }
            Log.d(TAG, "tryCaptureView() called with: child = [" + child + "], pointerId = [" + pointerId + "]");
            return mViewRef != null && mViewRef.get() == child;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            dispatchOnSlide(top);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == ViewDragHelper.STATE_DRAGGING) {
                setStateInternal(STATE_DRAGGING);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top;

            int currentTop = releasedChild.getTop();

            anchorOffect = Math.max(0, mParentHeight - mAnchorHeight);

            @AnchorSheetBehavior.State int targetState;
            if (yvel < 0 && currentTop < anchorOffect) { // Moving up
                top = mMinOffset;
                targetState = STATE_EXPANDED; //如果超过中间状态再向上移动，
            } else if (yvel < 0 && currentTop > anchorOffect){
                top = anchorOffect;
                targetState = STATE_ANCHOR; //如果未超过中间状态，那么直接移动到中间状态

            }else if (mHideable && shouldHide(releasedChild, yvel)) {
                top = mParentHeight;
                targetState = STATE_HIDDEN;
            } else if (yvel == 0.f) {
                if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - anchorOffect)) {
                    top = mMinOffset;
                    targetState = STATE_EXPANDED;
                } else {
                    top = anchorOffect;
                    targetState = STATE_ANCHOR;
                }
            } else {
                top = mMaxOffset;
                targetState = STATE_COLLAPSED;
            }
            if (mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
                setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(releasedChild,
                        new AnchorSheetBehavior.SettleRunnable(releasedChild, targetState));
            } else {
                setStateInternal(targetState);
            }
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return AnchorSheetBehavior.MathUtils.constrain(top, mMinOffset, mHideable ? mParentHeight : mMaxOffset);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return child.getLeft();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            if (mHideable) {
                return mParentHeight - mMinOffset;
            } else {
                return mMaxOffset - mMinOffset;
            }
        }
    };

    public void dispatchOnSlide(int top) {

        View bottomSheet = mViewRef.get();
        if (bottomSheet != null && mCallback != null) {
            if (top > mMaxOffset) {
                mCallback.onSlide(bottomSheet, (float) (mMaxOffset - top) / mPeekHeight);
            } else {
                mCallback.onSlide(bottomSheet,
                        (float) (mMaxOffset - top) / ((mMaxOffset - mMinOffset)));
            }
        }
    }

    public class SettleRunnable implements Runnable {

        private final View mView;

        @AnchorSheetBehavior.State
        private final int mTargetState;

        SettleRunnable(View view, @AnchorSheetBehavior.State int targetState) {
            mView = view;
            mTargetState = targetState;
        }

        @Override
        public void run() {
            if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(mView, this);
            } else {
                setStateInternal(mTargetState);
            }
        }
    }

    protected static class SavedState extends View.BaseSavedState {

        @AnchorSheetBehavior.State
        final int state;

        public SavedState(Parcel source) {
            super(source);
            //noinspection ResourceType
            state = source.readInt();
        }

        public SavedState(Parcelable superState, @AnchorSheetBehavior.State int state) {
            super(superState);
            this.state = state;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }

        public static final Parcelable.Creator<AnchorSheetBehavior.SavedState> CREATOR =
                new Parcelable.Creator<AnchorSheetBehavior.SavedState>() {
                    @Override
                    public AnchorSheetBehavior.SavedState createFromParcel(Parcel source) {
                        return new AnchorSheetBehavior.SavedState(source);
                    }

                    @Override
                    public AnchorSheetBehavior.SavedState[] newArray(int size) {
                        return new AnchorSheetBehavior.SavedState[size];
                    }
                };
    }


    static class MathUtils {

        static int constrain(int amount, int low, int high) {
            return amount < low ? low : (amount > high ? high : amount);
        }

        static float constrain(float amount, float low, float high) {
            return amount < low ? low : (amount > high ? high : amount);
        }

    }

    /**
     * A utility function to get the {@link AnchorSheetBehavior} associated with the {@code view}.
     *
     * @param view The {@link View} with {@link AnchorSheetBehavior}.
     * @return The {@link AnchorSheetBehavior} associated with the {@code view}.
     */
    @SuppressWarnings("unchecked")
    public static <V extends View> AnchorSheetBehavior<V> from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof AnchorSheetBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with AnchorSheetBehavior");
        }
        return (AnchorSheetBehavior<V>) behavior;
    }

    public boolean isAllowDragdown() {
        return isAllowDragdown;
    }

    @Override
    public void setAllowDragdown(boolean allowDragdown) {
        isAllowDragdown = allowDragdown;
    }


    @Override
    public void setAllowPullUp(boolean isAllowPullUp) {
        this.isAllowPullUp = isAllowPullUp;
    }

    /**
     * 指定可嵌套滑动的子View<br>
     * 通常BottomSheetBehavior会自动找到第一个可嵌套滑动的子View，参照 {@link #findScrollingChild(View)} <br>
     * 如果内容中有多个可嵌套滑动的View, 则只有第一个View会生效，第二个View将无法嵌套滑动，如果希望动态指定这个View，则调用此方法来指定
     * @param view 传null为不指定
     */
    public void assignNestedScrollingChild(View view) {
        if (view != null) {
            mNestedScrollingChildRef = new WeakReference<>(view);
            mNestedScrollingChildAssigned = true;
        } else {
            mNestedScrollingChildAssigned = false;
        }
    }

    public int getMaxOffset() {
        return mMaxOffset;
    }

    //不使用viewpager作为滑动的child，而是在viewpager中寻找嵌套滑动可用的子类，
    // 解决bottomSheetBavior下使用viewpager滑动失灵问题，仍待测试
    @VisibleForTesting
    public View findScrollingChild(View view) {
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view;
        }
        if (view instanceof ViewPager) {
            ViewPager viewPager = (ViewPager) view;
            View currentViewPagerChild = getCurrentView(viewPager);
            if (currentViewPagerChild == null) {
                return null;
            }

            View scrollingChild = findScrollingChild(currentViewPagerChild);
            if (scrollingChild != null) {
                return scrollingChild;
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0, count = group.getChildCount(); i < count; i++) {
                View scrollingChild = findScrollingChild(group.getChildAt(i));
                if (scrollingChild != null) {
                    return scrollingChild;
                }
            }
        }
        return null;
    }

    public View getCurrentView(ViewPager viewPager) {
        final int currentItem = viewPager.getCurrentItem();
        for (int i = 0; i < viewPager.getChildCount(); i++) {
            final View child = viewPager.getChildAt(i);
            final ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) child.getLayoutParams();
            try {
                if (!layoutParams.isDecor && currentItem == getPosition(layoutParams)) {
                    return child;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public int getPosition(Object object) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = object.getClass();
        Field field = clazz.getDeclaredField("position");
        field.setAccessible(true);
        return field.getInt(object);
    }
}
