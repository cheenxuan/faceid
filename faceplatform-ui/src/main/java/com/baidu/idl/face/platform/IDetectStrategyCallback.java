package com.baidu.idl.face.platform;

import com.baidu.idl.face.platform.model.ImageInfo;

import java.util.HashMap;

/**
 * Author: xuan
 * Created on 2021/9/7 09:38.
 * <p>
 * Describe:
 */
public interface IDetectStrategyCallback {
    String IMAGE_KEY_BEST_CROP_IMAGE = "bestCropDetectImage_";
    String IMAGE_KEY_BEST_SRC_IMAGE = "bestSrcDetectImage_";

    void onDetectCompletion(FaceStatusNewEnum var1, String var2, HashMap<String, ImageInfo> var3, HashMap<String, ImageInfo> var4);
}
