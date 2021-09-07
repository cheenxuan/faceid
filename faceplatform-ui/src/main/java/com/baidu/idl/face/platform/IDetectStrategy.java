package com.baidu.idl.face.platform;

import android.graphics.Rect;

/**
 * Author: xuan
 * Created on 2021/9/7 09:39.
 * <p>
 * Describe:
 */
public interface IDetectStrategy {
    void setDetectStrategyConfig(Rect var1, Rect var2, IDetectStrategyCallback var3);

    void setDetectStrategySoundEnable(boolean var1);

    void detectStrategy(byte[] var1);

    void setPreviewDegree(int var1);

    void reset();
}

