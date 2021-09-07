package com.baidu.idl.face.platform;

import android.graphics.Rect;

import java.util.List;

/**
 * Author: xuan
 * Created on 2021/9/7 09:38.
 * <p>
 * Describe:
 */
public interface ILivenessStrategy {
    void setLivenessStrategyConfig(List<LivenessTypeEnum> var1, Rect var2, Rect var3, ILivenessStrategyCallback var4);

    void setLivenessStrategySoundEnable(boolean var1);

    void livenessStrategy(byte[] var1);

    void setPreviewDegree(int var1);

    void reset();
}
