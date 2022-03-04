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

import ohos.agp.components.Component;

import java.util.List;

/**
 * An interface that has the common behavior as the flex container such as {@link FlexboxLayout}
 */
public interface FlexContainer {
    int NOT_SET = -1;

    /**
     * Method to get the total flex items in flex container
     *
     * @return the number of flex items contained in the flex container.
     */
    int getFlexItemCount();

    /**
     * Returns a flex item as a Component at the given index.
     *
     * @param index the index
     * @return the component at the index
     */
    Component getFlexItemAt(int index);

    /**
     * Returns a flex item as a Component, which is reordered by taking the order attribute into
     * account.
     *
     * @param index the index of the component
     * @return the reordered component, which order attribute is taken into account.
     * If the index is negative or out of bounds of the number of contained components,
     * returns {@code null}.
     * @see FlexItem#getOrder()
     */
    Component getReorderedFlexItemAt(int index);

    /**
     * Adds the component to the flex container as a flex item.
     *
     * @param component the component to be added
     */
    void addComponent(Component component);

    /**
     * Adds the component to the specified index of the flex container.
     *
     * @param component  the component to be added
     * @param index the index for the component to be added
     */
    void addComponent(Component component, int index);

    /**
     * Removes all the components contained in the flex container.
     */
    void removeAllComponents();

    /**
     * Removes the component at the specified index.
     *
     * @param index the index from which the component is removed.
     */
    void removeComponentAt(int index);

    /**
     * Get the configured flex direction value
     * @return the flex direction attribute of the flex container.
     * @see FlexDirection
     */
    FlexDirection getFlexDirection();

    /**
     * Sets the given flex direction attribute to the flex container.
     *
     * @param flexDirection the flex direction value
     * @see FlexDirection
     */
    void setFlexDirection(FlexDirection flexDirection);

    /**
     * Get the configured flex wrap value
     * @return the flex wrap attribute of the flex container.
     * @see FlexWrap
     */
    FlexWrap getFlexWrap();

    /**
     * Sets the given flex wrap attribute to the flex container.
     *
     * @param flexWrap the flex wrap value
     * @see FlexWrap
     */
    void setFlexWrap(FlexWrap flexWrap);

    /**
     * Get the configured justify content value
     * @return the justify content attribute of the flex container.
     * @see JustifyContent
     */
    JustifyContent getJustifyContent();

    /**
     * Sets the given justify content attribute to the flex container.
     *
     * @param justifyContent the justify content value
     * @see JustifyContent
     */
    void setJustifyContent(JustifyContent justifyContent);

    /**
     * Get the configured align content value
     * @return the align content attribute of the flex container.
     * @see AlignContent
     */
    AlignContent getAlignContent();

    /**
     * Sets the given align content attribute to the flex container.
     *
     * @param alignContent the align content value
     */
    void setAlignContent(AlignContent alignContent);

    /**
     * Get the configured align items value
     * @return the align items attribute of the flex container.
     * @see AlignItems
     */
    AlignItems getAlignItems();

    /**
     * Sets the given align items attribute to the flex container.
     *
     * @param alignItems the align items value
     * @see AlignItems
     */
    void setAlignItems(AlignItems alignItems);

    /**
     * Get the available flex lines
     * @return the flex lines composing this flex container. The overridden method should return a
     * copy of the original list excluding a dummy flex line (flex line that doesn't have any flex
     * items in it but used for the alignment along the cross axis) so that any changes of the
     * returned list are not reflected to the original list.
     */
    List<FlexLine> getFlexLines();

    /**
     * Returns true if the main axis is horizontal, false otherwise.
     *
     * @return true if the main axis is horizontal, false otherwise
     */
    boolean isMainAxisDirectionHorizontal();

    /**
     * Returns the length of decoration (such as dividers) of the flex item along the main axis.
     *
     * @param component            the component from which the length of the decoration is retrieved
     * @param index           the absolute index of the flex item within the flex container
     * @param indexInFlexLine the relative index of the flex item within the flex line
     * @return the length of the decoration. Note that the length of the flex item itself is not
     * included in the result.
     */
    int getDecorationLengthMainAxis(Component component, int index, int indexInFlexLine);

    /**
     * Returns the length of decoration (such as dividers) of the flex item along the cross axis.
     *
     * @param component the component from which the length of the decoration is retrieved
     * @return the length of the decoration. Note that the length of the flex item itself is not
     * included in the result.
     */
    int getDecorationLengthCrossAxis(Component component);

    /**
     * Get the configured top padding
     * @return the top padding of the flex container.
     */
    int getPaddingTop();

    /**
     * Get the configured left padding
     * @return the left padding of the flex container.
     */
    int getPaddingLeft();

    /**
     * Get the configured right padding
     * @return the right padding of the flex container.
     */
    int getPaddingRight();

    /**
     * Get the configured bottom padding
     * @return the bottom padding of the flex container.
     */
    int getPaddingBottom();

    /**
     * Get the configured start padding
     * @return the start padding of this component depending on its resolved layout direction.
     */
    int getPaddingStart();

    /**
     * Get the configured end padding
     * @return the end padding of this component depending on its resolved layout direction.
     */
    int getPaddingEnd();

    /**
     * Returns the child measure spec for its width.
     *
     * @param widthSpec      the measure spec for the width imposed by the parent
     * @param padding        the padding along the width for the parent
     * @param childDimension the value of the child dimension
     *
     * @return the child measure spec for its width.
     */
    int getChildWidthMeasureSpec(int widthSpec, int padding, int childDimension);

    /**
     * Returns the child measure spec for its height.
     *
     * @param heightSpec     the measure spec for the height imposed by the parent
     * @param padding        the padding along the height for the parent
     * @param childDimension the value of the child dimension
     *
     * @return the child measure spec for its height.
     */
    int getChildHeightMeasureSpec(int heightSpec, int padding, int childDimension);

    /**
     * Get the largest main sides size
     * @return the largest main size of all flex lines including decorator lengths.
     */
    int getLargestMainSize();

    /**
     * Get the sum of crossed / opposite sides
     * @return the sum of the cross sizes of all flex lines including decorator lengths.
     */
    int getSumOfCrossSize();

    /**
     * Callback when a new flex item is added to the current container
     *
     * @param component            the component as a flex item which is added
     * @param index           the absolute index of the flex item added
     * @param indexInFlexLine the relative index of the flex item added within the flex line
     * @param flexLine        the flex line where the new flex item is added
     */
    void onNewFlexItemAdded(Component component, int index, int indexInFlexLine, FlexLine flexLine);

    /**
     * Callback when a new flex line is added to the current container
     *
     * @param flexLine the new added flex line
     */
    void onNewFlexLineAdded(FlexLine flexLine);

    /**
     * Sets the list of the flex lines that compose the flex container to the one received as an
     * argument.
     *
     * @param flexLines the list of flex lines
     */
    void setFlexLines(List<FlexLine> flexLines);

    /**
     * Get the configured maximum line
     * @return the current value of the maximum number of flex lines. If not set, {@link #NOT_SET}
     * is returned.
     */
    int getMaxLine();

    /**
     * Set the max line parameter
     * @param maxLine the int value, which specifies the maximum number of flex lines
     */
    void setMaxLine(int maxLine);

    /**
     * Get the calculated flex lines for the items available in container
     * @return the list of the flex lines including dummy flex lines (flex line that doesn't have
     * any flex items in it but used for the alignment along the cross axis), which aren't included
     * in the {@link FlexContainer#getFlexLines()}.
     */
    List<FlexLine> getFlexLinesInternal();

    /**
     * Update the component cache in the flex container.
     *
     * @param position the position of the component to be updated
     * @param component     the component instance
     */
    void updateViewCache(int position, Component component);

    /**
     * Update the view with refreshed views
     */
    void refresh();
}
