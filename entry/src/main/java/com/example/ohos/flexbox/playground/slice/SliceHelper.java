/*
 * Copyright 2017 Google Inc. All rights reserved.
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

package com.example.ohos.flexbox.playground.slice;

import com.google.ohos.flexbox.*;
import com.example.ohos.flexbox.playground.util.*;

import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;

/**
 * Helper class that has the common logic for initializing the AbilitySlice for the play ground demo
 * such as [FlexboxLayoutSlice]
 */
public class SliceHelper {
    private Context context;
    private FlexContainer flexContainer;

    private Preferences preferences;

    public SliceHelper(Context context, FlexContainer flexContainer) {
        this.context = context;
        this.flexContainer = flexContainer;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        preferences = databaseHelper.getPreferences(Constants.PREFERENCE_NAME);
    }

    /**
     * Sets the attributes for a [FlexItem] based on the stored default values in
     * the SharedPreferences.

     * @param flexItem the FlexItem instance
     * *
     * @return a FlexItem instance, which attributes from the SharedPreferences are updated
     */
    FlexItem setFlexItemAttributes(FlexItem flexItem) {
        flexItem.setWidth(ResUtil.vpToPixel(context, readPreferenceAsInteger(Constants.newWidthKey, Constants.DEFAULT_WIDTH)));
        flexItem.setHeight(ResUtil.vpToPixel(context, readPreferenceAsInteger(Constants.newHeightKey, Constants.DEFAULT_HEIGHT)));
        flexItem.setOrder(readPreferenceAsInteger(Constants.newFlexItemOrderKey, 1));
        flexItem.setFlexGrow(readPreferenceAsFloat(Constants.newFlexGrowKey, 0.0f));
        flexItem.setFlexShrink(readPreferenceAsFloat(Constants.newFlexShrinkKey, 1.0f));
        int flexBasisPercent = readPreferenceAsInteger(Constants.newFlexBasisPercentKey, -1);
        if (flexBasisPercent == -1) {
            flexItem.setFlexBasisPercent(-1f);
        } else {
            flexItem.setFlexBasisPercent((float) (flexBasisPercent / 100.0));
        }
        return flexItem;
    }

    private int readPreferenceAsInteger(String key, int defValue) {
        if (preferences.hasKey(key)) {
            return preferences.getInt(key, defValue);
        } else {
            return defValue;
        }
    }

    private float readPreferenceAsFloat(String key, float defValue) {
        if (preferences.hasKey(key)) {
            return preferences.getFloat(key, defValue);
        } else {
            return defValue;
        }
    }
}
