package com.example.armmvvm.ui.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import java.lang.ref.WeakReference;

/**
 * @author 创建人 ：yanghaozhang
 * @version 1.0
 * @package 包名 ：com.augurit.agmobile.agwater5.drainage.common.behavior
 * @createTime 创建时间 ：2021/1/11
 * @modifyBy 修改人 ：
 * @modifyTime 修改时间 ：
 * @modifyMemo 修改备注：
 */
public class DrainageAnchorSheetBehavior<V extends View> extends AnchorSheetBehavior<V> {

    public ViewDragHelper.Callback mDelegateCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return mDragCallback.tryCaptureView(child, pointerId);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mDragCallback.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            mDragCallback.onViewDragStateChanged(state);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int currentTop = releasedChild.getTop();
            int anchorOffect = mParentHeight - mAnchorHeight;
            // 向下滑,且currentTop在(mMinOffset,anchorOffect)之间
            if (yvel > 0 && currentTop < anchorOffect && currentTop > mMinOffset) {
                setState(STATE_ANCHOR);
            } else if (yvel == 0f) {
                if (currentTop < anchorOffect) {
                    if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - anchorOffect)) {
                        setState(STATE_EXPANDED);
                    } else {
                        setState(STATE_ANCHOR);
                    }
                } else {
                    if (Math.abs(currentTop - mMaxOffset) < Math.abs(currentTop - anchorOffect)) {
                        setState(STATE_COLLAPSED);
                    } else {
                        setState(STATE_ANCHOR);
                    }
                }
            } else {
                mDragCallback.onViewReleased(releasedChild, xvel, yvel);
            }
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return mDragCallback.clampViewPositionVertical(child, top, dy);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return mDragCallback.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mDragCallback.getViewVerticalDragRange(child);
        }
    };

    public DrainageAnchorSheetBehavior() {
    }

    public DrainageAnchorSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 1,使用mDelegateCallback,修复向下滑直接从STATE_EXPANDED改变到STATE_COLLAPSED
     * 2,修改每次都调用parent.onLayoutChild(child, layoutDirection) 实现子View的改变能实时更新
     */
    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        // First let the parent lay it out
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
            mViewDragHelper = ViewDragHelper.create(parent, mDelegateCallback);
        }
        mViewRef = new WeakReference<>(child);
        if (!mNestedScrollingChildAssigned) {
            mNestedScrollingChildRef = new WeakReference<>(findScrollingChild(child));
        }
        return true;
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        int currentTop = child.getTop();
        int anchorOffect = mParentHeight - mAnchorHeight;
        // 向下滑,且currentTop在(mMinOffset,anchorOffect)之间
        if (mLastNestedScrollDy < 0 && currentTop < anchorOffect && currentTop > mMinOffset) {
            setState(STATE_ANCHOR);
        } else {
            super.onStopNestedScroll(coordinatorLayout, child, target);
        }
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (mViewDragHelper == null) {
            mViewDragHelper = ViewDragHelper.create(parent, mDelegateCallback);
        }
        return super.onTouchEvent(parent, child, event);
    }
}
