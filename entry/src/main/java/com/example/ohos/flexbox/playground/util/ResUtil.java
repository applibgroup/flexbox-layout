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

package com.example.ohos.flexbox.playground.util;

import java.io.IOException;
import java.util.Optional;

import ohos.agp.components.element.Element;
import ohos.agp.components.element.PixelMapElement;
import ohos.agp.components.element.ShapeElement;
import ohos.app.Context;
import ohos.global.resource.NotExistException;
import ohos.global.resource.RawFileEntry;
import ohos.global.resource.Resource;
import ohos.global.resource.ResourceManager;
import ohos.global.resource.WrongTypeException;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

public class ResUtil {
    private ResUtil() { }

    /**
     * get the path from id
     *
     * @param context the context
     * @param id      the id
     * @return the path from id
     */
    private static String getPathById(Context context, int id) {
        String path = "";
        if (context == null) {
            return path;
        }
        ResourceManager manager = context.getResourceManager();
        if (manager == null) {
            return path;
        }
        try {
            path = manager.getMediaPath(id);
        } catch (IOException | NotExistException | WrongTypeException e) {
            LogUtil.error(LogUtil.TAG_LOG, e.getMessage());
        }
        return path;
    }

    /**
     * get string
     *
     * @param context the context
     * @param id      the string id
     * @return string of the given id
     */
    public static String getString(Context context, int id) {
        String result = "";
        if (context == null) {
            return result;
        }
        ResourceManager manager = context.getResourceManager();
        if (manager == null) {
            return result;
        }
        try {
            result = manager.getElement(id).getString();
        } catch (IOException | NotExistException | WrongTypeException e) {
            LogUtil.error(LogUtil.TAG_LOG, e.getMessage());
        }
        return result;
    }

    /**
     * get the pixel map
     *
     * @param context the context
     * @param id      the id
     * @return the pixel map
     */
    private static Optional<PixelMap> getPixelMap(Context context, int id) {
        String path = getPathById(context, id);
        if (path.isEmpty()) {
            return Optional.empty();
        }
        RawFileEntry assetManager = context.getResourceManager().getRawFileEntry(path);
        ImageSource.SourceOptions options = new ImageSource.SourceOptions();
        options.formatHint = "image/png";
        ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
        try {
            Resource asset = assetManager.openRawFile();
            ImageSource source = ImageSource.create(asset, options);
            return Optional.ofNullable(source.createPixelmap(decodingOptions));
        } catch (IOException e) {
            LogUtil.error(LogUtil.TAG_LOG, e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * get the Pixel Map Element
     *
     * @param context the context
     * @param resId   the res id
     * @return the Pixel Map Element
     */
    public static PixelMapElement getPixelMapDrawable(Context context, int resId) {
        Optional<PixelMap> optional = getPixelMap(context, resId);
        return optional.map(PixelMapElement::new).orElse(null);
    }

    /**
     * get the shape Element By element id
     *
     * @param context  calling context
     * @param elementId shape element id
     * @return the Element By element id
     */
    public static Element getShapeElement(Context context, int elementId) {
        return new ShapeElement(context, elementId);
    }

    /**
     * Convert pixel to vp. Preserve the negative value as it's used for representing
     * MATCH_PARENT(-1) and MATCH_CONTENT(-2).
     * Ignore the round error that might happen in dividing the pixel by the density.
     *
     * @param context the calling context
     * @param pixel   the value in pixel
     *
     * @return the converted value in dp
     */
    public static int pixelToVp(Context context, int pixel) {
        int density = context.getResourceManager().getDeviceCapability().screenDensity / 160;
        return pixel < 0 ? pixel : Math.round((float) pixel / density);
    }

    /**
     * Convert vp to pixel. Preserve the negative value as it's used for representing
     * MATCH_PARENT(-1) and MATCH_CONTENT(-2).
     *
     * @param context the calling context
     * @param vp      the value in dp
     *
     * @return the converted value in pixel
     */
    public static int vpToPixel(Context context, int vp) {
        return context.getResourceManager().getDeviceCapability().screenDensity / 160 * vp;
    }

    /**
     * Returns whether the given CharSequence contains only digits.
     *
     * @param str given charSequence to check
     *
     * @return true if given charSequence is digit only else false
     */
    public static boolean isDigitsOnly(CharSequence str) {
        final int len = str.length();
        for (int cp, i = 0; i < len; i += Character.charCount(cp)) {
            cp = Character.codePointAt(str, i);
            if (!Character.isDigit(cp)) {
                return false;
            }
        }
        return true;
    }
}

