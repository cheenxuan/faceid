package com.baidu.idl.face.platform.model;

import android.graphics.Point;
import android.graphics.Rect;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceOcclusion;

import java.util.HashMap;

/**
 * Author: xuan
 * Created on 2021/9/7 09:31.
 * <p>
 * Describe:
 */
public class FaceExtInfo {
    private int mFaceID;
    private float mCenterX;
    private float mCenterY;
    private float mWidth;
    private float mHeight;
    private float mAngle;
    private float mScore;
    private float[] mLandmarks;
    private float mPitch;
    private float mYaw;
    private float mRoll;
    private float mBluriness;
    private int mIllum;
    private BDFaceOcclusion mOcclusion;
    private float mLeftEyeClose;
    private float mRightEyeClose;
    private HashMap<String, Point[]> facePointMap;
    private static int nComponents = 9;
    private static int[] comp1 = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private static int[] comp2 = new int[]{13, 14, 15, 16, 17, 18, 19, 20, 13, 21};
    private static int[] comp3 = new int[]{22, 23, 24, 25, 26, 27, 28, 29, 22};
    private static int[] comp4 = new int[]{30, 31, 32, 33, 34, 35, 36, 37, 30, 38};
    private static int[] comp5 = new int[]{39, 40, 41, 42, 43, 44, 45, 46, 39};
    private static int[] comp6 = new int[]{47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 47};
    private static int[] comp7 = new int[]{51, 57, 52};
    private static int[] comp8 = new int[]{58, 59, 60, 61, 62, 63, 64, 65, 58};
    private static int[] comp9 = new int[]{58, 66, 67, 68, 62, 69, 70, 71, 58};
    private static int[] nPoints = new int[]{13, 10, 9, 10, 9, 11, 3, 9, 9};

    public FaceExtInfo() {
    }

    public FaceExtInfo(FaceInfo info) {
        this.mFaceID = info.faceID;
        this.mCenterX = info.centerX;
        this.mCenterY = info.centerY;
        this.mWidth = info.width;
        this.mHeight = info.height;
        this.mAngle = info.angle;
        this.mScore = info.score;
        this.mLandmarks = info.landmarks;
        this.mPitch = info.pitch;
        this.mRoll = info.roll;
        this.mYaw = info.yaw;
        this.mBluriness = info.bluriness;
        this.mIllum = info.illum;
        this.mOcclusion = info.occlusion;
        this.mOcclusion.leftEye = info.occlusion.leftEye;
        this.mOcclusion.rightEye = info.occlusion.rightEye;
        this.mOcclusion.nose = info.occlusion.nose;
        this.mOcclusion.mouth = info.occlusion.mouth;
        this.mOcclusion.leftCheek = info.occlusion.leftCheek;
        this.mOcclusion.rightCheek = info.occlusion.rightCheek;
        this.mOcclusion.chin = info.occlusion.chin;
        this.mLeftEyeClose = info.leftEyeclose;
        this.mRightEyeClose = info.rightEyeclose;
    }

    public void addFaceInfo(FaceInfo info) {
        this.mFaceID = info.faceID;
        this.mCenterX = info.centerX;
        this.mCenterY = info.centerY;
        this.mWidth = info.width;
        this.mHeight = info.height;
        this.mAngle = info.angle;
        this.mScore = info.score;
        this.mLandmarks = info.landmarks;
        this.mPitch = info.pitch;
        this.mRoll = info.roll;
        this.mYaw = info.yaw;
        this.mBluriness = info.bluriness;
        this.mIllum = info.illum;
        this.mOcclusion = info.occlusion;
        this.mOcclusion.leftEye = info.occlusion.leftEye;
        this.mOcclusion.rightEye = info.occlusion.rightEye;
        this.mOcclusion.nose = info.occlusion.nose;
        this.mOcclusion.mouth = info.occlusion.mouth;
        this.mOcclusion.leftCheek = info.occlusion.leftCheek;
        this.mOcclusion.rightCheek = info.occlusion.rightCheek;
        this.mOcclusion.chin = info.occlusion.chin;
        this.mLeftEyeClose = info.leftEyeclose;
        this.mRightEyeClose = info.rightEyeclose;
    }

    public int getFaceId() {
        return this.mFaceID;
    }

    public void getRectPoints(int[] pts) {
        double degree_rad = (double)this.mAngle * 3.14159D / 180.0D;
        double cos_degree = Math.cos(degree_rad);
        double sin_degree = Math.sin(degree_rad);
        int center_x = (int)((double)this.mCenterX + cos_degree * (double)this.mWidth / 2.0D - sin_degree * (double)this.mWidth / 2.0D);
        int center_y = (int)((double)this.mCenterY + sin_degree * (double)this.mWidth / 2.0D + cos_degree * (double)this.mWidth / 2.0D);
        double _angle = (double)this.mAngle * 3.14159D / 180.0D;
        double b = Math.cos(_angle) * 0.5D;
        double a = Math.sin(_angle) * 0.5D;
        if (pts == null || pts.length == 0) {
            pts = new int[8];
        }

        pts[0] = (int)((double)center_x - a * (double)this.mWidth - b * (double)this.mWidth);
        pts[1] = (int)((double)center_y + b * (double)this.mWidth - a * (double)this.mWidth);
        pts[2] = (int)((double)center_x + a * (double)this.mWidth - b * (double)this.mWidth);
        pts[3] = (int)((double)center_y - b * (double)this.mWidth - a * (double)this.mWidth);
        pts[4] = 2 * center_x - pts[0];
        pts[5] = 2 * center_y - pts[1];
        pts[6] = 2 * center_x - pts[2];
        pts[7] = 2 * center_y - pts[3];
    }

    public Rect getFaceRect() {
        Rect rect = new Rect((int)(this.mCenterX - this.mWidth / 2.0F), (int)(this.mCenterY - this.mWidth / 2.0F), (int)this.mWidth, (int)this.mWidth);
        return rect;
    }

    public Rect getFaceRect(float ratioX, float ratioY, float surfaceRatio) {
        float x = this.mCenterX * ratioX;
        float y = this.mCenterY * ratioY;
        Rect rect = new Rect((int)(x - this.mWidth / 2.0F * ratioX * surfaceRatio), (int)(y - this.mWidth / 2.0F * ratioY * surfaceRatio), (int)(x + this.mWidth / 2.0F * ratioX * surfaceRatio), (int)(y + this.mWidth / 2.0F * ratioY * surfaceRatio));
        return rect;
    }

    public int getFaceWidth() {
        return (int)this.mWidth;
    }

    public float getPitch() {
        return this.mPitch;
    }

    public float getYaw() {
        return this.mYaw;
    }

    public float getRoll() {
        return this.mRoll;
    }

    public float getConfidence() {
        return this.mScore;
    }

    public float getBluriness() {
        return this.mBluriness;
    }

    public int getIllum() {
        return this.mIllum;
    }

    public BDFaceOcclusion getOcclusion() {
        return this.mOcclusion;
    }

    public float[] getmLandmarks() {
        return this.mLandmarks;
    }

    public float getLeftEyeClose() {
        return this.mLeftEyeClose;
    }

    public void setLeftEyeClose(float mLeftEyeClose) {
        this.mLeftEyeClose = mLeftEyeClose;
    }

    public float getRightEyeClose() {
        return this.mRightEyeClose;
    }

    public void setRightEyeClose(float mRightEyeClose) {
        this.mRightEyeClose = mRightEyeClose;
    }

    public int getLandmarksOutOfDetectCount(Rect detectRect) {
        float ratioX = 1.0F;
        float ratioY = 1.0F;
        int outCount = 0;
        if (this.mLandmarks.length == 144) {
            int[][] idx = new int[][]{comp1, comp2, comp3, comp4, comp5, comp6, comp7, comp8, comp9};
            float[] positionArr = new float[4];

            for(int i = 0; i < nComponents; ++i) {
                for(int j = 0; j < nPoints[i] - 1; ++j) {
                    positionArr[0] = this.mLandmarks[idx[i][j] << 1];
                    positionArr[1] = this.mLandmarks[1 + (idx[i][j] << 1)];
                    positionArr[2] = this.mLandmarks[idx[i][j + 1] << 1];
                    positionArr[3] = this.mLandmarks[1 + (idx[i][j + 1] << 1)];
                    if (!detectRect.contains((int)(positionArr[0] * ratioX), (int)(positionArr[1] * ratioY))) {
                        ++outCount;
                    }

                    if (!detectRect.contains((int)(positionArr[2] * ratioX), (int)(positionArr[3] * ratioY))) {
                        ++outCount;
                    }
                }
            }
        }

        return outCount;
    }

    public boolean isOutofDetectRect(Rect detectRect) {
        Rect rect = this.getFaceRect();
        return detectRect.contains(rect);
    }
}

