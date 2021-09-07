package com.baidu.idl.face.platform.decode;

import android.graphics.Bitmap;
import android.util.Log;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.model.FaceExtInfo;
import com.baidu.idl.face.platform.model.ImageInfo;
import com.baidu.idl.face.platform.utils.BitmapUtils;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;

import java.util.ArrayList;

/**
 * Author: xuan
 * Created on 2021/9/7 09:33.
 * <p>
 * Describe:
 */
public class FaceModuleNew {
    private static final String TAG = "FaceModuleNew";
    private FaceExtInfo[] mFaceExtInfos;
    private FaceExtInfo mFaceExtInfo;
    private FaceConfig mFaceConfig;

    public FaceModuleNew() {
    }

    public void setFaceConfig(FaceConfig faceConfig) {
        this.mFaceConfig = faceConfig;
    }

    public FaceExtInfo[] getFaceExtInfo(FaceInfo[] faceInfos) {
        if (this.mFaceExtInfos == null) {
            this.mFaceExtInfos = new FaceExtInfo[1];
            this.mFaceExtInfo = new FaceExtInfo();
        }

        if (faceInfos != null && faceInfos.length > 0) {
            if (this.mFaceExtInfo == null) {
                this.mFaceExtInfo = new FaceExtInfo();
            }

            this.mFaceExtInfo.addFaceInfo(faceInfos[0]);
            this.mFaceExtInfos[0] = this.mFaceExtInfo;
        } else {
            this.mFaceExtInfos[0] = null;
        }

        return this.mFaceExtInfos;
    }

    public ArrayList<ImageInfo> getDetectBestCropImageList(FaceExtInfo faceInfo, BDFaceImageInstance cropInstance) {
        if (faceInfo == null) {
            Log.e("FaceModuleNew", "faceInfo == null");
            return null;
        } else if (cropInstance == null) {
            Log.e("FaceModuleNew", "cropInstance == null");
            return null;
        } else {
            ArrayList<ImageInfo> list = new ArrayList();
            ImageInfo imageInfo = new ImageInfo();
            Bitmap image = BitmapUtils.getInstaceBmp(cropInstance);
            int secType = 0;
            if (this.mFaceConfig != null) {
                secType = this.mFaceConfig.getSecType();
            }

            byte[] imageData = FaceSDKManager.getInstance().compressImage(image, 90);
            String imageEncode = FaceSDKManager.getInstance().bitmapToBase64(imageData);
            if (imageEncode != null && imageEncode.length() > 0) {
                imageEncode = imageEncode.replace("\\/", "/");
            }

            imageInfo.setBase64(imageEncode);
            if (secType == 1) {
                String secValue = FaceSDKManager.getInstance().imageSec(imageData);
                secValue = secValue.replaceAll("\n", "");
                imageInfo.setSecBase64(secValue);
            }

            list.add(imageInfo);
            if (image != null) {
                image.recycle();
            }

            return list;
        }
    }

    public ArrayList<ImageInfo> getDetectBestSrcImageList(FaceExtInfo faceInfo, BDFaceImageInstance imageInstance) {
        if (faceInfo == null) {
            Log.e("FaceModuleNew", "faceInfo == null");
            return null;
        } else if (imageInstance == null) {
            Log.e("FaceModuleNew", "imageInstance == null");
            return null;
        } else {
            ArrayList<ImageInfo> list = new ArrayList();
            ImageInfo imageInfo = new ImageInfo();
            Bitmap image = BitmapUtils.getInstaceBmp(imageInstance);
            Bitmap scaleBmp = image;
            int secType = 0;
            if (this.mFaceConfig != null) {
                scaleBmp = FaceSDKManager.getInstance().scaleImage(image, this.mFaceConfig.getScale());
                secType = this.mFaceConfig.getSecType();
            }

            byte[] imageData = FaceSDKManager.getInstance().compressImage(scaleBmp, 90);
            String imageEncode = FaceSDKManager.getInstance().bitmapToBase64(imageData);
            if (imageEncode != null && imageEncode.length() > 0) {
                imageEncode = imageEncode.replace("\\/", "/");
            }

            imageInfo.setBase64(imageEncode);
            if (secType == 1) {
                String secValue = FaceSDKManager.getInstance().imageSec(imageData);
                secValue = secValue.replaceAll("\n", "");
                imageInfo.setSecBase64(secValue);
            }

            list.add(imageInfo);
            if (scaleBmp != null) {
                scaleBmp.recycle();
            }

            if (image != null) {
                image.recycle();
            }

            return list;
        }
    }
}