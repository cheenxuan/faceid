package com.baidu.idl.face.platform;

import com.baidu.idl.face.platform.model.ImageInfo;

import java.util.HashMap;

/**
 * Author: xuan
 * Created on 2021/9/7 09:37.
 * <p>
 * Describe:
 */
public interface ILivenessStrategyCallback {
    String IMAGE_KEY_BEST_CROP_IMAGE = "bestCropImage_";
    String IMAGE_KEY_BEST_SRC_IMAGE = "bestSrcImage_";

    void onLivenessCompletion(FaceStatusNewEnum var1, String var2, HashMap<String, ImageInfo> var3, HashMap<String, ImageInfo> var4, int var5);
}
