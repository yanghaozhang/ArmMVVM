package com.example.armmvvm.ui.refreshlayout

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import com.example.armmvvm.R
import com.example.armmvvm.ui.test.AnchorSheetBehavior
import com.example.armmvvm.ui.test.IBottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_toolbar2.*
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min

class ToolbarActivity : AppCompatActivity() {
    var mTopHeight: Int = 0
    var mHeaderHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_toolbar2)
        tool_bar.title = ""
        /**
         * 该方法会初始化ActionBar
         * 1,setNavigationOnClickListener,该方法必须放置setSupportActionBar之后,否则无效
         * 2,如果Toolbar在xml中设置了menu,在该方法中AppCompatDelegateImpl.invalidateOptionsMenu();会使设置的menu无效
         */
//        setSupportActionBar(tool_bar)
        tool_bar.setNavigationOnClickListener {
            finish()
        }

        var behaviorTemp: CoordinatorLayout.Behavior<*>? = null
        try {
            val behavior = AnchorSheetBehavior.from(bottom_scroll_view)
            useAnchorSheetBehavior(behavior)
            behaviorTemp = behavior
        } catch (exception: IllegalArgumentException) {
            // do nothing
        }

        try {
            val behavior = BottomSheetBehavior.from(bottom_scroll_view)
            ustBottomSheetBehavior(behavior)
            behaviorTemp = behavior
        } catch (exception: IllegalArgumentException) {
            // do nothing
        }

        // 处理bottom_scroll_view的高度问题
        bottom_scroll_view.post() {
            mTopHeight = ll_top.measuredHeight
            mHeaderHeight = header.height

            val expandedOffset = when (behaviorTemp) {
                is AnchorSheetBehavior -> {
                    behaviorTemp.peekHeight = mHeaderHeight + iv_arrow.height
                    behaviorTemp.mMinOffset
                }
                is BottomSheetBehavior -> {
                    behaviorTemp.peekHeight = mHeaderHeight + iv_arrow.height
                    behaviorTemp.expandedOffset
                }
                else -> 0
            }
            val layoutParams = bottom_scroll_view.layoutParams
            //如果控件本身的Height值就小于展开高度，就不用做处理
            if (bottom_scroll_view.height > parent_coordinator.height - expandedOffset) {
                //屏幕高度减去STATE_EXPANDED状态时顶部的高度
                layoutParams.height = parent_coordinator.height - expandedOffset
                bottom_scroll_view.layoutParams = layoutParams
            }
        }
    }

    private fun ustBottomSheetBehavior(behavior: BottomSheetBehavior<NestedScrollView>) {
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val resId =
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) R.mipmap.icon_arrow_up_gray else R.mipmap.icon_arrow_down_gray
                iv_arrow.setImageResource(resId)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val percent = min(behavior.halfExpandedRatio, slideOffset) / behavior.halfExpandedRatio
                Timber.d("---- $percent")

                val heightSet = (mHeaderHeight - (mHeaderHeight - mTopHeight) * percent).toInt()
                Timber.d("onSlide: %s", heightSet)

                header.updateLayoutParams {
                    height = heightSet
                }
                header.alpha = (1 - percent).toFloat()

                ll_top.updateLayoutParams {
                    height = heightSet
                }
                ll_top.alpha = max((percent - SHOW_PERCENT) / (1 - SHOW_PERCENT), 0.0).toFloat()
            }
        })
    }

    private fun useAnchorSheetBehavior(behavior: AnchorSheetBehavior<NestedScrollView>) {
        behavior.state = IBottomSheetBehavior.STATE_COLLAPSED
        behavior.anchorHeight = resources.displayMetrics.heightPixels / 2
        behavior.setBottomSheetCallback(object : AnchorSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, oldState: Int, newState: Int) {
                val resId =
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) R.mipmap.icon_arrow_up_gray else R.mipmap.icon_arrow_down_gray
                iv_arrow.setImageResource(resId)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val anchorOffset = behavior.mParentHeight - behavior.mAnchorHeight
                val top = max(bottomSheet.top, anchorOffset)
                val percent = (behavior.mMaxOffset - top).toFloat() / (behavior.mMaxOffset - anchorOffset)
                Timber.d("---- $percent")

                val heightSet = (mHeaderHeight - (mHeaderHeight - mTopHeight) * percent).toInt()
                Timber.d("onSlide: %s", heightSet)

                header.updateLayoutParams {
                    height = heightSet
                }
                header.alpha = 1 - percent

                ll_top.updateLayoutParams {
                    height = heightSet
                }
                ll_top.alpha = max((percent - SHOW_PERCENT) / (1 - SHOW_PERCENT), 0.0).toFloat()
            }
        })
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_test, menu)
//        return true
//    }

    companion object {
        private const val SHOW_PERCENT = 0.3
    }
}