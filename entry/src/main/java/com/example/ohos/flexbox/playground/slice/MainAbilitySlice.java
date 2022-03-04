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

import java.util.ArrayList;
import java.util.List;

import com.example.ohos.flexbox.ResourceTable;

import com.google.ohos.flexbox.AlignContent;
import com.google.ohos.flexbox.AlignItems;
import com.google.ohos.flexbox.FlexDirection;
import com.google.ohos.flexbox.FlexWrap;
import com.google.ohos.flexbox.JustifyContent;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.Text;
import ohos.agp.window.dialog.ListDialog;

public class MainAbilitySlice extends AbilitySlice {
    private FlexDirection selectedFlexDirection = FlexDirection.ROW;
    private FlexWrap selectedFlexWrap = FlexWrap.NOWRAP;
    private JustifyContent selectedJustifyContent = JustifyContent.FLEX_START;
    private AlignItems selectedAlignItems = AlignItems.FLEX_START;
    private AlignContent selectedAlignContent = AlignContent.FLEX_START;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        Text btnViewGroup = (Text) findComponentById(ResourceTable.Id_btn_view_group);
        Text btnSettings = ((Text)findComponentById(ResourceTable.Id_btn_settings));
        btnViewGroup.setClickedListener(component -> {
            Intent sliceIntent = new Intent();
            sliceIntent.addFlags(Intent.FLAG_ABILITY_NEW_MISSION);
            IntentParams params = new IntentParams();
            params.setParam(FlexDirection.class.getSimpleName(), selectedFlexDirection);
            params.setParam(FlexWrap.class.getSimpleName(), selectedFlexWrap);
            params.setParam(JustifyContent.class.getSimpleName(), selectedJustifyContent);
            params.setParam(AlignItems.class.getSimpleName(), selectedAlignItems);
            params.setParam(AlignContent.class.getSimpleName(), selectedAlignContent);
            sliceIntent.setParams(params);
            present(new FlexboxLayoutSlice(), sliceIntent);
        });
        btnSettings.setClickedListener(component -> {
            Intent sliceIntent = new Intent();
            sliceIntent.addFlags(Intent.FLAG_ABILITY_NEW_MISSION);
            present(new SettingsAbilitySlice(), sliceIntent);
        });
        setFlexDirectionView();
        setFlexWrapView();
        setJustifyContentView();
        setAlignItemsView();
        setAlignContentView();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    private void setFlexDirectionView() {
        Text flexDirectionLabel = (Text) findComponentById(ResourceTable.Id_flex_direction_label);
        flexDirectionLabel.setText(FlexDirection.class.getSimpleName());
        Text flexDirectionValue = (Text) findComponentById(ResourceTable.Id_flex_direction_value);
        flexDirectionValue.setText(selectedFlexDirection.name());
        List<String> valueNames = new ArrayList<>();
        for (FlexDirection value: FlexDirection.values()) {
            valueNames.add(value.name());
        }
        flexDirectionValue.setClickedListener((view) -> {
            ListDialog listDialog = new ListDialog(MainAbilitySlice.this);
            listDialog.setItems(valueNames.toArray(new String[0]));
            listDialog.setOnSingleSelectListener((iDialog, i) -> {
                selectedFlexDirection = FlexDirection.valueOf(valueNames.get(i));
                flexDirectionValue.setText(selectedFlexDirection.name());
                iDialog.hide();
            });
            listDialog.show();
        });
    }

    private void setFlexWrapView() {
        Text flexWrapLabel = (Text) findComponentById(ResourceTable.Id_flex_wrap_label);
        flexWrapLabel.setText(FlexWrap.class.getSimpleName());
        Text flexWrapValue = (Text) findComponentById(ResourceTable.Id_flex_wrap_value);
        flexWrapValue.setText(selectedFlexWrap.name());
        List<String> valueNames = new ArrayList<>();
        for (FlexWrap value: FlexWrap.values()) {
            valueNames.add(value.name());
        }
        flexWrapValue.setClickedListener((view) -> {
            ListDialog listDialog = new ListDialog(MainAbilitySlice.this);
            listDialog.setItems(valueNames.toArray(new String[0]));
            listDialog.setOnSingleSelectListener((iDialog, i) -> {
                selectedFlexWrap = FlexWrap.valueOf(valueNames.get(i));
                flexWrapValue.setText(selectedFlexWrap.name());
                iDialog.hide();
            });
            listDialog.show();
        });
    }

    private void setJustifyContentView() {
        Text justifyContentLabel = (Text) findComponentById(ResourceTable.Id_justify_content_label);
        justifyContentLabel.setText(JustifyContent.class.getSimpleName());
        Text justifyContentValue = (Text) findComponentById(ResourceTable.Id_justify_content_value);
        justifyContentValue.setText(selectedJustifyContent.name());
        List<String> valueNames = new ArrayList<>();
        for (JustifyContent value: JustifyContent.values()) {
            valueNames.add(value.name());
        }
        justifyContentValue.setClickedListener((view) -> {
            ListDialog listDialog = new ListDialog(MainAbilitySlice.this);
            listDialog.setItems(valueNames.toArray(new String[0]));
            listDialog.setOnSingleSelectListener((iDialog, i) -> {
                selectedJustifyContent = JustifyContent.valueOf(valueNames.get(i));
                justifyContentValue.setText(selectedJustifyContent.name());
                iDialog.hide();
            });
            listDialog.show();
        });
    }

    private void setAlignItemsView() {
        Text alignItemsLabel = (Text) findComponentById(ResourceTable.Id_align_items_label);
        alignItemsLabel.setText(AlignItems.class.getSimpleName());
        Text alignItemsValue = (Text) findComponentById(ResourceTable.Id_align_items_value);
        alignItemsValue.setText(selectedAlignItems.name());
        List<String> valueNames = new ArrayList<>();
        for (AlignItems value: AlignItems.values()) {
            valueNames.add(value.name());
        }
        alignItemsValue.setClickedListener((view) -> {
            ListDialog listDialog = new ListDialog(MainAbilitySlice.this);
            listDialog.setItems(valueNames.toArray(new String[0]));
            listDialog.setOnSingleSelectListener((iDialog, i) -> {
                selectedAlignItems = AlignItems.valueOf(valueNames.get(i));
                alignItemsValue.setText(selectedAlignItems.name());
                iDialog.hide();
            });
            listDialog.show();
        });
    }

    private void setAlignContentView() {
        Text alignContentLabel = (Text) findComponentById(ResourceTable.Id_align_content_label);
        alignContentLabel.setText(AlignContent.class.getSimpleName());
        Text alignContentValue = (Text) findComponentById(ResourceTable.Id_align_content_value);
        alignContentValue.setText(selectedAlignContent.name());
        List<String> valueNames = new ArrayList<>();
        for (AlignContent value: AlignContent.values()) {
            valueNames.add(value.name());
        }
        alignContentValue.setClickedListener((view) -> {
            ListDialog listDialog = new ListDialog(MainAbilitySlice.this);
            listDialog.setItems(valueNames.toArray(new String[0]));
            listDialog.setOnSingleSelectListener((iDialog, i) -> {
                selectedAlignContent = AlignContent.valueOf(valueNames.get(i));
                alignContentValue.setText(selectedAlignContent.name());
                iDialog.hide();
            });
            listDialog.show();
        });
    }
}
