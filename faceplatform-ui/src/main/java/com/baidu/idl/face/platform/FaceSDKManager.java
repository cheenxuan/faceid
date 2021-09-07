package com.baidu.idl.face.platform;

import android.content.Context;
import android.graphics.Bitmap;
import com.baidu.idl.face.platform.listener.IInitCallback;
import com.baidu.idl.face.platform.model.FaceExtInfo;
import com.baidu.idl.face.platform.stat.Ast;
import com.baidu.idl.face.platform.strategy.FaceDetectStrategyExtModule;
import com.baidu.idl.face.platform.strategy.FaceLivenessStrategyExtModule;
import com.baidu.idl.face.platform.utils.Base64Utils;
import com.baidu.idl.face.platform.utils.BitmapUtils;
import com.baidu.idl.main.facesdk.FaceActionLive;
import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.FaceCrop;
import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.FaceSec;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceCropParam;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceIsOutBoundary;
import com.baidu.idl.main.facesdk.model.BDFaceSDKConfig;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon.AlignType;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon.BDFaceActionLiveType;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon.BDFaceCoreRunMode;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon.BDFaceLogInfo;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon.DetectType;
import com.baidu.liantian.ac.LH;
import com.baidu.vis.unified.license.AndroidLicenser;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: xuan
 * Created on 2021/9/7 09:30.
 * <p>
 * Describe:
 */
public class FaceSDKManager {
    private static FaceSDKManager instance = null;
    private Context mContext;
    private FaceAuth mFaceAuth = new FaceAuth();
    private FaceDetect mFaceDetect;
    private FaceCrop mFaceCrop;
    private FaceActionLive mFaceActionLive;
    private FaceConfig mFaceConfig = new FaceConfig();
    private FaceSec mFaceSec;
    private boolean mInitFlag;

    private FaceSDKManager() {
        this.mFaceAuth.setActiveLog(BDFaceLogInfo.BDFACE_LOG_TYPE_DEBUG, 1);
        this.mFaceAuth.setCoreConfigure(BDFaceCoreRunMode.BDFACE_LITE_POWER_NO_BIND, 2);
        this.mFaceSec = new FaceSec();
    }

    public static FaceSDKManager getInstance() {
        if (instance == null) {
            synchronized(FaceSDKManager.class) {
                if (instance == null) {
                    instance = new FaceSDKManager();
                }
            }
        }

        return instance;
    }

    public void initialize(Context context, String licenseID, IInitCallback callback) {
        this.initialize(context, licenseID, "", callback);
    }

    public void initialize(final Context context, String licenseID, String licenseFileName, final IInitCallback callback) {
        this.mContext = context.getApplicationContext();
        AndroidLicenser.setAgree(true);
        AndroidLicenser.setOnline(true);
        this.mFaceAuth.initLicense(context, licenseID, licenseFileName, true, new Callback() {
            public void onResponse(int code, String response) {
                if (code == 0) {
                    Ast.getInstance().init(context, FaceEnvironment.SDK_VERSION, "facenormal");
                    if (mFaceConfig.getSecType() == 1) {
                        int status = mFaceSec.i(mContext);
                        if (status != 0) {
                            callback.initFailure(-1, "缺少加密文件");
                            return;
                        }
                    }

                    initModel(context, callback);
                } else if (callback != null) {
                    callback.initFailure(code, response);
                }

            }
        });
    }

    private void initModel(Context context, final IInitCallback callback) {
        this.mFaceDetect = new FaceDetect();
        this.mFaceCrop = new FaceCrop();
        this.mFaceActionLive = new FaceActionLive();
        BDFaceSDKConfig config = new BDFaceSDKConfig();
        config.minFaceSize = this.mFaceConfig.getMinFaceSize();
        config.notRGBFaceThreshold = this.mFaceConfig.getNotFaceValue();
        config.isMouthClose = true;
        config.isEyeClose = true;
        config.isCropFace = true;
        config.isCheckBlur = true;
        config.isIllumination = true;
        config.isOcclusion = true;
        config.isHeadPose = true;
        config.maxDetectNum = 1;
        this.mFaceDetect.loadConfig(config);
        this.mFaceDetect.initModel(context, "detect/detect_rgb-customized-pa-faceid4_0.model.int8.0.0.6.1", "align/align-customized-pa-offlineCapture_withScore_quant_20200909.model.int8.6.4.7.1", DetectType.DETECT_VIS, AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE, new Callback() {
            public void onResponse(int code, String response) {
                if (code != 0 && callback != null) {
                    callback.initFailure(code, response);
                }

            }
        });
        this.mFaceDetect.initQuality(context, "blur/blur-customized-pa-blurnet_9768.model.int8-3.0.9.1", "occlusion/occlusion-customized-pa-occ.model.float32.2.0.6.1", new Callback() {
            public void onResponse(int code, String response) {
                if (code != 0 && callback != null) {
                    callback.initFailure(code, response);
                }

            }
        });
        this.mFaceCrop.initFaceCrop(new Callback() {
            public void onResponse(int code, String response) {
                if (code != 0 && callback != null) {
                    callback.initFailure(code, response);
                }

            }
        });
        this.mFaceActionLive.initActionLiveModel(context, "eyes_close/eyes-customized-pa-caiji.model.float32.1.0.3.1", "mouth_close/mouth-customized-pa-caiji.model.float32.1.0.3.1", new Callback() {
            public void onResponse(int code, String response) {
                if (code != 0 && callback != null) {
                    callback.initFailure(code, response);
                }

                if (code == 0 && callback != null) {
                    FaceSDKManager.this.mInitFlag = true;
                    callback.initSuccess();
                }

            }
        });
    }

    public FaceConfig getFaceConfig() {
        return this.mFaceConfig;
    }

    public void setFaceConfig(FaceConfig config) {
        this.mFaceConfig = config;
    }

    public boolean getInitFlag() {
        return this.mInitFlag;
    }

    public static String getVersion() {
        return "4.1.1";
    }

    public void release() {
        synchronized(FaceSDKManager.class) {
            if (instance != null) {
                instance.mInitFlag = false;
                instance.mContext = null;
                instance.releaseModel();
                instance = null;
            }

            Ast.getInstance().immediatelyUpload();
        }
    }

    private void releaseModel() {
        if (this.mFaceDetect != null) {
            this.mFaceDetect.uninitModel();
        }

        if (this.mFaceCrop != null) {
            this.mFaceCrop.uninitFaceCrop();
        }

        if (this.mFaceActionLive != null) {
            this.mFaceActionLive.uninitActionLiveModel();
        }

        if (this.mFaceConfig != null) {
            this.mFaceConfig = null;
        }

    }

    public IDetectStrategy getDetectStrategyModule() {
        FaceDetectStrategyExtModule module = new FaceDetectStrategyExtModule(this.mContext);
        module.setConfigValue(this.mFaceConfig);
        return module;
    }

    public ILivenessStrategy getLivenessStrategyModule(ILivenessViewCallback viewCallback) {
        FaceLivenessStrategyExtModule module = new FaceLivenessStrategyExtModule(this.mContext);
        module.setILivenessViewCallback(viewCallback);
        module.setConfig(this.mFaceConfig);
        return module;
    }

    public String getZid(Context context) {
        return LH.gzfi(context, (String)null, 5002, (String)null);
    }

    public FaceInfo[] detect(BDFaceImageInstance imageInstance) {
        if (this.mFaceDetect == null) {
            return null;
        } else {
            FaceInfo[] faceInfos = this.mFaceDetect.track(DetectType.DETECT_VIS, imageInstance);
            return faceInfos;
        }
    }

    public int processLiveness(BDFaceActionLiveType actionLiveType, BDFaceImageInstance imageInstance, FaceExtInfo faceInfo, AtomicInteger isExist) {
        return this.mFaceActionLive.actionLive(actionLiveType, imageInstance, faceInfo.getmLandmarks(), isExist);
    }

    public void clearActionHistory() {
        if (this.mFaceActionLive != null) {
            this.mFaceActionLive.clearHistory();
        }

    }

    public BDFaceImageInstance cropFace(BDFaceImageInstance imageInstance, float[] landmarks, int height, int width) {
        BDFaceCropParam cropParam = new BDFaceCropParam();
        cropParam.foreheadExtend = FaceEnvironment.VALUE_CROP_FOREHEADEXTEND;
        cropParam.chinExtend = FaceEnvironment.VALUE_CROP_CHINEXTEND;
        cropParam.enlargeRatio = this.getFaceConfig().getEnlargeRatio();
        cropParam.height = height;
        cropParam.width = width;
        BDFaceIsOutBoundary isOutBoundary = this.mFaceCrop.cropFaceByLandmarkIsOutofBoundary(imageInstance, landmarks, cropParam);
        if (isOutBoundary == null) {
            return null;
        } else {
            BDFaceImageInstance cropInstance = this.mFaceCrop.cropFaceByLandmarkParam(imageInstance, landmarks, cropParam);
            return cropInstance;
        }
    }

    public Bitmap scaleImage(Bitmap bitmap, int width, int height) {
        return BitmapUtils.scale(bitmap, width, height);
    }

    public Bitmap scaleImage(Bitmap bitmap, float scale) {
        return BitmapUtils.scale(bitmap, scale);
    }

    public byte[] compressImage(Bitmap bitmap, int quality) {
        return BitmapUtils.bitmapCompress(bitmap, quality);
    }

    public String bitmapToBase64(byte[] data) {
        return Base64Utils.encodeToString(data, 2);
    }

    public String imageSec(byte[] imageData) {
        return this.mFaceSec.e(imageData);
    }
}
