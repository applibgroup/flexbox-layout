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

import com.google.ohos.flexbox.FlexContainer;
import com.google.ohos.flexbox.FlexItem;

import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;

/**
 * Default implementation for the [FlexItemChangedListener].
 */
public class FlexItemChangedListenerImpl implements FlexItemChangedListener {
    private final FlexContainer flexContainer;

    /**
     * Interface method called when a flex item configuration is changed or updated
     *
     * @param flexItem corresponding item
     * @param viewIndex view index in FlexContainer
     */
    public void onFlexItemChanged(FlexItem flexItem, int viewIndex) {
        Component view = this.flexContainer.getFlexItemAt(viewIndex);
        view.setLayoutConfig((ComponentContainer.LayoutConfig) flexItem);
        flexContainer.refresh();
    }

    FlexItemChangedListenerImpl(FlexContainer flexContainer) {
        this.flexContainer = flexContainer;
    }
}
