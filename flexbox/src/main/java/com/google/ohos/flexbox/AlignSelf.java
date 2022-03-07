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

/**
 * This attribute controls the alignment along the cross axis.
 * The alignment in the same direction can be determined by the {@link AlignItems} attribute in the
 * parent, but if this is set to other than {@link AlignSelf#AUTO},
 * the cross axis alignment is overridden for this child.
 */
public enum AlignSelf {
    /**
     * The default value for the AlignSelf attribute, which means use the inherit
     * the {@link AlignItems} attribute from its parent.
     */
    AUTO(-1),

    /** This item's edge is placed on the cross start line. */
    FLEX_START(AlignItems.FLEX_START.getValue()),

    /** This item's edge is placed on the cross end line. */
    FLEX_END(AlignItems.FLEX_END.getValue()),

    /** This item's edge is centered along the cross axis. */
    CENTER(AlignItems.CENTER.getValue()),

    /** This items is aligned based on their text's baselines. */
    BASELINE(AlignItems.BASELINE.getValue()),

    /** This item is stretched to fill the flex line's cross size. */
    STRETCH(AlignItems.STRETCH.getValue());

    private final int value;

    AlignSelf(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AlignSelf findByValue(int intValue){
        for(AlignSelf v : values()){
            if( v.getValue() == intValue){
                return v;
            }
        }
        return AUTO;
    }
}
