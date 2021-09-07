package com.baidu.idl.face.platform.strategy;

import android.graphics.Rect;
import android.util.Log;
import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.baidu.idl.face.platform.model.FaceExtInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon.BDFaceActionLiveType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: xuan
 * Created on 2021/9/7 09:20.
 * <p>
 * Describe:
 */
class LivenessStatusStrategy {
    private static final String TAG = LivenessStatusStrategy.class.getSimpleName();
    private List<LivenessTypeEnum> mLivenessList;
    private long mLivenessDuration;
    private volatile int mLivenessIndex = 0;
    private boolean mLivenessTimeoutFlag = false;
    private volatile LivenessTypeEnum mCurrentLivenessTypeEnum = null;
    private long mFaceID = -1L;
    private HashMap<LivenessTypeEnum, Boolean> mLivenessStatusMap = new HashMap();
    private long mQualityDuration = 0L;
    private long mLivenessTimeDuration = 0L;

    public LivenessStatusStrategy() {
        this.mLivenessIndex = 0;
        this.mLivenessDuration = 0L;
    }

    public void setLivenessList(List<LivenessTypeEnum> list) {
        if (list != null && list.size() > 0) {
            this.mLivenessList = list;
            this.mCurrentLivenessTypeEnum = (LivenessTypeEnum)this.mLivenessList.get(0);
            Log.e(TAG, "mCurrentLivenessTypeEnum = " + this.mCurrentLivenessTypeEnum);
            this.clearLivenessStatus();
        }

    }

    public LivenessTypeEnum getCurrentLivenessType() {
        return this.mCurrentLivenessTypeEnum;
    }

    public FaceStatusNewEnum getCurrentLivenessNewStatus() {
        FaceStatusNewEnum status = null;
        if (this.mCurrentLivenessTypeEnum != null) {
            switch(this.mCurrentLivenessTypeEnum) {
                case Eye:
                    status = FaceStatusNewEnum.FaceLivenessActionTypeLiveEye;
                    break;
                case Mouth:
                    status = FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth;
                    break;
                case HeadUp:
                    status = FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp;
                    break;
                case HeadDown:
                    status = FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown;
                    break;
                case HeadLeft:
                    status = FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft;
                    break;
                case HeadRight:
                    status = FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight;
            }
        }

        return status;
    }

    public boolean isLivenessSuccess() {
        boolean flag = true;
        String name = "";
        Iterator var3 = this.mLivenessStatusMap.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<LivenessTypeEnum, Boolean> entry = (Map.Entry)var3.next();
            if (!(Boolean)entry.getValue()) {
                flag = false;
                name = ((LivenessTypeEnum)entry.getKey()).name();
                break;
            }
        }

        if (flag) {
            this.mLivenessTimeDuration = 0L;
        }

        return flag;
    }

    public boolean isCurrentLivenessSuccess() {
        boolean flag = this.mLivenessStatusMap.containsKey(this.mCurrentLivenessTypeEnum) ? (Boolean)this.mLivenessStatusMap.get(this.mCurrentLivenessTypeEnum) : false;
        if (flag) {
            this.mLivenessTimeDuration = 0L;
        }

        return flag;
    }

    public boolean isTimeout() {
        return this.mLivenessTimeoutFlag;
    }

    public boolean isCourseTimeout(FaceConfig faceConfig) {
        if (this.mLivenessDuration == 0L) {
            this.mLivenessDuration = System.currentTimeMillis();
        }

        return System.currentTimeMillis() - this.mLivenessDuration > faceConfig.getTimeLivenessCourse();
    }

    public boolean nextLiveness() {
        if (this.mLivenessIndex + 1 < this.mLivenessList.size()) {
            ++this.mLivenessIndex;
            this.mCurrentLivenessTypeEnum = (LivenessTypeEnum)this.mLivenessList.get(this.mLivenessIndex);
            this.mLivenessDuration = 0L;
            Log.e(TAG, "ext 开始下个活体验证 =" + this.mCurrentLivenessTypeEnum.name());
            return true;
        } else {
            return false;
        }
    }

    protected boolean isExistNextLiveness() {
        return this.mLivenessIndex + 1 < this.mLivenessList.size();
    }

    protected int getCurrentLivenessCount() {
        return this.mLivenessIndex + 1;
    }

    protected void startNextLiveness() {
        ++this.mLivenessIndex;
        this.mCurrentLivenessTypeEnum = (LivenessTypeEnum)this.mLivenessList.get(this.mLivenessIndex);
        this.mLivenessDuration = 0L;
    }

    public void processLiveness(FaceExtInfo faceInfo, BDFaceImageInstance imageInstance, Rect imageRect) {
        if (this.mLivenessTimeDuration == 0L) {
            this.mLivenessTimeDuration = System.currentTimeMillis();
        }

        FaceConfig faceConfig = FaceSDKManager.getInstance().getFaceConfig();
        if (System.currentTimeMillis() - this.mLivenessTimeDuration > faceConfig.getTimeDetectModule()) {
            this.mLivenessTimeoutFlag = true;
        } else {
            if (faceInfo != null) {
                if ((long)faceInfo.getFaceId() != this.mFaceID) {
                    this.mFaceID = (long)faceInfo.getFaceId();
                }
                
                AtomicInteger isExist = new AtomicInteger();
                int errCode;
                switch(this.mCurrentLivenessTypeEnum) {
                    case Eye:
                        errCode = FaceSDKManager.getInstance().processLiveness(BDFaceActionLiveType.BDFace_ACTION_LIVE_BLINK, imageInstance, faceInfo, isExist);
                        Log.e(TAG, "ext Eye err " + errCode + "exist " + isExist);
                        break;
                    case Mouth:
                        errCode = FaceSDKManager.getInstance().processLiveness(BDFaceActionLiveType.BDFACE_ACTION_LIVE_OPEN_MOUTH, imageInstance, faceInfo, isExist);
                        Log.e(TAG, "ext Mouth err " + errCode + "exist " + isExist);
                        break;
                    case HeadUp:
                        errCode = FaceSDKManager.getInstance().processLiveness(BDFaceActionLiveType.BDFACE_ACTION_LIVE_LOOK_UP, imageInstance, faceInfo, isExist);
                        Log.e(TAG, "ext HeadUp err " + errCode + "exist " + isExist);
                        break;
                    case HeadDown:
                        errCode = FaceSDKManager.getInstance().processLiveness(BDFaceActionLiveType.BDFACE_ACTION_LIVE_NOD, imageInstance, faceInfo, isExist);
                        Log.e(TAG, "ext HeadDown err " + errCode + "exist " + isExist);
                        break;
                    case HeadLeft:
                        errCode = FaceSDKManager.getInstance().processLiveness(BDFaceActionLiveType.BDFACE_ACTION_LIVE_TURN_LEFT, imageInstance, faceInfo, isExist);
                        Log.e(TAG, "ext HeadLeft err " + errCode + "exist " + isExist);
                        break;
                    case HeadRight:
                        errCode = FaceSDKManager.getInstance().processLiveness(BDFaceActionLiveType.BDFACE_ACTION_LIVE_TURN_RIGHT, imageInstance, faceInfo, isExist);
                        Log.e(TAG, "ext HeadRight err " + errCode + "exist " + isExist);
                }

                if (this.mLivenessList.contains(LivenessTypeEnum.Eye) && !this.mLivenessStatusMap.containsKey(LivenessTypeEnum.Eye)) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.Eye, isExist.get() == 1);
                } else if (this.mCurrentLivenessTypeEnum == LivenessTypeEnum.Eye && isExist.get() == 1) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.Eye, isExist.get() == 1);
                }

                if (this.mLivenessList.contains(LivenessTypeEnum.Mouth) && !this.mLivenessStatusMap.containsKey(LivenessTypeEnum.Mouth)) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.Mouth, isExist.get() == 1);
                } else if (this.mCurrentLivenessTypeEnum == LivenessTypeEnum.Mouth && isExist.get() == 1) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.Mouth, isExist.get() == 1);
                }

                if (this.mLivenessList.contains(LivenessTypeEnum.HeadUp) && !this.mLivenessStatusMap.containsKey(LivenessTypeEnum.HeadUp)) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.HeadUp, isExist.get() == 1);
                } else if (this.mCurrentLivenessTypeEnum == LivenessTypeEnum.HeadUp && isExist.get() == 1) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.HeadUp, isExist.get() == 1);
                }

                if (this.mLivenessList.contains(LivenessTypeEnum.HeadDown) && !this.mLivenessStatusMap.containsKey(LivenessTypeEnum.HeadDown)) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.HeadDown, isExist.get() == 1);
                } else if (this.mCurrentLivenessTypeEnum == LivenessTypeEnum.HeadDown && isExist.get() == 1) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.HeadDown, isExist.get() == 1);
                }

                if (this.mLivenessList.contains(LivenessTypeEnum.HeadLeft) && !this.mLivenessStatusMap.containsKey(LivenessTypeEnum.HeadLeft)) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.HeadLeft, isExist.get() == 1);
                } else if (this.mCurrentLivenessTypeEnum == LivenessTypeEnum.HeadLeft && isExist.get() == 1) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.HeadLeft, isExist.get() == 1);
                }

                if (this.mLivenessList.contains(LivenessTypeEnum.HeadRight) && !this.mLivenessStatusMap.containsKey(LivenessTypeEnum.HeadRight)) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.HeadRight, isExist.get() == 1);
                } else if (this.mCurrentLivenessTypeEnum == LivenessTypeEnum.HeadRight && isExist.get() == 1) {
                    this.mLivenessStatusMap.put(LivenessTypeEnum.HeadRight, isExist.get() == 1);
                }
            }

        }
    }

    public boolean showQualityTips() {
        return System.currentTimeMillis() - this.mQualityDuration > 0L;
    }

    public void resetQualityTime() {
        this.mQualityDuration = System.currentTimeMillis();
    }

    public void reset() {
        this.mLivenessIndex = 0;
        this.clearLivenessStatus();
        if (this.mLivenessList != null && this.mLivenessIndex < this.mLivenessList.size()) {
            this.mCurrentLivenessTypeEnum = (LivenessTypeEnum)this.mLivenessList.get(this.mLivenessIndex);
        }

        this.mLivenessDuration = 0L;
        this.mLivenessTimeoutFlag = false;
        this.mLivenessTimeDuration = 0L;
    }

    private void clearLivenessStatus() {
        this.mLivenessStatusMap.clear();

        for(int i = 0; i < this.mLivenessList.size(); ++i) {
            this.mLivenessStatusMap.put(this.mLivenessList.get(i), false);
        }

    }

    public void resetState() {
        this.mLivenessDuration = 0L;
    }
}

