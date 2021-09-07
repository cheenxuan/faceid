package com.baidu.idl.face.platform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Author: xuan
 * Created on 2021/9/7 09:32.
 * <p>
 * Describe:
 */
public class FaceConfig implements Serializable {
    private static final String TAG = FaceConfig.class.getSimpleName();
    private int minFaceSize = 200;
    private float notFaceValue = 0.6F;
    private float brightnessValue = 82.0F;
    private float brightnessMaxValue = 200.0F;
    private float blurnessValue = 0.7F;
    private float occlusionValue = 0.5F;
    private float occlusionLeftEyeValue = 0.5F;
    private float occlusionRightEyeValue = 0.5F;
    private float occlusionNoseValue = 0.5F;
    private float occlusionMouthValue = 0.5F;
    private float occlusionLeftContourValue = 0.5F;
    private float occlusionRightContourValue = 0.5F;
    private float occlusionChinValue = 0.5F;
    private int headPitchValue = 8;
    private int headYawValue = 8;
    private int headRollValue = 8;
    private float eyeClosedValue = 0.7F;
    private boolean isSound = true;
    private boolean isLivenessRandom = false;
    private int cacheImageNum = 3;
    private int livenessRandomCount = 3;
    private List<LivenessTypeEnum> livenessTypeList;
    private boolean isOpenOnline;
    private int cropHeight;
    private int cropWidth;
    private float enlargeRatio;
    private float scale;
    private int secType;
    private float maskValue;
    private boolean isOpenMask;
    private long timeDetectModule;
    private long timeLivenessCourse;
    private int qualityLevel;
    private float faceFarRatio;
    private float faceClosedRatio;

    public FaceConfig() {
        this.livenessTypeList = FaceEnvironment.livenessTypeDefaultList;
        this.isOpenOnline = true;
        this.cropHeight = 640;
        this.cropWidth = 480;
        this.enlargeRatio = 1.5F;
        this.scale = 1.0F;
        this.secType = 0;
        this.maskValue = 0.7F;
        this.isOpenMask = true;
        this.timeDetectModule = 15000L;
        this.timeLivenessCourse = 5000L;
        this.qualityLevel = 0;
        this.faceFarRatio = 0.4F;
        this.faceClosedRatio = 1.0F;
    }

    public float getBrightnessValue() {
        return this.brightnessValue;
    }

    public void setBrightnessValue(float brightnessValue) {
        this.brightnessValue = brightnessValue;
    }

    public float getBrightnessMaxValue() {
        return this.brightnessMaxValue;
    }

    public void setBrightnessMaxValue(float brightnessMaxValue) {
        this.brightnessMaxValue = brightnessMaxValue;
    }

    public float getBlurnessValue() {
        return this.blurnessValue;
    }

    public void setBlurnessValue(float blurnessValue) {
        this.blurnessValue = blurnessValue;
    }

    public float getOcclusionValue() {
        return this.occlusionValue;
    }

    public void setOcclusionValue(float occlusionValue) {
        this.occlusionValue = occlusionValue;
    }

    public float getOcclusionLeftEyeValue() {
        return this.occlusionLeftEyeValue;
    }

    public void setOcclusionLeftEyeValue(float occlusionLeftEyeValue) {
        this.occlusionLeftEyeValue = occlusionLeftEyeValue;
    }

    public float getOcclusionRightEyeValue() {
        return this.occlusionRightEyeValue;
    }

    public void setOcclusionRightEyeValue(float occlusionRightEyeValue) {
        this.occlusionRightEyeValue = occlusionRightEyeValue;
    }

    public float getOcclusionNoseValue() {
        return this.occlusionNoseValue;
    }

    public void setOcclusionNoseValue(float occlusionNoseValue) {
        this.occlusionNoseValue = occlusionNoseValue;
    }

    public float getOcclusionMouthValue() {
        return this.occlusionMouthValue;
    }

    public void setOcclusionMouthValue(float occlusionMouthValue) {
        this.occlusionMouthValue = occlusionMouthValue;
    }

    public float getOcclusionLeftContourValue() {
        return this.occlusionLeftContourValue;
    }

    public void setOcclusionLeftContourValue(float occlusionLeftContourValue) {
        this.occlusionLeftContourValue = occlusionLeftContourValue;
    }

    public float getOcclusionRightContourValue() {
        return this.occlusionRightContourValue;
    }

    public void setOcclusionRightContourValue(float occlusionRightContourValue) {
        this.occlusionRightContourValue = occlusionRightContourValue;
    }

    public float getOcclusionChinValue() {
        return this.occlusionChinValue;
    }

    public void setOcclusionChinValue(float occlusionChinValue) {
        this.occlusionChinValue = occlusionChinValue;
    }

    public int getHeadPitchValue() {
        return this.headPitchValue;
    }

    public void setHeadPitchValue(int headPitchValue) {
        this.headPitchValue = headPitchValue;
    }

    public int getHeadYawValue() {
        return this.headYawValue;
    }

    public void setHeadYawValue(int headYawValue) {
        this.headYawValue = headYawValue;
    }

    public int getHeadRollValue() {
        return this.headRollValue;
    }

    public void setHeadRollValue(int headRollValue) {
        this.headRollValue = headRollValue;
    }

    public int getMinFaceSize() {
        return this.minFaceSize;
    }

    public void setMinFaceSize(int minFaceSize) {
        this.minFaceSize = minFaceSize;
    }

    public float getNotFaceValue() {
        return this.notFaceValue;
    }

    public void setNotFaceValue(float notFaceValue) {
        this.notFaceValue = notFaceValue;
    }

    public boolean isSound() {
        return this.isSound;
    }

    public void setSound(boolean sound) {
        this.isSound = sound;
    }

    public int getCacheImageNum() {
        return this.cacheImageNum;
    }

    public void setCacheImageNum(int cacheImageNum) {
        this.cacheImageNum = cacheImageNum;
    }

    public boolean isLivenessRandom() {
        return this.isLivenessRandom;
    }

    public void setLivenessRandom(boolean livenessRandom) {
        this.isLivenessRandom = livenessRandom;
    }

    public int getLivenessRandomCount() {
        return this.livenessRandomCount;
    }

    public void setLivenessRandomCount(int livenessRandomCount) {
        int count = FaceEnvironment.livenessTypeDefaultList.size();
        this.livenessRandomCount = livenessRandomCount <= count ? livenessRandomCount : count;
    }

    public List<LivenessTypeEnum> getLivenessTypeList() {
        if (this.livenessTypeList != null && this.livenessTypeList.size() != 0) {
            if (this.isLivenessRandom) {
                return getRandomList(this.livenessTypeList, this.livenessTypeList.size());
            }
        } else {
            this.livenessTypeList = new ArrayList();
            this.livenessTypeList.addAll(FaceEnvironment.livenessTypeDefaultList);
            Collections.shuffle(this.livenessTypeList);
            int count = this.getLivenessRandomCount();
            this.livenessTypeList = this.livenessTypeList.subList(0, count);
        }

        return this.livenessTypeList;
    }

    public void setLivenessTypeList(List<LivenessTypeEnum> list) {
        this.livenessTypeList = list;
    }

    public boolean isOpenOnline() {
        return this.isOpenOnline;
    }

    public void setOpenOnline(boolean openOnline) {
        this.isOpenOnline = openOnline;
    }

    public float getMaskValue() {
        return this.maskValue;
    }

    public void setMaskValue(float maskValue) {
        this.maskValue = maskValue;
    }

    public boolean isOpenMask() {
        return this.isOpenMask;
    }

    public void setOpenMask(boolean openMask) {
        this.isOpenMask = openMask;
    }

    public float getEyeClosedValue() {
        return this.eyeClosedValue;
    }

    public void setEyeClosedValue(float eyeClosedValue) {
        this.eyeClosedValue = eyeClosedValue;
    }

    public long getTimeDetectModule() {
        return this.timeDetectModule;
    }

    public void setTimeDetectModule(long timeDetectModule) {
        this.timeDetectModule = timeDetectModule;
    }

    public long getTimeLivenessCourse() {
        return this.timeLivenessCourse;
    }

    public void setTimeLivenessCourse(long timeLivenessCourse) {
        this.timeLivenessCourse = timeLivenessCourse;
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getCropHeight() {
        return this.cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropWidth() {
        return this.cropWidth;
    }

    public int getSecType() {
        return this.secType;
    }

    public void setSecType(int secType) {
        this.secType = secType;
    }

    public float getEnlargeRatio() {
        return this.enlargeRatio;
    }

    public void setEnlargeRatio(float enlargeRatio) {
        this.enlargeRatio = enlargeRatio;
    }

    public float getFaceFarRatio() {
        return this.faceFarRatio;
    }

    public void setFaceFarRatio(float faceFarRatio) {
        this.faceFarRatio = faceFarRatio;
    }

    public float getFaceClosedRatio() {
        return this.faceClosedRatio;
    }

    public void setFaceClosedRatio(float faceClosedRatio) {
        this.faceClosedRatio = faceClosedRatio;
    }

    public static List<LivenessTypeEnum> getRandomList(List<LivenessTypeEnum> paramList, int count) {
        if (paramList.size() < count) {
            return paramList;
        } else {
            Random random = new Random();
            List<Integer> tempList = new ArrayList();
            List<LivenessTypeEnum> newList = new ArrayList();

            for(int i = 0; i < count; ++i) {
                int temp = random.nextInt(paramList.size());
                if (!tempList.contains(temp)) {
                    tempList.add(temp);
                    newList.add(paramList.get(temp));
                } else {
                    --i;
                }
            }

            return newList;
        }
    }

    public int getQualityLevel() {
        return this.qualityLevel;
    }

    public void setQualityLevel(int qualityLevel) {
        this.qualityLevel = qualityLevel;
    }
}
