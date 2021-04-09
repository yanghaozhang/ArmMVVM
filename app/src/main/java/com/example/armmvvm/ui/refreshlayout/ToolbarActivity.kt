package com.example.armmvvm.ui.refreshlayout

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.arm.base.BaseActivity
import com.example.armmvvm.R
import com.example.armmvvm.databinding.ActivityToolbar2Binding
import com.example.armmvvm.ui.test.AnchorSheetBehavior
import com.example.armmvvm.ui.test.IBottomSheetBehavior
import com.example.armmvvm.ui.viewpager.SimpleBackgroundFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min

class ToolbarActivity : BaseActivity<ActivityToolbar2Binding>() {
    var mTopHeight: Int = 0
    var mHeaderHeight: Int = 0

    override fun initView(savedInstanceState: Bundle?): ActivityToolbar2Binding {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        return ActivityToolbar2Binding.inflate(layoutInflater)
    }

    override fun initData(savedInstanceState: Bundle?) {
        binding.toolBar.title = ""
        /**
         * 该方法会初始化ActionBar
         * 1,setNavigationOnClickListener,该方法必须放置setSupportActionBar之后,否则无效
         * 2,如果Toolbar在xml中设置了menu,在该方法中AppCompatDelegateImpl.invalidateOptionsMenu();会使设置的menu无效
         */
//        setSupportActionBar(tool_bar)
        binding.toolBar.setNavigationOnClickListener {
            finish()
        }

        val fragmentList = listOf(
            SimpleBackgroundFragment("aa"),
            SimpleBackgroundFragment("bb"),
            SimpleBackgroundFragment("cc"),
            SimpleBackgroundFragment("dd"),
        )
        binding.viewPagerUp.offscreenPageLimit = fragmentList.size
        binding.viewPagerUp.adapter = ToolReuseAdapter(fragmentList, supportFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPagerUp)

        var behaviorTemp: CoordinatorLayout.Behavior<*>? = null
        try {
            val behavior = AnchorSheetBehavior.from(binding.bottomScrollView)
            useAnchorSheetBehavior(behavior)
            behaviorTemp = behavior
        } catch (exception: IllegalArgumentException) {
            // do nothing
        }

        try {
            val behavior = BottomSheetBehavior.from(binding.bottomScrollView)
            ustBottomSheetBehavior(behavior)
            behaviorTemp = behavior
        } catch (exception: IllegalArgumentException) {
            // do nothing
        }

        // 处理bottom_scroll_view的高度问题
        binding.bottomScrollView.post() {
            mTopHeight = binding.llTop.measuredHeight
            mHeaderHeight = binding.header.llHeader.height

            val expandedOffset = when (behaviorTemp) {
                is AnchorSheetBehavior -> {
                    behaviorTemp.peekHeight = mHeaderHeight + binding.ivArrow.height
                    behaviorTemp.mMinOffset
                }
                is BottomSheetBehavior -> {
                    behaviorTemp.peekHeight = mHeaderHeight + binding.ivArrow.height
                    behaviorTemp.expandedOffset
                }
                else -> 0
            }
            val layoutParams = binding.bottomScrollView.layoutParams
            //如果控件本身的Height值就小于展开高度，就不用做处理
            if (binding.bottomScrollView.height > binding.parentCoordinator.height - expandedOffset) {
                //屏幕高度减去STATE_EXPANDED状态时顶部的高度
                layoutParams.height = binding.parentCoordinator.height - expandedOffset
                binding.bottomScrollView.layoutParams = layoutParams
            }
        }
    }

    private fun ustBottomSheetBehavior(behavior: BottomSheetBehavior<*>) {
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val resId =
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) R.mipmap.icon_arrow_up_gray else R.mipmap.icon_arrow_down_gray
                binding.ivArrow.setImageResource(resId)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val percent = min(behavior.halfExpandedRatio, slideOffset) / behavior.halfExpandedRatio
                Timber.d("---- $percent")

                val heightSet = (mHeaderHeight - (mHeaderHeight - mTopHeight) * percent).toInt()
                Timber.d("onSlide: %s", heightSet)

                binding.header.llHeader.updateLayoutParams {
                    height = heightSet
                }
                binding.header.llHeader.alpha = 1 - percent

                binding.llTop.updateLayoutParams {
                    height = heightSet
                }
                binding.llTop.alpha = max((percent - SHOW_PERCENT) / (1 - SHOW_PERCENT), 0.0).toFloat()
            }
        })
    }

    private fun useAnchorSheetBehavior(behavior: AnchorSheetBehavior<*>) {
        behavior.state = IBottomSheetBehavior.STATE_COLLAPSED
        behavior.anchorHeight = resources.displayMetrics.heightPixels / 2
        behavior.setBottomSheetCallback(object : AnchorSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, oldState: Int, newState: Int) {
                val resId =
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) R.mipmap.icon_arrow_up_gray else R.mipmap.icon_arrow_down_gray
                binding.ivArrow.setImageResource(resId)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val anchorOffset = behavior.mParentHeight - behavior.mAnchorHeight
                val top = max(bottomSheet.top, anchorOffset)
                val percent = (behavior.mMaxOffset - top).toFloat() / (behavior.mMaxOffset - anchorOffset)
                Timber.d("---- $percent")

                val heightSet = (mHeaderHeight - (mHeaderHeight - mTopHeight) * percent).toInt()
                Timber.d("onSlide: %s", heightSet)

                binding.header.llHeader.updateLayoutParams {
                    height = heightSet
                }
                binding.header.llHeader.alpha = 1 - percent

                binding.llTop.updateLayoutParams {
                    height = heightSet
                }
                binding.llTop.alpha = max((percent - SHOW_PERCENT) / (1 - SHOW_PERCENT), 0.0).toFloat()
            }
        })
    }

    fun changeAdapter(view: View) {
        val fragmentList = listOf(
            SimpleBackgroundFragment("ee"),
            SimpleBackgroundFragment("ff"),
            SimpleBackgroundFragment("gg"),
            SimpleBackgroundFragment("hh"),
        )
        binding.viewPagerUp.offscreenPageLimit = fragmentList.size
        binding.viewPagerUp.adapter = ToolReuseAdapter(fragmentList, supportFragmentManager)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_test, menu)
//        return true
//    }

    class ToolReuseAdapter(var fragmentlist: List<SimpleBackgroundFragment>, fragmentManager: FragmentManager) :
        FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_SET_USER_VISIBLE_HINT) {
        private var createNum = 0
        override fun getCount(): Int {
            return fragmentlist.size
        }

        override fun getItem(position: Int): Fragment {
            Timber.d("--::create Fragment $createNum")
            return fragmentlist[position].apply {
                name = "${name.split(",")[0]},${createNum++}"
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentlist[position].name + createNum
        }
    }

    companion object {
        private const val SHOW_PERCENT = 0.3
    }
}