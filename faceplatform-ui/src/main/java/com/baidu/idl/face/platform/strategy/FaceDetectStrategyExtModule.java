package com.baidu.idl.face.platform.strategy;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Build.VERSION;
import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.IDetectStrategy;
import com.baidu.idl.face.platform.IDetectStrategyCallback;
import com.baidu.idl.face.platform.common.LogHelper;
import com.baidu.idl.face.platform.common.SoundPoolHelper;
import com.baidu.idl.face.platform.decode.FaceModuleNew;
import com.baidu.idl.face.platform.model.FaceExtInfo;
import com.baidu.idl.face.platform.model.FaceModel;
import com.baidu.idl.face.platform.model.ImageInfo;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon.BDFaceImageType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: xuan
 * Created on 2021/9/7 09:19.
 * <p>
 * Describe:
 */
public class FaceDetectStrategyExtModule implements IDetectStrategy {
    private static final String TAG = FaceDetectStrategyExtModule.class.getName();
    private Context mContext;
    private Rect mPreviewRect;
    private Rect mDetectRect;
    private IDetectStrategyCallback mIDetectStrategyCallback;
    private int mDegree;
    private final FaceModuleNew mFaceModule;
    private boolean mIsFirstTipsed = false;
    private volatile boolean mIsProcessing;
    private volatile boolean mIsCompletion = false;
    private FaceConfig mFaceConfig;
    private volatile boolean mIsEnableSound = true;
    private final DetectStrategy mDetectStrategy;
    private final SoundPoolHelper mSoundPlayHelper;
    private static volatile int mProcessCount = 0;
    private int mDetectCount = 0;
    private long mNoFaceTime = 0L;
    private Map<FaceStatusNewEnum, String> mTipsMap = new HashMap();
    private HashMap<String, ImageInfo> mBase64ImageCropMap = new HashMap();
    private HashMap<String, ImageInfo> mBase64ImageSrcMap = new HashMap();

    public FaceDetectStrategyExtModule(Context context) {
        LogHelper.clear();
        LogHelper.addLog("ca", "Baidu-IDL-FaceSDK4.1.1");
        LogHelper.addLog("system", VERSION.SDK_INT);
        LogHelper.addLog("version", "4.1.1");
        LogHelper.addLog("device", Build.MODEL + " " + Build.MANUFACTURER);
        LogHelper.addLog("stm", System.currentTimeMillis());
        LogHelper.addLog("appid", context.getPackageName());
        this.mContext = context;
        this.mDetectStrategy = new DetectStrategy();
        this.mSoundPlayHelper = new SoundPoolHelper(context);
        this.mFaceModule = new FaceModuleNew();
    }

    public void setDetectStrategyConfig(Rect previewRect, Rect detectRect, IDetectStrategyCallback callback) {
        this.mPreviewRect = previewRect;
        this.mDetectRect = detectRect;
        this.mIDetectStrategyCallback = callback;
    }

    public void setConfigValue(FaceConfig config) {
        this.mFaceConfig = config;
    }

    public void setDetectStrategySoundEnable(boolean flag) {
        this.mIsEnableSound = flag;
    }

    public void setPreviewDegree(int degree) {
        this.mDegree = degree;
    }

    public void detectStrategy(byte[] imageData) {
        if (!this.mIsFirstTipsed) {
            this.mIsFirstTipsed = true;
            this.processUITips(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected, (FaceExtInfo)null);
        } else {
            if (!this.mIsProcessing) {
                this.process(imageData);
            }

        }
    }

    private void process(byte[] imageData) {
        if (mProcessCount <= 0) {
            ++mProcessCount;
            (new FaceDetectStrategyExtModule.FaceProcessRunnable(imageData)).run();
        }
    }

    private void processStrategy(byte[] imageData) {
        BDFaceImageInstance imageInstance = new BDFaceImageInstance(imageData, this.mPreviewRect.width(), this.mPreviewRect.height(), BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21, (float)(360 - this.mDegree), 1);
        FaceInfo[] faceInfos = FaceSDKManager.getInstance().detect(imageInstance);
        FaceModel model = this.setFaceModel(faceInfos, imageInstance);
        this.processUIResult(model, imageInstance);
    }

    private FaceModel setFaceModel(FaceInfo[] faceInfos, BDFaceImageInstance imageInstance) {
        if (imageInstance == null) {
            return null;
        } else {
            FaceModel model = new FaceModel();
            boolean isMasked = false;
            FaceExtInfo[] faceExtInfos = this.mFaceModule.getFaceExtInfo(faceInfos);
            FaceStatusNewEnum detectState = this.mDetectStrategy.getDetectState(faceExtInfos, this.mDetectRect, isMasked, this.mFaceConfig);
            model.setFaceModuleStateNew(detectState);
            model.setFaceInfos(faceExtInfos);
            model.setFrameTime(System.currentTimeMillis());
            return model;
        }
    }

    private void processUIResult(FaceModel faceModel, BDFaceImageInstance imageInstance) {
        if (imageInstance != null) {
            if (this.mIsProcessing) {
                imageInstance.destory();
            } else {
                FaceExtInfo faceInfo;
                if (faceModel != null && faceModel.getFaceInfos() != null && faceModel.getFaceInfos().length > 0) {
                    faceInfo = faceModel.getFaceInfos()[0];
                    LogHelper.addLogWithKey("ftm", System.currentTimeMillis());
                } else {
                    faceInfo = null;
                    if (this.mDetectStrategy != null) {
                        this.mDetectStrategy.reset();
                    }
                }

                if (faceInfo != null) {
                    if (this.mDetectStrategy == null) {
                        imageInstance.destory();
                        return;
                    }

                    if (this.mIsCompletion) {
                        this.processUITips(FaceStatusNewEnum.OK, faceInfo);
                        imageInstance.destory();
                        return;
                    }

                    if (faceModel == null) {
                        return;
                    }

                    FaceStatusNewEnum detectStatus = faceModel.getFaceModuleStateNew();
                    if (detectStatus == FaceStatusNewEnum.OK) {
                        LogHelper.addLogWithKey("btm", System.currentTimeMillis());
                        if (this.mDetectCount < this.mFaceConfig.getCacheImageNum()) {
                            boolean success = this.cropStrategy(imageInstance, faceInfo, this.mDetectCount);
                            if (success) {
                                ++this.mDetectCount;
                            }
                        } else {
                            this.mIsCompletion = true;
                            this.processUITips(FaceStatusNewEnum.OK, faceInfo);
                        }

                        imageInstance.destory();
                        return;
                    }

                    if (this.mDetectStrategy.isTimeout()) {
                        this.mIsProcessing = true;
                        imageInstance.destory();
                        this.processUICallback(FaceStatusNewEnum.DetectRemindCodeTimeout, (FaceExtInfo)null);
                        return;
                    }

                    this.processUITips(detectStatus, faceInfo);
                    imageInstance.destory();
                } else {
                    if (this.mDetectStrategy == null) {
                        imageInstance.destory();
                        return;
                    }

                    if (faceModel == null || faceModel.getFaceModuleStateNew() != FaceStatusNewEnum.DetectRemindCodeNoFaceDetected && faceModel.getFaceModuleStateNew() != FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame) {
                        this.mNoFaceTime = 0L;
                    } else {
                        this.mDetectStrategy.reset();
                        if (this.mNoFaceTime == 0L) {
                            this.mNoFaceTime = System.currentTimeMillis();
                        } else if (System.currentTimeMillis() - this.mNoFaceTime > this.mFaceConfig.getTimeDetectModule()) {
                            this.mIsProcessing = true;
                            imageInstance.destory();
                            this.processUICallback(FaceStatusNewEnum.DetectRemindCodeTimeout, (FaceExtInfo)null);
                            return;
                        }
                    }

                    if (this.mDetectStrategy.isTimeout()) {
                        imageInstance.destory();
                        this.mIsProcessing = true;
                        this.processUICallback(FaceStatusNewEnum.DetectRemindCodeTimeout, (FaceExtInfo)null);
                        return;
                    }

                    this.processUITips(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected, (FaceExtInfo)null);
                    imageInstance.destory();
                }

            }
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

    private void processUICallback(FaceStatusNewEnum status, FaceExtInfo faceExtInfo) {
        if (status == FaceStatusNewEnum.DetectRemindCodeTimeout) {
            LogHelper.addLogWithKey("etm", System.currentTimeMillis());
            LogHelper.sendLog();
        }

        if (this.mIDetectStrategyCallback != null) {
            if (status == FaceStatusNewEnum.OK) {
                this.mIsProcessing = true;
                this.mIsCompletion = true;
                LogHelper.addLogWithKey("etm", System.currentTimeMillis());
                LogHelper.addLogWithKey("finish", 1);
                LogHelper.sendLog();
                this.mIDetectStrategyCallback.onDetectCompletion(status, this.getStatusTextResId(status), this.mBase64ImageCropMap, this.mBase64ImageSrcMap);
            } else {
                this.mIDetectStrategyCallback.onDetectCompletion(status, this.getStatusTextResId(status), (HashMap)null, (HashMap)null);
            }
        }

    }

    private boolean cropStrategy(BDFaceImageInstance imageInstance, FaceExtInfo faceInfo, int index) {
        float totalScore = this.mDetectStrategy.getTotalCropScore();
        this.mFaceModule.setFaceConfig(this.mFaceConfig);
        BDFaceImageInstance cropInstance = FaceSDKManager.getInstance().cropFace(imageInstance, faceInfo.getmLandmarks(), this.mFaceConfig.getCropHeight(), this.mFaceConfig.getCropWidth());
        if (cropInstance == null) {
            return false;
        } else {
            this.saveCropImageInstance(faceInfo, cropInstance, index, totalScore);
            cropInstance.destory();
            this.saveSrcImageInstance(faceInfo, imageInstance.getImage(), index, totalScore);
            return true;
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

    private void saveCropImageInstance(FaceExtInfo faceInfo, BDFaceImageInstance cropInstance, int index, float totalScore) {
        ArrayList<ImageInfo> imageList = this.mFaceModule.getDetectBestCropImageList(faceInfo, cropInstance);
        if (imageList != null && imageList.size() > 0) {
            this.mBase64ImageCropMap.put("bestCropDetectImage_" + index + "_" + totalScore, imageList.get(0));
        }

    }

    private void saveSrcImageInstance(FaceExtInfo faceInfo, BDFaceImageInstance imageInstance, int index, float totalScore) {
        ArrayList<ImageInfo> imageList = this.mFaceModule.getDetectBestSrcImageList(faceInfo, imageInstance);
        if (imageList != null && imageList.size() > 0) {
            this.mBase64ImageSrcMap.put("bestSrcDetectImage_" + index + "_" + totalScore, imageList.get(0));
        }

    }

    public void reset() {
        this.mDetectCount = 0;
        if (this.mSoundPlayHelper != null) {
            this.mSoundPlayHelper.release();
        }

        if (this.mBase64ImageCropMap != null) {
            this.mBase64ImageCropMap.clear();
        }

        if (this.mBase64ImageSrcMap != null) {
            this.mBase64ImageSrcMap.clear();
        }

        this.mIsFirstTipsed = false;
        this.mIsProcessing = false;
    }

    private class FaceProcessRunnable implements Runnable {
        private byte[] imageData;

        public FaceProcessRunnable(byte[] imageData) {
            this.imageData = imageData;
        }

        public void run() {
            FaceDetectStrategyExtModule.this.processStrategy(this.imageData);
            --FaceDetectStrategyExtModule.mProcessCount;
        }
    }
}
