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

/** This attribute controls the alignment along the main axis. */
public enum JustifyContent {
    /** Flex items are packed toward the start line. */
    FLEX_START(0),

    /** Flex items are packed toward the end line. */
    FLEX_END(1),

    /** Flex items are centered along the flex line where the flex items belong. */
    CENTER(2),

    /**
     * Flex items are evenly distributed along the flex line, first flex item is on the
     * start line, the last flex item is on the end line.
     */
    SPACE_BETWEEN(3),

    /**
     * Flex items are evenly distributed along the flex line with the same amount of spaces between
     * the flex lines.
     */
    SPACE_AROUND(4),

    /**
     * Flex items are evenly distributed along the flex line. The difference between
     * {@link #SPACE_AROUND} is that all the spaces between items should be the same as the
     * space before the first item and after the last item.
     * See
     * <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/justify-content">the document on MDN</a>
     * for more details.
     */
    SPACE_EVENLY(5);

    private final int value;

    JustifyContent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static JustifyContent findByValue(int intValue){
        for(JustifyContent v : values()){
            if( v.getValue() == intValue){
                return v;
            }
        }
        return FLEX_START;
    }
}
