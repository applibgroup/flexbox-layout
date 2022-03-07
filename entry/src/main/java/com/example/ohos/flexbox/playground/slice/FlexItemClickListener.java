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

import com.google.ohos.flexbox.FlexItem;

import ohos.agp.components.Component;
import ohos.app.Context;

/**
 * Click listener for a single flex item
 */
public class FlexItemClickListener implements Component.ClickedListener {
    private Context context;
    private FlexItemChangedListener flexItemChangedListener;
    private int viewIndex;

    public FlexItemClickListener(Context context, FlexItemChangedListener flexItemChangedListener, int viewIndex) {
        this.context = context;
        this.flexItemChangedListener = flexItemChangedListener;
        this.viewIndex = viewIndex;
    }

    @Override
    public void onClick(Component component) {
        FlexItemEditDialog flexItemEditDialog = new FlexItemEditDialog(context, (FlexItem) component.getLayoutConfig(), viewIndex, flexItemChangedListener);
        flexItemEditDialog.show();
    }

}
