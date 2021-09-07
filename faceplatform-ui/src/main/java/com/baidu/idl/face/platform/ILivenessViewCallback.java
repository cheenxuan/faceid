package com.baidu.idl.face.platform;

import com.baidu.idl.face.platform.model.FaceExtInfo;

/**
 * Author: xuan
 * Created on 2021/9/7 09:39.
 * <p>
 * Describe:
 */
public interface ILivenessViewCallback {
    void setCurrentLiveType(LivenessTypeEnum var1);

    void viewReset();

    void animStop();

    void setFaceInfo(FaceExtInfo var1);
}
