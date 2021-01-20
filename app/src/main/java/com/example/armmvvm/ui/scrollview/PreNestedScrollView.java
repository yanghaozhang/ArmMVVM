package com.example.armmvvm.ui.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import timber.log.Timber;

/**
 * 向上滚动:消费子视图的滚动,并且先让父视图优先消费滚动
 *
 * 如果子类中不需要点击事件,可以使用{@link InterceptNestedScrollView},该类可以比较好地支持滚动,但会拦截所有事件
 *
 * @author 创建人 ：yanghaozhang
 * @version 1.0
 * @package 包名 ：com.augurit.agmobile.agwater5.drainage.common.scrollview
 * @createTime 创建时间 ：2021/1/13
 * @modifyBy 修改人 ：
 * @modifyTime 修改时间 ：
 * @modifyMemo 修改备注：
 */
public class PreNestedScrollView extends NestedScrollView {
    private static final String TAG = "PreNestedScrollView";
    private static final boolean DEBUG_LOG = true;

    public PreNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public PreNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PreNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 子视图的滚动
     */
    @Override
    public void onNestedPreScroll(@NotNull View target, int dx, int dy, int @NotNull [] consumed) {
        if (dy < 0) {
            super.onNestedPreScroll(target, dx, dy, consumed);
            return;
        }
        if (DEBUG_LOG) {
            Timber.tag(TAG).d("onNestedPreScroll() --: target = [" + target + "], dx = [" + dx + "], dy = [" + dy + "], consumed = [" + consumed[1] + "]");
        }
        // 顶层滚动
        if (dy > 0 && getScrollY() == 0) {
            int[] offsetInWindow = new int[2];
            dispatchNestedScroll(dx, dy, dx, dy, offsetInWindow, ViewCompat.TYPE_TOUCH);
            consumed[1] -= offsetInWindow[1];
            dy += offsetInWindow[1];
            if (DEBUG_LOG) {
                Timber.tag(TAG).d("dispatchNestedPreScroll: offsetInWindow[1]:%s", offsetInWindow[1]);
            }
        }
        // 当前PreNestedScrollView滚动
        final int oldScrollY = getScrollY();
        scrollBy(0, dy);
        final int myConsumed = getScrollY() - oldScrollY;
        consumed[1] += myConsumed;
        if (DEBUG_LOG) {
            Timber.tag(TAG).d("dispatchNestedPreScroll: myConsumed:%s", myConsumed);
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy < 0 || type != ViewCompat.TYPE_TOUCH) {
            super.onNestedPreScroll(target, dx, dy, consumed, type);
            return;
        }
        if (DEBUG_LOG) {
            Timber.tag(TAG).d("onNestedPreScroll() --: target = [" + target + "], dx = [" + dx + "], dy = [" + dy + "], consumed = [" + consumed[1] + "]");
        }
        // 顶层滚动
        if (dy > 0 && getScrollY() == 0) {
            int[] offsetInWindow = new int[2];
            dispatchNestedScroll(dx, dy, dx, dy, offsetInWindow, ViewCompat.TYPE_TOUCH);
            consumed[1] -= offsetInWindow[1];
            dy += offsetInWindow[1];
            if (DEBUG_LOG) {
                Timber.tag(TAG).d("dispatchNestedPreScroll: offsetInWindow[1]:%s", offsetInWindow[1]);
            }
        }
        // 当前PreNestedScrollView滚动
        final int oldScrollY = getScrollY();
        scrollBy(0, dy);
        final int myConsumed = getScrollY() - oldScrollY;
        consumed[1] += myConsumed;
        if (DEBUG_LOG) {
            Timber.tag(TAG).d("dispatchNestedPreScroll: myConsumed:%s", myConsumed);
        }
    }

    /**
     * 当前视图的滚动
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        if (dy < 0 || (dy > 0 && getScrollY() > 0)) {
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
        }
        if (DEBUG_LOG) {
            Timber.tag(TAG).d("dispatchNestedPreScroll() --: dx = [" + dx + "], dy = [" + dy + "], consumed = [" + consumed[1] + "], offsetInWindow = [" + Arrays.toString(offsetInWindow) + "]");
        }
        if (offsetInWindow == null) {
            offsetInWindow = new int[2];
        }

        // 顶层滚动
        dispatchNestedScroll(dx, dy, dx, dy, offsetInWindow);
        consumed[1] = -offsetInWindow[1];
        if (DEBUG_LOG) {
            Timber.tag(TAG).d("dispatchNestedPreScroll: offsetInWindow[1]:%s", offsetInWindow[1]);
        }
        return consumed[1] != 0;
    }

    /**
     * 当前视图的滚动
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (dy < 0 || (dy > 0 && getScrollY() > 0) || type != ViewCompat.TYPE_TOUCH) {
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        }
        if (DEBUG_LOG) {
            Timber.tag(TAG).d("dispatchNestedPreScroll() --: dx = [" + dx + "], dy = [" + dy + "], consumed = [" + consumed[1] + "], offsetInWindow = [" + Arrays.toString(offsetInWindow) + "], type = [" + type + "]");
        }
        if (offsetInWindow == null) {
            offsetInWindow = new int[2];
        }

        // 顶层滚动
        dispatchNestedScroll(dx, dy, dx, dy, offsetInWindow, type);
        consumed[1] = -offsetInWindow[1];
        if (DEBUG_LOG) {
            Timber.tag(TAG).d("dispatchNestedPreScroll: offsetInWindow[1]:%s", offsetInWindow[1]);
        }
        return consumed[1] != 0;
    }

    /**
     * 子视图的Fling
     */
    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        if (DEBUG_LOG) {
            Timber.tag(TAG).d("onNestedPreFling() called with: target = [" + target + "], velocityX = [" + velocityX + "], velocityY = [" + velocityY + "]");
        }
        if (velocityY < 0) {
            // RecyclerView向下滚动时,且已经到达顶部,PreNestedScrollView消费滚动
            boolean consumeFling = super.dispatchNestedPreFling(velocityX, velocityY);
            if (!consumeFling && target instanceof RecyclerView && ((RecyclerView) target).computeVerticalScrollOffset() == 0) {
                fling((int) velocityY);
                consumeFling = true;
            }
            return consumeFling;
        }
        if (!super.dispatchNestedPreFling(velocityX, velocityY)) {
            dispatchNestedFling(0, velocityY, false);
            // 如果PreNestedScrollView不能向上滚动时,交由子视图自行处理滚动
            if (canScrollVertically(1)) {
                fling((int) velocityY);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 当前视图的Fling
     */
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (DEBUG_LOG) {
            Timber.tag(TAG).d("dispatchNestedPreFling() called with: velocityX = [" + velocityX + "], velocityY = [" + velocityY + "]");
        }
        // 向下滚动 或向上滚动时,PreNestedScrollView已到达顶部,不处理
        if (velocityY < 0 || (velocityY > 0 && getScrollY() > 0)) {
            return super.dispatchNestedPreFling(velocityX, velocityY);
        }
        if (!super.dispatchNestedPreFling(velocityX, velocityY)) {
            return dispatchNestedFling(0, velocityY, false);
        }
        return true;
    }
}