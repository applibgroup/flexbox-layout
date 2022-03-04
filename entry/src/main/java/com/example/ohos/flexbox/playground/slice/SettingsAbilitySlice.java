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

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.InputAttribute;
import ohos.agp.components.Text;
import ohos.agp.components.TextField;
import ohos.agp.window.dialog.CommonDialog;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;

/**
 * Ability slice for settings.
 */
public class SettingsAbilitySlice extends AbilitySlice {
    private Preferences preferences;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_settings);
        DatabaseHelper databaseHelper = new DatabaseHelper(SettingsAbilitySlice.this);
        preferences = databaseHelper.getPreferences(Constants.PREFERENCE_NAME);
        Text btnOrder = ((Text) findComponentById(ResourceTable.Id_btn_order));
        Text btnFlexGrow = ((Text) findComponentById(ResourceTable.Id_btn_flex_grow));
        Text btnFlexShrink = ((Text) findComponentById(ResourceTable.Id_btn_flex_shrink));
        DirectionalLayout btnFlexBasis = ((DirectionalLayout) findComponentById(ResourceTable.Id_btn_flex_basis));
        DirectionalLayout btnFlexWidth = ((DirectionalLayout) findComponentById(ResourceTable.Id_btn_flex_width));
        DirectionalLayout btnFlexHeight = ((DirectionalLayout) findComponentById(ResourceTable.Id_btn_flex_height));

        btnOrder.setClickedListener(component -> showPreferenceDialog(getString(ResourceTable.String_order), String.valueOf(getIntPreference(Constants.newFlexItemOrderKey, 1)),
                prefValue -> saveIntPreference(Constants.newFlexItemOrderKey, Integer.parseInt(prefValue))
        ));
        btnFlexGrow.setClickedListener(component -> showPreferenceDialog(getString(ResourceTable.String_flex_grow), String.valueOf(getFloatPreference(Constants.newFlexGrowKey, 0.0f)),
                prefValue -> saveFloatPreference(Constants.newFlexGrowKey, Float.parseFloat(prefValue))
        ));
        btnFlexShrink.setClickedListener(component -> showPreferenceDialog(getString(ResourceTable.String_flex_shrink), String.valueOf(getFloatPreference(Constants.newFlexShrinkKey, 1.0f)),
                prefValue -> saveFloatPreference(Constants.newFlexShrinkKey, Float.parseFloat(prefValue))
        ));
        btnFlexBasis.setClickedListener(component -> showPreferenceDialog(getString(ResourceTable.String_flex_basis_percent), String.valueOf(getIntPreference(Constants.newFlexBasisPercentKey, -1)),
                prefValue -> saveIntPreference(Constants.newFlexBasisPercentKey, Integer.parseInt(prefValue))
        ));
        btnFlexWidth.setClickedListener(component -> showPreferenceDialog(getString(ResourceTable.String_width), String.valueOf(getIntPreference(Constants.newWidthKey, Constants.DEFAULT_WIDTH)),
                prefValue -> saveIntPreference(Constants.newWidthKey, Integer.parseInt(prefValue))
        ));
        btnFlexHeight.setClickedListener(component -> showPreferenceDialog(getString(ResourceTable.String_height), String.valueOf(getIntPreference(Constants.newHeightKey, Constants.DEFAULT_HEIGHT)),
                prefValue -> saveIntPreference(Constants.newHeightKey, Integer.parseInt(prefValue))
        ));
    }

    private void showPreferenceDialog(String title, String initialText, PreferenceDialogButtonClickListener positiveClickListener) {
        CommonDialog dialog = new CommonDialog(SettingsAbilitySlice.this);
        dialog.setTitleText(title);
        TextField textField = new TextField(SettingsAbilitySlice.this);
        ComponentContainer.LayoutConfig lp = new ComponentContainer.LayoutConfig(
                ComponentContainer.LayoutConfig.MATCH_PARENT,
                ComponentContainer.LayoutConfig.MATCH_CONTENT
        );
        int margin = ResUtil.vpToPixel(SettingsAbilitySlice.this, Constants.PREFERENCE_MARGIN_IN_VP);
        lp.setMargins(margin, margin, margin, margin);
        textField.setLayoutConfig(lp);
        textField.setTextInputType(InputAttribute.PATTERN_NUMBER);
        textField.setTextSize(Constants.PREFERENCE_TEXT_SIZE_IN_FP, Text.TextSizeType.FP);
        textField.setText(initialText);
        dialog.setContentCustomComponent(textField);
        dialog.setButton(CommonDialog.BUTTON1, getString(ResourceTable.String_ok), (iDialog, i) -> {
            positiveClickListener.onDialogOkClick(textField.getText());
            iDialog.hide();
        });
        dialog.setButton(CommonDialog.BUTTON2, getString(ResourceTable.String_cancel), (iDialog, i) -> iDialog.hide());
        dialog.show();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    /**
     * Save the integer preference
     *
     * @param key preference key name
     * @param prefValue corresponding value
     */
    private void saveIntPreference(String key, int prefValue) {
        preferences.putInt(key, prefValue);
    }

    /**
     * Save the float preference
     *
     * @param key preference key name
     * @param prefValue corresponding value
     */
    private void saveFloatPreference(String key, float prefValue) {
        preferences.putFloat(key, prefValue);
    }

    /**
     * Get the integer preference
     *
     * @param key preference key name
     * @param defaultValue default preference value if key not found
     *
     * @return preference value
     */
    private int getIntPreference(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    /**
     * Get the float preference
     *
     * @param key preference key name
     * @param defaultValue default preference value if key not found
     *
     * @return preference value
     */
    private float getFloatPreference(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    interface PreferenceDialogButtonClickListener {
        void onDialogOkClick(String prefValue);
    }
}
