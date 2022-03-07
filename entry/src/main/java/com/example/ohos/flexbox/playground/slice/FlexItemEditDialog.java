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

import com.google.ohos.flexbox.AlignSelf;
import com.google.ohos.flexbox.FlexItem;
import com.google.ohos.flexbox.FlexboxLayout;

import com.example.ohos.flexbox.ResourceTable;
import com.example.ohos.flexbox.playground.util.ResUtil;
import com.example.ohos.flexbox.playground.validators.DimensionInputValidator;
import com.example.ohos.flexbox.playground.validators.FixedDimensionInputValidator;
import com.example.ohos.flexbox.playground.validators.FlexBasisPercentInputValidator;
import com.example.ohos.flexbox.playground.validators.InputValidator;
import com.example.ohos.flexbox.playground.validators.IntegerInputValidator;
import com.example.ohos.flexbox.playground.validators.NonNegativeDecimalInputValidator;

import ohos.agp.components.AbsButton;
import ohos.agp.components.Button;
import ohos.agp.components.Checkbox;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.InputAttribute;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.Text;
import ohos.agp.components.TextField;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.ListDialog;
import ohos.app.Context;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;

/**
 * Dialog class that changes the properties for a flex item.
 */
public class FlexItemEditDialog extends CommonDialog {
    private Context mContext;
    private FlexItem flexItem;
    private int viewIndex;
    private FlexItemChangedListener flexItemChangedListener;

    /**
     * Instance of a [FlexItem] being edited. At first it's created as another instance from
     * the [flexItem] because otherwise changes before clicking the ok button will be
     * reflected if the [flexItem] is changed directly.
     */
    private FlexItem flexItemInEdit;

    private ComponentContainer mDialogLayout;

    public FlexItemEditDialog(Context mContext, FlexItem flexItem, int viewIndex, FlexItemChangedListener flexItemChangedListener) {
        super(mContext);
        this.mContext = mContext;
        this.flexItem = flexItem;
        this.viewIndex = viewIndex;
        this.flexItemChangedListener = flexItemChangedListener;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        initDialogLayout();
        new EventHandler(EventRunner.getMainEventRunner()).postTask(new Runnable() {
            @Override
            public void run() {
                mDialogLayout.postLayout();
            }
        }, 50);
    }

    private void initDialogLayout() {
        mDialogLayout = (ComponentContainer) LayoutScatter.getInstance(mContext).parse(ResourceTable.Layout_dialog_flex_item_edit, null, false);
        setContentCustomComponent(mDialogLayout);
        if (!(mDialogLayout instanceof ComponentContainer)) {
            return;
        }
        flexItemInEdit = createNewFlexItem(flexItem);
        setTitleText(String.valueOf(this.viewIndex + 1));

        TextField orderEdit = (TextField) mDialogLayout.findComponentById(ResourceTable.Id_edit_text_order);
        orderEdit.setText(String.valueOf(flexItem.getOrder()));
        orderEdit.addTextObserver(new FlexEditTextWatcher(mContext, orderEdit, new IntegerInputValidator(), ResourceTable.String_must_be_integer));

        TextField flexGrowEdit = (TextField) mDialogLayout.findComponentById(ResourceTable.Id_edit_text_flex_grow);
        flexGrowEdit.setText(String.valueOf(flexItem.getFlexGrow()));
        flexGrowEdit.addTextObserver(new FlexEditTextWatcher(mContext, flexGrowEdit, new NonNegativeDecimalInputValidator(), ResourceTable.String_must_be_non_negative_float));

        TextField flexShrinkEdit = (TextField) mDialogLayout.findComponentById(ResourceTable.Id_edit_text_flex_shrink);
        flexShrinkEdit.setText(String.valueOf(flexItem.getFlexShrink()));
        flexShrinkEdit.addTextObserver(new FlexEditTextWatcher(mContext, flexShrinkEdit, new NonNegativeDecimalInputValidator(), ResourceTable.String_must_be_non_negative_float));

        TextField flexBasisPercentEdit = (TextField) mDialogLayout.findComponentById(ResourceTable.Id_edit_text_flex_basis_percent);
        if (flexItem.getFlexBasisPercent() != FlexboxLayout.LayoutConfig.FLEX_BASIS_PERCENT_DEFAULT) {
            flexBasisPercentEdit.setText(String.valueOf(Math.round(flexItem.getFlexBasisPercent() * 100)));
        } else {
            flexBasisPercentEdit.setText(String.valueOf((int) flexItem.getFlexBasisPercent()));
        }
        flexBasisPercentEdit.addTextObserver(new FlexEditTextWatcher(mContext, flexBasisPercentEdit, new FlexBasisPercentInputValidator(), ResourceTable.String_must_be_minus_one_or_non_negative_integer));

        TextField widthEdit = (TextField) mDialogLayout.findComponentById(ResourceTable.Id_edit_text_width);
        widthEdit.setText(String.valueOf(ResUtil.pixelToVp(mContext, flexItem.getWidth())));
        widthEdit.addTextObserver(new FlexEditTextWatcher(mContext, widthEdit, new DimensionInputValidator(), ResourceTable.String_must_be_minus_one_or_minus_two_or_non_negative_integer));

        TextField heightEdit = (TextField) mDialogLayout.findComponentById(ResourceTable.Id_edit_text_height);
        heightEdit.setText(String.valueOf(ResUtil.pixelToVp(mContext, flexItem.getHeight())));
        heightEdit.addTextObserver(new FlexEditTextWatcher(mContext, heightEdit, new DimensionInputValidator(), ResourceTable.String_must_be_minus_one_or_minus_two_or_non_negative_integer));

        TextField minWidthEdit = (TextField) mDialogLayout.findComponentById(ResourceTable.Id_edit_text_min_width);
        minWidthEdit.setText(String.valueOf(ResUtil.pixelToVp(mContext, flexItem.getMinWidth())));
        minWidthEdit.addTextObserver(new FlexEditTextWatcher(mContext, minWidthEdit, new FixedDimensionInputValidator(), ResourceTable.String_must_be_non_negative_integer));

        TextField minHeightEdit = (TextField) mDialogLayout.findComponentById(ResourceTable.Id_edit_text_min_height);
        minHeightEdit.setText(String.valueOf(ResUtil.pixelToVp(mContext, flexItem.getMinHeight())));
        minHeightEdit.addTextObserver(new FlexEditTextWatcher(mContext, minHeightEdit, new FixedDimensionInputValidator(), ResourceTable.String_must_be_non_negative_integer));

        TextField maxWidthEdit = (TextField) mDialogLayout.findComponentById(ResourceTable.Id_edit_text_max_width);
        maxWidthEdit.setText(String.valueOf(ResUtil.pixelToVp(mContext, flexItem.getMaxWidth())));
        maxWidthEdit.addTextObserver(new FlexEditTextWatcher(mContext, maxWidthEdit, new FixedDimensionInputValidator(), ResourceTable.String_must_be_non_negative_integer));

        TextField maxHeightEdit = (TextField) mDialogLayout.findComponentById(ResourceTable.Id_edit_text_max_height);
        maxHeightEdit.setText(String.valueOf(ResUtil.pixelToVp(mContext, flexItem.getMaxHeight())));
        maxHeightEdit.addTextObserver(new FlexEditTextWatcher(mContext, maxHeightEdit, new FixedDimensionInputValidator(), ResourceTable.String_must_be_non_negative_integer));

        setNextFocusesOnEnterDown(orderEdit, flexGrowEdit, flexShrinkEdit, flexBasisPercentEdit,
                widthEdit, heightEdit, minWidthEdit, minHeightEdit, maxWidthEdit, maxHeightEdit);

        Text alignSelfSpinner = (Text) mDialogLayout.findComponentById(ResourceTable.Id_spinner_align_self);
        alignSelfSpinner.setText(flexItem.getAlignSelf().name());
        List<String> valueNames = new ArrayList<>();
        for (AlignSelf value : AlignSelf.values()) {
            valueNames.add(value.name());
        }
        alignSelfSpinner.setClickedListener((view) -> {
            ListDialog listDialog = new ListDialog(mContext);
            listDialog.setItems(valueNames.toArray(new String[0]));
            listDialog.setOnSingleSelectListener((iDialog, i) -> {
                flexItemInEdit.setAlignSelf(AlignSelf.valueOf(valueNames.get(i)));
                alignSelfSpinner.setText(flexItemInEdit.getAlignSelf().name());
                iDialog.hide();
            });
            listDialog.show();
        });

        Checkbox wrapBeforeCheckBox = (Checkbox) mDialogLayout.findComponentById(ResourceTable.Id_checkbox_wrap_before);
        wrapBeforeCheckBox.setChecked(flexItem.isWrapBefore());
        wrapBeforeCheckBox.setCheckedStateChangedListener(new AbsButton.CheckedStateChangedListener() {
            @Override
            public void onCheckedChanged(AbsButton absButton, boolean b) {
                flexItemInEdit.setWrapBefore(b);
                if (wrapBeforeCheckBox.isChecked()) {
                    wrapBeforeCheckBox.setButtonElement(ResUtil.getPixelMapDrawable(mContext, ResourceTable.Media_checkbox_on_background));
                } else {
                    wrapBeforeCheckBox.setButtonElement(ResUtil.getPixelMapDrawable(mContext, ResourceTable.Media_checkbox_off_background));
                }
            }
        });
        if (wrapBeforeCheckBox.isChecked()) {
            wrapBeforeCheckBox.setButtonElement(ResUtil.getPixelMapDrawable(mContext, ResourceTable.Media_checkbox_on_background));
        } else {
            wrapBeforeCheckBox.setButtonElement(ResUtil.getPixelMapDrawable(mContext, ResourceTable.Media_checkbox_off_background));
        }

        mDialogLayout.findComponentById(ResourceTable.Id_button_cancel).setClickedListener(component -> {
            copyFlexItemValues(flexItem, flexItemInEdit);
            hide();
        });
        Button okButton = (Button) mDialogLayout.findComponentById(ResourceTable.Id_button_ok);
        okButton.setClickedListener(component -> {
            if (flexItemChangedListener != null) {
                copyFlexItemValues(flexItemInEdit, flexItem);
                flexItemChangedListener.onFlexItemChanged(flexItem, viewIndex);
            }
            hide();
        });
    }

    private void setNextFocusesOnEnterDown(Text... textViews) {
        for (int i = 0; i < textViews.length; i++) {
            int finalI = i;
            textViews[i].setEditorActionListener(new Text.EditorActionListener() {
                @Override
                public boolean onTextEditorAction(int actionId) {
                    if (actionId == InputAttribute.ENTER_KEY_TYPE_GO ||
                            actionId == InputAttribute.ENTER_KEY_TYPE_SEND) {
                        if (finalI + 1 < textViews.length) {
                            textViews[finalI + 1].requestFocus();
                        } else if (finalI == textViews.length - 1) {
                            // Hide Keyboard
                            return false;
                        }
                    }
                    return true;
                }
            });
        }
    }

    private FlexItem createNewFlexItem(FlexItem item) {
        if (item instanceof FlexboxLayout.LayoutConfig) {
            FlexboxLayout.LayoutConfig newItem = new FlexboxLayout.LayoutConfig(item.getWidth(), item.getHeight());
            this.copyFlexItemValues(item, newItem);
            return newItem;
        } else {
            throw new IllegalArgumentException("Unknown FlexItem: " + item);
        }
    }

    private void copyFlexItemValues(FlexItem from, FlexItem to) {
        to.setOrder(from.getOrder());
        to.setFlexGrow(from.getFlexGrow());
        to.setFlexShrink(from.getFlexShrink());
        to.setFlexBasisPercent(from.getFlexBasisPercent());
        to.setHeight(from.getHeight());
        to.setWidth(from.getWidth());
        to.setMaxHeight(from.getMaxHeight());
        to.setMinHeight(from.getMinHeight());
        to.setMaxWidth(from.getMaxWidth());
        to.setMinWidth(from.getMinWidth());
        to.setAlignSelf(from.getAlignSelf());
        to.setWrapBefore(from.isWrapBefore());
    }

    private class FlexEditTextWatcher implements Text.TextObserver {
        private Context context;
        private Text textView;
        private InputValidator inputValidator;
        private int errorMessageId;

        private boolean isErrorEnabled = false;
        private String error = "";

        FlexEditTextWatcher(Context context, Text textView, InputValidator inputValidator, int errorMessageId) {
            this.context = context;
            this.textView = textView;
            this.inputValidator = inputValidator;
            this.errorMessageId = errorMessageId;
        }

        @Override
        public void onTextUpdated(String text, int start, int before, int count) {
            if (inputValidator.isValidInput(text)) {
                isErrorEnabled = false;
                error = "";
            } else {
                isErrorEnabled = true;
                error = ResUtil.getString(context, errorMessageId);
            }

            if (isErrorEnabled || text.isEmpty() ||
                    !inputValidator.isValidInput(text)) {
                return;
            }
            float value = Float.parseFloat(text);
            switch (textView.getId()) {
                case ResourceTable.Id_edit_text_order:
                    flexItemInEdit.setOrder((int) value);
                    break;
                case ResourceTable.Id_edit_text_flex_grow:
                    flexItemInEdit.setFlexGrow(value);
                    break;
                case ResourceTable.Id_edit_text_flex_shrink:
                    flexItemInEdit.setFlexShrink(value);
                    break;
                case ResourceTable.Id_edit_text_flex_basis_percent:
                    if (value != FlexboxLayout.LayoutConfig.FLEX_BASIS_PERCENT_DEFAULT) {
                        flexItemInEdit.setFlexBasisPercent(((int) value) / 100.0f);
                    } else {
                        flexItemInEdit.setFlexBasisPercent(FlexItem.FLEX_BASIS_PERCENT_DEFAULT);
                    }
                    break;
                case ResourceTable.Id_edit_text_width:
                    flexItemInEdit.setWidth(ResUtil.vpToPixel(context, (int) value));
                    break;
                case ResourceTable.Id_edit_text_height:
                    flexItemInEdit.setHeight(ResUtil.vpToPixel(context, (int) value));
                    break;
                case ResourceTable.Id_edit_text_min_width:
                    flexItemInEdit.setMinWidth(ResUtil.vpToPixel(context, (int) value));
                    break;
                case ResourceTable.Id_edit_text_min_height:
                    flexItemInEdit.setMinHeight(ResUtil.vpToPixel(context, (int) value));
                    break;
                case ResourceTable.Id_edit_text_max_width:
                    flexItemInEdit.setMaxWidth(ResUtil.vpToPixel(context, (int) value));
                    break;
                case ResourceTable.Id_edit_text_max_height:
                    flexItemInEdit.setMaxHeight(ResUtil.vpToPixel(context, (int) value));
                    break;
            }
        }
    }

}

