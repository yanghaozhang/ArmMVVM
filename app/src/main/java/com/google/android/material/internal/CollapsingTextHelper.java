/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.material.internal;

import android.animation.TimeInterpolator;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.math.MathUtils;
import androidx.core.text.TextDirectionHeuristicsCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.internal.StaticLayoutBuilderCompat.StaticLayoutBuilderCompatException;
import com.google.android.material.resources.CancelableFontCallback;
import com.google.android.material.resources.CancelableFontCallback.ApplyFont;
import com.google.android.material.resources.TextAppearance;

import static android.text.Layout.Alignment.ALIGN_NORMAL;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static androidx.core.util.Preconditions.checkNotNull;
import static java.lang.Math.min;

/**
 * Helper class for rendering and animating collapsed text.
 *
 * @hide
 */
@RestrictTo(LIBRARY_GROUP)
public final class CollapsingTextHelper {

    // Pre-JB-MR2 doesn't support HW accelerated canvas scaled text so we will workaround it
    // by using our own texture
    //JB-MR2之前的版本不支持硬件加速的画布缩放文本，因此我们将使用自己的纹理来解决它
    private static final boolean USE_SCALING_TEXTURE = Build.VERSION.SDK_INT < 18;
    private static final String TAG = "CollapsingTextHelper";
    // ...的ELLIPSIS,文本太长的省略符
    private static final String ELLIPSIS_NORMAL = "\u2026"; // HORIZONTAL ELLIPSIS (…)

    // 测试模式下将为Text画品红色的背景
    private static final boolean DEBUG_DRAW = false;
    @NonNull
    private static final Paint DEBUG_DRAW_PAINT;

    static {
        DEBUG_DRAW_PAINT = DEBUG_DRAW ? new Paint() : null;
        if (DEBUG_DRAW_PAINT != null) {
            DEBUG_DRAW_PAINT.setAntiAlias(true);
            DEBUG_DRAW_PAINT.setColor(Color.MAGENTA);
        }
    }

    // 加载text的view,在这里是CollapsingToolbarLayout
    private final View view;

    // 是否需要画title,
    private boolean drawTitle;
    // expanded滚动的比例
    private float expandedFraction;

    // 拓展的区间
    @NonNull
    private final Rect expandedBounds;
    // 折叠的区间
    @NonNull
    private final Rect collapsedBounds;
    // 当前的区间
    @NonNull
    private final RectF currentBounds;
    // 拓展文本时的Gravity
    private int expandedTextGravity = Gravity.CENTER_VERTICAL;
    // 折叠文本时的Gravity
    private int collapsedTextGravity = Gravity.CENTER_VERTICAL;
    // 拓展文本时的大小
    private float expandedTextSize = 15;
    // 折叠文本时的大小
    private float collapsedTextSize = 15;
    // 拓展文本时的颜色
    private ColorStateList expandedTextColor;
    // 折叠文本时的颜色
    private ColorStateList collapsedTextColor;

    // 拓展时/折叠时/当前的,text的X/Y坐标
    private float expandedDrawY;
    private float collapsedDrawY;
    private float expandedDrawX;
    private float collapsedDrawX;
    private float currentDrawX;
    private float currentDrawY;
    // 拓展时/折叠时/当前的文本样式
    private Typeface collapsedTypeface;
    private Typeface expandedTypeface;
    private Typeface currentTypeface;
    // 拓展时/折叠时的回调
    private CancelableFontCallback expandedFontCallback;
    private CancelableFontCallback collapsedFontCallback;

    // setText的text
    @Nullable
    private CharSequence text;
    // draw的text
    @Nullable
    private CharSequence textToDraw;
    // 是否是Right to Left
    private boolean isRtl;

    // 等于USE_SCALING_TEXTURE,Build.VERSION.SDK_INT < 18,使用自己的纹理来处理画布缩放功能
    private boolean useTexture;
    // 处理纹理的Bitmap
    @Nullable
    private Bitmap expandedTitleTexture;
    // 处理纹理的Paint
    private Paint texturePaint;

    // 缩放比例
    private float scale;
    // 当前文本的字体大小
    private float currentTextSize;

    // getDrawableState
    private int[] state;

    // 是否已改变展示区域发生改变标志位
    private boolean boundsChanged;

    // 画笔
    @NonNull
    private final TextPaint textPaint;
    @NonNull
    private final TextPaint tmpPaint;

    // 动画差值器
    private TimeInterpolator positionInterpolator;
    private TimeInterpolator textSizeInterpolator;

    // 折叠阴影半径/坐标/颜色
    private float collapsedShadowRadius;
    private float collapsedShadowDx;
    private float collapsedShadowDy;
    private ColorStateList collapsedShadowColor;

    // 拓展阴影半径/坐标/颜色
    private float expandedShadowRadius;
    private float expandedShadowDx;
    private float expandedShadowDy;
    private ColorStateList expandedShadowColor;

    //??
    private StaticLayout textLayout;
    private float collapsedTextBlend;
    private float expandedTextBlend;
    private float expandedFirstLineDrawX;
    private CharSequence textToDrawCollapsed;
    // 最多行数
    private int maxLines = 1;

    public CollapsingTextHelper(View view) {
        this.view = view;

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        tmpPaint = new TextPaint(textPaint);

        collapsedBounds = new Rect();
        expandedBounds = new Rect();
        currentBounds = new RectF();
    }

    public void setTextSizeInterpolator(TimeInterpolator interpolator) {
        textSizeInterpolator = interpolator;
        recalculate();
    }

    public void setPositionInterpolator(TimeInterpolator interpolator) {
        positionInterpolator = interpolator;
        recalculate();
    }

    public void setExpandedTextSize(float textSize) {
        if (expandedTextSize != textSize) {
            expandedTextSize = textSize;
            recalculate();
        }
    }

    public void setCollapsedTextSize(float textSize) {
        if (collapsedTextSize != textSize) {
            collapsedTextSize = textSize;
            recalculate();
        }
    }

    public void setCollapsedTextColor(ColorStateList textColor) {
        if (collapsedTextColor != textColor) {
            collapsedTextColor = textColor;
            recalculate();
        }
    }

    public void setExpandedTextColor(ColorStateList textColor) {
        if (expandedTextColor != textColor) {
            expandedTextColor = textColor;
            recalculate();
        }
    }

    // 修改rect范围
    public void setExpandedBounds(int left, int top, int right, int bottom) {
        if (!rectEquals(expandedBounds, left, top, right, bottom)) {
            expandedBounds.set(left, top, right, bottom);
            boundsChanged = true;
            onBoundsChanged();
        }
    }

    public void setExpandedBounds(@NonNull Rect bounds) {
        setExpandedBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public void setCollapsedBounds(int left, int top, int right, int bottom) {
        if (!rectEquals(collapsedBounds, left, top, right, bottom)) {
            collapsedBounds.set(left, top, right, bottom);
            boundsChanged = true;
            onBoundsChanged();
        }
    }

    public void setCollapsedBounds(@NonNull Rect bounds) {
        setCollapsedBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    /**
     * 获取折叠的文本实际边界,提供一个宽度和文本Gravity,计算出在这个宽度下,折叠文本的Rectangle边界
     *
     * @param bounds      边界赋值对象
     * @param labelWidth  提供的宽度,计算在这个宽度内布局折叠文本(Gravity居中时才有效)
     * @param textGravity 文本的布局方向
     */
    public void getCollapsedTextActualBounds(@NonNull RectF bounds, int labelWidth, int textGravity) {
        isRtl = calculateIsRtl(text);
        bounds.left = getCollapsedTextLeftBound(labelWidth, textGravity);
        bounds.top = collapsedBounds.top;
        bounds.right = getCollapsedTextRightBound(bounds, labelWidth, textGravity);
        bounds.bottom = collapsedBounds.top + getCollapsedTextHeight();
    }

    /**
     * @param width   Gravity居中时,用以计算左侧边界位置
     * @param gravity 提供设定的Gravity
     * @return 获取左侧边界
     */
    private float getCollapsedTextLeftBound(int width, int gravity) {
        // 居中,计算左侧边界位置,位置以提供的宽度来计算
        if (gravity == Gravity.CENTER
                || (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
            return width / 2f - calculateCollapsedTextWidth() / 2;
        }
        /**
         *  如果不是居中,宽度以计算好的collapsedBounds折叠文本的边界为主
         *  当View是Right to Left时,布局在右侧,从右往左布局,此时如果给的Gravity是END或Right时,说明与当前布局一致
         *  返回collapsedBounds的Left不需要重新计算
         *
         *  当View是Left to Right时,布局在左侧,从左往右布局,此时如果给的Gravity是END或Right时,说明与当前布局是不一样的,需要另外计算
         *  返回right - calculateCollapsedTextWidth(),以collapsedBounds.right为最右边界,减去文本的宽度作为Left
         *
         */
        else if ((gravity & Gravity.END) == Gravity.END
                || (gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            return isRtl ? collapsedBounds.left : (collapsedBounds.right - calculateCollapsedTextWidth());
        }
        /**
         * 当View是Right to Left时,布局在右侧,从右往左布局,此时如果给的Gravity不是END或Right时,默认为Left,说明与当前布局是不一样的,需要另外计算
         * 返回right - calculateCollapsedTextWidth(),以collapsedBounds.right为最右边界,减去文本的宽度作为Left
         */
        else {
            return isRtl ? (collapsedBounds.right - calculateCollapsedTextWidth()) : collapsedBounds.left;
        }
    }

    /**
     * @param bounds  当View的方向(isRtl控制)与Gravity方向相同时,需要另外计算右侧的边界;如果不同,获取左侧边界时已经计算过了,这里直接返回collapsedBounds.right
     * @param width   Gravity居中时,用以计算左侧边界位置
     * @param gravity 提供设定的Gravity
     * @return 获取右侧边界
     */
    private float getCollapsedTextRightBound(@NonNull RectF bounds, int width, int gravity) {
        // 居中,计算左侧边界位置,位置以提供的宽度来计算
        if (gravity == Gravity.CENTER
                || (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {
            return width / 2f + calculateCollapsedTextWidth() / 2;
        }
        /**
         *  如果不是居中,宽度以计算好的collapsedBounds折叠文本的边界为主
         *  当View是Right to Left时,布局在右侧,从右往左布局,此时如果给的Gravity是END或Right时,说明与当前布局一致
         *  此时左侧边界为collapsedBounds.left,需要计算出右侧的边界,因为collapsedBounds.left + calculateCollapsedTextWidth() != collapsedBounds.right
         *
         *  当View是Left to Right时,布局在左侧,从左往右布局,此时如果给的Gravity是END或Right时,说明与当前布局是不一样的,需要另外计算
         *  以collapsedBounds.right为最右边界,从右往左布局,所以右侧边界为collapsedBounds.right
         *  如果计算,此时左侧边界为collapsedBounds.right - calculateCollapsedTextWidth(),右侧应为
         *  collapsedBounds.right - calculateCollapsedTextWidth() + calculateCollapsedTextWidth() = collapsedBounds.right
         *
         */
        else if ((gravity & Gravity.END) == Gravity.END
                || (gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            return isRtl ? (bounds.left + calculateCollapsedTextWidth()) : collapsedBounds.right;
        } else {
            return isRtl ? collapsedBounds.right : (bounds.left + calculateCollapsedTextWidth());
        }
    }

    /**
     * @return 通过tmpPaint计算text占用的宽度(受折叠文本的字体大小和字体样式限制)
     */
    public float calculateCollapsedTextWidth() {
        if (text == null) {
            return 0;
        }
        getTextPaintCollapsed(tmpPaint);
        return tmpPaint.measureText(text, 0, text.length());
    }

    /**
     * @return 获取拓展时字体的高度(ascent到baseline的高度)
     */
    public float getExpandedTextHeight() {
        getTextPaintExpanded(tmpPaint);
        // Return expanded height measured from the baseline.
        return -tmpPaint.ascent();
    }

    /**
     * @return 获取折叠时字体的高度(ascent到baseline的高度)
     */
    public float getCollapsedTextHeight() {
        getTextPaintCollapsed(tmpPaint);
        // Return collapsed height measured from the baseline.
        return -tmpPaint.ascent();
    }

    private void getTextPaintExpanded(@NonNull TextPaint textPaint) {
        textPaint.setTextSize(expandedTextSize);
        textPaint.setTypeface(expandedTypeface);
    }

    private void getTextPaintCollapsed(@NonNull TextPaint textPaint) {
        textPaint.setTextSize(collapsedTextSize);
        textPaint.setTypeface(collapsedTypeface);
    }

    /**
     * 当collapsedBounds/expandedBounds发生改变的时候,需要改变drawTitle的值,是否需要绘制Title
     */
    void onBoundsChanged() {
        drawTitle =
                collapsedBounds.width() > 0
                        && collapsedBounds.height() > 0
                        && expandedBounds.width() > 0
                        && expandedBounds.height() > 0;
    }

    public void setExpandedTextGravity(int gravity) {
        if (expandedTextGravity != gravity) {
            expandedTextGravity = gravity;
            recalculate();
        }
    }

    public int getExpandedTextGravity() {
        return expandedTextGravity;
    }

    public void setCollapsedTextGravity(int gravity) {
        if (collapsedTextGravity != gravity) {
            collapsedTextGravity = gravity;
            recalculate();
        }
    }

    public int getCollapsedTextGravity() {
        return collapsedTextGravity;
    }

    /**
     * 设置折叠时文字的样式
     * 提取需要的样式
     */
    public void setCollapsedTextAppearance(int resId) {
        TextAppearance textAppearance = new TextAppearance(view.getContext(), resId);

        if (textAppearance.textColor != null) {
            collapsedTextColor = textAppearance.textColor;
        }
        if (textAppearance.textSize != 0) {
            collapsedTextSize = textAppearance.textSize;
        }
        if (textAppearance.shadowColor != null) {
            collapsedShadowColor = textAppearance.shadowColor;
        }
        collapsedShadowDx = textAppearance.shadowDx;
        collapsedShadowDy = textAppearance.shadowDy;
        collapsedShadowRadius = textAppearance.shadowRadius;

        // Cancel pending async fetch, if any, and replace with a new one.
        if (collapsedFontCallback != null) {
            collapsedFontCallback.cancel();
        }
        // 异步使用fontFamily，style和fonts解析请求的字体Typeface
        // 如果解析失败，则调用expandedFontCallback.onFontRetrievalFailed(),使用textAppearance.getFallbackFont()备用字体Typeface调用new ApplyFont()
        // 如果解析成功,生成新的Typeface调用new ApplyFont()
        collapsedFontCallback =
                new CancelableFontCallback(
                        new ApplyFont() {
                            @Override
                            public void apply(Typeface font) {
                                setCollapsedTypeface(font);
                            }
                        },
                        textAppearance.getFallbackFont());
        textAppearance.getFontAsync(view.getContext(), collapsedFontCallback);

        recalculate();
    }

    public void setExpandedTextAppearance(int resId) {
        TextAppearance textAppearance = new TextAppearance(view.getContext(), resId);
        if (textAppearance.textColor != null) {
            expandedTextColor = textAppearance.textColor;
        }
        if (textAppearance.textSize != 0) {
            expandedTextSize = textAppearance.textSize;
        }
        if (textAppearance.shadowColor != null) {
            expandedShadowColor = textAppearance.shadowColor;
        }
        expandedShadowDx = textAppearance.shadowDx;
        expandedShadowDy = textAppearance.shadowDy;
        expandedShadowRadius = textAppearance.shadowRadius;

        // Cancel pending async fetch, if any, and replace with a new one.
        if (expandedFontCallback != null) {
            expandedFontCallback.cancel();
        }
        expandedFontCallback =
                new CancelableFontCallback(
                        new ApplyFont() {
                            @Override
                            public void apply(Typeface font) {
                                setExpandedTypeface(font);
                            }
                        },
                        textAppearance.getFallbackFont());
        textAppearance.getFontAsync(view.getContext(), expandedFontCallback);

        recalculate();
    }

    /**
     * 修改折叠字体的Typeface,如果Typeface改变了,重新计算界面
     */
    public void setCollapsedTypeface(Typeface typeface) {
        if (setCollapsedTypefaceInternal(typeface)) {
            recalculate();
        }
    }

    public void setExpandedTypeface(Typeface typeface) {
        if (setExpandedTypefaceInternal(typeface)) {
            recalculate();
        }
    }

    public void setTypefaces(Typeface typeface) {
        boolean collapsedFontChanged = setCollapsedTypefaceInternal(typeface);
        boolean expandedFontChanged = setExpandedTypefaceInternal(typeface);
        if (collapsedFontChanged || expandedFontChanged) {
            recalculate();
        }
    }

    // 改变折叠字体样式Typeface
    @SuppressWarnings("ReferenceEquality") // Matches the Typeface comparison in TextView
    private boolean setCollapsedTypefaceInternal(Typeface typeface) {
        // Explicit Typeface setting cancels pending async fetch, if any, to avoid old font overriding
        // already updated one when async op comes back after a while.
        if (collapsedFontCallback != null) {
            collapsedFontCallback.cancel();
        }
        if (collapsedTypeface != typeface) {
            collapsedTypeface = typeface;
            return true;
        }
        return false;
    }

    @SuppressWarnings("ReferenceEquality") // Matches the Typeface comparison in TextView
    private boolean setExpandedTypefaceInternal(Typeface typeface) {
        // Explicit Typeface setting cancels pending async fetch, if any, to avoid old font overriding
        // already updated one when async op comes back after a while.
        if (expandedFontCallback != null) {
            expandedFontCallback.cancel();
        }
        if (expandedTypeface != typeface) {
            expandedTypeface = typeface;
            return true;
        }
        return false;
    }

    public Typeface getCollapsedTypeface() {
        return collapsedTypeface != null ? collapsedTypeface : Typeface.DEFAULT;
    }

    public Typeface getExpandedTypeface() {
        return expandedTypeface != null ? expandedTypeface : Typeface.DEFAULT;
    }

    /**
     * 滚动到当前拓展的百分比值,0.0为完全折叠,1.0为拓展全部
     *
     * Set the value indicating the current scroll value. This decides how much of the background will
     * be displayed, as well as the title metrics/positioning.
     *
     * <p>A value of {@code 0.0} indicates that the layout is fully expanded. A value of {@code 1.0}
     * indicates that the layout is fully collapsed.
     */
    public void setExpansionFraction(float fraction) {
        fraction = MathUtils.clamp(fraction, 0f, 1f);

        if (fraction != expandedFraction) {
            expandedFraction = fraction;
            calculateCurrentOffsets();
        }
    }

    public final boolean setState(final int[] state) {
        this.state = state;

        if (isStateful()) {
            recalculate();
            return true;
        }

        return false;
    }

    public final boolean isStateful() {
        return (collapsedTextColor != null && collapsedTextColor.isStateful())
                || (expandedTextColor != null && expandedTextColor.isStateful());
    }

    public float getExpansionFraction() {
        return expandedFraction;
    }

    public float getCollapsedTextSize() {
        return collapsedTextSize;
    }

    public float getExpandedTextSize() {
        return expandedTextSize;
    }

    private void calculateCurrentOffsets() {
        calculateOffsets(expandedFraction);
    }

    private void calculateOffsets(final float fraction) {
        interpolateBounds(fraction);
        currentDrawX = lerp(expandedDrawX, collapsedDrawX, fraction, positionInterpolator);
        currentDrawY = lerp(expandedDrawY, collapsedDrawY, fraction, positionInterpolator);

        setInterpolatedTextSize(
                lerp(expandedTextSize, collapsedTextSize, fraction, textSizeInterpolator));

        setCollapsedTextBlend(
                1 - lerp(0, 1, 1 - fraction, AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR));
        setExpandedTextBlend(lerp(1, 0, fraction, AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR));

        if (collapsedTextColor != expandedTextColor) {
            // If the collapsed and expanded text colors are different, blend them based on the
            // fraction
            textPaint.setColor(
                    blendColors(getCurrentExpandedTextColor(), getCurrentCollapsedTextColor(), fraction));
        } else {
            textPaint.setColor(getCurrentCollapsedTextColor());
        }

        textPaint.setShadowLayer(
                lerp(expandedShadowRadius, collapsedShadowRadius, fraction, null),
                lerp(expandedShadowDx, collapsedShadowDx, fraction, null),
                lerp(expandedShadowDy, collapsedShadowDy, fraction, null),
                blendColors(
                        getCurrentColor(expandedShadowColor), getCurrentColor(collapsedShadowColor), fraction));

        ViewCompat.postInvalidateOnAnimation(view);
    }

    @ColorInt
    private int getCurrentExpandedTextColor() {
        return getCurrentColor(expandedTextColor);
    }

    @ColorInt
    public int getCurrentCollapsedTextColor() {
        return getCurrentColor(collapsedTextColor);
    }

    @ColorInt
    private int getCurrentColor(@Nullable ColorStateList colorStateList) {
        if (colorStateList == null) {
            return 0;
        }
        if (state != null) {
            return colorStateList.getColorForState(state, 0);
        }
        return colorStateList.getDefaultColor();
    }

    private void calculateBaseOffsets() {
        final float currentTextSize = this.currentTextSize;

        // We then calculate the collapsed text size, using the same logic
        calculateUsingTextSize(collapsedTextSize);
        if (textToDraw != null && textLayout != null) {
            textToDrawCollapsed =
                    TextUtils.ellipsize(textToDraw, textPaint, textLayout.getWidth(), TruncateAt.END);
        }
        float width =
                textToDrawCollapsed != null
                        ? textPaint.measureText(textToDrawCollapsed, 0, textToDrawCollapsed.length())
                        : 0;
        final int collapsedAbsGravity =
                GravityCompat.getAbsoluteGravity(
                        collapsedTextGravity,
                        isRtl ? ViewCompat.LAYOUT_DIRECTION_RTL : ViewCompat.LAYOUT_DIRECTION_LTR);

        switch (collapsedAbsGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.BOTTOM:
                collapsedDrawY = collapsedBounds.bottom + textPaint.ascent();
                break;
            case Gravity.TOP:
                collapsedDrawY = collapsedBounds.top;
                break;
            case Gravity.CENTER_VERTICAL:
            default:
                float textOffset = (textPaint.descent() - textPaint.ascent()) / 2;
                collapsedDrawY = collapsedBounds.centerY() - textOffset;
                break;
        }

        switch (collapsedAbsGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                collapsedDrawX = collapsedBounds.centerX() - (width / 2);
                break;
            case Gravity.RIGHT:
                collapsedDrawX = collapsedBounds.right - width;
                break;
            case Gravity.LEFT:
            default:
                collapsedDrawX = collapsedBounds.left;
                break;
        }

        calculateUsingTextSize(expandedTextSize);
        float expandedTextHeight = textLayout != null ? textLayout.getHeight() : 0;

        float measuredWidth = textToDraw != null
                ? textPaint.measureText(textToDraw, 0, textToDraw.length()) : 0;
        width = textLayout != null && maxLines > 1 && !isRtl
                ? textLayout.getWidth()
                : measuredWidth;
        expandedFirstLineDrawX = textLayout != null ? textLayout.getLineLeft(0) : 0;

        final int expandedAbsGravity =
                GravityCompat.getAbsoluteGravity(
                        expandedTextGravity,
                        isRtl ? ViewCompat.LAYOUT_DIRECTION_RTL : ViewCompat.LAYOUT_DIRECTION_LTR);
        switch (expandedAbsGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.BOTTOM:
                expandedDrawY = expandedBounds.bottom - expandedTextHeight + textPaint.descent();
                break;
            case Gravity.TOP:
                expandedDrawY = expandedBounds.top;
                break;
            case Gravity.CENTER_VERTICAL:
            default:
                float textOffset = expandedTextHeight / 2;
                expandedDrawY = expandedBounds.centerY() - textOffset;
                break;
        }

        switch (expandedAbsGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                expandedDrawX = expandedBounds.centerX() - (width / 2);
                break;
            case Gravity.RIGHT:
                expandedDrawX = expandedBounds.right - width;
                break;
            case Gravity.LEFT:
            default:
                expandedDrawX = expandedBounds.left;
                break;
        }

        // The bounds have changed so we need to clear the texture
        clearTexture();
        // Now reset the text size back to the original
        setInterpolatedTextSize(currentTextSize);
    }

    private void interpolateBounds(float fraction) {
        currentBounds.left =
                lerp(expandedBounds.left, collapsedBounds.left, fraction, positionInterpolator);
        currentBounds.top = lerp(expandedDrawY, collapsedDrawY, fraction, positionInterpolator);
        currentBounds.right =
                lerp(expandedBounds.right, collapsedBounds.right, fraction, positionInterpolator);
        currentBounds.bottom =
                lerp(expandedBounds.bottom, collapsedBounds.bottom, fraction, positionInterpolator);
    }

    private void setCollapsedTextBlend(float blend) {
        collapsedTextBlend = blend;
        ViewCompat.postInvalidateOnAnimation(view);
    }

    private void setExpandedTextBlend(float blend) {
        expandedTextBlend = blend;
        ViewCompat.postInvalidateOnAnimation(view);
    }

    public void draw(@NonNull Canvas canvas) {
        final int saveCount = canvas.save();
        // Compute where to draw textLayout for this frame
        if (textToDraw != null && drawTitle) {
            final float currentExpandedX =
                    currentDrawX + textLayout.getLineLeft(0) - expandedFirstLineDrawX * 2;

            textPaint.setTextSize(currentTextSize);
            float x = currentDrawX;
            float y = currentDrawY;
            final boolean drawTexture = useTexture && expandedTitleTexture != null;

            if (DEBUG_DRAW) {
                // Just a debug tool, which drawn a magenta rect in the text bounds
                canvas.drawRect(
                        currentBounds.left,
                        y,
                        currentBounds.right,
                        y + textLayout.getHeight() * scale,
                        DEBUG_DRAW_PAINT);
            }

            if (scale != 1f) {
                canvas.scale(scale, scale, x, y);
            }

            if (drawTexture) {
                // If we should use a texture, draw it instead of text
                canvas.drawBitmap(expandedTitleTexture, x, y, texturePaint);
                canvas.restoreToCount(saveCount);
                return;
            }

            if (shouldDrawMultiline()) {
                drawMultinlineTransition(canvas, currentExpandedX, y);
            } else {
                canvas.translate(x, y);
                textLayout.draw(canvas);
            }

            canvas.restoreToCount(saveCount);
        }
    }

    private boolean shouldDrawMultiline() {
        return maxLines > 1 && !isRtl && !useTexture;
    }

    private void drawMultinlineTransition(@NonNull Canvas canvas, float currentExpandedX, float y) {
        int originalAlpha = textPaint.getAlpha();
        // positon expanded text appropriately
        canvas.translate(currentExpandedX, y);
        // Expanded text
        textPaint.setAlpha((int) (expandedTextBlend * originalAlpha));
        textLayout.draw(canvas);

        // Collapsed text
        textPaint.setAlpha((int) (collapsedTextBlend * originalAlpha));
        int lineBaseline = textLayout.getLineBaseline(0);
        canvas.drawText(
                textToDrawCollapsed,
                /* start = */ 0,
                textToDrawCollapsed.length(),
                /* x = */ 0,
                lineBaseline,
                textPaint);
        // Remove ellipsis for Cross-section animation
        String tmp = textToDrawCollapsed.toString().trim();
        if (tmp.endsWith(ELLIPSIS_NORMAL)) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }
        // Cross-section between both texts (should stay at original alpha)
        textPaint.setAlpha(originalAlpha);
        canvas.drawText(
                tmp,
                /* start = */ 0,
                min(textLayout.getLineEnd(0), tmp.length()),
                /* x = */ 0,
                lineBaseline,
                textPaint);
    }

    private boolean calculateIsRtl(@NonNull CharSequence text) {
        final boolean defaultIsRtl = isDefaultIsRtl();
        return (defaultIsRtl
                ? TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL
                : TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR)
                .isRtl(text, 0, text.length());
    }

    private boolean isDefaultIsRtl() {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    private void setInterpolatedTextSize(float textSize) {
        calculateUsingTextSize(textSize);

        // Use our texture if the scale isn't 1.0
        useTexture = USE_SCALING_TEXTURE && scale != 1f;

        if (useTexture) {
            // Make sure we have an expanded texture if needed
            ensureExpandedTexture();
        }

        ViewCompat.postInvalidateOnAnimation(view);
    }

    @SuppressWarnings("ReferenceEquality") // Matches the Typeface comparison in TextView
    private void calculateUsingTextSize(final float textSize) {
        if (text == null) {
            return;
        }

        float collapsedWidth = collapsedBounds.width();
        float expandedWidth = expandedBounds.width();

        float availableWidth;
        float newTextSize;
        boolean updateDrawText = false;

        if (isClose(textSize, collapsedTextSize)) {
            newTextSize = collapsedTextSize;
            scale = 1f;
            if (currentTypeface != collapsedTypeface) {
                currentTypeface = collapsedTypeface;
                updateDrawText = true;
            }
            availableWidth = collapsedWidth;
        } else {
            newTextSize = expandedTextSize;
            if (currentTypeface != expandedTypeface) {
                currentTypeface = expandedTypeface;
                updateDrawText = true;
            }
            if (isClose(textSize, expandedTextSize)) {
                // If we're close to the expanded text size, snap to it and use a scale of 1
                scale = 1f;
            } else {
                // Else, we'll scale down from the expanded text size
                scale = textSize / expandedTextSize;
            }

            float textSizeRatio = collapsedTextSize / expandedTextSize;
            // This is the size of the expanded bounds when it is scaled to match the
            // collapsed text size
            float scaledDownWidth = expandedWidth * textSizeRatio;

            // If the scaled down size is larger than the actual collapsed width, we need to
            // cap the available width so that when the expanded text scales down, it matches
            // the collapsed width
            // Otherwise we'll just use the expanded width

            availableWidth = scaledDownWidth > collapsedWidth
                    ? min(collapsedWidth / textSizeRatio, expandedWidth)
                    : expandedWidth;
        }

        if (availableWidth > 0) {
            updateDrawText = (currentTextSize != newTextSize) || boundsChanged || updateDrawText;
            currentTextSize = newTextSize;
            boundsChanged = false;
        }

        if (textToDraw == null || updateDrawText) {
            textPaint.setTextSize(currentTextSize);
            textPaint.setTypeface(currentTypeface);
            // Use linear text scaling if we're scaling the canvas
            textPaint.setLinearText(scale != 1f);

            isRtl = calculateIsRtl(text);
            textLayout = createStaticLayout(shouldDrawMultiline() ? maxLines : 1, availableWidth, isRtl);
            textToDraw = textLayout.getText();
        }
    }

    private StaticLayout createStaticLayout(int maxLines, float availableWidth, boolean isRtl) {
        StaticLayout textLayout = null;
        try {
            textLayout =
                    StaticLayoutBuilderCompat.obtain(text, textPaint, (int) availableWidth)
                            .setEllipsize(TruncateAt.END)
                            .setIsRtl(isRtl)
                            .setAlignment(ALIGN_NORMAL)
                            .setIncludePad(false)
                            .setMaxLines(maxLines)
                            .build();
        } catch (StaticLayoutBuilderCompatException e) {
            Log.e(TAG, e.getCause().getMessage(), e);
        }

        return checkNotNull(textLayout);
    }

    private void ensureExpandedTexture() {
        if (expandedTitleTexture != null || expandedBounds.isEmpty() || TextUtils.isEmpty(textToDraw)) {
            return;
        }

        calculateOffsets(0f);
        int width = textLayout.getWidth();
        int height = textLayout.getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        expandedTitleTexture = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(expandedTitleTexture);
        textLayout.draw(c);

        if (texturePaint == null) {
            // Make sure we have a paint
            texturePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        }
    }

    public void recalculate() {
        if (view.getHeight() > 0 && view.getWidth() > 0) {
            // If we've already been laid out, calculate everything now otherwise we'll wait
            // until a layout
            calculateBaseOffsets();
            calculateCurrentOffsets();
        }
    }

    /**
     * Set the title to display
     *
     * @param text
     */
    public void setText(@Nullable CharSequence text) {
        if (text == null || !TextUtils.equals(this.text, text)) {
            this.text = text;
            textToDraw = null;
            clearTexture();
            recalculate();
        }
    }

    @Nullable
    public CharSequence getText() {
        return text;
    }

    private void clearTexture() {
        if (expandedTitleTexture != null) {
            expandedTitleTexture.recycle();
            expandedTitleTexture = null;
        }
    }

    public void setMaxLines(int maxLines) {
        if (maxLines != this.maxLines) {
            this.maxLines = maxLines;
            clearTexture();
            recalculate();
        }
    }

    public int getMaxLines() {
        return maxLines;
    }

    /**
     * Returns true if {@code value} is 'close' to it's closest decimal value. Close is currently
     * defined as it's difference being < 0.001.
     */
    private static boolean isClose(float value, float targetValue) {
        return Math.abs(value - targetValue) < 0.001f;
    }

    public ColorStateList getExpandedTextColor() {
        return expandedTextColor;
    }

    public ColorStateList getCollapsedTextColor() {
        return collapsedTextColor;
    }

    /**
     * Blend {@code color1} and {@code color2} using the given ratio.
     *
     * @param ratio of which to blend. 0.0 will return {@code color1}, 0.5 will give an even blend,
     *              1.0 will return {@code color2}.
     */
    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRatio = 1f - ratio;
        float a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio);
        float r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio);
        float g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio);
        float b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    private static float lerp(
            float startValue, float endValue, float fraction, @Nullable TimeInterpolator interpolator) {
        if (interpolator != null) {
            fraction = interpolator.getInterpolation(fraction);
        }
        return AnimationUtils.lerp(startValue, endValue, fraction);
    }

    private static boolean rectEquals(@NonNull Rect r, int left, int top, int right, int bottom) {
        return !(r.left != left || r.top != top || r.right != right || r.bottom != bottom);
    }
}
