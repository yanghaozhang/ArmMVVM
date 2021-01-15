package com.example.armmvvm.ui.test;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author 创建人 ：xuciluan
 * @version 1.0
 * @package 包名 ：com.augurit.agmobile.agmultiplan.bottomsheet
 * @createTime 创建时间 ：2017-05-19
 * @modifyBy 修改人 ：xuciluan
 * @modifyTime 修改时间 ：2017-05-19
 * @modifyMemo 修改备注：
 */

public interface IBottomSheetBehavior {

    /**
     * The bottom sheet is dragging.
     */
    int STATE_DRAGGING = 1;

    /**
     * The bottom sheet is settling.
     */
    int STATE_SETTLING = 2;

    /**
     * The bottom sheet is expanded.
     */
    int STATE_EXPANDED = 3;

    /**
     * The bottom sheet is collapsed.
     */
    int STATE_COLLAPSED = 4;

    /**
     * The bottom sheet is hidden.
     */
    int STATE_HIDDEN = 5;

    /**
     * The bottom sheet is at anchor.
     */
    int STATE_ANCHOR = 6;

    /** @hide */
    @IntDef({STATE_EXPANDED, STATE_COLLAPSED, STATE_DRAGGING, STATE_SETTLING, STATE_HIDDEN, STATE_ANCHOR})
    @Retention(RetentionPolicy.SOURCE)
    @interface State {}

    void setAnchorHeight(int mAnchorHeight);

    /**
     * 设置状态
     * @param state
     */
    void setState(int state);

    /**
     * 设置是否可下拉
     * @param allowDragdown
     */
    void setAllowDragdown(boolean allowDragdown) ;

    /**
     * 设置是否可上拉
     * @param allowPullUp
     */
    void setAllowPullUp(boolean allowPullUp);
}
