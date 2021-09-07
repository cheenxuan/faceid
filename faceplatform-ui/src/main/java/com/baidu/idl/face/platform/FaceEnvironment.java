package com.baidu.idl.face.platform;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: xuan
 * Created on 2021/9/7 09:33.
 * <p>
 * Describe:
 */
public final class FaceEnvironment {
    public static final String TAG = "Baidu-IDL-FaceSDK";
    public static final String OS = "android";
    public static final String SDK_VERSION = "4.1.1";
    public static final int VALUE_MIN_FACE_SIZE = 200;
    public static final float VALUE_NOT_FACE_THRESHOLD = 0.6F;
    public static final float VALUE_BRIGHTNESS = 82.0F;
    public static final float VALUE_MAX_BRIGHTNESS = 200.0F;
    public static final float VALUE_BLURNESS = 0.7F;
    public static final float VALUE_OCCLUSION = 0.5F;
    public static final int VALUE_HEAD_PITCH = 8;
    public static final int VALUE_HEAD_YAW = 8;
    public static final int VALUE_HEAD_ROLL = 8;
    public static final float VALUE_CLOSE_EYES = 0.7F;
    public static final int VALUE_CACHE_IMAGE_NUM = 3;
    public static final List<LivenessTypeEnum> livenessTypeDefaultList = new ArrayList();
    public static final int VALUE_DECODE_THREAD_NUM = 2;
    public static final int VALUE_LIVENESS_DEFAULT_RANDOM_COUNT = 3;
    public static final int VALUE_IMAGESTANCE_IS_MIRROR = 1;
    public static final float VALUE_MASK_THRESHOLD = 0.7F;
    public static final boolean VALUE_OPEN_ONLINE = true;
    public static final boolean VALUE_OPEN_MASK = true;
    public static final float VALUE_CROP_FOREHEADEXTEND = 0.22222222F;
    public static final float VALUE_CROP_CHINEXTEND = 0.11111111F;
    public static final float VALUE_CROP_ENLARGERATIO = 1.5F;
    public static final int VALUE_CROP_HEIGHT = 640;
    public static final int VALUE_CROP_WIDTH = 480;
    public static final float VALUE_SCALE = 1.0F;
    public static final int VALUE_SEC_TYPE = 0;
    public static final float VALUE_FAR_RATIO = 0.4F;
    public static final float VALUE_CLOSED_RATIO = 1.0F;
    public static final long TIME_DETECT_MODULE = 15000L;
    public static final long TIME_LIVENESS_COURSE = 5000L;
    public static long TIME_TIPS_REPEAT = 2000L;
    public static long TIME_MODULE = 0L;
    public static long TIME_DETECT_NO_FACE_CONTINUOUS = 1000L;
    public static long TIME_LIVENESS_MODULE = 15000L;
    private static int[] mSoundIds;
    private static int[] mTipsTextIds;

    public FaceEnvironment() {
    }

    public static void setSoundId(FaceStatusNewEnum status, int soundId) {
        if (mSoundIds != null) {
            try {
                mSoundIds[status.ordinal()] = soundId;
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    public static int getSoundId(FaceStatusNewEnum status) {
        int soundId = mSoundIds[status.ordinal()];
        return soundId;
    }

    public static void setTipsId(FaceStatusNewEnum status, int tipsId) {
        if (mTipsTextIds != null) {
            try {
                mTipsTextIds[status.ordinal()] = tipsId;
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    public static int getTipsId(FaceStatusNewEnum status) {
        int tipsId = mTipsTextIds[status.ordinal()];
        return tipsId;
    }

    static {
        livenessTypeDefaultList.add(LivenessTypeEnum.Eye);
        livenessTypeDefaultList.add(LivenessTypeEnum.Mouth);
        livenessTypeDefaultList.add(LivenessTypeEnum.HeadRight);
        mSoundIds = new int[FaceStatusNewEnum.values().length];
        mTipsTextIds = new int[FaceStatusNewEnum.values().length];

        for(int i = 0; i < mSoundIds.length; ++i) {
            mSoundIds[i] = 0;
            mTipsTextIds[i] = 0;
        }

    }
}

