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

/** This attribute controls the alignment along the cross axis. */
public enum AlignItems {
    /** Flex item's edge is placed on the cross start line. */
    FLEX_START(0),

    /** Flex item's edge is placed on the cross end line. */
    FLEX_END(1),

    /** Flex item's edge is centered along the cross axis. */
    CENTER(2),

    /** Flex items are aligned based on their text's baselines. */
    BASELINE(3),

    /** Flex items are stretched to fill the flex line's cross size. */
    STRETCH(4);

    private final int value;

    AlignItems(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AlignItems findByValue(int intValue){
        for(AlignItems v : values()){
            if( v.getValue() == intValue){
                return v;
            }
        }
        return FLEX_START;
    }
}
