package com.example.armmvvm.ui.refreshlayout

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.armmvvm.R
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

        // 处理bottom_scroll_view的高度问题
        val behavior = BottomSheetBehavior.from(bottom_scroll_view)
        bottom_scroll_view.post() {
            val layoutParams = bottom_scroll_view.layoutParams
            //如果控件本身的Height值就小于返回按钮的高度，就不用做处理
            if (bottom_scroll_view.height > parent_coordinator.height - behavior.peekHeight) {
                //屏幕高度减去marinTop作为控件的Height
                layoutParams.height = parent_coordinator.height - behavior.peekHeight
                bottom_scroll_view.layoutParams = layoutParams
            }

            mTopHeight = ll_top.measuredHeight
            mHeaderHeight = header.height
        }
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val resId =
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) R.mipmap.icon_arrow_up_gray else R.mipmap.icon_arrow_down_gray
                iv_arrow.setImageResource(resId)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val percent = min(0.5f, slideOffset) / 0.5
                Timber.d("---- $percent")

                val layoutParamsHeader = header.layoutParams
                layoutParamsHeader.height = (mHeaderHeight - (mHeaderHeight - mTopHeight) * percent).toInt()
                Timber.d("onSlide: %s", layoutParamsHeader.height)
                header.layoutParams = layoutParamsHeader
                header.alpha = (1 - percent).toFloat()

                val layoutParamsTop = ll_top.layoutParams
                layoutParamsTop.height = layoutParamsHeader.height
                ll_top.layoutParams = layoutParamsTop
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