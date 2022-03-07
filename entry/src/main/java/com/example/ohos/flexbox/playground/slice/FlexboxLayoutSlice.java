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

import com.example.ohos.flexbox.ResourceTable;
import com.example.ohos.flexbox.playground.util.ResUtil;
import com.google.ohos.flexbox.AlignContent;
import com.google.ohos.flexbox.AlignItems;
import com.google.ohos.flexbox.FlexDirection;
import com.google.ohos.flexbox.FlexWrap;
import com.google.ohos.flexbox.FlexboxLayout;
import com.google.ohos.flexbox.JustifyContent;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.Button;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.Text;
import ohos.agp.utils.TextAlignment;
import ohos.app.Context;

public class FlexboxLayoutSlice extends AbilitySlice {
    private FlexboxLayout flexLayout;
    private SliceHelper sliceHelper;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_flexbox_layout);
        IntentParams params = intent.getParams();
        flexLayout = ((FlexboxLayout) findComponentById(ResourceTable.Id_flexbox));
        sliceHelper = new SliceHelper(this, flexLayout);
        updateLayout(params);
        Button addButton = (Button) findComponentById(ResourceTable.Id_btn_add);
        addButton.setClickedListener(component -> {
            int viewIndex = flexLayout.getFlexItemCount();
            // index starts from 0. New View's index is N if N views ([0, 1, 2, ... N-1])
            // exist.
            Text textComponent = createBaseFlexItemTextView(FlexboxLayoutSlice.this, viewIndex);
            FlexboxLayout.LayoutConfig lp = new FlexboxLayout.LayoutConfig(
                    ComponentContainer.LayoutConfig.MATCH_CONTENT,
                    ComponentContainer.LayoutConfig.MATCH_CONTENT);
            sliceHelper.setFlexItemAttributes(lp);
            textComponent.setLayoutConfig(lp);
            textComponent.setClickedListener(new FlexItemClickListener(FlexboxLayoutSlice.this,
                    new FlexItemChangedListenerImpl(flexLayout), viewIndex));
            flexLayout.addComponent(textComponent);
            flexLayout.refresh();
        });
        Button removeButton = (Button) findComponentById(ResourceTable.Id_btn_remove);
        removeButton.setClickedListener(component -> {
            if (flexLayout.getFlexItemCount() == 0) {
                return;
            }
            flexLayout.removeComponentAt(flexLayout.getFlexItemCount() - 1);
        });
    }

    private void updateLayout(IntentParams params) {
        flexLayout.setFlexDirection((FlexDirection) params.getParam(FlexDirection.class.getSimpleName()));
        flexLayout.setFlexWrap((FlexWrap) params.getParam(FlexWrap.class.getSimpleName()));
        flexLayout.setJustifyContent((JustifyContent) params.getParam(JustifyContent.class.getSimpleName()));
        flexLayout.setAlignItems((AlignItems) params.getParam(AlignItems.class.getSimpleName()));
        flexLayout.setAlignContent((AlignContent) params.getParam(AlignContent.class.getSimpleName()));

        for (int i = 0; i < flexLayout.getFlexItemCount(); i++) {
            flexLayout.getFlexItemAt(i).setClickedListener(
                    new FlexItemClickListener(this, new FlexItemChangedListenerImpl(flexLayout), i)
            );
        }
        flexLayout.refresh();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
        updateLayout(intent.getParams());
    }

    private Text createBaseFlexItemTextView(Context context, int index) {
        Text textView = new Text(context);
        textView.setBackground(ResUtil.getShapeElement(this, ResourceTable.Graphic_flex_item_background));
        textView.setText(String.valueOf(index + 1));
        textView.setTextSize(Constants.DEFAULT_TEXT_SIZE_IN_FP, Text.TextSizeType.FP);
        textView.setTextAlignment(TextAlignment.CENTER);
        return textView;
    }
}
