package com.baidu.idl.face.platform.model;

import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;

/**
 * Author: xuan
 * Created on 2021/9/7 09:32.
 * <p>
 * Describe:
 */
public class FaceModel {
    private FaceExtInfo[] faceInfos;
    private FaceInfo[] faceSDKInfos;
    private FaceStatusNewEnum faceNewStatus;
    private BDFaceImageInstance imageInstance;
    private BDFaceImageInstance BitmapInstance;
    private BDFaceImageInstance cropInstance;
    private long frameTime;

    public FaceModel() {
    }

    public FaceStatusNewEnum getFaceModuleStateNew() {
        return this.faceNewStatus;
    }

    public void setFaceModuleStateNew(FaceStatusNewEnum faceNewStatus) {
        this.faceNewStatus = faceNewStatus;
    }

    public BDFaceImageInstance getImageInstance() {
        return this.imageInstance;
    }

    public void setImageInstance(BDFaceImageInstance imageInstance) {
        this.imageInstance = imageInstance;
    }

    public long getFrameTime() {
        return this.frameTime;
    }

    public void setFrameTime(long frameTime) {
        this.frameTime = frameTime;
    }

    public BDFaceImageInstance getBitmapInstance() {
        return this.BitmapInstance;
    }

    public void setBitmapInstance(BDFaceImageInstance bitmapInstance) {
        this.BitmapInstance = bitmapInstance;
    }

    public BDFaceImageInstance getCropInstance() {
        return this.cropInstance;
    }

    public void setCropInstance(BDFaceImageInstance cropInstance) {
        this.cropInstance = cropInstance;
    }

    public FaceExtInfo[] getFaceInfos() {
        return this.faceInfos;
    }

    public void setFaceInfos(FaceExtInfo[] faceInfos) {
        this.faceInfos = faceInfos;
    }

    public FaceInfo[] getFaceSDKInfos() {
        return this.faceSDKInfos;
    }

    public void setFaceSDKInfos(FaceInfo[] faceSDKInfos) {
        this.faceSDKInfos = faceSDKInfos;
    }
}
