package com.example.armmvvm.ui.refreshlayout

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.example.armmvvm.R
import kotlinx.android.synthetic.main.activity_toolbar2.*

class ToolbarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_test, menu)
//        return true
//    }
}