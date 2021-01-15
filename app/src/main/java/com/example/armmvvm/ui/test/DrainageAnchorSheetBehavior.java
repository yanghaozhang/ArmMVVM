package com.example.armmvvm.ui.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.customview.widget.ViewDragHelper;

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
                if (currentTop > anchorOffect) {
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

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        if (mViewDragHelper == null) {
            mViewDragHelper = ViewDragHelper.create(parent, mDelegateCallback);
        }
        return super.onLayoutChild(parent, child, layoutDirection);
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
