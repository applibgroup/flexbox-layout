/*
 * Copyright 2016 Google Inc. All rights reserved.
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

package com.google.ohos.flexbox;

import java.util.ArrayList;
import java.util.List;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.DependentLayout;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.element.Element;
import ohos.agp.render.Canvas;
import ohos.agp.utils.Rect;
import ohos.app.Context;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.utils.Parcel;
import ohos.utils.PlainIntArray;
import ohos.utils.Sequenceable;

import org.jetbrains.annotations.Nullable;

import static com.google.ohos.flexbox.FlexDirection.ROW;
import static com.google.ohos.flexbox.FlexDirection.ROW_REVERSE;
import static com.google.ohos.flexbox.FlexboxLayout.DividerMode.SHOW_DIVIDER_NONE;
import static com.google.ohos.flexbox.FlexboxLayout.DividerMode.SHOW_DIVIDER_BEGINNING;
import static com.google.ohos.flexbox.FlexboxLayout.DividerMode.SHOW_DIVIDER_MIDDLE;
import static com.google.ohos.flexbox.FlexboxLayout.DividerMode.SHOW_DIVIDER_END;

/**
 * A layout that arranges its children in a way its attributes can be specified like the
 * CSS Flexible Box Layout Module.
 * This class extends the {@link ComponentContainer} like other layout classes such as {@link DirectionalLayout}
 * or {@link DependentLayout}, the attributes can be specified from a layout XML or from code.
 *
 * The supported attributes that you can use are:
 * <ul>
 * <li>{@code flexDirection}</li>
 * <li>{@code flexWrap}</li>
 * <li>{@code justifyContent}</li>
 * <li>{@code alignItems}</li>
 * <li>{@code alignContent}</li>
 * <li>{@code showDivider}</li>
 * <li>{@code showDividerHorizontal}</li>
 * <li>{@code showDividerVertical}</li>
 * <li>{@code dividerDrawable}</li>
 * <li>{@code dividerDrawableHorizontal}</li>
 * <li>{@code dividerDrawableVertical}</li>
 * <li>{@code maxLine}</li>
 * </ul>
 * for the FlexboxLayout.
 *
 * And for the children of the FlexboxLayout, you can use:
 * <ul>
 * <li>{@code layout_order}</li>
 * <li>{@code layout_flexGrow}</li>
 * <li>{@code layout_flexShrink}</li>
 * <li>{@code layout_flexBasisPercent}</li>
 * <li>{@code layout_alignSelf}</li>
 * <li>{@code layout_minWidth}</li>
 * <li>{@code layout_minHeight}</li>
 * <li>{@code layout_maxWidth}</li>
 * <li>{@code layout_maxHeight}</li>
 * <li>{@code layout_wrapBefore}</li>
 * </ul>
 */
public class FlexboxLayout extends ComponentContainer implements FlexContainer, Component.DrawTask, Component.LayoutRefreshedListener {
    /**
     * The current value of the {@link FlexDirection}, the default value is {@link
     * FlexDirection#ROW}.
     *
     * @see FlexDirection
     */
    private FlexDirection mFlexDirection;

    /**
     * The current value of the {@link FlexWrap}, the default value is {@link FlexWrap#NOWRAP}.
     *
     * @see FlexWrap
     */
    private FlexWrap mFlexWrap;

    /**
     * The current value of the {@link JustifyContent}, the default value is
     * {@link JustifyContent#FLEX_START}.
     *
     * @see JustifyContent
     */
    private JustifyContent mJustifyContent;

    /**
     * The current value of the {@link AlignItems}, the default value is
     * {@link AlignItems#FLEX_START}.
     *
     * @see AlignItems
     */
    private AlignItems mAlignItems;

    /**
     * The current value of the {@link AlignContent}, the default value is
     * {@link AlignContent#FLEX_START}.
     *
     * @see AlignContent
     */
    private AlignContent mAlignContent;

    /**
     * The current value of the maxLine attribute, which specifies the maximum number of flex lines.
     */
    private int mMaxLine = NOT_SET;

    /**
     * The Enums to be used as the arguments for the {@link #setShowDivider(DividerMode)},
     * {@link #setShowDividerHorizontal(DividerMode)} or {@link #setShowDividerVertical(DividerMode)}.
     * One or more of the values (such as
     * {@link #SHOW_DIVIDER_BEGINNING} | {@link #SHOW_DIVIDER_MIDDLE}) can be passed to those set
     * methods.
     */
    public enum  DividerMode {
        /** Constant to show no dividers */
        SHOW_DIVIDER_NONE(0),

        /** Constant to show a divider at the beginning of the flex lines (or flex items). */
        SHOW_DIVIDER_BEGINNING(1),

        /** Constant to show dividers between flex lines or flex items. */
        SHOW_DIVIDER_MIDDLE(1 << 1),

        /** Constant to show a divider at the end of the flex lines or flex items. */
        SHOW_DIVIDER_END(1 << 2);

        private final int value;

        DividerMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static DividerMode findByValue(int intValue){
            for(DividerMode v : values()){
                if( v.getValue() == intValue){
                    return v;
                }
            }
            return SHOW_DIVIDER_NONE;
        }
    }

    /** The Element to be drawn for the horizontal dividers. */
    @Nullable
    private Element mDividerDrawableHorizontal;

    /** The Element to be drawn for the vertical dividers. */
    @Nullable
    private Element mDividerDrawableVertical;

    /**
     * Indicates the divider mode for the {@link #mDividerDrawableHorizontal}. The value needs to
     * be the combination of the value of {@link DividerMode#SHOW_DIVIDER_NONE},
     * {@link DividerMode#SHOW_DIVIDER_BEGINNING}, {@link DividerMode#SHOW_DIVIDER_MIDDLE} and {@link DividerMode#SHOW_DIVIDER_END}
     */
    private DividerMode mShowDividerHorizontal = SHOW_DIVIDER_NONE;

    /**
     * Indicates the divider mode for the {@link #mDividerDrawableVertical}. The value needs to
     * be the combination of the value of {@link DividerMode#SHOW_DIVIDER_NONE},
     * {@link DividerMode#SHOW_DIVIDER_BEGINNING}, {@link DividerMode#SHOW_DIVIDER_MIDDLE} and {@link DividerMode#SHOW_DIVIDER_END}
     */
    private DividerMode mShowDividerVertical = SHOW_DIVIDER_NONE;

    /** The height of the {@link #mDividerDrawableHorizontal}. */
    private int mDividerHorizontalHeight;

    /** The width of the {@link #mDividerDrawableVertical}. */
    private int mDividerVerticalWidth;

    /**
     * Holds reordered indices, which {@link FlexItem#getOrder()} parameters are taken
     * into account
     */
    private int[] mReorderedIndices;

    /**
     * Caches the {@link FlexItem#getOrder()} attributes for children components.
     * Key: the index of the component reordered indices using the {@link FlexItem#getOrder()}
     * isn't taken into account)
     * Value: the value for the order attribute
     */
    private PlainIntArray mOrderCache;

    private FlexboxHelper mFlexboxHelper = new FlexboxHelper(this);

    private List<FlexLine> mFlexLines = new ArrayList<>();

    /**
     * Used for receiving the calculation of the flex results to avoid creating a new instance
     * every time flex lines are calculated.
     */
    private FlexboxHelper.FlexLinesResult mFlexLinesResult = new FlexboxHelper.FlexLinesResult();

    // custom attributes
    private static final String flexDirection = "flexDirection";
    private static final String flexWrap = "flexWrap";
    private static final String justifyContent = "justifyContent";
    private static final String alignItems = "alignItems";
    private static final String alignContent = "alignContent";
    private static final String maxLine = "maxLine";
    private static final String dividerDrawable = "dividerDrawable";
    private static final String dividerDrawableHorizontal = "dividerDrawableHorizontal";
    private static final String dividerDrawableVertical = "dividerDrawableVertical";
    private static final String showDivider = "showDivider";
    private static final String showDividerVertical = "showDividerVertical";
    private static final String showDividerHorizontal = "showDividerHorizontal";

    public FlexboxLayout(Context context) {
        this(context, null);
    }

    public FlexboxLayout(Context context, AttrSet attrSet) {
        this(context, attrSet, null);
    }

    public FlexboxLayout(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);

        mFlexDirection = FlexDirection.findByValue(attrSet.getAttr(flexDirection).isPresent() ?
                attrSet.getAttr(flexDirection).get().getIntegerValue() : 0);
        mFlexWrap = FlexWrap.findByValue(attrSet.getAttr(flexWrap).isPresent() ?
                attrSet.getAttr(flexWrap).get().getIntegerValue() : 0);
        mJustifyContent = JustifyContent.findByValue(attrSet.getAttr(justifyContent).isPresent() ?
                attrSet.getAttr(justifyContent).get().getIntegerValue() : 0);
        mAlignItems = AlignItems.findByValue(attrSet.getAttr(alignItems).isPresent() ?
                attrSet.getAttr(alignItems).get().getIntegerValue() : 0);
        mAlignContent = AlignContent.findByValue(attrSet.getAttr(alignContent).isPresent() ?
                attrSet.getAttr(alignContent).get().getIntegerValue() : 0);
        mMaxLine = attrSet.getAttr(maxLine).isPresent() ?
                attrSet.getAttr(maxLine).get().getIntegerValue() : NOT_SET;
        Element drawable = attrSet.getAttr(dividerDrawable).isPresent() ?
                attrSet.getAttr(dividerDrawable).get().getElement() : null;
        if (drawable != null) {
            setDividerDrawableHorizontal(drawable);
            setDividerDrawableVertical(drawable);
        }
        Element drawableHorizontal = attrSet.getAttr(dividerDrawableHorizontal).isPresent() ?
                attrSet.getAttr(dividerDrawableHorizontal).get().getElement() : null;
        if (drawableHorizontal != null) {
            setDividerDrawableHorizontal(drawableHorizontal);
        }
        Element drawableVertical = attrSet.getAttr(dividerDrawableVertical).isPresent() ?
                attrSet.getAttr(dividerDrawableVertical).get().getElement() : null;
        if (drawableVertical != null) {
            setDividerDrawableVertical(drawableVertical);
        }
        int dividerMode = attrSet.getAttr(showDivider).isPresent() ?
                attrSet.getAttr(showDivider).get().getIntegerValue() : SHOW_DIVIDER_NONE.getValue();
        if (dividerMode != SHOW_DIVIDER_NONE.getValue()) {
            mShowDividerVertical = DividerMode.findByValue(dividerMode);
            mShowDividerHorizontal = DividerMode.findByValue(dividerMode);
        }
        int dividerModeVertical = attrSet.getAttr(showDividerVertical).isPresent() ?
                attrSet.getAttr(showDividerVertical).get().getIntegerValue() : SHOW_DIVIDER_NONE.getValue();
        if (dividerModeVertical != SHOW_DIVIDER_NONE.getValue()) {
            mShowDividerVertical = DividerMode.findByValue(dividerModeVertical);
        }
        int dividerModeHorizontal = attrSet.getAttr(showDividerHorizontal).isPresent() ?
                attrSet.getAttr(showDividerHorizontal).get().getIntegerValue() : SHOW_DIVIDER_NONE.getValue();
        if (dividerModeHorizontal != SHOW_DIVIDER_NONE.getValue()) {
            mShowDividerHorizontal = DividerMode.findByValue(dividerModeHorizontal);
        }
        addDrawTask(this);
        setLayoutRefreshedListener(this);
    }

    private void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrderCache == null) {
            mOrderCache = new PlainIntArray(getChildCount());
        }
        if (mFlexboxHelper.isOrderChangedFromLastMeasurement(mOrderCache)) {
            mReorderedIndices = mFlexboxHelper.createReorderedIndices(mOrderCache);
        }

        // TODO: Only calculate the children components which are affected from the last measure.

        switch (mFlexDirection) {
            case ROW: // Intentional fall through
            case ROW_REVERSE:
                measureHorizontal(widthMeasureSpec, heightMeasureSpec);
                break;
            case COLUMN: // Intentional fall through
            case COLUMN_REVERSE:
                measureVertical(widthMeasureSpec, heightMeasureSpec);
                break;
            default:
                throw new IllegalStateException(
                        "Invalid value for the flex direction is set: " + mFlexDirection);
        }
    }

    @Override
    public int getFlexItemCount() {
        return getChildCount();
    }

    @Override
    public Component getFlexItemAt(int index) {
        return getComponentAt(index);
    }

    /**
     * Returns a Component, which is reordered by taking {@link LayoutConfig#mOrder} parameters
     * into account.
     *
     * @param index the index of the component
     * @return the reordered component, which {@link LayoutConfig@order} is taken into account.
     * If the index is negative or out of bounds of the number of contained components,
     * returns {@code null}.
     */
    public Component getReorderedChildAt(int index) {
        if (index < 0 || index >= mReorderedIndices.length) {
            return null;
        }
        return getComponentAt(mReorderedIndices[index]);
    }

    @Override
    public Component getReorderedFlexItemAt(int index) {
        return getReorderedChildAt(index);
    }

    @Override
    public void addComponent(Component child, int index, ComponentContainer.LayoutConfig params) {
        if (mOrderCache == null) {
            mOrderCache = new PlainIntArray(getChildCount());
        }
        // Create an array for the reordered indices before the Component is added in the parent
        // ComponentContainer since otherwise reordered indices won't be in effect before the
        // FlexboxLayout's onMeasure is called.
        // Because postLayout is requested in the super.addComponent method.
        mReorderedIndices = mFlexboxHelper
                .createReorderedIndices(child, index, params, mOrderCache);
        super.addComponent(child, index, params);
    }

    /**
     * Sub method for {@link #onMeasure(int, int)}, when the main axis direction is horizontal
     * (either left to right or right to left).
     *
     * @param widthMeasureSpec  horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec vertical space requirements as imposed by the parent
     * @see #onMeasure(int, int)
     * @see #setFlexDirection(FlexDirection)
     * @see #setFlexWrap(FlexWrap)
     * @see #setAlignItems(AlignItems)
     * @see #setAlignContent(AlignContent)
     */
    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        mFlexLines.clear();
        mFlexLinesResult.reset();
        mFlexboxHelper
                .calculateHorizontalFlexLines(mFlexLinesResult, widthMeasureSpec,
                        heightMeasureSpec);
        mFlexLines = mFlexLinesResult.mFlexLines;

        mFlexboxHelper.determineMainSize(widthMeasureSpec, heightMeasureSpec);

        // TODO: Consider the case any individual child's mAlignSelf is set to ALIGN_SELF_BASELINE
        if (mAlignItems == AlignItems.BASELINE) {
            for (FlexLine flexLine : mFlexLines) {
                // The largest height value that also take the baseline shift into account
                int largestHeightInLine = Integer.MIN_VALUE;
                for (int i = 0; i < flexLine.mItemCount; i++) {
                    int componentIndex = flexLine.mFirstIndex + i;
                    Component child = getReorderedChildAt(componentIndex);
                    if (child == null || child.getVisibility() == Component.HIDE) {
                        continue;
                    }
                    LayoutConfig lp = (LayoutConfig) child.getLayoutConfig();
                    if (mFlexWrap != FlexWrap.WRAP_REVERSE) {
                        int marginTop = flexLine.mMaxBaseline - -1;
                        marginTop = Math.max(marginTop, lp.getMarginTop());
                        largestHeightInLine = Math.max(largestHeightInLine,
                                child.getEstimatedHeight() + marginTop + lp.getMarginBottom());
                    } else {
                        int marginBottom = flexLine.mMaxBaseline - child.getEstimatedHeight() + -1;
                        marginBottom = Math.max(marginBottom, lp.getMarginBottom());
                        largestHeightInLine = Math.max(largestHeightInLine,
                                child.getEstimatedHeight() + lp.getMarginTop() + marginBottom);
                    }
                }
                flexLine.mCrossSize = largestHeightInLine;
            }
        }

        mFlexboxHelper.determineCrossSize(widthMeasureSpec, heightMeasureSpec,
                getPaddingTop() + getPaddingBottom());
        // Now cross size for each flex line is determined.
        // Expand the components if alignItems (or mAlignSelf in each child component) is set to stretch
        mFlexboxHelper.stretchViews();
        setMeasuredDimensionForFlex(mFlexDirection, widthMeasureSpec, heightMeasureSpec,
                mFlexLinesResult.mChildState);
    }

    /**
     * Sub method for {@link #onMeasure(int, int)} when the main axis direction is vertical
     * (either from top to bottom or bottom to top).
     *
     * @param widthMeasureSpec  horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec vertical space requirements as imposed by the parent
     * @see #onMeasure(int, int)
     * @see #setFlexDirection(FlexDirection)
     * @see #setFlexWrap(FlexWrap)
     * @see #setAlignItems(AlignItems)
     * @see #setAlignContent(AlignContent)
     */
    private void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        mFlexLines.clear();
        mFlexLinesResult.reset();
        mFlexboxHelper.calculateVerticalFlexLines(mFlexLinesResult, widthMeasureSpec,
                heightMeasureSpec);
        mFlexLines = mFlexLinesResult.mFlexLines;

        mFlexboxHelper.determineMainSize(widthMeasureSpec, heightMeasureSpec);
        mFlexboxHelper.determineCrossSize(widthMeasureSpec, heightMeasureSpec,
                getPaddingLeft() + getPaddingRight());
        // Now cross size for each flex line is determined.
        // Expand the components if alignItems (or mAlignSelf in each child component) is set to stretch
        mFlexboxHelper.stretchViews();
        setMeasuredDimensionForFlex(mFlexDirection, widthMeasureSpec, heightMeasureSpec,
                mFlexLinesResult.mChildState);
    }

    /**
     * Set this FlexboxLayouts' width and height depending on the calculated size of main axis and
     * cross axis.
     *
     * @param flexDirection     the value of the flex direction
     * @param widthMeasureSpec  horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec vertical space requirements as imposed by the parent
     * @param childState        the child state of the Component
     * @see #getFlexDirection()
     * @see #setFlexDirection(FlexDirection)
     */
    private void setMeasuredDimensionForFlex(FlexDirection flexDirection, int widthMeasureSpec,
                                             int heightMeasureSpec, int childState) {
        int widthMode = EstimateSpec.getMode(widthMeasureSpec);
        int widthSize = EstimateSpec.getSize(widthMeasureSpec);
        int heightMode = EstimateSpec.getMode(heightMeasureSpec);
        int heightSize = EstimateSpec.getSize(heightMeasureSpec);
        int calculatedMaxHeight;
        int calculatedMaxWidth;
        switch (flexDirection) {
            case ROW: // Intentional fall through
            case ROW_REVERSE:
                calculatedMaxHeight = getSumOfCrossSize() + getPaddingTop()
                        + getPaddingBottom();
                calculatedMaxWidth = getLargestMainSize();
                break;
            case COLUMN: // Intentional fall through
            case COLUMN_REVERSE:
                calculatedMaxHeight = getLargestMainSize();
                calculatedMaxWidth = getSumOfCrossSize() + getPaddingLeft() + getPaddingRight();
                break;
            default:
                throw new IllegalArgumentException("Invalid flex direction: " + flexDirection);
        }
        int widthSizeAndState;
        switch (widthMode) {
            case EstimateSpec.PRECISE:
                if (widthSize < calculatedMaxWidth) {
                    childState = childState | 0x01000000;
                }
                widthSizeAndState = new UiUtil(getContext()).resolveSizeAndState(widthSize, widthMeasureSpec,
                        childState);
                break;
            case EstimateSpec.NOT_EXCEED: {
                if (widthSize < calculatedMaxWidth) {
                    childState = childState | 0x01000000;
                } else {
                    widthSize = calculatedMaxWidth;
                }
                widthSizeAndState = new UiUtil(getContext()).resolveSizeAndState(widthSize, widthMeasureSpec,
                        childState);
                break;
            }
            case EstimateSpec.UNCONSTRAINT: {
                widthSizeAndState = new UiUtil(getContext())
                        .resolveSizeAndState(calculatedMaxWidth, widthMeasureSpec, childState);
                break;
            }
            default:
                throw new IllegalStateException("Unknown width mode is set: " + widthMode);
        }
        int heightSizeAndState;
        switch (heightMode) {
            case EstimateSpec.PRECISE:
                if (heightSize < calculatedMaxHeight) {
                    childState = childState | 0x01000000 >> 16;
                }
                heightSizeAndState = new UiUtil(getContext()).resolveSizeAndState(heightSize, heightMeasureSpec,
                        childState);
                break;
            case EstimateSpec.NOT_EXCEED: {
                if (heightSize < calculatedMaxHeight) {
                    childState = childState | 0x01000000 >> 16;
                } else {
                    heightSize = calculatedMaxHeight;
                }
                heightSizeAndState = new UiUtil(getContext()).resolveSizeAndState(heightSize, heightMeasureSpec,
                        childState);
                break;
            }
            case EstimateSpec.UNCONSTRAINT: {
                heightSizeAndState = new UiUtil(getContext()).resolveSizeAndState(calculatedMaxHeight,
                        heightMeasureSpec, childState);
                break;
            }
            default:
                throw new IllegalStateException("Unknown height mode is set: " + heightMode);
        }
        setEstimatedSize(widthSizeAndState, heightSizeAndState);
    }

    @Override
    public int getLargestMainSize() {
        int largestSize = Integer.MIN_VALUE;
        for (FlexLine flexLine : mFlexLines) {
            largestSize = Math.max(largestSize, flexLine.mMainSize);
        }
        return largestSize;
    }

    @Override
    public int getSumOfCrossSize() {
        int sum = 0;
        for (int i = 0, size = mFlexLines.size(); i < size; i++) {
            FlexLine flexLine = mFlexLines.get(i);

            // Judge if the beginning or middle dividers are required
            if (hasDividerBeforeFlexLine(i)) {
                if (isMainAxisDirectionHorizontal()) {
                    sum += mDividerHorizontalHeight;
                } else {
                    sum += mDividerVerticalWidth;
                }
            }

            // Judge if the end divider is required
            if (hasEndDividerAfterFlexLine(i)) {
                if (isMainAxisDirectionHorizontal()) {
                    sum += mDividerHorizontalHeight;
                } else {
                    sum += mDividerVerticalWidth;
                }
            }
            sum += flexLine.mCrossSize;
        }
        return sum;
    }

    @Override
    public boolean isMainAxisDirectionHorizontal() {
        return mFlexDirection == ROW || mFlexDirection == ROW_REVERSE;
    }

    @Override
    public void onRefreshed(Component component) {
        int widthSpec = new UiUtil(getContext()).getMeasureSpec(getLayoutConfig().width, getWidth());
        int heightSpec = new UiUtil(getContext()).getMeasureSpec(getLayoutConfig().height, getHeight());
        onMeasure(widthSpec, heightSpec);
        Rect parentRect = getComponentPosition();
        onLayout(parentRect.left, parentRect.top, parentRect.right, parentRect.bottom);
    }

    private void onLayout(int left, int top, int right, int bottom) {
        LayoutDirection layoutDirection = getLayoutDirection();
        boolean isRtl;
        switch (mFlexDirection) {
            case ROW:
                isRtl = layoutDirection == LayoutDirection.RTL;
                layoutHorizontal(isRtl, left, top, right, bottom);
                break;
            case ROW_REVERSE:
                isRtl = layoutDirection != LayoutDirection.RTL;
                layoutHorizontal(isRtl, left, top, right, bottom);
                break;
            case COLUMN:
                isRtl = layoutDirection == LayoutDirection.RTL;
                if (mFlexWrap == FlexWrap.WRAP_REVERSE) {
                    isRtl = !isRtl;
                }
                layoutVertical(isRtl, false, left, top, right, bottom);
                break;
            case COLUMN_REVERSE:
                isRtl = layoutDirection == LayoutDirection.RTL;
                if (mFlexWrap == FlexWrap.WRAP_REVERSE) {
                    isRtl = !isRtl;
                }
                layoutVertical(isRtl, true, left, top, right, bottom);
                break;
            default:
                throw new IllegalStateException("Invalid flex direction is set: " + mFlexDirection);
        }
    }

    /**
     * Sub method for {@link #onLayout(int, int, int, int)} when the
     * {@link #mFlexDirection} is either {@link FlexDirection#ROW} or
     * {@link FlexDirection#ROW_REVERSE}.
     *
     * @param isRtl  {@code true} if the horizontal layout direction is right to left, {@code
     *               false} otherwise.
     * @param left   the left position of this Component
     * @param top    the top position of this Component
     * @param right  the right position of this Component
     * @param bottom the bottom position of this Component
     * @see #getFlexWrap()
     * @see #setFlexWrap(FlexWrap)
     * @see #getJustifyContent()
     * @see #setJustifyContent(JustifyContent)
     * @see #getAlignItems()
     * @see #setAlignItems(AlignItems)
     * @see LayoutConfig#mAlignSelf
     */
    private void layoutHorizontal(boolean isRtl, int left, int top, int right, int bottom) {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        // Use float to reduce the round error that may happen in when justifyContent ==
        // SPACE_BETWEEN or SPACE_AROUND
        float childLeft;

        int height = bottom - top;
        int width = right - left;
        // childBottom is used if the mFlexWrap is WRAP_REVERSE otherwise
        // childTop is used to align the vertical position of the children components.
        int childBottom = height - getPaddingBottom();
        int childTop = getPaddingTop();

        // Used only for RTL layout
        // Use float to reduce the round error that may happen in when justifyContent ==
        // SPACE_BETWEEN or SPACE_AROUND
        float childRight;
        for (int i = 0, size = mFlexLines.size(); i < size; i++) {
            FlexLine flexLine = mFlexLines.get(i);
            if (hasDividerBeforeFlexLine(i)) {
                childBottom -= mDividerHorizontalHeight;
                childTop += mDividerHorizontalHeight;
            }
            float spaceBetweenItem = 0f;
            switch (mJustifyContent) {
                case FLEX_START:
                    childLeft = paddingLeft;
                    childRight = (float) width - paddingRight;
                    break;
                case FLEX_END:
                    childLeft = width - flexLine.mMainSize + paddingRight;
                    childRight = flexLine.mMainSize - paddingLeft;
                    break;
                case CENTER:
                    childLeft = paddingLeft + (width - flexLine.mMainSize) / 2f;
                    childRight = width - paddingRight - (width - flexLine.mMainSize) / 2f;
                    break;
                case SPACE_AROUND: {
                    int visibleCount = flexLine.getItemCountNotGone();
                    if (visibleCount != 0) {
                        spaceBetweenItem = (width - flexLine.mMainSize)
                                / (float) visibleCount;
                    }
                    childLeft = paddingLeft + spaceBetweenItem / 2f;
                    childRight = width - paddingRight - spaceBetweenItem / 2f;
                    break;
                }
                case SPACE_BETWEEN: {
                    childLeft = paddingLeft;
                    int visibleCount = flexLine.getItemCountNotGone();
                    float denominator = visibleCount != 1 ? visibleCount - 1 : 1f;
                    spaceBetweenItem = (width - flexLine.mMainSize) / denominator;
                    childRight = (float) width - paddingRight;
                    break;
                }
                case SPACE_EVENLY: {
                    int visibleCount = flexLine.getItemCountNotGone();
                    if (visibleCount != 0) {
                        spaceBetweenItem = (width - flexLine.mMainSize)
                                / (float) (visibleCount + 1);
                    }
                    childLeft = paddingLeft + spaceBetweenItem;
                    childRight = width - paddingRight - spaceBetweenItem;
                    break;
                }
                default:
                    throw new IllegalStateException(
                            "Invalid justifyContent is set: " + mJustifyContent);
            }
            spaceBetweenItem = Math.max(spaceBetweenItem, 0);

            for (int j = 0; j < flexLine.mItemCount; j++) {
                int index = flexLine.mFirstIndex + j;
                Component child = getReorderedChildAt(index);
                if (child == null || child.getVisibility() == Component.HIDE) {
                    continue;
                }
                LayoutConfig lp = ((LayoutConfig) child.getLayoutConfig());
                childLeft += lp.getMarginLeft();
                childRight -= lp.getMarginRight();
                int beforeDividerLength = 0;
                int endDividerLength = 0;
                if (hasDividerBeforeChildAtAlongMainAxis(index, j)) {
                    beforeDividerLength = mDividerVerticalWidth;
                    childLeft += beforeDividerLength;
                    childRight -= beforeDividerLength;
                }
                if (j == flexLine.mItemCount - 1 && (mShowDividerVertical.getValue() & SHOW_DIVIDER_END.getValue()) > 0) {
                    endDividerLength = mDividerVerticalWidth;
                }
                if (mFlexWrap == FlexWrap.WRAP_REVERSE) {
                    if (isRtl) {
                        mFlexboxHelper.layoutSingleChildHorizontal(child, flexLine,
                                Math.round(childRight) - child.getEstimatedWidth(),
                                childBottom - child.getEstimatedHeight(), Math.round(childRight),
                                childBottom);
                    } else {
                        mFlexboxHelper.layoutSingleChildHorizontal(child, flexLine,
                                Math.round(childLeft), childBottom - child.getEstimatedHeight(),
                                Math.round(childLeft) + child.getEstimatedWidth(), childBottom);
                    }
                } else {
                    if (isRtl) {
                        mFlexboxHelper.layoutSingleChildHorizontal(child, flexLine,
                                Math.round(childRight) - child.getEstimatedWidth(),
                                childTop, Math.round(childRight),
                                childTop + child.getEstimatedHeight());
                    } else {
                        mFlexboxHelper.layoutSingleChildHorizontal(child, flexLine,
                                Math.round(childLeft), childTop,
                                Math.round(childLeft) + child.getEstimatedWidth(),
                                childTop + child.getEstimatedHeight());
                    }
                }
                childLeft += child.getEstimatedWidth() + spaceBetweenItem + lp.getMarginRight();
                childRight -= child.getEstimatedWidth() + spaceBetweenItem + lp.getMarginLeft();

                if (isRtl) {
                    flexLine.updatePositionFromView(child, endDividerLength, 0,
                             beforeDividerLength, 0);
                } else {
                    flexLine.updatePositionFromView(child, beforeDividerLength, 0,
                             endDividerLength, 0);
                }
            }
            childTop += flexLine.mCrossSize;
            childBottom -= flexLine.mCrossSize;
        }
    }

    /**
     * Sub method for {@link #onLayout(int, int, int, int)} when the
     * {@link #mFlexDirection} is either {@link FlexDirection#COLUMN} or
     * {@link FlexDirection#COLUMN_REVERSE}.
     *
     * @param isRtl           {@code true} if the horizontal layout direction is right to left,
     *                        {@code false}
     *                        otherwise
     * @param fromBottomToTop {@code true} if the layout direction is bottom to top, {@code false}
     *                        otherwise
     * @param left            the left position of this Component
     * @param top             the top position of this Component
     * @param right           the right position of this Component
     * @param bottom          the bottom position of this Component
     * @see #getFlexWrap()
     * @see #setFlexWrap(FlexWrap)
     * @see #getJustifyContent()
     * @see #setJustifyContent(JustifyContent)
     * @see #getAlignItems()
     * @see #setAlignItems(AlignItems)
     * @see LayoutConfig#mAlignSelf
     */
    private void layoutVertical(boolean isRtl, boolean fromBottomToTop, int left, int top,
                                int right, int bottom) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int paddingRight = getPaddingRight();
        int childLeft = getPaddingLeft();

        int width = right - left;
        int height = bottom - top;
        // childRight is used if the mFlexWrap is WRAP_REVERSE otherwise
        // childLeft is used to align the horizontal position of the children components.
        int childRight = width - paddingRight;

        // Use float to reduce the round error that may happen in when justifyContent ==
        // SPACE_BETWEEN or SPACE_AROUND
        float childTop;

        // Used only for if the direction is from bottom to top
        float childBottom;

        for (int i = 0, size = mFlexLines.size(); i < size; i++) {
            FlexLine flexLine = mFlexLines.get(i);
            if (hasDividerBeforeFlexLine(i)) {
                childLeft += mDividerVerticalWidth;
                childRight -= mDividerVerticalWidth;
            }
            float spaceBetweenItem = 0f;
            switch (mJustifyContent) {
                case FLEX_START:
                    childTop = paddingTop;
                    childBottom = (float) height - paddingBottom;
                    break;
                case FLEX_END:
                    childTop = height - flexLine.mMainSize + paddingBottom;
                    childBottom = flexLine.mMainSize - paddingTop;
                    break;
                case CENTER:
                    childTop = paddingTop + (height - flexLine.mMainSize) / 2f;
                    childBottom = height - paddingBottom - (height - flexLine.mMainSize) / 2f;
                    break;
                case SPACE_AROUND: {
                    int visibleCount = flexLine.getItemCountNotGone();
                    if (visibleCount != 0) {
                        spaceBetweenItem = (height - flexLine.mMainSize)
                                / (float) visibleCount;
                    }
                    childTop = paddingTop + spaceBetweenItem / 2f;
                    childBottom = height - paddingBottom - spaceBetweenItem / 2f;
                    break;
                }
                case SPACE_BETWEEN: {
                    childTop = paddingTop;
                    int visibleCount = flexLine.getItemCountNotGone();
                    float denominator = visibleCount != 1 ? visibleCount - 1 : 1f;
                    spaceBetweenItem = (height - flexLine.mMainSize) / denominator;
                    childBottom = (float) height - paddingBottom;
                    break;
                }
                case SPACE_EVENLY: {
                    int visibleCount = flexLine.getItemCountNotGone();
                    if (visibleCount != 0) {
                        spaceBetweenItem = (height - flexLine.mMainSize)
                                / (float) (visibleCount + 1);
                    }
                    childTop = paddingTop + spaceBetweenItem;
                    childBottom = height - paddingBottom - spaceBetweenItem;
                    break;
                }
                default:
                    throw new IllegalStateException(
                            "Invalid justifyContent is set: " + mJustifyContent);
            }
            spaceBetweenItem = Math.max(spaceBetweenItem, 0);

            for (int j = 0; j < flexLine.mItemCount; j++) {
                int index = flexLine.mFirstIndex + j;
                Component child = getReorderedChildAt(index);
                if (child == null || child.getVisibility() == Component.HIDE) {
                    continue;
                }
                LayoutConfig lp = ((LayoutConfig) child.getLayoutConfig());
                childTop += lp.getMarginTop();
                childBottom -= lp.getMarginBottom();
                int beforeDividerLength = 0;
                int endDividerLength = 0;
                if (hasDividerBeforeChildAtAlongMainAxis(index, j)) {
                    beforeDividerLength = mDividerHorizontalHeight;
                    childTop += beforeDividerLength;
                    childBottom -= beforeDividerLength;
                }
                if (j == flexLine.mItemCount - 1
                        && (mShowDividerHorizontal.getValue() & SHOW_DIVIDER_END.getValue()) > 0) {
                    endDividerLength = mDividerHorizontalHeight;
                }
                if (isRtl) {
                    if (fromBottomToTop) {
                        mFlexboxHelper.layoutSingleChildVertical(child, flexLine, true,
                                childRight - child.getEstimatedWidth(),
                                Math.round(childBottom) - child.getEstimatedHeight(), childRight,
                                Math.round(childBottom));
                    } else {
                        mFlexboxHelper.layoutSingleChildVertical(child, flexLine, true,
                                childRight - child.getEstimatedWidth(), Math.round(childTop),
                                childRight, Math.round(childTop) + child.getEstimatedHeight());
                    }
                } else {
                    if (fromBottomToTop) {
                        mFlexboxHelper.layoutSingleChildVertical(child, flexLine, false,
                                childLeft, Math.round(childBottom) - child.getEstimatedHeight(),
                                childLeft + child.getEstimatedWidth(), Math.round(childBottom));
                    } else {
                        mFlexboxHelper.layoutSingleChildVertical(child, flexLine, false,
                                childLeft, Math.round(childTop),
                                childLeft + child.getEstimatedWidth(),
                                Math.round(childTop) + child.getEstimatedHeight());
                    }
                }
                childTop += child.getEstimatedHeight() + spaceBetweenItem + lp.getMarginBottom();
                childBottom -= child.getEstimatedHeight() + spaceBetweenItem + lp.getMarginTop();

                if (fromBottomToTop) {
                    flexLine.updatePositionFromView(child, 0, endDividerLength, 0,
                             beforeDividerLength);
                } else {
                    flexLine.updatePositionFromView(child, 0, beforeDividerLength,
                            0, endDividerLength);
                }
            }
            childLeft += flexLine.mCrossSize;
            childRight -= flexLine.mCrossSize;
        }
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        if (mDividerDrawableVertical == null && mDividerDrawableHorizontal == null) {
            return;
        }
        if (mShowDividerHorizontal == SHOW_DIVIDER_NONE
                && mShowDividerVertical == SHOW_DIVIDER_NONE) {
            return;
        }
        LayoutDirection layoutDirection = getLayoutDirection();
        boolean isRtl;
        boolean fromBottomToTop = false;
        switch (mFlexDirection) {
            case ROW:
                isRtl = layoutDirection == LayoutDirection.RTL;
                if (mFlexWrap == FlexWrap.WRAP_REVERSE) {
                    fromBottomToTop = true;
                }
                drawDividersHorizontal(canvas, isRtl, fromBottomToTop);
                break;
            case ROW_REVERSE:
                isRtl = layoutDirection != LayoutDirection.RTL;
                if (mFlexWrap == FlexWrap.WRAP_REVERSE) {
                    fromBottomToTop = true;
                }
                drawDividersHorizontal(canvas, isRtl, fromBottomToTop);
                break;
            case COLUMN:
                isRtl = layoutDirection == LayoutDirection.RTL;
                if (mFlexWrap == FlexWrap.WRAP_REVERSE) {
                    isRtl = !isRtl;
                }
                drawDividersVertical(canvas, isRtl, false);
                break;
            case COLUMN_REVERSE:
                isRtl = layoutDirection == LayoutDirection.RTL;
                if (mFlexWrap == FlexWrap.WRAP_REVERSE) {
                    isRtl = !isRtl;
                }
                drawDividersVertical(canvas, isRtl, true);
                break;
        }
    }

    /**
     * Sub method for {@link #onDraw(Component, Canvas)} when the main axis direction is horizontal
     * ({@link #mFlexDirection} is either of {@link FlexDirection#ROW} or
     * {@link FlexDirection#ROW_REVERSE}.
     *
     * @param canvas          the canvas on which the background will be drawn
     * @param isRtl           {@code true} when the horizontal layout direction is right to left,
     *                        {@code false} otherwise
     * @param fromBottomToTop {@code true} when the vertical layout direction is bottom to top,
     *                        {@code false} otherwise
     */
    private void drawDividersHorizontal(Canvas canvas, boolean isRtl, boolean fromBottomToTop) {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int horizontalDividerLength = Math.max(0, getWidth() - paddingRight - paddingLeft);
        for (int i = 0, size = mFlexLines.size(); i < size; i++) {
            FlexLine flexLine = mFlexLines.get(i);
            for (int j = 0; j < flexLine.mItemCount; j++) {
                int componentIndex = flexLine.mFirstIndex + j;
                Component component = getReorderedChildAt(componentIndex);
                if (component == null || component.getVisibility() == Component.HIDE) {
                    continue;
                }
                LayoutConfig lp = (LayoutConfig) component.getLayoutConfig();

                // Judge if the beginning or middle divider is needed
                if (hasDividerBeforeChildAtAlongMainAxis(componentIndex, j)) {
                    int dividerLeft;
                    if (isRtl) {
                        dividerLeft = component.getRight() + lp.getMarginRight();
                    } else {
                        dividerLeft = component.getLeft() - lp.getMarginLeft() - mDividerVerticalWidth;
                    }

                    drawVerticalDivider(canvas, dividerLeft, flexLine.mTop, flexLine.mCrossSize);
                }

                // Judge if the end divider is needed
                if (j == flexLine.mItemCount - 1) {
                    if ((mShowDividerVertical.getValue() & SHOW_DIVIDER_END.getValue()) > 0) {
                        int dividerLeft;
                        if (isRtl) {
                            dividerLeft = component.getLeft() - lp.getMarginLeft() - mDividerVerticalWidth;
                        } else {
                            dividerLeft = component.getRight() + lp.getMarginRight();
                        }

                        drawVerticalDivider(canvas, dividerLeft, flexLine.mTop,
                                flexLine.mCrossSize);
                    }
                }
            }

            // Judge if the beginning or middle dividers are needed before the flex line
            if (hasDividerBeforeFlexLine(i)) {
                int horizontalDividerTop;
                if (fromBottomToTop) {
                    horizontalDividerTop = flexLine.mBottom;
                } else {
                    horizontalDividerTop = flexLine.mTop - mDividerHorizontalHeight;
                }
                drawHorizontalDivider(canvas, paddingLeft, horizontalDividerTop,
                        horizontalDividerLength);
            }
            // Judge if the end divider is needed before the flex line
            if (hasEndDividerAfterFlexLine(i)) {
                if ((mShowDividerHorizontal.getValue() & SHOW_DIVIDER_END.getValue()) > 0) {
                    int horizontalDividerTop;
                    if (fromBottomToTop) {
                        horizontalDividerTop = flexLine.mTop - mDividerHorizontalHeight;
                    } else {
                        horizontalDividerTop = flexLine.mBottom;
                    }
                    drawHorizontalDivider(canvas, paddingLeft, horizontalDividerTop,
                            horizontalDividerLength);
                }
            }
        }
    }

    /**
     * Sub method for {@link #onDraw(Component, Canvas)} when the main axis direction is vertical
     * ({@link #mFlexDirection} is either of {@link FlexDirection#COLUMN} or
     * {@link FlexDirection#COLUMN_REVERSE}.
     *
     * @param canvas          the canvas on which the background will be drawn
     * @param isRtl           {@code true} when the horizontal layout direction is right to left,
     *                        {@code false} otherwise
     * @param fromBottomToTop {@code true} when the vertical layout direction is bottom to top,
     *                        {@code false} otherwise
     */
    private void drawDividersVertical(Canvas canvas, boolean isRtl, boolean fromBottomToTop) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int verticalDividerLength = Math.max(0, getHeight() - paddingBottom - paddingTop);
        for (int i = 0, size = mFlexLines.size(); i < size; i++) {
            FlexLine flexLine = mFlexLines.get(i);

            // Draw horizontal dividers if needed
            for (int j = 0; j < flexLine.mItemCount; j++) {
                int componentIndex = flexLine.mFirstIndex + j;
                Component component = getReorderedChildAt(componentIndex);
                if (component == null || component.getVisibility() == Component.HIDE) {
                    continue;
                }
                LayoutConfig lp = (LayoutConfig) component.getLayoutConfig();

                // Judge if the beginning or middle divider is needed
                if (hasDividerBeforeChildAtAlongMainAxis(componentIndex, j)) {
                    int dividerTop;
                    if (fromBottomToTop) {
                        dividerTop = component.getBottom() + lp.getMarginBottom();
                    } else {
                        dividerTop = component.getTop() - lp.getMarginTop() - mDividerHorizontalHeight;
                    }

                    drawHorizontalDivider(canvas, flexLine.mLeft, dividerTop, flexLine.mCrossSize);
                }

                // Judge if the end divider is needed
                if (j == flexLine.mItemCount - 1) {
                    if ((mShowDividerHorizontal.getValue() & SHOW_DIVIDER_END.getValue()) > 0) {
                        int dividerTop;
                        if (fromBottomToTop) {
                            dividerTop = component.getTop() - lp.getMarginTop() - mDividerHorizontalHeight;
                        } else {
                            dividerTop = component.getBottom() + lp.getMarginBottom();
                        }

                        drawHorizontalDivider(canvas, flexLine.mLeft, dividerTop,
                                flexLine.mCrossSize);
                    }
                }
            }

            // Judge if the beginning or middle dividers are needed before the flex line
            if (hasDividerBeforeFlexLine(i)) {
                int verticalDividerLeft;
                if (isRtl) {
                    verticalDividerLeft = flexLine.mRight;
                } else {
                    verticalDividerLeft = flexLine.mLeft - mDividerVerticalWidth;
                }
                drawVerticalDivider(canvas, verticalDividerLeft, paddingTop,
                        verticalDividerLength);
            }
            if (hasEndDividerAfterFlexLine(i)) {
                if ((mShowDividerVertical.getValue() & SHOW_DIVIDER_END.getValue()) > 0) {
                    int verticalDividerLeft;
                    if (isRtl) {
                        verticalDividerLeft = flexLine.mLeft - mDividerVerticalWidth;
                    } else {
                        verticalDividerLeft = flexLine.mRight;
                    }
                    drawVerticalDivider(canvas, verticalDividerLeft, paddingTop,
                            verticalDividerLength);
                }
            }
        }
    }

    private void drawVerticalDivider(Canvas canvas, int left, int top, int length) {
        if (mDividerDrawableVertical == null) {
            return;
        }
        mDividerDrawableVertical.setBounds(left, top, left + mDividerVerticalWidth, top + length);
        mDividerDrawableVertical.drawToCanvas(canvas);
    }

    private void drawHorizontalDivider(Canvas canvas, int left, int top, int length) {
        if (mDividerDrawableHorizontal == null) {
            return;
        }
        mDividerDrawableHorizontal
                .setBounds(left, top, left + length, top + mDividerHorizontalHeight);
        mDividerDrawableHorizontal.drawToCanvas(canvas);
    }

    @Override
    public ComponentContainer.LayoutConfig createLayoutConfig(Context context, AttrSet attrSet) {
        return new LayoutConfig(context, attrSet);
    }

    @Override
    public ComponentContainer.LayoutConfig verifyLayoutConfig(ComponentContainer.LayoutConfig config) {
        if (config instanceof LayoutConfig) {
            return new LayoutConfig((LayoutConfig) config);
        }
        return new LayoutConfig(config);
    }

    @Override
    public FlexDirection getFlexDirection() {
        return mFlexDirection;
    }

    @Override
    public void setFlexDirection(FlexDirection flexDirection) {
        if (mFlexDirection != flexDirection) {
            mFlexDirection = flexDirection;
            postLayout();
        }
    }

    @Override
    public FlexWrap getFlexWrap() {
        return mFlexWrap;
    }

    @Override
    public void setFlexWrap(FlexWrap flexWrap) {
        if (mFlexWrap != flexWrap) {
            mFlexWrap = flexWrap;
            postLayout();
        }
    }

    @Override
    public JustifyContent getJustifyContent() {
        return mJustifyContent;
    }

    @Override
    public void setJustifyContent(JustifyContent justifyContent) {
        if (mJustifyContent != justifyContent) {
            mJustifyContent = justifyContent;
            postLayout();
        }
    }

    @Override
    public AlignItems getAlignItems() {
        return mAlignItems;
    }

    @Override
    public void setAlignItems(AlignItems alignItems) {
        if (mAlignItems != alignItems) {
            mAlignItems = alignItems;
            postLayout();
        }
    }

    @Override
    public AlignContent getAlignContent() {
        return mAlignContent;
    }

    @Override
    public void setAlignContent(AlignContent alignContent) {
        if (mAlignContent != alignContent) {
            mAlignContent = alignContent;
            postLayout();
        }
    }

    @Override
    public int getMaxLine() {
        return mMaxLine;
    }

    @Override
    public void setMaxLine(int maxLine) {
        if (mMaxLine != maxLine) {
            mMaxLine = maxLine;
            postLayout();
        }
    }

    /**
     * Overridden method from the FlexContainer to calculate the flex lines to be configured
     * @return the flex lines composing this flex container. This method returns a copy of the
     * original list excluding a dummy flex line (flex line that doesn't have any flex items in it
     * but used for the alignment along the cross axis).
     * Thus any changes of the returned list are not reflected to the original list.
     */
    @Override
    public List<FlexLine> getFlexLines() {
        List<FlexLine> result = new ArrayList<>(mFlexLines.size());
        for (FlexLine flexLine : mFlexLines) {
            if (flexLine.getItemCountNotGone() == 0) {
                continue;
            }
            result.add(flexLine);
        }
        return result;
    }

    @Override
    public int getDecorationLengthMainAxis(Component component, int index, int indexInFlexLine) {
        int decorationLength = 0;
        if (isMainAxisDirectionHorizontal()) {
            if (hasDividerBeforeChildAtAlongMainAxis(index, indexInFlexLine)) {
                decorationLength += mDividerVerticalWidth;
            }
            if ((mShowDividerVertical.getValue() & SHOW_DIVIDER_END.getValue()) > 0) {
                decorationLength += mDividerVerticalWidth;
            }
        } else {
            if (hasDividerBeforeChildAtAlongMainAxis(index, indexInFlexLine)) {
                decorationLength += mDividerHorizontalHeight;
            }
            if ((mShowDividerHorizontal.getValue() & SHOW_DIVIDER_END.getValue()) > 0) {
                decorationLength += mDividerHorizontalHeight;
            }
        }
        return decorationLength;
    }

    @Override
    public int getDecorationLengthCrossAxis(Component component) {
        // Decoration along the cross axis for an individual component is not supported in the
        // FlexboxLayout.
        return 0;
    }

    @Override
    public void onNewFlexLineAdded(FlexLine flexLine) {
        // The size of the end divider isn't added until the flexLine is added to the flex container
        // take the divider width (or height) into account when adding the flex line.
        if (isMainAxisDirectionHorizontal()) {
            if ((mShowDividerVertical.getValue() & SHOW_DIVIDER_END.getValue()) > 0) {
                flexLine.mMainSize += mDividerVerticalWidth;
                flexLine.mDividerLengthInMainSize += mDividerVerticalWidth;
            }
        } else {
            if ((mShowDividerHorizontal.getValue() & SHOW_DIVIDER_END.getValue()) > 0) {
                flexLine.mMainSize += mDividerHorizontalHeight;
                flexLine.mDividerLengthInMainSize += mDividerHorizontalHeight;
            }
        }
    }

    @Override
    public int getChildWidthMeasureSpec(int widthSpec, int padding, int childDimension) {
        return new UiUtil(getContext()).getChildMeasuredWidthHeight(widthSpec, padding, childDimension);
    }

    @Override
    public int getChildHeightMeasureSpec(int heightSpec, int padding, int childDimension) {
        return new UiUtil(getContext()).getChildMeasuredWidthHeight(heightSpec, padding, childDimension);
    }

    @Override
    public void onNewFlexItemAdded(Component component, int index, int indexInFlexLine, FlexLine flexLine) {
        // Check if the beginning or middle divider is required for the flex item
        if (hasDividerBeforeChildAtAlongMainAxis(index, indexInFlexLine)) {
            if (isMainAxisDirectionHorizontal()) {
                flexLine.mMainSize += mDividerVerticalWidth;
                flexLine.mDividerLengthInMainSize += mDividerVerticalWidth;
            } else {
                flexLine.mMainSize += mDividerHorizontalHeight;
                flexLine.mDividerLengthInMainSize += mDividerHorizontalHeight;
            }
        }
    }

    @Override
    public void setFlexLines(List<FlexLine> flexLines) {
        mFlexLines = flexLines;
    }

    @Override
    public List<FlexLine> getFlexLinesInternal() {
        return mFlexLines;
    }

    @Override
    public void updateViewCache(int position, Component component) {
        // No op
    }

    @Override
    public void refresh() {
        new EventHandler(EventRunner.getMainEventRunner()).postTask(this::postLayout, 50);
    }

    /**
     * Get the horizontal divider element
     * @return the horizontal divider drawable that will divide each item.
     * @see #setDividerDrawable(Element)
     * @see #setDividerDrawableHorizontal(Element)
     */
    @Nullable
    @SuppressWarnings("UnusedDeclaration")
    public Element getDividerDrawableHorizontal() {
        return mDividerDrawableHorizontal;
    }

    /**
     * Get the vertical divider element
     * @return the vertical divider drawable that will divide each item.
     * @see #setDividerDrawable(Element)
     * @see #setDividerDrawableVertical(Element)
     */
    @Nullable
    @SuppressWarnings("UnusedDeclaration")
    public Element getDividerDrawableVertical() {
        return mDividerDrawableVertical;
    }

    /**
     * Set a Element to be used as a divider between items. The Element is used for both
     * horizontal and vertical dividers.
     *
     * @param divider Element that will divide each item for both horizontally and vertically.
     * @see #setShowDivider(DividerMode)
     */
    public void setDividerDrawable(Element divider) {
        setDividerDrawableHorizontal(divider);
        setDividerDrawableVertical(divider);
    }

    /**
     * Set a Element to be used as a horizontal divider between items.
     *
     * @param divider Element that will divide each item.
     * @see #setDividerDrawable(Element)
     * @see #setShowDivider(DividerMode)
     * @see #setShowDividerHorizontal(DividerMode)
     */
    public void setDividerDrawableHorizontal(@Nullable Element divider) {
        if (divider == mDividerDrawableHorizontal) {
            return;
        }
        mDividerDrawableHorizontal = divider;
        if (divider != null) {
            mDividerHorizontalHeight = divider.getHeight();
        } else {
            mDividerHorizontalHeight = 0;
        }
        postLayout();
    }

    /**
     * Set a Element to be used as a vertical divider between items.
     *
     * @param divider Element that will divide each item.
     * @see #setDividerDrawable(Element)
     * @see #setShowDivider(DividerMode)
     * @see #setShowDividerVertical(DividerMode)
     */
    public void setDividerDrawableVertical(@Nullable Element divider) {
        if (divider == mDividerDrawableVertical) {
            return;
        }
        mDividerDrawableVertical = divider;
        if (divider != null) {
            mDividerVerticalWidth = divider.getWidth();
        } else {
            mDividerVerticalWidth = 0;
        }
        postLayout();
    }

    public DividerMode getShowDividerVertical() {
        return mShowDividerVertical;
    }

    public DividerMode getShowDividerHorizontal() {
        return mShowDividerHorizontal;
    }

    /**
     * Set how dividers should be shown between items in this layout. This method sets the
     * divider mode for both horizontally and vertically.
     *
     * @param dividerMode One or more of {@link DividerMode#SHOW_DIVIDER_BEGINNING},
     *                    {@link DividerMode#SHOW_DIVIDER_MIDDLE}, or {@link DividerMode#SHOW_DIVIDER_END},
     *                    or {@link DividerMode#SHOW_DIVIDER_NONE} to show no dividers.
     * @see #setShowDividerVertical(DividerMode)
     * @see #setShowDividerHorizontal(DividerMode)
     */
    public void setShowDivider(DividerMode dividerMode) {
        setShowDividerVertical(dividerMode);
        setShowDividerHorizontal(dividerMode);
    }

    /**
     * Set how vertical dividers should be shown between items in this layout
     *
     * @param dividerMode One or more of {@link DividerMode#SHOW_DIVIDER_BEGINNING},
     *                    {@link DividerMode#SHOW_DIVIDER_MIDDLE}, or {@link DividerMode#SHOW_DIVIDER_END},
     *                    or {@link DividerMode#SHOW_DIVIDER_NONE} to show no dividers.
     * @see #setShowDivider(DividerMode)
     */
    public void setShowDividerVertical(DividerMode dividerMode) {
        if (dividerMode != mShowDividerVertical) {
            mShowDividerVertical = dividerMode;
            postLayout();
        }
    }

    /**
     * Set how horizontal dividers should be shown between items in this layout.
     *
     * @param dividerMode One or more of {@link DividerMode#SHOW_DIVIDER_BEGINNING},
     *                    {@link DividerMode#SHOW_DIVIDER_MIDDLE}, or {@link DividerMode#SHOW_DIVIDER_END},
     *                    or {@link DividerMode#SHOW_DIVIDER_NONE} to show no dividers.
     * @see #setShowDivider(DividerMode)
     */
    public void setShowDividerHorizontal(DividerMode dividerMode) {
        if (dividerMode != mShowDividerHorizontal) {
            mShowDividerHorizontal = dividerMode;
            postLayout();
        }
    }

    /**
     * Check if a divider is needed before the component whose indices are passed as arguments.
     *
     * @param index           the absolute index of the component to be judged
     * @param indexInFlexLine the relative index in the flex line where the component
     *                        belongs
     * @return {@code true} if a divider is needed, {@code false} otherwise
     */
    private boolean hasDividerBeforeChildAtAlongMainAxis(int index, int indexInFlexLine) {
        if (allViewsAreGoneBefore(index, indexInFlexLine)) {
            if (isMainAxisDirectionHorizontal()) {
                return (mShowDividerVertical.getValue() & SHOW_DIVIDER_BEGINNING.getValue()) != 0;
            } else {
                return (mShowDividerHorizontal.getValue() & SHOW_DIVIDER_BEGINNING.getValue()) != 0;
            }
        } else {
            if (isMainAxisDirectionHorizontal()) {
                return (mShowDividerVertical.getValue() & SHOW_DIVIDER_MIDDLE.getValue()) != 0;
            } else {
                return (mShowDividerHorizontal.getValue() & SHOW_DIVIDER_MIDDLE.getValue()) != 0;
            }
        }
    }

    private boolean allViewsAreGoneBefore(int index, int indexInFlexLine) {
        for (int i = 1; i <= indexInFlexLine; i++) {
            Component component = getReorderedChildAt(index - i);
            if (component != null && component.getVisibility() != Component.HIDE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a divider is needed before the flex line whose index is passed as an argument.
     *
     * @param flexLineIndex the index of the flex line to be checked
     * @return {@code true} if a divider is needed, {@code false} otherwise
     */
    private boolean hasDividerBeforeFlexLine(int flexLineIndex) {
        if (flexLineIndex < 0 || flexLineIndex >= mFlexLines.size()) {
            return false;
        }
        if (allFlexLinesAreDummyBefore(flexLineIndex)) {
            if (isMainAxisDirectionHorizontal()) {
                return (mShowDividerHorizontal.getValue() & SHOW_DIVIDER_BEGINNING.getValue()) != 0;
            } else {
                return (mShowDividerVertical.getValue() & SHOW_DIVIDER_BEGINNING.getValue()) != 0;
            }
        } else {
            if (isMainAxisDirectionHorizontal()) {
                return (mShowDividerHorizontal.getValue() & SHOW_DIVIDER_MIDDLE.getValue()) != 0;
            } else {
                return (mShowDividerVertical.getValue() & SHOW_DIVIDER_MIDDLE.getValue()) != 0;
            }
        }
    }

    private boolean allFlexLinesAreDummyBefore(int flexLineIndex) {
        for (int i = 0; i < flexLineIndex; i++) {
            if (mFlexLines.get(i).getItemCountNotGone() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a end divider is needed after the flex line whose index is passed as an argument.
     *
     * @param flexLineIndex the index of the flex line to be checked
     * @return {@code true} if a divider is needed, {@code false} otherwise
     */
    private boolean hasEndDividerAfterFlexLine(int flexLineIndex) {
        if (flexLineIndex < 0 || flexLineIndex >= mFlexLines.size()) {
            return false;
        }

        for (int i = flexLineIndex + 1; i < mFlexLines.size(); i++) {
            if (mFlexLines.get(i).getItemCountNotGone() > 0) {
                return false;
            }
        }
        if (isMainAxisDirectionHorizontal()) {
            return (mShowDividerHorizontal.getValue() & SHOW_DIVIDER_END.getValue()) != 0;
        } else {
            return (mShowDividerVertical.getValue() & SHOW_DIVIDER_END.getValue()) != 0;
        }

    }

    /**
     * Per child parameters for children components of the {@link FlexboxLayout}.
     *
     * Note that some parent fields (which are not primitive nor a class implements
     * {@link Sequenceable}) are not included as the stored/restored fields after this class
     * is serialized/de-serialized as an {@link Sequenceable}.
     */
    public static class LayoutConfig extends ComponentContainer.LayoutConfig implements FlexItem {

        /**
         * @see FlexItem#getOrder()
         */
        private int mOrder = FlexItem.ORDER_DEFAULT;

        /**
         * @see FlexItem#getFlexGrow()
         */
        private float mFlexGrow = FlexItem.FLEX_GROW_DEFAULT;

        /**
         * @see FlexItem#getFlexShrink()
         */
        private float mFlexShrink = FlexItem.FLEX_SHRINK_DEFAULT;

        /**
         * @see FlexItem#getAlignSelf()
         */
        private AlignSelf mAlignSelf = AlignSelf.AUTO;

        /**
         * @see FlexItem#getFlexBasisPercent()
         */
        private float mFlexBasisPercent = FlexItem.FLEX_BASIS_PERCENT_DEFAULT;

        /**
         * @see FlexItem#getMinWidth()
         */
        private int mMinWidth = NOT_SET;

        /**
         * @see FlexItem#getMinHeight()
         */
        private int mMinHeight = NOT_SET;

        /**
         * @see FlexItem#getMaxWidth()
         */
        private int mMaxWidth = MAX_SIZE;

        /**
         * @see FlexItem#getMaxHeight()
         */
        private int mMaxHeight = MAX_SIZE;

        /**
         * @see FlexItem#isWrapBefore()
         */
        private boolean mWrapBefore;

        //custom attributes
        private static final String layout_order = "layout_order";
        private static final String layout_flexGrow = "layout_flexGrow";
        private static final String layout_flexShrink = "layout_flexShrink";
        private static final String layout_alignSelf = "layout_alignSelf";
        private static final String layout_flexBasisPercent = "layout_flexBasisPercent";
        private static final String layout_minWidth = "layout_minWidth";
        private static final String layout_minHeight = "layout_minHeight";
        private static final String layout_maxWidth = "layout_maxWidth";
        private static final String layout_maxHeight = "layout_maxHeight";
        private static final String layout_wrapBefore = "layout_wrapBefore";

        public LayoutConfig(Context context, AttrSet attrSet) {
            super(context, attrSet);
            mOrder = attrSet.getAttr(layout_order).isPresent() ?
                    attrSet.getAttr(layout_order).get().getIntegerValue() : ORDER_DEFAULT;
            mFlexGrow = attrSet.getAttr(layout_flexGrow).isPresent() ?
                    attrSet.getAttr(layout_flexGrow).get().getFloatValue() : FLEX_GROW_DEFAULT;
            mFlexShrink = attrSet.getAttr(layout_flexShrink).isPresent() ?
                    attrSet.getAttr(layout_flexShrink).get().getFloatValue() : FLEX_SHRINK_DEFAULT;
            mAlignSelf = AlignSelf.findByValue(attrSet.getAttr(layout_alignSelf).isPresent() ?
                    attrSet.getAttr(layout_alignSelf).get().getIntegerValue() : AlignSelf.AUTO.getValue());
            mFlexBasisPercent = attrSet.getAttr(layout_flexBasisPercent).isPresent() ?
                    attrSet.getAttr(layout_flexBasisPercent).get().getFloatValue() : FLEX_BASIS_PERCENT_DEFAULT;
            mMinWidth = attrSet.getAttr(layout_minWidth).isPresent() ?
                    attrSet.getAttr(layout_minWidth).get().getDimensionValue() : NOT_SET;
            mMinHeight = attrSet.getAttr(layout_minHeight).isPresent() ?
                    attrSet.getAttr(layout_minHeight).get().getDimensionValue() : NOT_SET;
            mMaxWidth = attrSet.getAttr(layout_maxWidth).isPresent() ?
                    attrSet.getAttr(layout_maxWidth).get().getDimensionValue() : MAX_SIZE;
            mMaxHeight = attrSet.getAttr(layout_maxHeight).isPresent() ?
                    attrSet.getAttr(layout_maxHeight).get().getDimensionValue() : MAX_SIZE;
            mWrapBefore = attrSet.getAttr(layout_wrapBefore).isPresent() && attrSet.getAttr(layout_wrapBefore).get().getBoolValue();
        }

        public LayoutConfig(LayoutConfig source) {
            super(source);

            mOrder = source.mOrder;
            mFlexGrow = source.mFlexGrow;
            mFlexShrink = source.mFlexShrink;
            mAlignSelf = source.mAlignSelf;
            mFlexBasisPercent = source.mFlexBasisPercent;
            mMinWidth = source.mMinWidth;
            mMinHeight = source.mMinHeight;
            mMaxWidth = source.mMaxWidth;
            mMaxHeight = source.mMaxHeight;
            mWrapBefore = source.mWrapBefore;
        }

        public LayoutConfig(ComponentContainer.LayoutConfig source) {
            super(source);
        }

        public LayoutConfig(int width, int height) {
            super(new ComponentContainer.LayoutConfig(width, height));
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public void setWidth(int width) {
            this.width = width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public int getOrder() {
            return mOrder;
        }

        @Override
        public void setOrder(int order) {
            mOrder = order;
        }

        @Override
        public float getFlexGrow() {
            return mFlexGrow;
        }

        @Override
        public void setFlexGrow(float flexGrow) {
            this.mFlexGrow = flexGrow;
        }

        @Override
        public float getFlexShrink() {
            return mFlexShrink;
        }

        @Override
        public void setFlexShrink(float flexShrink) {
            this.mFlexShrink = flexShrink;
        }

        @Override
        public AlignSelf getAlignSelf() {
            return mAlignSelf;
        }

        @Override
        public void setAlignSelf(AlignSelf alignSelf) {
            this.mAlignSelf = alignSelf;
        }

        @Override
        public int getMinWidth() {
            return mMinWidth;
        }

        @Override
        public void setMinWidth(int minWidth) {
            this.mMinWidth = minWidth;
        }

        @Override
        public int getMinHeight() {
            return mMinHeight;
        }

        @Override
        public void setMinHeight(int minHeight) {
            this.mMinHeight = minHeight;
        }

        @Override
        public int getMaxWidth() {
            return mMaxWidth;
        }

        @Override
        public void setMaxWidth(int maxWidth) {
            this.mMaxWidth = maxWidth;
        }

        @Override
        public int getMaxHeight() {
            return mMaxHeight;
        }

        @Override
        public void setMaxHeight(int maxHeight) {
            this.mMaxHeight = maxHeight;
        }

        @Override
        public boolean isWrapBefore() {
            return mWrapBefore;
        }

        @Override
        public void setWrapBefore(boolean wrapBefore) {
            this.mWrapBefore = wrapBefore;
        }

        @Override
        public float getFlexBasisPercent() {
            return mFlexBasisPercent;
        }

        @Override
        public void setFlexBasisPercent(float flexBasisPercent) {
            this.mFlexBasisPercent = flexBasisPercent;
        }

        @Override
        public int getMarginStart() {
            return super.getHorizontalStartMargin();
        }

        @Override
        public int getMarginEnd() {
            return super.getHorizontalEndMargin();
        }

        @Override
        public boolean marshalling(Parcel dest) {
            dest.writeInt(this.mOrder);
            dest.writeFloat(this.mFlexGrow);
            dest.writeFloat(this.mFlexShrink);
            dest.writeInt(this.mAlignSelf.getValue());
            dest.writeFloat(this.mFlexBasisPercent);
            dest.writeInt(this.mMinWidth);
            dest.writeInt(this.mMinHeight);
            dest.writeInt(this.mMaxWidth);
            dest.writeInt(this.mMaxHeight);
            dest.writeByte(this.mWrapBefore ? (byte) 1 : (byte) 0);
            dest.writeInt(this.getMarginBottom());
            dest.writeInt(this.getMarginLeft());
            dest.writeInt(this.getMarginRight());
            dest.writeInt(this.getMarginTop());
            dest.writeInt(this.height);
            dest.writeInt(this.width);
            return true;
        }

        @Override
        public boolean unmarshalling(Parcel in) {
            // Passing a resolved value to resolve a lint warning
            // height and width are set in this method anyway.
            this.mOrder = in.readInt();
            this.mFlexGrow = in.readFloat();
            this.mFlexShrink = in.readFloat();
            this.mAlignSelf = AlignSelf.findByValue(in.readInt());
            this.mFlexBasisPercent = in.readFloat();
            this.mMinWidth = in.readInt();
            this.mMinHeight = in.readInt();
            this.mMaxWidth = in.readInt();
            this.mMaxHeight = in.readInt();
            this.mWrapBefore = in.readByte() != 0;
            this.setMarginBottom(in.readInt());
            this.setMarginLeft(in.readInt());
            this.setMarginRight(in.readInt());
            this.setMarginTop(in.readInt());
            this.height = in.readInt();
            this.width = in.readInt();
            return true;
        }

        public static final Sequenceable.Producer<LayoutConfig> CREATOR
                = new Sequenceable.Producer<LayoutConfig>() {
            @Override
            public LayoutConfig createFromParcel(Parcel source) {
                // Initialize an instance first, then do customized unmarshlling.
                LayoutConfig instance = new LayoutConfig(0, 0);
                instance.unmarshalling(source);
                return instance;
            }
        };
    }
}
