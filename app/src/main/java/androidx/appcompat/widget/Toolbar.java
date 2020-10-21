
package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.R;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.CollapsibleActionView;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.view.menu.MenuPresenter;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.view.menu.SubMenuBuilder;
import androidx.core.view.GravityCompat;
import androidx.core.view.MarginLayoutParamsCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.view.AbsSavedState;

import java.util.ArrayList;
import java.util.List;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;
import static androidx.annotation.RestrictTo.Scope.TESTS;

/**
 * A standard toolbar for use within application content.
 *
 * <p>A Toolbar is a generalization of {@link ActionBar action bars} for use
 * within application layouts. While an action bar is traditionally part of an
 * {@link android.app.Activity Activity's} opaque window decor controlled by the framework,
 * a Toolbar may be placed at any arbitrary level of nesting within a view hierarchy.
 * An application may choose to designate a Toolbar as the action bar for an Activity
 * using the {@link androidx.appcompat.app.AppCompatActivity#setSupportActionBar(Toolbar)
 * setSupportActionBar()} method.</p>
 *
 * <p>Toolbar supports a more focused feature set than ActionBar. From start to end, a toolbar
 * may contain a combination of the following optional elements:
 *
 * <ul>
 *     <li><em>A navigation button.</em> This may be an Up arrow, navigation menu toggle, close,
 *     collapse, done or another glyph of the app's choosing. This button should always be used
 *     to access other navigational destinations within the container of the Toolbar and
 *     its signified content or otherwise leave the current context signified by the Toolbar.
 *     The navigation button is vertically aligned within the Toolbar's minimum height,
 *     if set.</li>
 *     <li><em>A branded logo image.</em> This may extend to the height of the bar and can be
 *     arbitrarily wide.</li>
 *     <li><em>A title and subtitle.</em> The title should be a signpost for the Toolbar's current
 *     position in the navigation hierarchy and the content contained there. The subtitle,
 *     if present should indicate any extended information about the current content.
 *     If an app uses a logo image it should strongly consider omitting a title and subtitle.</li>
 *     <li><em>One or more custom views.</em> The application may add arbitrary child views
 *     to the Toolbar. They will appear at this position within the layout. If a child view's
 *     {@link LayoutParams} indicates a {@link Gravity} value of
 *     {@link Gravity#CENTER_HORIZONTAL CENTER_HORIZONTAL} the view will attempt to center
 *     within the available space remaining in the Toolbar after all other elements have been
 *     measured.</li>
 *     <li><em>An {@link ActionMenuView action menu}.</em> The menu of actions will pin to the
 *     end of the Toolbar offering a few
 *     <a href="http://developer.android.com/design/patterns/actionbar.html#ActionButtons">
 *     frequent, important or typical</a> actions along with an optional overflow menu for
 *     additional actions. Action buttons are vertically aligned within the Toolbar's
 *     minimum height, if set.</li>
 * </ul>
 * </p>
 *
 * <p>In modern Android UIs developers should lean more on a visually distinct color scheme for
 * toolbars than on their application icon. The use of application icon plus title as a standard
 * layout is discouraged on API 21 devices and newer.</p>
 * --->所有button的布局方式:center_vertical/top/bottom处于Toolbar的纵向中心/顶部/底部,按钮包括有nav/menu/CollapseButton<---
 * {@link androidx.appcompat.R.attr#buttonGravity}
 * {@link androidx.appcompat.R.attr#collapseContentDescription}
 * --->用作collapseActionView的drawable,与Menu配合使用,点击menu显示相应组件和collapseActionView(作用:点击返回),比如SearchView<---
 * https://developer.android.com/training/appbar/action-views
 * {@link androidx.appcompat.R.attr#collapseIcon}
 *  --->内容(content View)与尾部的距离,以下规则相应<---
 * {@link androidx.appcompat.R.attr#contentInsetEnd}
 * {@link androidx.appcompat.R.attr#contentInsetLeft}
 * {@link androidx.appcompat.R.attr#contentInsetRight}
 * {@link androidx.appcompat.R.attr#contentInsetStart}
 * --->内容(content View)与Navigation的距离<---
 * {@link androidx.appcompat.R.attr#contentInsetStartWithNavigation}
 * --->Math.max(getContentInsetEnd(), Math.max(mContentInsetEndWithActions, 0))  与ContentInsetEnd一起决定内容与menu的距离<---
 * {@link androidx.appcompat.R.attr#contentInsetEndWithActions}
 * {@link android.R.attr#gravity}
 * --->logo drawable<---
 * {@link androidx.appcompat.R.attr#logo}
 * {@link androidx.appcompat.R.attr#logoDescription}
 * --->返回按钮/menu等按钮的最高高度<---
 * {@link androidx.appcompat.R.attr#maxButtonHeight}
 * {@link androidx.appcompat.R.attr#navigationContentDescription}
 * --->导航按钮drawable<---
 * {@link androidx.appcompat.R.attr#navigationIcon}
 * --->menu popup即OverflowMenu 的主题样式<---
 * {@link androidx.appcompat.R.attr#popupTheme}
 * --->子标题<---
 * {@link androidx.appcompat.R.attr#subtitle}
 * {@link androidx.appcompat.R.attr#subtitleTextAppearance}
 * --->子标题颜色<---
 * {@link androidx.appcompat.R.attr#subtitleTextColor}
 * --->标题<---
 * {@link androidx.appcompat.R.attr#title}
 * --->标题的一些margin<---
 * {@link androidx.appcompat.R.attr#titleMargin}
 * {@link androidx.appcompat.R.attr#titleMarginBottom}
 * {@link androidx.appcompat.R.attr#titleMarginEnd}
 * {@link androidx.appcompat.R.attr#titleMarginStart}
 * {@link androidx.appcompat.R.attr#titleMarginTop}
 * {@link androidx.appcompat.R.attr#titleTextAppearance}
 * {@link androidx.appcompat.R.attr#titleTextColor}
 * {@link androidx.appcompat.R.attr#menu}
 */
public class Toolbar extends ViewGroup {
    private static final String TAG = "Toolbar";

    // Menu菜单
    private ActionMenuView mMenuView;
    // 标题View
    private TextView mTitleTextView;
    // 子标题View
    private TextView mSubtitleTextView;
    // 导航图标View
    private ImageButton mNavButtonView;
    // 图标View
    private ImageView mLogoView;

    // 返回图标drawable,用于mCollapseButtonView
    // 使用场景menu 的showAsAction = collapseActionView时
    // 点击该menu图标会切换到查看Action详情状态,Toolbar展示对应的actionViewClass界面,此时会展示mCollapseButtonView(点击返回到正常的状态)
    private Drawable mCollapseIcon;
    private CharSequence mCollapseDescription;
    // 查看Action详情状态时,展示mCollapseButtonView,点击该View Toolbar返回正常状态
    ImageButton mCollapseButtonView;
    // 查看Action详情状态时,Toolbar所展示的View,即Menu中声明的actionViewClass生成的View
    // 如果返回正常状态,会被置空
    // 比如android.widget.SearchView
    View mExpandedActionView;

    /**
     * Context against which to inflate popup menus.
     */
    // 用于Menu的popup window,如果为popup设置了主题,通过ContextThemeWrapper将Theme传给context,并通过context传给popup
    // 初始化Menu和添加MenuPresenter时使用
    private Context mPopupContext;

    /**
     * Theme resource against which to inflate popup menus.
     */
    // popup window的主题id
    private int mPopupTheme;

    // 标题的样式id
    private int mTitleTextAppearance;
    // 副标题的样式id
    private int mSubtitleTextAppearance;

    // Menu/Nav/mCollapseButtonView/mExpandedActionView这些View在Vertical上的Gravity
    // mButtonGravity & Gravity.VERTICAL_GRAVITY_MASK 只保留Vertical上的Gravity,去除Horizontal的Gravity
    // 上述的View在Horizontal上为Start或End,都是写死的
    int mButtonGravity;

    // Nav/mCollapseButtonView/mMenuView的最高高度,在Measure的时候限制高度
    private int mMaxButtonHeight;

    // 标题的margin
    private int mTitleMarginStart;
    private int mTitleMarginEnd;
    private int mTitleMarginTop;
    private int mTitleMarginBottom;

    // content的Left/Right/Start/End
    // mContentInsets与mContentInsetStartWithNavigation/mContentInsetEndWithActions的关系
    // mContentInsetStartWithNavigation如果不存在,mContentInsets为
    private RtlSpacingHelper mContentInsets;
    // content与Nav的距离
    private int mContentInsetStartWithNavigation;
    // content与Menu的距离
    private int mContentInsetEndWithActions;

    // Title的Gravity,TOP/BOTTOM/CENTER_VERTICAL的布局方式不一样
    private int mGravity = GravityCompat.START | Gravity.CENTER_VERTICAL;

    // 标题内容
    private CharSequence mTitleText;
    // 副标题内容
    private CharSequence mSubtitleText;

    // 标题颜色
    private ColorStateList mTitleTextColor;
    // 副标题颜色
    private ColorStateList mSubtitleTextColor;

    // Toolbar默认消费所有的点击事件
    // 如果Toolbar消费事件,则一直调用super.onTouchEvent(ev)
    // 如果不消费事件,super.onTouchEvent(ev)->false,那么后续不会调用super.onTouchEvent(ev)
    private boolean mEatingTouch;
    // 如果super.onHoverEvent(ev)消费事件返回true,则一直调用super.onHoverEvent(ev),否则只调用一次
    private boolean mEatingHover;

    // Clear me after use.
    // 为了在onLayout中处理布局的位置,有Left/Right/CENTER_HORIZONTAL,使用前使用后都需要clear,只是临时使用
    private final ArrayList<View> mTempViews = new ArrayList<View>();

    // Used to hold views that will be removed while we have an expanded action view.
    // 点击Menu显示ActionView,此时TitleView,Nav,Logo等View需要进行隐藏,在这里进行保存
    private final ArrayList<View> mHiddenViews = new ArrayList<>();

    // 保存onMesure中的MarginStart/MarginEnd,在onLayout中使用
    private final int[] mTempMargins = new int[2];

    // Menu点击事件监听
    OnMenuItemClickListener mOnMenuItemClickListener;

    private final ActionMenuView.OnMenuItemClickListener mMenuViewItemClickListener =
            new ActionMenuView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (mOnMenuItemClickListener != null) {
                        return mOnMenuItemClickListener.onMenuItemClick(item);
                    }
                    return false;
                }
            };

    // 系统内部使用
    private ToolbarWidgetWrapper mWrapper;
    /**
     * 关于Menu的处理
     * mMenuView是创建的ActionMenuView,用于承载菜单按钮与更多按钮
     * ActionMenuView主要通过MenuBuilder和MenuPresenter来管理所有事件
     * MenuPresenter是监听者,MenuBuilder是被监听者,当发生点击/展开/关闭等事件时,会向Presenter发送事件
     * Toolbar实现Expanded的功能,即点击menu可以展示mExpandedActionView
     * 1,Toolbar添加返回按钮mCollapseButtonView
     * 2,Toolbar添加拓展功能布局mExpandedActionView
     * 3,除了1,2的View和mMenuView,隐藏Toolbar其他子View
     * 上述功能通过mExpandedMenuPresenter来根据ActionMenuView的事件来变动
     * <p>
     * ActionMenuView的生成有两种方式
     * 方式一:在Activity中配置生成Menu
     * 方式二:通过Toolbar生成,包括XML或者代码设置
     * 如果是方式一,将通过内部调用setMenu(MenuBuilder menu, ActionMenuPresenter outerPresenter),此时会比方式二多了
     * mOuterActionMenuPresenter/mActionMenuPresenterCallback/mMenuBuilderCallback
     */
    // Menu的外部管理类Presenter
    private ActionMenuPresenter mOuterActionMenuPresenter;
    // 订阅ActionMenuView事件,并依据事件管理mExpandedActionView
    private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
    // 系统外部提供的回调函数
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    // 系统外部提供的回调函数
    private MenuBuilder.Callback mMenuBuilderCallback;

    // Toolbar是否高度为0,内部不存在可见元素
    private boolean mCollapsible;

    private final Runnable mShowOverflowMenuRunnable = new Runnable() {
        @Override
        public void run() {
            showOverflowMenu();
        }
    };

    public Toolbar(@NonNull Context context) {
        this(context, null);
    }

    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.toolbarStyle);
    }

    public Toolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Need to use getContext() here so that we use the themed context
        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                R.styleable.Toolbar, defStyleAttr, 0);
        ViewCompat.saveAttributeDataForStyleable(this, context, R.styleable.Toolbar, attrs,
                a.getWrappedTypeArray(), defStyleAttr, 0);

        mTitleTextAppearance = a.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
        mSubtitleTextAppearance = a.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, 0);
        mGravity = a.getInteger(R.styleable.Toolbar_android_gravity, mGravity);
        mButtonGravity = a.getInteger(R.styleable.Toolbar_buttonGravity, Gravity.TOP);

        // First read the correct attribute
        int titleMargin = a.getDimensionPixelOffset(R.styleable.Toolbar_titleMargin, 0);
        if (a.hasValue(R.styleable.Toolbar_titleMargins)) {
            // Now read the deprecated attribute, if it has a value
            titleMargin = a.getDimensionPixelOffset(R.styleable.Toolbar_titleMargins, titleMargin);
        }
        mTitleMarginStart = mTitleMarginEnd = mTitleMarginTop = mTitleMarginBottom = titleMargin;

        final int marginStart = a.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginStart, -1);
        if (marginStart >= 0) {
            mTitleMarginStart = marginStart;
        }

        final int marginEnd = a.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginEnd, -1);
        if (marginEnd >= 0) {
            mTitleMarginEnd = marginEnd;
        }

        final int marginTop = a.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginTop, -1);
        if (marginTop >= 0) {
            mTitleMarginTop = marginTop;
        }

        final int marginBottom = a.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginBottom,
                -1);
        if (marginBottom >= 0) {
            mTitleMarginBottom = marginBottom;
        }

        mMaxButtonHeight = a.getDimensionPixelSize(R.styleable.Toolbar_maxButtonHeight, -1);

        final int contentInsetStart =
                a.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStart,
                        RtlSpacingHelper.UNDEFINED);
        final int contentInsetEnd =
                a.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEnd,
                        RtlSpacingHelper.UNDEFINED);
        final int contentInsetLeft =
                a.getDimensionPixelSize(R.styleable.Toolbar_contentInsetLeft, 0);
        final int contentInsetRight =
                a.getDimensionPixelSize(R.styleable.Toolbar_contentInsetRight, 0);

        ensureContentInsets();
        mContentInsets.setAbsolute(contentInsetLeft, contentInsetRight);

        if (contentInsetStart != RtlSpacingHelper.UNDEFINED ||
                contentInsetEnd != RtlSpacingHelper.UNDEFINED) {
            mContentInsets.setRelative(contentInsetStart, contentInsetEnd);
        }

        mContentInsetStartWithNavigation = a.getDimensionPixelOffset(
                R.styleable.Toolbar_contentInsetStartWithNavigation, RtlSpacingHelper.UNDEFINED);
        mContentInsetEndWithActions = a.getDimensionPixelOffset(
                R.styleable.Toolbar_contentInsetEndWithActions, RtlSpacingHelper.UNDEFINED);

        mCollapseIcon = a.getDrawable(R.styleable.Toolbar_collapseIcon);
        mCollapseDescription = a.getText(R.styleable.Toolbar_collapseContentDescription);

        final CharSequence title = a.getText(R.styleable.Toolbar_title);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        final CharSequence subtitle = a.getText(R.styleable.Toolbar_subtitle);
        if (!TextUtils.isEmpty(subtitle)) {
            setSubtitle(subtitle);
        }

        // Set the default context, since setPopupTheme() may be a no-op.
        mPopupContext = getContext();
        setPopupTheme(a.getResourceId(R.styleable.Toolbar_popupTheme, 0));

        final Drawable navIcon = a.getDrawable(R.styleable.Toolbar_navigationIcon);
        if (navIcon != null) {
            setNavigationIcon(navIcon);
        }
        final CharSequence navDesc = a.getText(R.styleable.Toolbar_navigationContentDescription);
        if (!TextUtils.isEmpty(navDesc)) {
            setNavigationContentDescription(navDesc);
        }

        final Drawable logo = a.getDrawable(R.styleable.Toolbar_logo);
        if (logo != null) {
            setLogo(logo);
        }

        final CharSequence logoDesc = a.getText(R.styleable.Toolbar_logoDescription);
        if (!TextUtils.isEmpty(logoDesc)) {
            setLogoDescription(logoDesc);
        }

        if (a.hasValue(R.styleable.Toolbar_titleTextColor)) {
            setTitleTextColor(a.getColorStateList(R.styleable.Toolbar_titleTextColor));
        }

        if (a.hasValue(R.styleable.Toolbar_subtitleTextColor)) {
            setSubtitleTextColor(a.getColorStateList(R.styleable.Toolbar_subtitleTextColor));
        }

        if (a.hasValue(R.styleable.Toolbar_menu)) {
            inflateMenu(a.getResourceId(R.styleable.Toolbar_menu, 0));
        }

        a.recycle();
    }

    /**
     * Specifies the theme to use when inflating popup menus. By default, uses
     * the same theme as the toolbar itself.
     *
     * @param resId theme used to inflate popup menus
     * @see #getPopupTheme()
     */
    public void setPopupTheme(@StyleRes int resId) {
        if (mPopupTheme != resId) {
            mPopupTheme = resId;
            if (resId == 0) {
                mPopupContext = getContext();
            } else {
                mPopupContext = new ContextThemeWrapper(getContext(), resId);
            }
        }
    }

    /**
     * @return resource identifier of the theme used to inflate popup menus, or
     * 0 if menus are inflated against the toolbar theme
     * @see #setPopupTheme(int)
     */
    public int getPopupTheme() {
        return mPopupTheme;
    }

    /**
     * Sets the title margin.
     *
     * @param start  the starting title margin in pixels
     * @param top    the top title margin in pixels
     * @param end    the ending title margin in pixels
     * @param bottom the bottom title margin in pixels
     * @see #getTitleMarginStart()
     * @see #getTitleMarginTop()
     * @see #getTitleMarginEnd()
     * @see #getTitleMarginBottom()
     * {@link androidx.appcompat.R.attr#titleMargin}
     */
    public void setTitleMargin(int start, int top, int end, int bottom) {
        mTitleMarginStart = start;
        mTitleMarginTop = top;
        mTitleMarginEnd = end;
        mTitleMarginBottom = bottom;

        requestLayout();
    }

    /**
     * @return the starting title margin in pixels
     * @see #setTitleMarginStart(int)
     * {@link androidx.appcompat.R.attr#titleMarginStart}
     */
    public int getTitleMarginStart() {
        return mTitleMarginStart;
    }

    /**
     * Sets the starting title margin in pixels.
     *
     * @param margin the starting title margin in pixels
     * @see #getTitleMarginStart()
     * {@link androidx.appcompat.R.attr#titleMarginStart}
     */
    public void setTitleMarginStart(int margin) {
        mTitleMarginStart = margin;

        requestLayout();
    }

    /**
     * @return the top title margin in pixels
     * @see #setTitleMarginTop(int)
     * {@link androidx.appcompat.R.attr#titleMarginTop}
     */
    public int getTitleMarginTop() {
        return mTitleMarginTop;
    }

    /**
     * Sets the top title margin in pixels.
     *
     * @param margin the top title margin in pixels
     * @see #getTitleMarginTop()
     * {@link androidx.appcompat.R.attr#titleMarginTop}
     */
    public void setTitleMarginTop(int margin) {
        mTitleMarginTop = margin;

        requestLayout();
    }

    /**
     * @return the ending title margin in pixels
     * @see #setTitleMarginEnd(int)
     * {@link androidx.appcompat.R.attr#titleMarginEnd}
     */
    public int getTitleMarginEnd() {
        return mTitleMarginEnd;
    }

    /**
     * Sets the ending title margin in pixels.
     *
     * @param margin the ending title margin in pixels
     * @see #getTitleMarginEnd()
     * {@link androidx.appcompat.R.attr#titleMarginEnd}
     */
    public void setTitleMarginEnd(int margin) {
        mTitleMarginEnd = margin;

        requestLayout();
    }

    /**
     * @return the bottom title margin in pixels
     * @see #setTitleMarginBottom(int)
     * {@link androidx.appcompat.R.attr#titleMarginBottom}
     */
    public int getTitleMarginBottom() {
        return mTitleMarginBottom;
    }

    /**
     * Sets the bottom title margin in pixels.
     *
     * @param margin the bottom title margin in pixels
     * @see #getTitleMarginBottom()
     * {@link androidx.appcompat.R.attr#titleMarginBottom}
     */
    public void setTitleMarginBottom(int margin) {
        mTitleMarginBottom = margin;
        requestLayout();
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        if (Build.VERSION.SDK_INT >= 17) {
            super.onRtlPropertiesChanged(layoutDirection);
        }

        ensureContentInsets();
        mContentInsets.setDirection(layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL);
    }

    /**
     * Set a logo drawable from a resource id.
     *
     * <p>This drawable should generally take the place of title text. The logo cannot be
     * clicked. Apps using a logo should also supply a description using
     * {@link #setLogoDescription(int)}.</p>
     *
     * @param resId ID of a drawable resource
     */
    public void setLogo(@DrawableRes int resId) {
        setLogo(AppCompatResources.getDrawable(getContext(), resId));
    }

    /** @hide */
    /**
     * 菜单popup是否存在,不论弹出状态
     */
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    public boolean canShowOverflowMenu() {
        return getVisibility() == VISIBLE && mMenuView != null && mMenuView.isOverflowReserved();
    }

    /**
     * Check whether the overflow menu is currently showing. This may not reflect
     * a pending show operation in progress.
     * -->菜单popup是否正在展示<--
     *
     * @return true if the overflow menu is currently showing
     */
    public boolean isOverflowMenuShowing() {
        return mMenuView != null && mMenuView.isOverflowMenuShowing();
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    public boolean isOverflowMenuShowPending() {
        return mMenuView != null && mMenuView.isOverflowMenuShowPending();
    }

    /**
     * Show the overflow items from the associated menu.
     *
     * @return true if the menu was able to be shown, false otherwise
     */
    public boolean showOverflowMenu() {
        return mMenuView != null && mMenuView.showOverflowMenu();
    }

    /**
     * Hide the overflow items from the associated menu.
     *
     * @return true if the menu was able to be hidden, false otherwise
     */
    public boolean hideOverflowMenu() {
        return mMenuView != null && mMenuView.hideOverflowMenu();
    }

    /** @hide */
    /**
     * 外部Activity/Fragment通过改方法生成Menu
     */
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    public void setMenu(MenuBuilder menu, ActionMenuPresenter outerPresenter) {
        if (menu == null && mMenuView == null) {
            return;
        }

        ensureMenuView();
        final MenuBuilder oldMenu = mMenuView.peekMenu();
        if (oldMenu == menu) {
            return;
        }

        if (oldMenu != null) {
            oldMenu.removeMenuPresenter(mOuterActionMenuPresenter);
            oldMenu.removeMenuPresenter(mExpandedMenuPresenter);
        }

        if (mExpandedMenuPresenter == null) {
            mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
        }

        outerPresenter.setExpandedActionViewsExclusive(true);
        if (menu != null) {
            menu.addMenuPresenter(outerPresenter, mPopupContext);
            menu.addMenuPresenter(mExpandedMenuPresenter, mPopupContext);
        } else {
            outerPresenter.initForMenu(mPopupContext, null);
            mExpandedMenuPresenter.initForMenu(mPopupContext, null);
            outerPresenter.updateMenuView(true);
            mExpandedMenuPresenter.updateMenuView(true);
        }
        mMenuView.setPopupTheme(mPopupTheme);
        mMenuView.setPresenter(outerPresenter);
        mOuterActionMenuPresenter = outerPresenter;
    }

    /**
     * Dismiss all currently showing popup menus, including overflow or submenus.
     */
    public void dismissPopupMenus() {
        if (mMenuView != null) {
            mMenuView.dismissPopupMenus();
        }
    }

    /**
     * @hide
     */
    // 标题是否有省略
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    public boolean isTitleTruncated() {
        if (mTitleTextView == null) {
            return false;
        }

        final Layout titleLayout = mTitleTextView.getLayout();
        if (titleLayout == null) {
            return false;
        }

        final int lineCount = titleLayout.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            if (titleLayout.getEllipsisCount(i) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set a logo drawable.
     *
     * <p>This drawable should generally take the place of title text. The logo cannot be
     * clicked. Apps using a logo should also supply a description using
     * {@link #setLogoDescription(int)}.</p>
     *
     * @param drawable Drawable to use as a logo
     */
    public void setLogo(Drawable drawable) {
        if (drawable != null) {
            ensureLogoView();
            if (!isChildOrHidden(mLogoView)) {
                addSystemView(mLogoView, true);
            }
        } else if (mLogoView != null && isChildOrHidden(mLogoView)) {
            removeView(mLogoView);
            mHiddenViews.remove(mLogoView);
        }
        if (mLogoView != null) {
            mLogoView.setImageDrawable(drawable);
        }
    }

    /**
     * Return the current logo drawable.
     *
     * @return The current logo drawable
     * @see #setLogo(int)
     * @see #setLogo(android.graphics.drawable.Drawable)
     */
    public Drawable getLogo() {
        return mLogoView != null ? mLogoView.getDrawable() : null;
    }

    /**
     * Set a description of the toolbar's logo.
     *
     * <p>This description will be used for accessibility or other similar descriptions
     * of the UI.</p>
     *
     * @param resId String resource id
     */
    public void setLogoDescription(@StringRes int resId) {
        setLogoDescription(getContext().getText(resId));
    }

    /**
     * Set a description of the toolbar's logo.
     *
     * <p>This description will be used for accessibility or other similar descriptions
     * of the UI.</p>
     *
     * @param description Description to set
     */
    public void setLogoDescription(CharSequence description) {
        if (!TextUtils.isEmpty(description)) {
            ensureLogoView();
        }
        if (mLogoView != null) {
            mLogoView.setContentDescription(description);
        }
    }

    /**
     * Return the description of the toolbar's logo.
     *
     * @return A description of the logo
     */
    public CharSequence getLogoDescription() {
        return mLogoView != null ? mLogoView.getContentDescription() : null;
    }

    private void ensureLogoView() {
        if (mLogoView == null) {
            mLogoView = new AppCompatImageView(getContext());
        }
    }

    /**
     * Check whether this Toolbar is currently hosting an expanded action view.
     *
     * <p>An action view may be expanded either directly from the
     * {@link android.view.MenuItem MenuItem} it belongs to or by user action. If the Toolbar
     * has an expanded action view it can be collapsed using the {@link #collapseActionView()}
     * method.</p>
     *
     * @return true if the Toolbar has an expanded action view
     */
    public boolean hasExpandedActionView() {
        return mExpandedMenuPresenter != null &&
                mExpandedMenuPresenter.mCurrentExpandedItem != null;
    }

    /**
     * Collapse a currently expanded action view. If this Toolbar does not have an
     * expanded action view this method has no effect.
     *
     * <p>An action view may be expanded either directly from the
     * {@link android.view.MenuItem MenuItem} it belongs to or by user action.</p>
     *
     * @see #hasExpandedActionView()
     */
    public void collapseActionView() {
        final MenuItemImpl item = mExpandedMenuPresenter == null ? null :
                mExpandedMenuPresenter.mCurrentExpandedItem;
        if (item != null) {
            item.collapseActionView();
        }
    }

    /**
     * Returns the title of this toolbar.
     *
     * @return The current title.
     */
    public CharSequence getTitle() {
        return mTitleText;
    }

    /**
     * Set the title of this toolbar.
     *
     * <p>A title should be used as the anchor for a section of content. It should
     * describe or name the content being viewed.</p>
     *
     * @param resId Resource ID of a string to set as the title
     */
    public void setTitle(@StringRes int resId) {
        setTitle(getContext().getText(resId));
    }

    /**
     * Set the title of this toolbar.
     *
     * <p>A title should be used as the anchor for a section of content. It should
     * describe or name the content being viewed.</p>
     *
     * @param title Title to set
     */
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            if (mTitleTextView == null) {
                final Context context = getContext();
                mTitleTextView = new AppCompatTextView(context);
                mTitleTextView.setSingleLine();
                mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                if (mTitleTextAppearance != 0) {
                    mTitleTextView.setTextAppearance(context, mTitleTextAppearance);
                }
                if (mTitleTextColor != null) {
                    mTitleTextView.setTextColor(mTitleTextColor);
                }
            }
            if (!isChildOrHidden(mTitleTextView)) {
                addSystemView(mTitleTextView, true);
            }
        } else if (mTitleTextView != null && isChildOrHidden(mTitleTextView)) {
            removeView(mTitleTextView);
            mHiddenViews.remove(mTitleTextView);
        }
        if (mTitleTextView != null) {
            mTitleTextView.setText(title);
        }
        mTitleText = title;
    }

    /**
     * Return the subtitle of this toolbar.
     *
     * @return The current subtitle
     */
    public CharSequence getSubtitle() {
        return mSubtitleText;
    }

    /**
     * Set the subtitle of this toolbar.
     *
     * <p>Subtitles should express extended information about the current content.</p>
     *
     * @param resId String resource ID
     */
    public void setSubtitle(@StringRes int resId) {
        setSubtitle(getContext().getText(resId));
    }

    /**
     * Set the subtitle of this toolbar.
     *
     * <p>Subtitles should express extended information about the current content.</p>
     *
     * @param subtitle Subtitle to set
     */
    public void setSubtitle(CharSequence subtitle) {
        if (!TextUtils.isEmpty(subtitle)) {
            if (mSubtitleTextView == null) {
                final Context context = getContext();
                mSubtitleTextView = new AppCompatTextView(context);
                mSubtitleTextView.setSingleLine();
                mSubtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                if (mSubtitleTextAppearance != 0) {
                    mSubtitleTextView.setTextAppearance(context, mSubtitleTextAppearance);
                }
                if (mSubtitleTextColor != null) {
                    mSubtitleTextView.setTextColor(mSubtitleTextColor);
                }
            }
            if (!isChildOrHidden(mSubtitleTextView)) {
                addSystemView(mSubtitleTextView, true);
            }
        } else if (mSubtitleTextView != null && isChildOrHidden(mSubtitleTextView)) {
            removeView(mSubtitleTextView);
            mHiddenViews.remove(mSubtitleTextView);
        }
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setText(subtitle);
        }
        mSubtitleText = subtitle;
    }

    /**
     * Sets the text color, size, style, hint color, and highlight color
     * from the specified TextAppearance resource.
     */
    public void setTitleTextAppearance(Context context, @StyleRes int resId) {
        mTitleTextAppearance = resId;
        if (mTitleTextView != null) {
            mTitleTextView.setTextAppearance(context, resId);
        }
    }

    /**
     * Sets the text color, size, style, hint color, and highlight color
     * from the specified TextAppearance resource.
     */
    public void setSubtitleTextAppearance(Context context, @StyleRes int resId) {
        mSubtitleTextAppearance = resId;
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setTextAppearance(context, resId);
        }
    }

    /**
     * Sets the text color of the title, if present.
     *
     * @param color The new text color in 0xAARRGGBB format
     */
    public void setTitleTextColor(@ColorInt int color) {
        setTitleTextColor(ColorStateList.valueOf(color));
    }

    /**
     * Sets the text color of the title, if present.
     *
     * @param color The new text color
     */
    public void setTitleTextColor(@NonNull ColorStateList color) {
        mTitleTextColor = color;
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(color);
        }
    }

    /**
     * Sets the text color of the subtitle, if present.
     *
     * @param color The new text color in 0xAARRGGBB format
     */
    public void setSubtitleTextColor(@ColorInt int color) {
        setSubtitleTextColor(ColorStateList.valueOf(color));
    }

    /**
     * Sets the text color of the subtitle, if present.
     *
     * @param color The new text color
     */
    public void setSubtitleTextColor(@NonNull ColorStateList color) {
        mSubtitleTextColor = color;
        if (mSubtitleTextView != null) {
            mSubtitleTextView.setTextColor(color);
        }
    }

    /**
     * Retrieve the currently configured content description for the navigation button view.
     * This will be used to describe the navigation action to users through mechanisms such
     * as screen readers or tooltips.
     *
     * @return The navigation button's content description
     * <p>
     * {@link androidx.appcompat.R.attr#navigationContentDescription}
     */
    @Nullable
    public CharSequence getNavigationContentDescription() {
        return mNavButtonView != null ? mNavButtonView.getContentDescription() : null;
    }

    /**
     * Set a content description for the navigation button if one is present. The content
     * description will be read via screen readers or other accessibility systems to explain
     * the action of the navigation button.
     *
     * @param resId Resource ID of a content description string to set, or 0 to
     *              clear the description
     *              <p>
     *              {@link androidx.appcompat.R.attr#navigationContentDescription}
     */
    public void setNavigationContentDescription(@StringRes int resId) {
        setNavigationContentDescription(resId != 0 ? getContext().getText(resId) : null);
    }

    /**
     * Set a content description for the navigation button if one is present. The content
     * description will be read via screen readers or other accessibility systems to explain
     * the action of the navigation button.
     *
     * @param description Content description to set, or <code>null</code> to
     *                    clear the content description
     *                    <p>
     *                    {@link androidx.appcompat.R.attr#navigationContentDescription}
     */
    public void setNavigationContentDescription(@Nullable CharSequence description) {
        if (!TextUtils.isEmpty(description)) {
            ensureNavButtonView();
        }
        if (mNavButtonView != null) {
            mNavButtonView.setContentDescription(description);
        }
    }

    /**
     * Set the icon to use for the toolbar's navigation button.
     *
     * <p>The navigation button appears at the start of the toolbar if present. Setting an icon
     * will make the navigation button visible.</p>
     *
     * <p>If you use a navigation icon you should also set a description for its action using
     * {@link #setNavigationContentDescription(int)}. This is used for accessibility and
     * tooltips.</p>
     *
     * @param resId Resource ID of a drawable to set
     *              <p>
     *              {@link androidx.appcompat.R.attr#navigationIcon}
     */
    public void setNavigationIcon(@DrawableRes int resId) {
        setNavigationIcon(AppCompatResources.getDrawable(getContext(), resId));
    }

    /**
     * Set the icon to use for the toolbar's navigation button.
     *
     * <p>The navigation button appears at the start of the toolbar if present. Setting an icon
     * will make the navigation button visible.</p>
     *
     * <p>If you use a navigation icon you should also set a description for its action using
     * {@link #setNavigationContentDescription(int)}. This is used for accessibility and
     * tooltips.</p>
     *
     * @param icon Drawable to set, may be null to clear the icon
     *             <p>
     *             {@link androidx.appcompat.R.attr#navigationIcon}
     */
    public void setNavigationIcon(@Nullable Drawable icon) {
        if (icon != null) {
            ensureNavButtonView();
            if (!isChildOrHidden(mNavButtonView)) {
                addSystemView(mNavButtonView, true);
            }
        } else if (mNavButtonView != null && isChildOrHidden(mNavButtonView)) {
            removeView(mNavButtonView);
            mHiddenViews.remove(mNavButtonView);
        }
        if (mNavButtonView != null) {
            mNavButtonView.setImageDrawable(icon);
        }
    }

    /**
     * Return the current drawable used as the navigation icon.
     *
     * @return The navigation icon drawable
     * <p>
     * {@link androidx.appcompat.R.attr#navigationIcon}
     */
    @Nullable
    public Drawable getNavigationIcon() {
        return mNavButtonView != null ? mNavButtonView.getDrawable() : null;
    }

    /**
     * Set a listener to respond to navigation events.
     *
     * <p>This listener will be called whenever the user clicks the navigation button
     * at the start of the toolbar. An icon must be set for the navigation button to appear.</p>
     *
     * @param listener Listener to set
     * @see #setNavigationIcon(android.graphics.drawable.Drawable)
     */
    public void setNavigationOnClickListener(OnClickListener listener) {
        ensureNavButtonView();
        mNavButtonView.setOnClickListener(listener);
    }

    /**
     * Retrieve the currently configured content description for the collapse button view.
     * This will be used to describe the collapse action to users through mechanisms such
     * as screen readers or tooltips.
     *
     * @return The collapse button's content description
     * <p>
     * {@link androidx.appcompat.R.attr#collapseContentDescription}
     */
    @Nullable
    public CharSequence getCollapseContentDescription() {
        return mCollapseButtonView != null ? mCollapseButtonView.getContentDescription() : null;
    }

    /**
     * Set a content description for the collapse button if one is present. The content description
     * will be read via screen readers or other accessibility systems to explain the action of the
     * collapse button.
     *
     * @param resId Resource ID of a content description string to set, or 0 to
     *              clear the description
     *              <p>
     *              {@link androidx.appcompat.R.attr#collapseContentDescription}
     */
    public void setCollapseContentDescription(@StringRes int resId) {
        setCollapseContentDescription(resId != 0 ? getContext().getText(resId) : null);
    }

    /**
     * Set a content description for the collapse button if one is present. The content description
     * will be read via screen readers or other accessibility systems to explain the action of the
     * navigation button.
     *
     * @param description Content description to set, or <code>null</code> to
     *                    clear the content description
     *                    <p>
     *                    {@link androidx.appcompat.R.attr#collapseContentDescription}
     */
    public void setCollapseContentDescription(@Nullable CharSequence description) {
        if (!TextUtils.isEmpty(description)) {
            ensureCollapseButtonView();
        }
        if (mCollapseButtonView != null) {
            mCollapseButtonView.setContentDescription(description);
        }
    }

    /**
     * Return the current drawable used as the collapse icon.
     *
     * @return The collapse icon drawable
     * <p>
     * {@link androidx.appcompat.R.attr#collapseIcon}
     */
    @Nullable
    public Drawable getCollapseIcon() {
        return mCollapseButtonView != null ? mCollapseButtonView.getDrawable() : null;
    }

    /**
     * Set the icon to use for the toolbar's collapse button.
     *
     * <p>The collapse button appears at the start of the toolbar when an action view is present
     * .</p>
     *
     * @param resId Resource ID of a drawable to set
     *              <p>
     *              {@link androidx.appcompat.R.attr#collapseIcon}
     */
    public void setCollapseIcon(@DrawableRes int resId) {
        setCollapseIcon(AppCompatResources.getDrawable(getContext(), resId));
    }

    /**
     * Set the icon to use for the toolbar's collapse button.
     *
     * <p>The collapse button appears at the start of the toolbar when an action view is present
     * .</p>
     *
     * @param icon Drawable to set, may be null to use the default icon
     *             <p>
     *             {@link androidx.appcompat.R.attr#collapseIcon}
     */
    public void setCollapseIcon(@Nullable Drawable icon) {
        if (icon != null) {
            ensureCollapseButtonView();
            mCollapseButtonView.setImageDrawable(icon);
        } else if (mCollapseButtonView != null) {
            mCollapseButtonView.setImageDrawable(mCollapseIcon);
        }
    }

    /**
     * Return the Menu shown in the toolbar.
     *
     * <p>Applications that wish to populate the toolbar's menu can do so from here. To use
     * an XML menu resource, use {@link #inflateMenu(int)}.</p>
     *
     * @return The toolbar's Menu
     * {@link androidx.appcompat.R.attr#menu}
     */
    public Menu getMenu() {
        ensureMenu();
        return mMenuView.getMenu();
    }

    /**
     * Set the icon to use for the overflow button.
     *
     * @param icon Drawable to set, may be null to clear the icon
     */
    public void setOverflowIcon(@Nullable Drawable icon) {
        ensureMenu();
        mMenuView.setOverflowIcon(icon);
    }

    /**
     * Return the current drawable used as the overflow icon.
     *
     * @return The overflow icon drawable
     */
    @Nullable
    public Drawable getOverflowIcon() {
        ensureMenu();
        return mMenuView.getOverflowIcon();
    }

    private void ensureMenu() {
        ensureMenuView();
        if (mMenuView.peekMenu() == null) {
            // Initialize a new menu for the first time.
            final MenuBuilder menu = (MenuBuilder) mMenuView.getMenu();
            if (mExpandedMenuPresenter == null) {
                mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
            }
            mMenuView.setExpandedActionViewsExclusive(true);
            menu.addMenuPresenter(mExpandedMenuPresenter, mPopupContext);
        }
    }

    private void ensureMenuView() {
        if (mMenuView == null) {
            mMenuView = new ActionMenuView(getContext());
            mMenuView.setPopupTheme(mPopupTheme);
            mMenuView.setOnMenuItemClickListener(mMenuViewItemClickListener);
            mMenuView.setMenuCallbacks(mActionMenuPresenterCallback, mMenuBuilderCallback);
            final LayoutParams lp = generateDefaultLayoutParams();
            lp.gravity = GravityCompat.END | (mButtonGravity & Gravity.VERTICAL_GRAVITY_MASK);
            mMenuView.setLayoutParams(lp);
            addSystemView(mMenuView, false);
        }
    }

    private MenuInflater getMenuInflater() {
        return new SupportMenuInflater(getContext());
    }

    /**
     * Inflate a menu resource into this toolbar.
     *
     * <p>Inflate an XML menu resource into this toolbar. Existing items in the menu will not
     * be modified or removed.</p>
     *
     * @param resId ID of a menu resource to inflate
     *              {@link androidx.appcompat.R.attr#menu}
     */
    public void inflateMenu(@MenuRes int resId) {
        getMenuInflater().inflate(resId, getMenu());
    }

    /**
     * Set a listener to respond to menu item click events.
     *
     * <p>This listener will be invoked whenever a user selects a menu item from
     * the action buttons presented at the end of the toolbar or the associated overflow.</p>
     *
     * @param listener Listener to set
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        mOnMenuItemClickListener = listener;
    }

    /**
     * Sets the content insets for this toolbar relative to layout direction.
     *
     * <p>The content inset affects the valid area for Toolbar content other than
     * the navigation button and menu. Insets define the minimum margin for these components
     * and can be used to effectively align Toolbar content along well-known gridlines.</p>
     *
     * @param contentInsetStart Content inset for the toolbar starting edge
     * @param contentInsetEnd   Content inset for the toolbar ending edge
     * @see #setContentInsetsAbsolute(int, int)
     * @see #getContentInsetStart()
     * @see #getContentInsetEnd()
     * @see #getContentInsetLeft()
     * @see #getContentInsetRight()
     * {@link androidx.appcompat.R.attr#contentInsetEnd}
     * {@link androidx.appcompat.R.attr#contentInsetStart}
     */
    public void setContentInsetsRelative(int contentInsetStart, int contentInsetEnd) {
        ensureContentInsets();
        mContentInsets.setRelative(contentInsetStart, contentInsetEnd);
    }

    /**
     * Gets the starting content inset for this toolbar.
     *
     * <p>The content inset affects the valid area for Toolbar content other than
     * the navigation button and menu. Insets define the minimum margin for these components
     * and can be used to effectively align Toolbar content along well-known gridlines.</p>
     *
     * @return The starting content inset for this toolbar
     * @see #setContentInsetsRelative(int, int)
     * @see #setContentInsetsAbsolute(int, int)
     * @see #getContentInsetEnd()
     * @see #getContentInsetLeft()
     * @see #getContentInsetRight()
     * {@link androidx.appcompat.R.attr#contentInsetStart}
     */
    public int getContentInsetStart() {
        return mContentInsets != null ? mContentInsets.getStart() : 0;
    }

    /**
     * Gets the ending content inset for this toolbar.
     *
     * <p>The content inset affects the valid area for Toolbar content other than
     * the navigation button and menu. Insets define the minimum margin for these components
     * and can be used to effectively align Toolbar content along well-known gridlines.</p>
     *
     * @return The ending content inset for this toolbar
     * @see #setContentInsetsRelative(int, int)
     * @see #setContentInsetsAbsolute(int, int)
     * @see #getContentInsetStart()
     * @see #getContentInsetLeft()
     * @see #getContentInsetRight()
     * {@link androidx.appcompat.R.attr#contentInsetEnd}
     */
    public int getContentInsetEnd() {
        return mContentInsets != null ? mContentInsets.getEnd() : 0;
    }

    /**
     * Sets the content insets for this toolbar.
     *
     * <p>The content inset affects the valid area for Toolbar content other than
     * the navigation button and menu. Insets define the minimum margin for these components
     * and can be used to effectively align Toolbar content along well-known gridlines.</p>
     *
     * @param contentInsetLeft  Content inset for the toolbar's left edge
     * @param contentInsetRight Content inset for the toolbar's right edge
     * @see #setContentInsetsAbsolute(int, int)
     * @see #getContentInsetStart()
     * @see #getContentInsetEnd()
     * @see #getContentInsetLeft()
     * @see #getContentInsetRight()
     * {@link androidx.appcompat.R.attr#contentInsetLeft}
     * {@link androidx.appcompat.R.attr#contentInsetRight}
     */
    public void setContentInsetsAbsolute(int contentInsetLeft, int contentInsetRight) {
        ensureContentInsets();
        mContentInsets.setAbsolute(contentInsetLeft, contentInsetRight);
    }

    /**
     * Gets the left content inset for this toolbar.
     *
     * <p>The content inset affects the valid area for Toolbar content other than
     * the navigation button and menu. Insets define the minimum margin for these components
     * and can be used to effectively align Toolbar content along well-known gridlines.</p>
     *
     * @return The left content inset for this toolbar
     * @see #setContentInsetsRelative(int, int)
     * @see #setContentInsetsAbsolute(int, int)
     * @see #getContentInsetStart()
     * @see #getContentInsetEnd()
     * @see #getContentInsetRight()
     * {@link androidx.appcompat.R.attr#contentInsetLeft}
     */
    public int getContentInsetLeft() {
        return mContentInsets != null ? mContentInsets.getLeft() : 0;
    }

    /**
     * Gets the right content inset for this toolbar.
     *
     * <p>The content inset affects the valid area for Toolbar content other than
     * the navigation button and menu. Insets define the minimum margin for these components
     * and can be used to effectively align Toolbar content along well-known gridlines.</p>
     *
     * @return The right content inset for this toolbar
     * @see #setContentInsetsRelative(int, int)
     * @see #setContentInsetsAbsolute(int, int)
     * @see #getContentInsetStart()
     * @see #getContentInsetEnd()
     * @see #getContentInsetLeft()
     * {@link androidx.appcompat.R.attr#contentInsetRight}
     */
    public int getContentInsetRight() {
        return mContentInsets != null ? mContentInsets.getRight() : 0;
    }

    /**
     * Gets the start content inset to use when a navigation button is present.
     *
     * <p>Different content insets are often called for when additional buttons are present
     * in the toolbar, as well as at different toolbar sizes. The larger value of
     * {@link #getContentInsetStart()} and this value will be used during layout.</p>
     *
     * @return the start content inset used when a navigation icon has been set in pixels
     * @see #setContentInsetStartWithNavigation(int)
     * {@link androidx.appcompat.R.attr#contentInsetStartWithNavigation}
     */
    public int getContentInsetStartWithNavigation() {
        return mContentInsetStartWithNavigation != RtlSpacingHelper.UNDEFINED
                ? mContentInsetStartWithNavigation
                : getContentInsetStart();
    }

    /**
     * Sets the start content inset to use when a navigation button is present.
     *
     * <p>Different content insets are often called for when additional buttons are present
     * in the toolbar, as well as at different toolbar sizes. The larger value of
     * {@link #getContentInsetStart()} and this value will be used during layout.</p>
     *
     * @param insetStartWithNavigation the inset to use when a navigation icon has been set
     *                                 in pixels
     * @see #getContentInsetStartWithNavigation()
     * {@link androidx.appcompat.R.attr#contentInsetStartWithNavigation}
     */
    public void setContentInsetStartWithNavigation(int insetStartWithNavigation) {
        if (insetStartWithNavigation < 0) {
            insetStartWithNavigation = RtlSpacingHelper.UNDEFINED;
        }
        if (insetStartWithNavigation != mContentInsetStartWithNavigation) {
            mContentInsetStartWithNavigation = insetStartWithNavigation;
            if (getNavigationIcon() != null) {
                requestLayout();
            }
        }
    }

    /**
     * Gets the end content inset to use when action buttons are present.
     *
     * <p>Different content insets are often called for when additional buttons are present
     * in the toolbar, as well as at different toolbar sizes. The larger value of
     * {@link #getContentInsetEnd()} and this value will be used during layout.</p>
     *
     * @return the end content inset used when a menu has been set in pixels
     * @see #setContentInsetEndWithActions(int)
     * {@link androidx.appcompat.R.attr#contentInsetEndWithActions}
     */
    public int getContentInsetEndWithActions() {
        return mContentInsetEndWithActions != RtlSpacingHelper.UNDEFINED
                ? mContentInsetEndWithActions
                : getContentInsetEnd();
    }

    /**
     * Sets the start content inset to use when action buttons are present.
     *
     * <p>Different content insets are often called for when additional buttons are present
     * in the toolbar, as well as at different toolbar sizes. The larger value of
     * {@link #getContentInsetEnd()} and this value will be used during layout.</p>
     *
     * @param insetEndWithActions the inset to use when a menu has been set in pixels
     * @see #getContentInsetEndWithActions()
     * {@link androidx.appcompat.R.attr#contentInsetEndWithActions}
     */
    public void setContentInsetEndWithActions(int insetEndWithActions) {
        if (insetEndWithActions < 0) {
            insetEndWithActions = RtlSpacingHelper.UNDEFINED;
        }
        if (insetEndWithActions != mContentInsetEndWithActions) {
            mContentInsetEndWithActions = insetEndWithActions;
            if (getNavigationIcon() != null) {
                requestLayout();
            }
        }
    }

    /**
     * Gets the content inset that will be used on the starting side of the bar in the current
     * toolbar configuration.
     * <p>
     * 用两个值getContentInsetStart()和mContentInsetStartWithNavigation控制content View到Toolbar边距的距离
     * 配置有Nav和没有Nav时到Toolbar边距的距离
     *
     * @return the current content inset start in pixels
     * @see #getContentInsetStartWithNavigation()
     */
    public int getCurrentContentInsetStart() {
        return getNavigationIcon() != null
                ? Math.max(getContentInsetStart(), Math.max(mContentInsetStartWithNavigation, 0))
                : getContentInsetStart();
    }

    /**
     * Gets the content inset that will be used on the ending side of the bar in the current
     * toolbar configuration.
     *
     * @return the current content inset end in pixels
     * @see #getContentInsetEndWithActions()
     */
    public int getCurrentContentInsetEnd() {
        boolean hasActions = false;
        if (mMenuView != null) {
            final MenuBuilder mb = mMenuView.peekMenu();
            hasActions = mb != null && mb.hasVisibleItems();
        }
        return hasActions
                ? Math.max(getContentInsetEnd(), Math.max(mContentInsetEndWithActions, 0))
                : getContentInsetEnd();
    }

    /**
     * Gets the content inset that will be used on the left side of the bar in the current
     * toolbar configuration.
     *
     * @return the current content inset left in pixels
     * @see #getContentInsetStartWithNavigation()
     * @see #getContentInsetEndWithActions()
     */
    public int getCurrentContentInsetLeft() {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
                ? getCurrentContentInsetEnd()
                : getCurrentContentInsetStart();
    }

    /**
     * Gets the content inset that will be used on the right side of the bar in the current
     * toolbar configuration.
     *
     * @return the current content inset right in pixels
     * @see #getContentInsetStartWithNavigation()
     * @see #getContentInsetEndWithActions()
     */
    public int getCurrentContentInsetRight() {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
                ? getCurrentContentInsetStart()
                : getCurrentContentInsetEnd();
    }

    private void ensureNavButtonView() {
        if (mNavButtonView == null) {
            mNavButtonView = new AppCompatImageButton(getContext(), null,
                    R.attr.toolbarNavigationButtonStyle);
            final LayoutParams lp = generateDefaultLayoutParams();
            lp.gravity = GravityCompat.START | (mButtonGravity & Gravity.VERTICAL_GRAVITY_MASK);
            mNavButtonView.setLayoutParams(lp);
        }
    }

    void ensureCollapseButtonView() {
        if (mCollapseButtonView == null) {
            mCollapseButtonView = new AppCompatImageButton(getContext(), null,
                    R.attr.toolbarNavigationButtonStyle);
            mCollapseButtonView.setImageDrawable(mCollapseIcon);
            mCollapseButtonView.setContentDescription(mCollapseDescription);
            final LayoutParams lp = generateDefaultLayoutParams();
            lp.gravity = GravityCompat.START | (mButtonGravity & Gravity.VERTICAL_GRAVITY_MASK);
            // 在显示mExpandedActionView时不hidemCollapseButtonView
            lp.mViewType = LayoutParams.EXPANDED;
            mCollapseButtonView.setLayoutParams(lp);
            mCollapseButtonView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    collapseActionView();
                }
            });
        }
    }

    /**
     * Logo/Nav/Title/SubTitle/Menu属于LayoutParams.SYSTEM
     * mCollapseButtonView/mExpandedActionView属于lp.mViewType = LayoutParams.EXPANDED,标志为EXPANDED类型,不走这个方法
     *
     * @param allowHide 如果此时正展示mExpandedActionView,而该View属于需要隐藏的View(allowHide为true),则仅添加到mHiddenViews
     *                  当mExpandedActionView collapse的时候,mHiddenViews将重新添加到Toolbar
     *                  如果allowHide为false,即mExpandedActionView显示与否,该View都会显示,目前只有Menu如此
     */
    private void addSystemView(View v, boolean allowHide) {
        final ViewGroup.LayoutParams vlp = v.getLayoutParams();
        final LayoutParams lp;
        if (vlp == null) {
            lp = generateDefaultLayoutParams();
        } else if (!checkLayoutParams(vlp)) {
            lp = generateLayoutParams(vlp);
        } else {
            lp = (LayoutParams) vlp;
        }
        lp.mViewType = LayoutParams.SYSTEM;

        if (allowHide && mExpandedActionView != null) {
            v.setLayoutParams(lp);
            mHiddenViews.add(v);
        } else {
            addView(v, lp);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());

        // 如果展示mExpandedActionView正在展示,记录该id
        if (mExpandedMenuPresenter != null && mExpandedMenuPresenter.mCurrentExpandedItem != null) {
            state.expandedMenuItemId = mExpandedMenuPresenter.mCurrentExpandedItem.getItemId();
        }

        // 记录Menu的popup是否正在展示
        state.isOverflowOpen = isOverflowMenuShowing();
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        final Menu menu = mMenuView != null ? mMenuView.peekMenu() : null;
        if (ss.expandedMenuItemId != 0 && mExpandedMenuPresenter != null && menu != null) {
            final MenuItem item = menu.findItem(ss.expandedMenuItemId);
            if (item != null) {
                item.expandActionView();
            }
        }

        if (ss.isOverflowOpen) {
            postShowOverflowMenu();
        }
    }

    private void postShowOverflowMenu() {
        removeCallbacks(mShowOverflowMenuRunnable);
        post(mShowOverflowMenuRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mShowOverflowMenuRunnable);
    }

    /**
     * Toolbar默认消费所有的点击事件
     * 如果Toolbar消费事件,则一直调用super.onTouchEvent(ev)
     * 如果不消费事件,super.onTouchEvent(ev)->false,那么后续不会调用super.onTouchEvent(ev)
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Toolbars always eat touch events, but should still respect the touch event dispatch
        // contract. If the normal View implementation doesn't want the events, we'll just silently
        // eat the rest of the gesture without reporting the events to the default implementation
        // since that's what it expects.

        final int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mEatingTouch = false;
        }

        if (!mEatingTouch) {
            final boolean handled = super.onTouchEvent(ev);
            if (action == MotionEvent.ACTION_DOWN && !handled) {
                mEatingTouch = true;
            }
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mEatingTouch = false;
        }

        return true;
    }

    /**
     * 如果super.onHoverEvent(ev)消费事件返回true,则一直调用super.onHoverEvent(ev),否则只调用一次
     */
    @Override
    public boolean onHoverEvent(MotionEvent ev) {
        // Same deal as onTouchEvent() above. Eat all hover events, but still
        // respect the touch event dispatch contract.

        final int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_HOVER_ENTER) {
            mEatingHover = false;
        }

        if (!mEatingHover) {
            final boolean handled = super.onHoverEvent(ev);
            if (action == MotionEvent.ACTION_HOVER_ENTER && !handled) {
                mEatingHover = true;
            }
        }

        if (action == MotionEvent.ACTION_HOVER_EXIT || action == MotionEvent.ACTION_CANCEL) {
            mEatingHover = false;
        }

        return true;
    }

    /**
     * @param child            子View
     * @param parentWidthSpec  Toolbar的WidthSpec
     * @param widthUsed        View已使用的Width
     * @param parentHeightSpec Toolbar的HeightSpec
     * @param heightUsed       View已使用的Height
     * @param heightConstraint 高度限制
     */
    private void measureChildConstrained(View child, int parentWidthSpec, int widthUsed,
                                         int parentHeightSpec, int heightUsed, int heightConstraint) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        /**
         * getChildMeasureSpec(int spec, int padding, int childDimension)
         * 该方法是用来获取子类的MeasureSpec值
         * spec:父View的HeightMeasureSpec或者WidthMeasureSpec
         * padding:已经使用的宽度或者高度
         *      如果是width的话：父View左右Padding+子View左右Margin+widthUsed
         *      如果是Height的话：父View上下Padding+子View上下Margin+heightUsed
         *      widthUsed/heightUsed是当前LinearLayout中已经使用了的宽度/高度
         * childDimension:
         *      如果是Width的话：传入lp.width
         *      如果是Height的话：传入lp.height
         * 参数一和参数二可以计算出剩余的宽度/高度
         * 如果父类为MeasureSpec.EXACTLY
         *      子类有指定宽度:MeasureSpec.EXACTLY + 子类设定宽度
         *      子类宽度为MATCH_PARENT:MeasureSpec.EXACTLY + 父类可用宽度
         *      子类宽度为 WRAP_CONTENT:MeasureSpec.AT_MOST + 父类可用宽度
         * 如果父类为MeasureSpec.AT_MOST
         *      子类有指定宽度:MeasureSpec.EXACTLY + 子类设定宽度
         *      子类宽度为MATCH_PARENT:MeasureSpec.AT_MOST + 父类可用宽度
         *      子类宽度为 WRAP_CONTENT:MeasureSpec.AT_MOST + 父类可用宽度
         * 如果父类为MeasureSpec.UNSPECIFIED
         *      子类有指定宽度:MeasureSpec.EXACTLY + 子类设定宽度
         *      子类宽度为MATCH_PARENT:MeasureSpec.UNSPECIFIED + 父类可用宽度(或者是0表示UNSPECIFIED,ConstraintLayout就是这么表示的)
         *      子类宽度为 WRAP_CONTENT:MeasureSpec.UNSPECIFIED + 父类可用宽度(或者是0表示UNSPECIFIED)
         * 对于子类:
         *      指明大小:MeasureSpec.EXACTLY时,measure返回EXACTLY + 子类设定宽度
         *      MATCH_PARENT:EXACTLY/AT_MOST/UNSPECIFIED + 父类可用宽度,MeasureSpec跟随父类
         *      WRAP_CONTENT:AT_MOST/UNSPECIFIED + 父类可用宽度,不会是EXACTLY
         *
         *
         */
        int childWidthSpec = getChildMeasureSpec(parentWidthSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width);
        int childHeightSpec = getChildMeasureSpec(parentHeightSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin
                        + heightUsed, lp.height);

//     限制子类的宽度,不能超过heightConstraint
        final int childHeightMode = MeasureSpec.getMode(childHeightSpec);
        if (childHeightMode != MeasureSpec.EXACTLY && heightConstraint >= 0) {
            final int size = childHeightMode != MeasureSpec.UNSPECIFIED ?
                    Math.min(MeasureSpec.getSize(childHeightSpec), heightConstraint) :
                    heightConstraint;
            childHeightSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * Returns the width + uncollapsed margins
     *
     * @param child                   子View
     * @param parentWidthMeasureSpec  父类宽度的MeasureSpec
     * @param widthUsed               已经被使用了的width
     * @param parentHeightMeasureSpec 父类高度的MeasureSpec
     * @param heightUsed              已经被使用了的height
     * @param collapsingMargins       content View距离左侧Nav和右侧Menu的margins
     * @return 返回子类所占的宽度 = getMeasuredWidth + 横向margin和
     */
    private int measureChildCollapseMargins(View child,
                                            int parentWidthMeasureSpec, int widthUsed,
                                            int parentHeightMeasureSpec, int heightUsed, int[] collapsingMargins) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        // margin diff:子View与左侧Nav/与右侧menu的margin,设计规则为不叠加collapsingMargins和自身的margin,而是max(collapsingMargins,lp.leftMargin)
        // 以下面代码说明:
        //      如果leftDiff > collapsingMargins ,leftMargin为leftDiff,实际上在onLayout时,子View与Nav的距离为leftDiff + collapsingMargins = lp.leftMargin
        //      如果leftDiff < collapsingMargins,在onLayout时,子View与Nav的距离为collapsingMargins
        final int leftDiff = lp.leftMargin - collapsingMargins[0];
        final int rightDiff = lp.rightMargin - collapsingMargins[1];
        final int leftMargin = Math.max(0, leftDiff);
        final int rightMargin = Math.max(0, rightDiff);
        // 子View横向margin和
        final int hMargins = leftMargin + rightMargin;
        // 当leftDiff < collapsingMargins时,修改collapsingMargins,消耗collapsingMargins的值直到变成0
        // 当collapsingMargins变成0后,后面的marginLeft就能正常显示
        // 当leftDiff > collapsingMargins时,collapsingMargins变成0,后面的View的marginLeft能正常显示
        collapsingMargins[0] = Math.max(0, -leftDiff);
        collapsingMargins[1] = Math.max(0, -rightDiff);

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + hMargins + widthUsed, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin
                        + heightUsed, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        // 返回子类所占的宽度
        return child.getMeasuredWidth() + hMargins;
    }

    /**
     * Returns true if the Toolbar is collapsible and has no child views with a measured size > 0.
     */
    private boolean shouldCollapse() {
        if (!mCollapsible) return false;

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (shouldLayout(child) && child.getMeasuredWidth() > 0 &&
                    child.getMeasuredHeight() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 子View占用的width总和
        int width = 0;
        // 取所有View最大值
        int height = 0;
        int childState = 0;

        /**
         * 由mNavButtonView/mCollapseButtonView决定Left,mMenuView决定Right
         * 影响mExpandedActionView/mLogoView/CUSTOM View的onMeasure
         *      当显示mExpandedActionView时,该值表示mExpandedActionView距离mCollapseButtonView的宽度
         *      当不显示mExpandedActionView时,该值表示content View距离mNavButtonView的宽度
         * collapsingMargins = marginLeft marginRight
         * 如果是Left To Right,marginLeft = marginStartIndex,marginRight = marginEndIndex
         * 如果是Right To Left,marginLeft = marginEndIndex,marginRight = marginStartIndex
         */
        final int[] collapsingMargins = mTempMargins;
        final int marginStartIndex;
        final int marginEndIndex;
        if (ViewUtils.isLayoutRtl(this)) {
            marginStartIndex = 1;
            marginEndIndex = 0;
        } else {
            marginStartIndex = 0;
            marginEndIndex = 1;
        }

        // System views measure first.

        // navWidth
        // 显示mCollapseButtonView时,则为mCollapseButtonView,此时是mNavButtonView处于hide状态
        // 如果不展示mCollapseButtonView,并存在mNavButtonView,则通过mNavButtonView来计算
        int navWidth = 0;
        if (shouldLayout(mNavButtonView)) {
            // 调用Nav的measure()方法,让Nav计算自身的需要的宽高
            measureChildConstrained(mNavButtonView, widthMeasureSpec, width, heightMeasureSpec, 0,
                    mMaxButtonHeight);
            // 获取Nav计算结果,加上Nav的margin值
            navWidth = mNavButtonView.getMeasuredWidth() + getHorizontalMargins(mNavButtonView);
            height = Math.max(height, mNavButtonView.getMeasuredHeight() +
                    getVerticalMargins(mNavButtonView));
            childState = View.combineMeasuredStates(childState,
                    mNavButtonView.getMeasuredState());
        }

        if (shouldLayout(mCollapseButtonView)) {
            measureChildConstrained(mCollapseButtonView, widthMeasureSpec, width,
                    heightMeasureSpec, 0, mMaxButtonHeight);
            navWidth = mCollapseButtonView.getMeasuredWidth() +
                    getHorizontalMargins(mCollapseButtonView);
            height = Math.max(height, mCollapseButtonView.getMeasuredHeight() +
                    getVerticalMargins(mCollapseButtonView));
            childState = View.combineMeasuredStates(childState,
                    mCollapseButtonView.getMeasuredState());
        }

        //存在Navigation,取getContentInsetStart(),mContentInsetStartWithNavigation的最大值
        //不存在Navigation,取getContentInsetStart()
        final int contentInsetStart = getCurrentContentInsetStart();

        /**
         * contentInsetStart: content view(logo/Title/subTitle)距离Toolbar左侧的宽度
         * navWidth: mCollapseButtonView/mNavButtonView 占用的宽度
         * 1,Nav所占宽度
         * 2,mCollapseButtonView所占宽度
         * 3,该值在两个状态是不一致的,因为mCollapseButtonView与Nav的宽度可能是不一致的
         * 4,取contentInsetStart, navWidth的最大值,不取叠加值,Toolbar自定规则
         * 5,所以决定content View距离Toolbar左侧的距离为contentInsetStart, navWidth,在编码时要根据这个来写
         */
        width += Math.max(contentInsetStart, navWidth);

        // 1,content View与Nav的距离
        // 2,mExpandedActionView与mCollapseButtonView的距离
        // 3,该值在两个状态是不一致的,因为mCollapseButtonView与Nav的宽度可能是不一致的
        collapsingMargins[marginStartIndex] = Math.max(0, contentInsetStart - navWidth);

        int menuWidth = 0;
        if (shouldLayout(mMenuView)) {
            measureChildConstrained(mMenuView, widthMeasureSpec, width, heightMeasureSpec, 0,
                    mMaxButtonHeight);
            menuWidth = mMenuView.getMeasuredWidth() + getHorizontalMargins(mMenuView);
            height = Math.max(height, mMenuView.getMeasuredHeight() +
                    getVerticalMargins(mMenuView));
            childState = View.combineMeasuredStates(childState,
                    mMenuView.getMeasuredState());
        }

        final int contentInsetEnd = getCurrentContentInsetEnd();
        // end占用位置以contentInsetEnd, menuWidth 较大值为主
        width += Math.max(contentInsetEnd, menuWidth);
        // content View距离Menu的距离
        collapsingMargins[marginEndIndex] = Math.max(0, contentInsetEnd - menuWidth);

        // 如果展示mExpandedActionView
        if (shouldLayout(mExpandedActionView)) {
            width += measureChildCollapseMargins(mExpandedActionView, widthMeasureSpec, width,
                    heightMeasureSpec, 0, collapsingMargins);
            height = Math.max(height, mExpandedActionView.getMeasuredHeight() +
                    getVerticalMargins(mExpandedActionView));
            childState = View.combineMeasuredStates(childState,
                    mExpandedActionView.getMeasuredState());
        }

        // 如果不展示mExpandedActionView,需要考虑Logo/xml子view/title/subTitle
        if (shouldLayout(mLogoView)) {
            width += measureChildCollapseMargins(mLogoView, widthMeasureSpec, width,
                    heightMeasureSpec, 0, collapsingMargins);
            height = Math.max(height, mLogoView.getMeasuredHeight() +
                    getVerticalMargins(mLogoView));
            childState = View.combineMeasuredStates(childState,
                    mLogoView.getMeasuredState());
        }

        /**
         * Logo/Nav/Title/SubTitle/Menu属于LayoutParams.SYSTEM
         * mCollapseButtonView/mExpandedActionView属于LayoutParams.EXPANDED
         * 在xml中添加的View属于LayoutParams.CUSTOM
         * CUSTOM View比Title/SubTitle优先级更高,所以Title/SubTitle可能会不展示
         *
         */
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.mViewType != LayoutParams.CUSTOM || !shouldLayout(child)) {
                // We already got all system views above. Skip them and GONE views.
                continue;
            }

            width += measureChildCollapseMargins(child, widthMeasureSpec, width,
                    heightMeasureSpec, 0, collapsingMargins);
            height = Math.max(height, child.getMeasuredHeight() + getVerticalMargins(child));
            childState = View.combineMeasuredStates(childState, child.getMeasuredState());
        }

        // 宽度由title/subTitle的max值决定
        // 宽度由两者之和决定
        int titleWidth = 0;
        int titleHeight = 0;
        final int titleVertMargins = mTitleMarginTop + mTitleMarginBottom;
        final int titleHorizMargins = mTitleMarginStart + mTitleMarginEnd;
        if (shouldLayout(mTitleTextView)) {
            titleWidth = measureChildCollapseMargins(mTitleTextView, widthMeasureSpec,
                    width + titleHorizMargins, heightMeasureSpec, titleVertMargins,
                    collapsingMargins);
            titleWidth = mTitleTextView.getMeasuredWidth() + getHorizontalMargins(mTitleTextView);
            titleHeight = mTitleTextView.getMeasuredHeight() + getVerticalMargins(mTitleTextView);
            childState = View.combineMeasuredStates(childState, mTitleTextView.getMeasuredState());
        }
        if (shouldLayout(mSubtitleTextView)) {
            titleWidth = Math.max(titleWidth, measureChildCollapseMargins(mSubtitleTextView,
                    widthMeasureSpec, width + titleHorizMargins,
                    heightMeasureSpec, titleHeight + titleVertMargins,
                    collapsingMargins));
            titleHeight += mSubtitleTextView.getMeasuredHeight() +
                    getVerticalMargins(mSubtitleTextView);
            childState = View.combineMeasuredStates(childState,
                    mSubtitleTextView.getMeasuredState());
        }

        width += titleWidth;
        height = Math.max(height, titleHeight);

        // Measurement already took padding into account for available space for the children,
        // add it in for the final size.
        width += getPaddingLeft() + getPaddingRight();
        height += getPaddingTop() + getPaddingBottom();

        final int measuredWidth = View.resolveSizeAndState(
                Math.max(width, getSuggestedMinimumWidth()),
                widthMeasureSpec, childState & View.MEASURED_STATE_MASK);
        final int measuredHeight = View.resolveSizeAndState(
                Math.max(height, getSuggestedMinimumHeight()),
                heightMeasureSpec, childState << View.MEASURED_HEIGHT_STATE_SHIFT);

        setMeasuredDimension(measuredWidth, shouldCollapse() ? 0 : measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final boolean isRtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
        final int width = getWidth();
        final int height = getHeight();
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        // 如果是left to right,以left来逐步layout,从左到右增加所占用宽度
        int left = paddingLeft;
        int right = width - paddingRight;

        // 初始化为0,后面进行计算赋值,并在子View中对其进行消耗
        final int[] collapsingMargins = mTempMargins;
        collapsingMargins[0] = collapsingMargins[1] = 0;

        // Align views within the minimum toolbar height, if set.
        // 所有view只有在Gravity.TOP || Gravity.BOTTOM 时,alignmentHeight才会起作用
        // 取较小值来进行计算,所以哪怕Toolbar设定固定值高度/设置padding超过56dp,alignmentHeight都不会超过56dp(?attr/actionBarSize)
        final int minHeight = ViewCompat.getMinimumHeight(this);
        final int alignmentHeight = minHeight >= 0 ? Math.min(minHeight, b - t) : 0;

        // mNavButtonView/mCollapseButtonView/mMenuView 都相对独立,优先进行layout
        if (shouldLayout(mNavButtonView)) {
            if (isRtl) {
                right = layoutChildRight(mNavButtonView, right, collapsingMargins,
                        alignmentHeight);
            } else {
                left = layoutChildLeft(mNavButtonView, left, collapsingMargins,
                        alignmentHeight);
            }
        }

        if (shouldLayout(mCollapseButtonView)) {
            if (isRtl) {
                right = layoutChildRight(mCollapseButtonView, right, collapsingMargins,
                        alignmentHeight);
            } else {
                left = layoutChildLeft(mCollapseButtonView, left, collapsingMargins,
                        alignmentHeight);
            }
        }

        if (shouldLayout(mMenuView)) {
            if (isRtl) {
                left = layoutChildLeft(mMenuView, left, collapsingMargins,
                        alignmentHeight);
            } else {
                right = layoutChildRight(mMenuView, right, collapsingMargins,
                        alignmentHeight);
            }
        }

        // collapsingMargins 计算content 跟mNavButtonView/mCollapseButtonView/mMenuView的距离
        final int contentInsetLeft = getCurrentContentInsetLeft();
        final int contentInsetRight = getCurrentContentInsetRight();
        // content view距离nav的距离
        collapsingMargins[0] = Math.max(0, contentInsetLeft - left);
        // content view距离menu的距离
        // (width - paddingRight - right) -->menu占用的宽度
        // 如果contentInsetRight比menu占用的宽度大,计算 content view距离menu的距离,否则距离为0
        collapsingMargins[1] = Math.max(0, contentInsetRight - (width - paddingRight - right));
        left = Math.max(left, contentInsetLeft);
        // (width - paddingRight - contentInsetRight) -->通过contentInsetRight直接计算right值,不算View的宽度
        // 如果contentInsetRight比menu占用的宽度大,使用contentInsetRight来layout
        right = Math.min(right, width - paddingRight - contentInsetRight);

        // 后续collapsingMargins主要作用在mExpandedActionView,或mLogoView/title/subTitle/xml声明的子view
        // 后面的View的marginLeft会不断消耗collapsingMargins的值,直到collapsingMargins变为0,然后marginLeft才会正常显示
        // 否则marginLeft<collapsingMargins,这部分margin是不会显示的
        if (shouldLayout(mExpandedActionView)) {
            if (isRtl) {
                right = layoutChildRight(mExpandedActionView, right, collapsingMargins,
                        alignmentHeight);
            } else {
                left = layoutChildLeft(mExpandedActionView, left, collapsingMargins,
                        alignmentHeight);
            }
        }

        if (shouldLayout(mLogoView)) {
            if (isRtl) {
                right = layoutChildRight(mLogoView, right, collapsingMargins,
                        alignmentHeight);
            } else {
                left = layoutChildLeft(mLogoView, left, collapsingMargins,
                        alignmentHeight);
            }
        }

        final boolean layoutTitle = shouldLayout(mTitleTextView);
        final boolean layoutSubtitle = shouldLayout(mSubtitleTextView);
        int titleHeight = 0;
        if (layoutTitle) {
            final LayoutParams lp = (LayoutParams) mTitleTextView.getLayoutParams();
            titleHeight += lp.topMargin + mTitleTextView.getMeasuredHeight() + lp.bottomMargin;
        }
        if (layoutSubtitle) {
            final LayoutParams lp = (LayoutParams) mSubtitleTextView.getLayoutParams();
            titleHeight += lp.topMargin + mSubtitleTextView.getMeasuredHeight() + lp.bottomMargin;
        }

        if (layoutTitle || layoutSubtitle) {
            int titleTop;
            final View topChild = layoutTitle ? mTitleTextView : mSubtitleTextView;
            final View bottomChild = layoutSubtitle ? mSubtitleTextView : mTitleTextView;
            final LayoutParams toplp = (LayoutParams) topChild.getLayoutParams();
            final LayoutParams bottomlp = (LayoutParams) bottomChild.getLayoutParams();
            final boolean titleHasWidth = (layoutTitle && (mTitleTextView.getMeasuredWidth() > 0))
                    || (layoutSubtitle && mSubtitleTextView.getMeasuredWidth() > 0);
            // 计算获取titleTop
            switch (mGravity & Gravity.VERTICAL_GRAVITY_MASK) {
                case Gravity.TOP:
                    titleTop = getPaddingTop() + toplp.topMargin + mTitleMarginTop;
                    break;
                default:
                case Gravity.CENTER_VERTICAL:
                    final int space = height - paddingTop - paddingBottom;
                    int spaceAbove = (space - titleHeight) / 2;
                    // space < titleHeight
                    // 或者space > titleHeight 并且 顶部空余的空间小于marginTop
                    if (spaceAbove < toplp.topMargin + mTitleMarginTop) {
                        spaceAbove = toplp.topMargin + mTitleMarginTop;
                    } else {
                        final int spaceBelow = height - paddingBottom - titleHeight -
                                spaceAbove - paddingTop;
                        // 底部空余的空间小于marginBottom
                        if (spaceBelow < toplp.bottomMargin + mTitleMarginBottom) {
                            spaceAbove = Math.max(0, spaceAbove -
                                    (bottomlp.bottomMargin + mTitleMarginBottom - spaceBelow));
                        }
                    }
                    titleTop = paddingTop + spaceAbove;
                    break;
                case Gravity.BOTTOM:
                    titleTop = height - paddingBottom - bottomlp.bottomMargin - mTitleMarginBottom -
                            titleHeight;
                    break;
            }
            if (isRtl) {
                final int rd = (titleHasWidth ? mTitleMarginStart : 0) - collapsingMargins[1];
                right -= Math.max(0, rd);
                collapsingMargins[1] = Math.max(0, -rd);
                int titleRight = right;
                int subtitleRight = right;

                if (layoutTitle) {
                    final LayoutParams lp = (LayoutParams) mTitleTextView.getLayoutParams();
                    final int titleLeft = titleRight - mTitleTextView.getMeasuredWidth();
                    final int titleBottom = titleTop + mTitleTextView.getMeasuredHeight();
                    mTitleTextView.layout(titleLeft, titleTop, titleRight, titleBottom);
                    titleRight = titleLeft - mTitleMarginEnd;
                    titleTop = titleBottom + lp.bottomMargin;
                }
                if (layoutSubtitle) {
                    final LayoutParams lp = (LayoutParams) mSubtitleTextView.getLayoutParams();
                    titleTop += lp.topMargin;
                    final int subtitleLeft = subtitleRight - mSubtitleTextView.getMeasuredWidth();
                    final int subtitleBottom = titleTop + mSubtitleTextView.getMeasuredHeight();
                    mSubtitleTextView.layout(subtitleLeft, titleTop, subtitleRight, subtitleBottom);
                    subtitleRight = subtitleRight - mTitleMarginEnd;
                    titleTop = subtitleBottom + lp.bottomMargin;
                }
                if (titleHasWidth) {
                    right = Math.min(titleRight, subtitleRight);
                }
            } else {
                final int ld = (titleHasWidth ? mTitleMarginStart : 0) - collapsingMargins[0];
                left += Math.max(0, ld);
                collapsingMargins[0] = Math.max(0, -ld);
                int titleLeft = left;
                int subtitleLeft = left;

                if (layoutTitle) {
                    final LayoutParams lp = (LayoutParams) mTitleTextView.getLayoutParams();
                    final int titleRight = titleLeft + mTitleTextView.getMeasuredWidth();
                    final int titleBottom = titleTop + mTitleTextView.getMeasuredHeight();
                    mTitleTextView.layout(titleLeft, titleTop, titleRight, titleBottom);
                    titleLeft = titleRight + mTitleMarginEnd;
                    titleTop = titleBottom + lp.bottomMargin;
                }
                if (layoutSubtitle) {
                    final LayoutParams lp = (LayoutParams) mSubtitleTextView.getLayoutParams();
                    titleTop += lp.topMargin;
                    final int subtitleRight = subtitleLeft + mSubtitleTextView.getMeasuredWidth();
                    final int subtitleBottom = titleTop + mSubtitleTextView.getMeasuredHeight();
                    mSubtitleTextView.layout(subtitleLeft, titleTop, subtitleRight, subtitleBottom);
                    subtitleLeft = subtitleRight + mTitleMarginEnd;
                    titleTop = subtitleBottom + lp.bottomMargin;
                }
                if (titleHasWidth) {
                    left = Math.max(titleLeft, subtitleLeft);
                }
            }
        }

        // Get all remaining children sorted for layout. This is all prepared
        // such that absolute layout direction can be used below.

        // 获取xml中添加的Gravity.LEFT的子View
        addCustomViewsWithGravity(mTempViews, Gravity.LEFT);
        final int leftViewsCount = mTempViews.size();
        for (int i = 0; i < leftViewsCount; i++) {
            left = layoutChildLeft(mTempViews.get(i), left, collapsingMargins,
                    alignmentHeight);
        }

        addCustomViewsWithGravity(mTempViews, Gravity.RIGHT);
        final int rightViewsCount = mTempViews.size();
        for (int i = 0; i < rightViewsCount; i++) {
            right = layoutChildRight(mTempViews.get(i), right, collapsingMargins,
                    alignmentHeight);
        }

        // Centered views try to center with respect to the whole bar, but views pinned
        // to the left or right can push the mass of centered views to one side or the other.
        // 将View居中,但是因为左侧和右侧的View,导致可能会偏向一边
        // 居中是在padding内居中,找到padding内的中心点parentCenter,中心点减去二分一中间内容的宽度,就是中间View layout的起点
        addCustomViewsWithGravity(mTempViews, Gravity.CENTER_HORIZONTAL);
        final int centerViewsWidth = getViewListMeasuredWidth(mTempViews, collapsingMargins);
        final int parentCenter = paddingLeft + (width - paddingLeft - paddingRight) / 2;
        final int halfCenterViewsWidth = centerViewsWidth / 2;
        int centerLeft = parentCenter - halfCenterViewsWidth;
        final int centerRight = centerLeft + centerViewsWidth;
        // 校验:如果布局左侧会发生重叠,那么直接接着left进行layout;
        if (centerLeft < left) {
            centerLeft = left;
        }
        // 校验:如果布局左侧未重叠,而右侧会发生重叠,从右侧开始layout
        else if (centerRight > right) {
            centerLeft -= centerRight - right;
        }

        final int centerViewsCount = mTempViews.size();
        for (int i = 0; i < centerViewsCount; i++) {
            centerLeft = layoutChildLeft(mTempViews.get(i), centerLeft, collapsingMargins,
                    alignmentHeight);
        }

        mTempViews.clear();
    }

    private int getViewListMeasuredWidth(List<View> views, int[] collapsingMargins) {
        int collapseLeft = collapsingMargins[0];
        int collapseRight = collapsingMargins[1];
        int width = 0;
        final int count = views.size();
        for (int i = 0; i < count; i++) {
            final View v = views.get(i);
            final LayoutParams lp = (LayoutParams) v.getLayoutParams();
            final int l = lp.leftMargin - collapseLeft;
            final int r = lp.rightMargin - collapseRight;
            final int leftMargin = Math.max(0, l);
            final int rightMargin = Math.max(0, r);
            collapseLeft = Math.max(0, -l);
            collapseRight = Math.max(0, -r);
            width += leftMargin + v.getMeasuredWidth() + rightMargin;
        }
        return width;
    }

    /**
     * @param child             子view
     * @param left              左侧已占用的宽度,从该坐标开始layout
     * @param collapsingMargins content View距离nav的距离
     * @param alignmentHeight   计算的高度
     * @return 子view占用的宽度
     */
    private int layoutChildLeft(View child, int left, int[] collapsingMargins,
                                int alignmentHeight) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        // 子View的marginLeft不起效,除非比collapsingMargins大
        final int l = lp.leftMargin - collapsingMargins[0];
        left += Math.max(0, l);
        // 子View的marginLeft不断消耗collapsingMargins的值
        collapsingMargins[0] = Math.max(0, -l);
        final int top = getChildTop(child, alignmentHeight);
        final int childWidth = child.getMeasuredWidth();
        child.layout(left, top, left + childWidth, top + child.getMeasuredHeight());
        left += childWidth + lp.rightMargin;
        return left;
    }

    private int layoutChildRight(View child, int right, int[] collapsingMargins,
                                 int alignmentHeight) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final int r = lp.rightMargin - collapsingMargins[1];
        right -= Math.max(0, r);
        collapsingMargins[1] = Math.max(0, -r);
        final int top = getChildTop(child, alignmentHeight);
        final int childWidth = child.getMeasuredWidth();
        child.layout(right - childWidth, top, right, top + child.getMeasuredHeight());
        right -= childWidth + lp.leftMargin;
        return right;
    }

    private int getChildTop(View child, int alignmentHeight) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final int childHeight = child.getMeasuredHeight();
        // 只有在Gravity.TOP || Gravity.BOTTOM 时起效,Gravity.CENTER_VERTICAL失效
        // alignmentHeight:为minHeight和layout高度的最小值,所以哪怕设定有padding值,高度大于?attr/actionBarSize,alignmentHeight的值都是56dp:?attr/actionBarSize
        // 如果minHeight < 0,alignmentHeight = 0,那么不需要考虑alignmentOffset
        // alignmentHeight:
        // 如果minHeight >= 0
        //      Gravity.TOP:不考虑子View的topMargin
        //          如果Toolbar.getPaddingTop() == 0,相当于居中显示
        //          如果Toolbar.getPaddingTop() > 0,当childHeight > alignmentHeight,但实际上当childHeight<=getHeight()(measure时会计算最大的高度),子View会被遮挡
        //      Gravity.BOTTOM:考虑子View的bottomMargin
        //          不懂为什么这里是-alignmentOffset,如果childHeight < alignmentHeight,则子View在底部的同时要往下移alignmentOffset
        //          如果childHeight > alignmentHeight时,往上移alignmentOffset
        //          即在Gravity.BOTTOM情况下,子View会在底部自动上移或下移(childHeight - alignmentHeight)高度
        // 所以,Gravity.TOP时,子View仍是居中的,而如果Toolbar存在paddingTop,那么是不会在Y轴上居中的,getPaddingTop() - alignmentOffset 是一个不规则的数,布局是乱的
        // Gravity.BOTTOM时,子View最好是match_parent的,不然上移下移的高度alignmentOffset不规则,布局肯定是乱的
        // 如果有特别需求,最好是先通过布局ViewGroup match_parent,然后在ViewGroup里操作
        final int alignmentOffset = alignmentHeight > 0 ? (childHeight - alignmentHeight) / 2 : 0;
        switch (getChildVerticalGravity(lp.gravity)) {
            case Gravity.TOP:
                return getPaddingTop() - alignmentOffset;

            case Gravity.BOTTOM:
                return getHeight() - getPaddingBottom() - childHeight
                        - lp.bottomMargin - alignmentOffset;

            default:
            case Gravity.CENTER_VERTICAL:
                final int paddingTop = getPaddingTop();
                final int paddingBottom = getPaddingBottom();
                final int height = getHeight();
                final int space = height - paddingTop - paddingBottom;
                int spaceAbove = (space - childHeight) / 2;
                // 如果childHeight > Toolbar.getHeight(),spaceAbove为负值,则界面相当于align TOP
                // 如果childHeight < Toolbar.getHeight()
                //      如果居中后顶部的剩余空间比topMargin小,包括当childHeight>Toolbar.getHeight()时,直接使用topMargin
                //      如果比topMargin大,剩余空间充裕,则以bottomMargin为主,topMargin失效
                if (spaceAbove < lp.topMargin) {
                    spaceAbove = lp.topMargin;
                } else {
                    // 实际等于space- childHeight - spaceAbove
                    // 不直接用spaceAbove是因为奇数时是两个值是不一样的
                    final int spaceBelow = height - paddingBottom - childHeight -
                            spaceAbove - paddingTop;
                    // 如果居中后底部的剩余空间比bottomMargin小,直接使用topMargin
                    // 如果比topMargin大,底部剩余空间充裕,直接居中,bottomMargin也失效
                    if (spaceBelow < lp.bottomMargin) {
                        spaceAbove = Math.max(0, spaceAbove - (lp.bottomMargin - spaceBelow));
                    }
                }
                // topMargin/bottomMargin 如果比spaceAbove/spaceBelow 大时,以childView的margin为主
                // 否则如果剩余空间充裕时,直接居中,topMargin/bottomMargin失效
                return paddingTop + spaceAbove;
        }
    }

    private int getChildVerticalGravity(int gravity) {
        final int vgrav = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        switch (vgrav) {
            case Gravity.TOP:
            case Gravity.BOTTOM:
            case Gravity.CENTER_VERTICAL:
                return vgrav;
            default:
                return mGravity & Gravity.VERTICAL_GRAVITY_MASK;
        }
    }

    /**
     * Prepare a list of non-SYSTEM child views. If the layout direction is RTL
     * this will be in reverse child order.
     *
     * @param views   List to populate. It will be cleared before use.
     * @param gravity Horizontal gravity to match against
     */
    private void addCustomViewsWithGravity(List<View> views, int gravity) {
        final boolean isRtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
        final int childCount = getChildCount();
        final int absGrav = GravityCompat.getAbsoluteGravity(gravity,
                ViewCompat.getLayoutDirection(this));

        views.clear();

        if (isRtl) {
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.mViewType == LayoutParams.CUSTOM && shouldLayout(child) &&
                        getChildHorizontalGravity(lp.gravity) == absGrav) {
                    views.add(child);
                }
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.mViewType == LayoutParams.CUSTOM && shouldLayout(child) &&
                        getChildHorizontalGravity(lp.gravity) == absGrav) {
                    views.add(child);
                }
            }
        }
    }

    private int getChildHorizontalGravity(int gravity) {
        final int ld = ViewCompat.getLayoutDirection(this);
        final int absGrav = GravityCompat.getAbsoluteGravity(gravity, ld);
        final int hGrav = absGrav & Gravity.HORIZONTAL_GRAVITY_MASK;
        switch (hGrav) {
            case Gravity.LEFT:
            case Gravity.RIGHT:
            case Gravity.CENTER_HORIZONTAL:
                return hGrav;
            default:
                return ld == ViewCompat.LAYOUT_DIRECTION_RTL ? Gravity.RIGHT : Gravity.LEFT;
        }
    }

    private boolean shouldLayout(View view) {
        return view != null && view.getParent() == this && view.getVisibility() != GONE;
    }

    private int getHorizontalMargins(View v) {
        final MarginLayoutParams mlp = (MarginLayoutParams) v.getLayoutParams();
        return MarginLayoutParamsCompat.getMarginStart(mlp) +
                MarginLayoutParamsCompat.getMarginEnd(mlp);
    }

    private int getVerticalMargins(View v) {
        final MarginLayoutParams mlp = (MarginLayoutParams) v.getLayoutParams();
        return mlp.topMargin + mlp.bottomMargin;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) p);
        } else if (p instanceof ActionBar.LayoutParams) {
            return new LayoutParams((ActionBar.LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        } else {
            return new LayoutParams(p);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p) && p instanceof LayoutParams;
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    public DecorToolbar getWrapper() {
        if (mWrapper == null) {
            mWrapper = new ToolbarWidgetWrapper(this, true);
        }
        return mWrapper;
    }

    void removeChildrenForExpandedActionView() {
        final int childCount = getChildCount();
        // Go backwards since we're removing from the list
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.mViewType != LayoutParams.EXPANDED && child != mMenuView) {
                removeViewAt(i);
                mHiddenViews.add(child);
            }
        }
    }

    void addChildrenForExpandedActionView() {
        final int count = mHiddenViews.size();
        // Re-add in reverse order since we removed in reverse order
        for (int i = count - 1; i >= 0; i--) {
            addView(mHiddenViews.get(i));
        }
        mHiddenViews.clear();
    }

    private boolean isChildOrHidden(View child) {
        return child.getParent() == this || mHiddenViews.contains(child);
    }

    /**
     * Force the toolbar to collapse to zero-height during measurement if
     * it could be considered "empty" (no visible elements with nonzero measured size)
     *
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    public void setCollapsible(boolean collapsible) {
        mCollapsible = collapsible;
        requestLayout();
    }

    /**
     * Must be called before the menu is accessed
     *
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    public void setMenuCallbacks(MenuPresenter.Callback pcb, MenuBuilder.Callback mcb) {
        mActionMenuPresenterCallback = pcb;
        mMenuBuilderCallback = mcb;
        if (mMenuView != null) {
            mMenuView.setMenuCallbacks(pcb, mcb);
        }
    }

    private void ensureContentInsets() {
        if (mContentInsets == null) {
            mContentInsets = new RtlSpacingHelper();
        }
    }

    /**
     * @hide
     */
    @RestrictTo(TESTS)
    @Nullable
    final TextView getTitleTextView() {
        return mTitleTextView;
    }

    /**
     * @hide
     */
    @RestrictTo(TESTS)
    @Nullable
    final TextView getSubtitleTextView() {
        return mSubtitleTextView;
    }

    /**
     * Accessor to enable LayoutLib to get ActionMenuPresenter directly.
     */
    ActionMenuPresenter getOuterActionMenuPresenter() {
        return mOuterActionMenuPresenter;
    }

    Context getPopupContext() {
        return mPopupContext;
    }

    /**
     * Interface responsible for receiving menu item click events if the items themselves
     * do not have individual item click listeners.
     */
    public interface OnMenuItemClickListener {
        /**
         * This method will be invoked when a menu item is clicked if the item itself did
         * not already handle the event.
         *
         * @param item {@link MenuItem} that was clicked
         * @return <code>true</code> if the event was handled, <code>false</code> otherwise.
         */
        public boolean onMenuItemClick(MenuItem item);
    }

    /**
     * Layout information for child views of Toolbars.
     *
     * <p>Toolbar.LayoutParams extends ActionBar.LayoutParams for compatibility with existing
     * ActionBar API. See
     * {@link androidx.appcompat.app.AppCompatActivity#setSupportActionBar(Toolbar)
     * AppCompatActivity.setSupportActionBar}
     * for more info on how to use a Toolbar as your Activity's ActionBar.</p>
     */
    public static class LayoutParams extends ActionBar.LayoutParams {
        static final int CUSTOM = 0;
        static final int SYSTEM = 1;
        static final int EXPANDED = 2;

        int mViewType = CUSTOM;

        public LayoutParams(@NonNull Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.gravity = Gravity.CENTER_VERTICAL | GravityCompat.START;
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(int gravity) {
            this(WRAP_CONTENT, MATCH_PARENT, gravity);
        }

        public LayoutParams(LayoutParams source) {
            super(source);

            mViewType = source.mViewType;
        }

        public LayoutParams(ActionBar.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            // ActionBar.LayoutParams doesn't have a MarginLayoutParams constructor.
            // Fake it here and copy over the relevant data.
            copyMarginsFromCompat(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        void copyMarginsFromCompat(MarginLayoutParams source) {
            this.leftMargin = source.leftMargin;
            this.topMargin = source.topMargin;
            this.rightMargin = source.rightMargin;
            this.bottomMargin = source.bottomMargin;
        }
    }

    public static class SavedState extends AbsSavedState {
        // 展示的mExpandedActionView的menuid
        int expandedMenuItemId;
        // 是否点击menu的更多按钮展示popup
        boolean isOverflowOpen;

        public SavedState(Parcel source) {
            this(source, null);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            expandedMenuItemId = source.readInt();
            isOverflowOpen = source.readInt() != 0;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(expandedMenuItemId);
            out.writeInt(isOverflowOpen ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private class ExpandedActionViewMenuPresenter implements MenuPresenter {
        MenuBuilder mMenu;
        // 用户点击某MenuItem展示mExpandedActionView,并隐藏该MenuItem,这是哪个MenuItem的引用
        // 用于判断是否在展示mExpandedActionView
        // 点击返回按钮mCollapseButtonView时通过MenuItem完成缩回操作
        // 初始化以及更新Menu时做更新操作
        MenuItemImpl mCurrentExpandedItem;

        ExpandedActionViewMenuPresenter() {
        }

        @Override
        public void initForMenu(Context context, MenuBuilder menu) {
            // Clear the expanded action view when menus change.
            // menu改变时,先将原来展Expanded的mExpandedActionView collapse收缩回来
            if (mMenu != null && mCurrentExpandedItem != null) {
                mMenu.collapseItemActionView(mCurrentExpandedItem);
            }
            mMenu = menu;
        }

        @Override
        public MenuView getMenuView(ViewGroup root) {
            return null;
        }

        @Override
        public void updateMenuView(boolean cleared) {
            // Make sure the expanded item we have is still there.
            // 正在展示 mExpandedActionView,如果Menu中不含有该MenuItem,说明已被Menu移除,需要将mExpandedActionView缩回
            if (mCurrentExpandedItem != null) {
                boolean found = false;

                if (mMenu != null) {
                    final int count = mMenu.size();
                    for (int i = 0; i < count; i++) {
                        final MenuItem item = mMenu.getItem(i);
                        if (item == mCurrentExpandedItem) {
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    // The item we had expanded disappeared. Collapse.
                    collapseItemActionView(mMenu, mCurrentExpandedItem);
                }
            }
        }

        @Override
        public void setCallback(Callback cb) {
        }

        @Override
        public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
            return false;
        }

        @Override
        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        @Override
        public boolean flagActionItems() {
            return false;
        }

        @Override
        public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
            // 确保初始化mCollapseButtonView,返回按钮
            ensureCollapseButtonView();
            ViewParent collapseButtonParent = mCollapseButtonView.getParent();
            if (collapseButtonParent != Toolbar.this) {
                if (collapseButtonParent instanceof ViewGroup) {
                    ((ViewGroup) collapseButtonParent).removeView(mCollapseButtonView);
                }
                addView(mCollapseButtonView);
            }
            // 添加MenuItem对应的tActionView
            mExpandedActionView = item.getActionView();
            mCurrentExpandedItem = item;
            ViewParent expandedActionParent = mExpandedActionView.getParent();
            if (expandedActionParent != Toolbar.this) {
                if (expandedActionParent instanceof ViewGroup) {
                    ((ViewGroup) expandedActionParent).removeView(mExpandedActionView);
                }
                final LayoutParams lp = generateDefaultLayoutParams();
                lp.gravity = GravityCompat.START | (mButtonGravity & Gravity.VERTICAL_GRAVITY_MASK);
                lp.mViewType = LayoutParams.EXPANDED;
                mExpandedActionView.setLayoutParams(lp);
                addView(mExpandedActionView);
            }

            // 移除除了mExpandedActionView/mCollapseButtonView/Menu外的Toolbar的子View
            removeChildrenForExpandedActionView();
            requestLayout();
            // 设置MenuItem为Expanded状态,隐藏该MenuItem
            item.setActionViewExpanded(true);

            // CollapsibleActionView执行回调
            if (mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) mExpandedActionView).onActionViewExpanded();
            }

            return true;
        }

        /**
         * 缩回mExpandedActionView
         */
        @Override
        public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
            // Do this before detaching the actionview from the hierarchy, in case
            // it needs to dismiss the soft keyboard, etc.
            // CollapsibleActionView执行回调,隐藏软键盘
            if (mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) mExpandedActionView).onActionViewCollapsed();
            }

            // Toolbar移除mExpandedActionView/mCollapseButtonView返回键
            removeView(mExpandedActionView);
            removeView(mCollapseButtonView);
            mExpandedActionView = null;

            // Toolbar重新添加回原来View
            addChildrenForExpandedActionView();
            // 相当于更新外部状态
            mCurrentExpandedItem = null;
            requestLayout();
            item.setActionViewExpanded(false);

            return true;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public Parcelable onSaveInstanceState() {
            return null;
        }

        @Override
        public void onRestoreInstanceState(Parcelable state) {
        }
    }

}
