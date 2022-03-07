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

/** This attribute controls the alignment of the flex lines in the flex container. */
public enum AlignContent {
    /** Flex lines are packed to the start of the flex container. */
    FLEX_START(0),

    /** Flex lines are packed to the end of the flex container. */
    FLEX_END(1),

    /** Flex lines are centered in the flex container. */
    CENTER(2),

    /**
     * Flex lines are evenly distributed in the flex container. The first flex line is
     * placed at the start of the flex container, the last flex line is placed at the
     * end of the flex container.
     */
    SPACE_BETWEEN(3),

    /**
     * Flex lines are evenly distributed in the flex container with the same amount of spaces
     * between the flex lines.
     */
    SPACE_AROUND(4),

    /**
     * Flex lines are stretched to fill the remaining space along the cross axis.
     */
    STRETCH(5);

    private final int value;

    AlignContent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AlignContent findByValue(int intValue){
        for(AlignContent v : values()){
            if( v.getValue() == intValue){
                return v;
            }
        }
        return FLEX_START;
    }
}
