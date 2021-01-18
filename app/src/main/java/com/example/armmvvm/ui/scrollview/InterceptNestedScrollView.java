package com.example.armmvvm.ui.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 *
 * 上滑时顶层先滚动
 * @author 创建人 ：yanghaozhang
 * @version 1.0
 * @package 包名 ：com.augurit.agmobile.agwater5.drainage.common.scrollview
 * @createTime 创建时间 ：2021/1/13
 * @modifyBy 修改人 ：
 * @modifyTime 修改时间 ：
 * @modifyMemo 修改备注：
 */
public class InterceptNestedScrollView extends NestedScrollView {

    public InterceptNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public InterceptNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && canScrollVertically(1)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
}