package com.baidu.idl.face.platform.strategy;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Build.VERSION;
import android.util.Log;
import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.ILivenessStrategy;
import com.baidu.idl.face.platform.ILivenessStrategyCallback;
import com.baidu.idl.face.platform.ILivenessViewCallback;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.baidu.idl.face.platform.common.LogHelper;
import com.baidu.idl.face.platform.common.SoundPoolHelper;
import com.baidu.idl.face.platform.decode.FaceModuleNew;
import com.baidu.idl.face.platform.manager.TimeManager;
import com.baidu.idl.face.platform.model.FaceExtInfo;
import com.baidu.idl.face.platform.model.FaceModel;
import com.baidu.idl.face.platform.model.ImageInfo;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon.BDFaceImageType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: xuan
 * Created on 2021/9/7 09:18.
 * <p>
 * Describe:
 */
public class FaceLivenessStrategyExtModule implements ILivenessStrategy {
    private static final String TAG = FaceLivenessStrategyExtModule.class.getSimpleName();
    private Context mContext;
    private Rect mPreviewRect;
    private Rect mDetectRect;
    private DetectStrategy mDetectStrategy;
    private LivenessStatusStrategy mLivenessStrategy;
    private FaceModuleNew mFaceModule;
    private ILivenessStrategyCallback mILivenessStrategyCallback;
    private ILivenessViewCallback mILivenessViewCallback;
    private volatile boolean mIsEnableSound = true;
    private SoundPoolHelper mSoundPlayHelper = null;
    private int mPreviewDegree;
    private boolean mIsFirstTipsed;
    private volatile boolean mIsProcessing;
    private volatile boolean mIsCompletion;
    private Map<FaceStatusNewEnum, String> mTipsMap = new HashMap();
    private HashMap<String, ImageInfo> mBase64ImageCropMap = new HashMap();
    private HashMap<String, ImageInfo> mBase64ImageSrcMap = new HashMap();
    private FaceConfig mFaceConfig;
    private static volatile int mProcessCount = 0;
    private long mNoFaceTime = 0L;
    private boolean mIsFirstLivenessSuccessTipsed;
    private volatile FaceLivenessStrategyExtModule.LivenessStatus mLivenessStatus;
    private Handler mAnimHandler;
    private long mFaceID;
    private int mCropCount;
    private boolean mTipLiveTimeout;

    public FaceLivenessStrategyExtModule(Context context) {
        this.mLivenessStatus = FaceLivenessStrategyExtModule.LivenessStatus.LivenessCrop;
        this.mFaceID = -1L;
        LogHelper.clear();
        LogHelper.addLog("ca", "Baidu-IDL-FaceSDK4.1.1");
        LogHelper.addLog("system", VERSION.SDK_INT);
        LogHelper.addLog("version", "4.1.1");
        LogHelper.addLog("device", Build.MODEL + " " + Build.MANUFACTURER);
        LogHelper.addLog("stm", System.currentTimeMillis());
        LogHelper.addLog("appid", context.getPackageName());
        this.mContext = context;
        this.mDetectStrategy = new DetectStrategy();
        this.mLivenessStrategy = new LivenessStatusStrategy();
        this.mFaceModule = new FaceModuleNew();
        this.mSoundPlayHelper = new SoundPoolHelper(context);
        this.mAnimHandler = new Handler(Looper.getMainLooper());
    }

    public void setILivenessViewCallback(ILivenessViewCallback viewCallback) {
        this.mILivenessViewCallback = viewCallback;
    }

    public void setConfig(FaceConfig faceConfig) {
        this.mFaceConfig = faceConfig;
    }

    public void setLivenessStrategyConfig(List<LivenessTypeEnum> livenessList, Rect previewRect, Rect detectRect, ILivenessStrategyCallback callback) {
        this.mLivenessStrategy.setLivenessList(livenessList);
        this.mPreviewRect = previewRect;
        this.mDetectRect = detectRect;
        this.mILivenessStrategyCallback = callback;
    }

    public void setLivenessStrategySoundEnable(boolean flag) {
        this.mIsEnableSound = flag;
    }

    public void setPreviewDegree(int degree) {
        this.mPreviewDegree = degree;
    }

    public void livenessStrategy(byte[] imageData) {
        if (!this.mIsFirstTipsed) {
            this.mIsFirstTipsed = true;
            this.processUITips(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected, (FaceExtInfo)null);
        } else {
            if (!this.mIsProcessing) {
                this.process(imageData);
            }

        }
    }

    public void reset() {
        FaceSDKManager.getInstance().clearActionHistory();
        if (this.mLivenessStrategy != null) {
            this.mLivenessStrategy.reset();
        }

        if (this.mBase64ImageCropMap != null) {
            this.mBase64ImageCropMap.clear();
        }

        if (this.mBase64ImageSrcMap != null) {
            this.mBase64ImageSrcMap.clear();
        }

        if (this.mSoundPlayHelper != null) {
            this.mSoundPlayHelper.release();
        }

        if (this.mAnimHandler != null) {
            this.mAnimHandler.removeCallbacksAndMessages((Object)null);
            this.mAnimHandler = null;
        }

        this.mIsFirstTipsed = false;
        this.mIsProcessing = false;
    }

    private void process(byte[] imageData) {
        if (mProcessCount <= 0) {
            ++mProcessCount;
            (new FaceLivenessStrategyExtModule.FaceProcessRunnable(imageData)).run();
        }
    }

    private void processStrategy(byte[] imageData) {
        BDFaceImageInstance imageInstance = new BDFaceImageInstance(imageData, this.mPreviewRect.width(), this.mPreviewRect.height(), BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21, (float)(360 - this.mPreviewDegree), 1);
        FaceInfo[] faceInfos = FaceSDKManager.getInstance().detect(imageInstance);
        FaceModel model = this.setFaceModel(faceInfos);
        this.processUIResult(model, imageInstance);
    }

    private FaceModel setFaceModel(FaceInfo[] faceInfos) {
        FaceExtInfo[] faceExtInfos = this.mFaceModule.getFaceExtInfo(faceInfos);
        FaceModel model = new FaceModel();
        model.setFaceInfos(faceExtInfos);
        FaceStatusNewEnum statusEnum = this.mDetectStrategy.checkDetect(this.mDetectRect, faceExtInfos, this.mFaceConfig);
        model.setFaceModuleStateNew(statusEnum);
        model.setFrameTime(System.currentTimeMillis());
        return model;
    }

    private void processUIResult(FaceModel faceModel, BDFaceImageInstance imageInstance) {
        if (imageInstance != null) {
            if (this.mIsProcessing) {
                imageInstance.destory();
            } else if (faceModel != null && faceModel.getFaceInfos() != null && faceModel.getFaceInfos().length != 0) {
                FaceStatusNewEnum decodeStatus = faceModel.getFaceModuleStateNew();
                FaceExtInfo faceInfo = faceModel.getFaceInfos()[0];
                if (decodeStatus != FaceStatusNewEnum.OK) {
                    if (this.mDetectStrategy.isTimeout()) {
                        imageInstance.destory();
                        this.mIsProcessing = true;
                        this.processUICallback(FaceStatusNewEnum.DetectRemindCodeTimeout, (FaceExtInfo)null);
                        return;
                    }

                    switch(decodeStatus) {
                        case DetectRemindCodeNoFaceDetected:
                            if (this.mNoFaceTime == 0L) {
                                this.mNoFaceTime = System.currentTimeMillis();
                            }

                            if (System.currentTimeMillis() - this.mNoFaceTime > this.mFaceConfig.getTimeDetectModule()) {
                                imageInstance.destory();
                                this.mIsProcessing = true;
                                this.processUICallback(FaceStatusNewEnum.DetectRemindCodeTimeout, (FaceExtInfo)null);
                                return;
                            }

                            if (this.mIsFirstLivenessSuccessTipsed && this.mNoFaceTime != 0L && System.currentTimeMillis() - this.mNoFaceTime < FaceEnvironment.TIME_DETECT_NO_FACE_CONTINUOUS) {
                                imageInstance.destory();
                                return;
                            }

                            this.mIsFirstLivenessSuccessTipsed = false;
                            imageInstance.destory();
                            this.mDetectStrategy.reset();
                            this.mLivenessStrategy.resetState();
                            this.processUITips(decodeStatus, (FaceExtInfo)null);
                            break;
                        default:
                            imageInstance.destory();
                            this.processUITips(decodeStatus, faceInfo);
                            this.mDetectStrategy.reset();
                            this.mLivenessStrategy.resetState();
                    }
                } else {
                    if (faceInfo == null) {
                        return;
                    }

                    this.mILivenessViewCallback.setFaceInfo(faceInfo);
                    if (this.mLivenessStatus == FaceLivenessStrategyExtModule.LivenessStatus.LivenessCrop) {
                        if (this.mCropCount < this.mFaceConfig.getCacheImageNum()) {
                            boolean cropStatus = this.cropStrategy(imageInstance, faceInfo, this.mLivenessStrategy.getCurrentLivenessType(), this.mCropCount);
                            if (cropStatus) {
                                ++this.mCropCount;
                            }
                        } else {
                            this.mLivenessStatus = FaceLivenessStrategyExtModule.LivenessStatus.LivenessReady;
                        }
                    }

                    if (this.mLivenessStatus == FaceLivenessStrategyExtModule.LivenessStatus.LivenessReady || this.mLivenessStatus == FaceLivenessStrategyExtModule.LivenessStatus.LivenessTips) {
                        if ((long)faceInfo.getFaceId() != this.mFaceID) {
                            this.mLivenessStrategy.reset();
                            FaceSDKManager.getInstance().clearActionHistory();
                            if (this.mFaceID != -1L) {
                                this.mLivenessStatus = FaceLivenessStrategyExtModule.LivenessStatus.LivenessCrop;
                                this.mCropCount = 0;
                                if (this.mBase64ImageCropMap != null) {
                                    this.mBase64ImageCropMap.clear();
                                }

                                if (this.mBase64ImageSrcMap != null) {
                                    this.mBase64ImageSrcMap.clear();
                                }
                            }

                            this.mILivenessViewCallback.viewReset();
                            this.mFaceID = (long)faceInfo.getFaceId();
                        }

                        this.mLivenessStrategy.processLiveness(faceInfo, imageInstance, this.mPreviewRect);
                    }

                    this.mNoFaceTime = 0L;
                    LogHelper.addLogWithKey("btm", System.currentTimeMillis());
                    Log.e(TAG, "switch start");
                    switch(this.mLivenessStatus) {
                        case LivenessReady:
                            if (this.processUITips(this.mLivenessStrategy.getCurrentLivenessNewStatus(), faceInfo)) {
                                this.mLivenessStatus = FaceLivenessStrategyExtModule.LivenessStatus.LivenessTips;
                            }
                            break;
                        case LivenessTips:
                            if (this.mLivenessStrategy.isCurrentLivenessSuccess()) {
                                this.mLivenessStatus = FaceLivenessStrategyExtModule.LivenessStatus.LivenessOK;
                            } else {
                                this.processUITips(this.mLivenessStrategy.getCurrentLivenessNewStatus(), faceInfo);
                                this.judgeLivenessTimeout();
                                if (this.mLivenessStrategy.isTimeout()) {
                                    imageInstance.destory();
                                    this.mIsProcessing = true;
                                    this.processUICallback(FaceStatusNewEnum.DetectRemindCodeTimeout, (FaceExtInfo)null);
                                    return;
                                }
                            }
                            break;
                        case LivenessOK:
                            if (this.processUITips(FaceStatusNewEnum.FaceLivenessActionComplete, faceInfo)) {
                                if (!this.mIsFirstLivenessSuccessTipsed) {
                                    this.mIsFirstLivenessSuccessTipsed = true;
                                }

                                if (this.mLivenessStrategy.isExistNextLiveness()) {
                                    this.mLivenessStrategy.startNextLiveness();
                                    this.mLivenessStatus = FaceLivenessStrategyExtModule.LivenessStatus.LivenessReady;
                                } else if (this.mLivenessStrategy.isLivenessSuccess()) {
                                    this.processUICallback(FaceStatusNewEnum.OK, faceInfo);
                                }
                            }
                    }

                    imageInstance.destory();
                }

            } else {
                imageInstance.destory();
                if (this.mDetectStrategy != null) {
                    this.mDetectStrategy.reset();
                }

            }
        }
    }

    private void judgeLivenessTimeout() {
        if (this.mLivenessStrategy.isCourseTimeout(this.mFaceConfig) && !this.mTipLiveTimeout) {
            if (this.mILivenessViewCallback != null) {
                this.mILivenessViewCallback.setCurrentLiveType(this.mLivenessStrategy.getCurrentLivenessType());
            }

            this.processUICallback(FaceStatusNewEnum.FaceLivenessActionCodeTimeout, (FaceExtInfo)null);
            this.mAnimHandler.postDelayed(new Runnable() {
                public void run() {
                    FaceLivenessStrategyExtModule.this.mLivenessStrategy.resetState();
                    TimeManager.getInstance().setActiveAnimTime(0);
                    FaceLivenessStrategyExtModule.this.mILivenessViewCallback.animStop();
                    FaceLivenessStrategyExtModule.this.mTipLiveTimeout = false;
                }
            }, (long)(TimeManager.getInstance().getActiveAnimTime() + 1000));
            this.mTipLiveTimeout = true;
        }

    }

    private boolean cropStrategy(BDFaceImageInstance imageInstance, FaceExtInfo faceInfo, LivenessTypeEnum type, int cropCount) {
        FaceStatusNewEnum cropStatus = this.mDetectStrategy.getCropStatus(faceInfo, this.mFaceConfig);
        if (cropStatus != FaceStatusNewEnum.OK) {
            this.mILivenessStrategyCallback.onLivenessCompletion(cropStatus, this.getStatusTextResId(cropStatus), (HashMap)null, (HashMap)null, 0);
            return false;
        } else {
            float totalScore = this.mDetectStrategy.getTotalCropScore();
            this.mFaceModule.setFaceConfig(this.mFaceConfig);
            BDFaceImageInstance cropInstance = FaceSDKManager.getInstance().cropFace(imageInstance, faceInfo.getmLandmarks(), this.mFaceConfig.getCropHeight(), this.mFaceConfig.getCropWidth());
            if (cropInstance == null) {
                return false;
            } else {
                this.saveCropImageInstance(faceInfo, cropInstance, cropCount, totalScore);
                cropInstance.destory();
                this.saveSrcImageInstance(faceInfo, imageInstance.getImage(), cropCount, totalScore);
                return true;
            }
        }
    }

    private void saveCropImageInstance(FaceExtInfo faceInfo, BDFaceImageInstance cropInstance, int cropCount, float totalScore) {
        ArrayList<ImageInfo> imageList = this.mFaceModule.getDetectBestCropImageList(faceInfo, cropInstance);
        if (imageList != null && imageList.size() > 0) {
            this.mBase64ImageCropMap.put("bestCropImage_" + cropCount + "_" + totalScore, imageList.get(0));
        }

    }

    private void saveSrcImageInstance(FaceExtInfo faceInfo, BDFaceImageInstance imageInstance, int cropCount, float totalScore) {
        ArrayList<ImageInfo> imageList = this.mFaceModule.getDetectBestSrcImageList(faceInfo, imageInstance);
        if (imageList != null && imageList.size() > 0) {
            this.mBase64ImageSrcMap.put("bestSrcImage_" + cropCount + "_" + totalScore, imageList.get(0));
        }

    }

    private boolean processUITips(FaceStatusNewEnum status, FaceExtInfo faceExtInfo) {
        boolean flag = false;
        if (status != null) {
            this.mSoundPlayHelper.setEnableSound(this.mIsEnableSound);
            flag = this.mSoundPlayHelper.playSound(status);
            if (flag) {
                LogHelper.addTipsLogWithKey(status.name());
                this.processUICallback(status, faceExtInfo);
            }
        }

        return flag;
    }

    private void processUICallback(FaceStatusNewEnum status, FaceExtInfo faceInfo) {
        if (status == FaceStatusNewEnum.DetectRemindCodeTimeout) {
            LogHelper.addLogWithKey("etm", System.currentTimeMillis());
            LogHelper.sendLog();
        }

        if (status == FaceStatusNewEnum.OK) {
            Log.e(TAG, "processUICompletion");
            this.mIsProcessing = true;
            this.mIsCompletion = true;
            LogHelper.addLogWithKey("etm", System.currentTimeMillis());
            LogHelper.addLogWithKey("finish", 1);
            LogHelper.sendLog();
            if (this.mILivenessStrategyCallback != null) {
                this.mILivenessStrategyCallback.onLivenessCompletion(status, this.getStatusTextResId(status), this.mBase64ImageCropMap, this.mBase64ImageSrcMap, this.mLivenessStrategy.getCurrentLivenessCount());
            }
        } else if (status == FaceStatusNewEnum.FaceLivenessActionComplete) {
            if (this.mILivenessStrategyCallback != null) {
                this.mILivenessStrategyCallback.onLivenessCompletion(status, this.getStatusTextResId(status), this.mBase64ImageCropMap, this.mBase64ImageSrcMap, this.mLivenessStrategy.getCurrentLivenessCount());
            }
        } else if (this.mILivenessStrategyCallback != null) {
            this.mILivenessStrategyCallback.onLivenessCompletion(status, this.getStatusTextResId(status), this.mBase64ImageCropMap, this.mBase64ImageSrcMap, this.mLivenessStrategy.getCurrentLivenessCount() - 1);
        }

    }

    private String getStatusTextResId(FaceStatusNewEnum status) {
        String tips = "";
        if (this.mTipsMap.containsKey(status)) {
            tips = (String)this.mTipsMap.get(status);
        } else {
            int resId = FaceEnvironment.getTipsId(status);
            if (resId > 0) {
                tips = this.mContext.getResources().getString(resId);
                this.mTipsMap.put(status, tips);
            }
        }

        return tips;
    }

    private class FaceProcessRunnable implements Runnable {
        private byte[] imageData;

        public FaceProcessRunnable(byte[] imageData) {
            this.imageData = imageData;
        }

        public void run() {
            FaceLivenessStrategyExtModule.this.processStrategy(this.imageData);
            --FaceLivenessStrategyExtModule.mProcessCount;
        }
    }

    private static enum LivenessStatus {
        LivenessReady,
        LivenessTips,
        LivenessOK,
        LivenessCourse,
        LivenessCrop;

        private LivenessStatus() {
        }
    }
}
