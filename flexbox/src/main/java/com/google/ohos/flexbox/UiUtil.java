/* * Copyright (C) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ohos.flexbox;

import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.app.Context;
import ohos.interwork.utils.ParcelableEx;
import ohos.utils.Parcel;
import ohos.utils.ParcelException;

public class UiUtil extends ComponentContainer {

    public UiUtil(Context context) {
        super(context);
    }

    /**
     * Ask one of the children of this view to measure itself, taking into account both the EstimateSpec requirements for this view and its padding.
     *
     * @param component         The child to measure
     * @param widthMeasureSpec  The width requirements for this view
     * @param heightMeasureSpec The height requirements for this view
     */
    public void measureChild(Component component, int widthMeasureSpec, int heightMeasureSpec) {
        final LayoutConfig layoutConfig = component.getLayoutConfig();
        int measuredWidth = getChildMeasuredWidthHeight(widthMeasureSpec, layoutConfig.getMarginLeft() + layoutConfig.getMarginRight(), layoutConfig.width);
        int measuredHeight = getChildMeasuredWidthHeight(heightMeasureSpec, layoutConfig.getMarginLeft() + layoutConfig.getMarginRight(), layoutConfig.height);
        measure(component, measuredWidth, measuredHeight);
    }

    /**
     * This method figures out the right EstimateSpec for one dimension (height or width) of one child view.
     *
     * @param widthHeightSpec The requirements for this view
     * @param padding         The padding of this view for the current dimension and margins, if applicable
     * @param childDimension  How big the child wants to be in the current dimension
     * @return a EstimateSpec integer for the child
     */
    public int getChildMeasuredWidthHeight(int widthHeightSpec, int padding, int childDimension) {
        int specMode = EstimateSpec.getMode(widthHeightSpec);
        int specSize = EstimateSpec.getSize(widthHeightSpec);
        int size = Math.max(0, specSize - padding);

        int resultSize = 0;
        int resultMode = 0;

        switch (specMode) {
            // Parent has imposed an exact size on us
            case EstimateSpec.PRECISE:
                if (childDimension >= 0) {
                    resultSize = childDimension;
                    resultMode = EstimateSpec.PRECISE;
                } else if (childDimension == LayoutConfig.MATCH_PARENT) {
                    // Child wants to be our size. So be it.
                    resultSize = size;
                    resultMode = EstimateSpec.PRECISE;
                } else if (childDimension == LayoutConfig.MATCH_CONTENT) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size;
                    resultMode = EstimateSpec.NOT_EXCEED;
                }
                break;

            // Parent has imposed a maximum size on us
            case EstimateSpec.NOT_EXCEED:
                if (childDimension >= 0) {
                    // Child wants a specific size... so be it
                    resultSize = childDimension;
                    resultMode = EstimateSpec.PRECISE;
                } else if (childDimension == LayoutConfig.MATCH_PARENT) {
                    // Child wants to be our size, but our size is not fixed.
                    // Constrain child to not be bigger than us.
                    resultSize = size;
                    resultMode = EstimateSpec.NOT_EXCEED;
                } else if (childDimension == LayoutConfig.MATCH_CONTENT) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size;
                    resultMode = EstimateSpec.NOT_EXCEED;
                }
                break;

            // Parent asked to see how big we want to be
            case EstimateSpec.UNCONSTRAINT:
                if (childDimension >= 0) {
                    // Child wants a specific size... let him have it
                    resultSize = childDimension;
                    resultMode = EstimateSpec.PRECISE;
                } else if (childDimension == LayoutConfig.MATCH_PARENT) {
                    // Child wants to be our size... find out how big it should
                    // be
                    resultSize = size;
                    resultMode = EstimateSpec.UNCONSTRAINT;
                } else if (childDimension == LayoutConfig.MATCH_CONTENT) {
                    // Child wants to determine its own size.... find out how
                    // big it should be
                    resultSize = size;
                    resultMode = EstimateSpec.UNCONSTRAINT;
                }
                break;
        }
        //noinspection ResourceType
        return EstimateSpec.getSizeWithMode(resultSize, resultMode);
    }

    /**
     * This is called to find out how big a view should be. The parent supplies constraint information in the width and height parameters.
     *
     * @param component         The child to measure
     * @param widthMeasureSpec  Horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec Vertical space requirements as imposed by the parent
     */
    public void measure(Component component, int widthMeasureSpec, int heightMeasureSpec) {
        Insets insets = new Insets(0, 0, 0, 0);
        int oWidth = insets.left + insets.right;
        int oHeight = insets.top + insets.bottom;
        widthMeasureSpec = widthMeasureSpec + oWidth;
        heightMeasureSpec = heightMeasureSpec + oHeight;
        component.estimateSize(widthMeasureSpec, heightMeasureSpec);
    }

    public int getMeasuredState(Component component) {
        return (component.getWidth() & 0xff000000)
                | ((component.getHeight() >> 16)
                & (0xff000000 >> 16));
    }

    /**
     * Utility to reconcile a desired size and state, with constraints imposed
     * by a EstimateSpec. Will take the desired size, unless a different size
     * is imposed by the constraints.
     *
     * @param size How big the view wants to be.
     * @param measureSpec Constraints imposed by the parent.
     * @param childMeasuredState Size information bit mask for the view's
     *                           children.
     * @return Size information bit mask
     */
    public int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        final int specMode = EstimateSpec.getMode(measureSpec);
        final int specSize = EstimateSpec.getSize(measureSpec);
        final int result;
        switch (specMode) {
            case EstimateSpec.NOT_EXCEED:
                if (specSize < size) {
                    result = specSize | 0x01000000;
                } else {
                    result = size;
                }
                break;
            case EstimateSpec.PRECISE:
                result = specSize;
                break;
            case EstimateSpec.UNCONSTRAINT:
            default:
                result = size;
        }
        return result | (childMeasuredState & 0xff000000);
    }

    public int getMeasureSpec(int value, int size) {
        if (value == LayoutConfig.MATCH_PARENT) {
            return EstimateSpec.getSizeWithMode(size, EstimateSpec.PRECISE);
        } else if (value == LayoutConfig.MATCH_CONTENT) {
            return EstimateSpec.getSizeWithMode(size, EstimateSpec.NOT_EXCEED);
        } else {
            return value;
        }
    }

    private class Insets implements ParcelableEx {
        final int left;
        final int top;
        final int right;
        final int bottom;

        private Insets(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        @Override
        public void marshallingEx(Parcel out) throws ParcelException {
            out.writeInt(left);
            out.writeInt(top);
            out.writeInt(right);
            out.writeInt(bottom);
        }

        @Override
        public void unmarshallingEx(Parcel in) throws ParcelException {

        }
    }
}
