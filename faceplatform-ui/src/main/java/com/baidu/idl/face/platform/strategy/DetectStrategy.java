package com.baidu.idl.face.platform.strategy;

import android.graphics.Rect;
import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.model.FaceExtInfo;

/**
 * Author: xuan
 * Created on 2021/9/7 09:21.
 * <p>
 * Describe:
 */
class DetectStrategy {
    private static final String TAG = DetectStrategy.class.getSimpleName();
    private FaceStatusNewEnum mCurrentFaceStatus;
    private long mDuration = 0L;
    private boolean mTimeoutFlag = false;
    private float mTotalCropScore;

    public DetectStrategy() {
    }

    public boolean isTimeout() {
        return this.mTimeoutFlag;
    }

    public FaceStatusNewEnum checkDetect(Rect detectRect, FaceExtInfo[] faceInfos, FaceConfig faceConfig) {
        FaceStatusNewEnum status = FaceStatusNewEnum.OK;
        if (faceInfos != null && faceInfos.length > 0 && faceInfos[0] != null && faceConfig != null) {
            if ((float)faceInfos[0].getFaceWidth() > (float)detectRect.width() * faceConfig.getFaceClosedRatio()) {
                status = FaceStatusNewEnum.DetectRemindCodeTooClose;
                this.checkTimeout(status, faceConfig);
                return status;
            } else if ((float)faceInfos[0].getFaceWidth() < (float)detectRect.width() * faceConfig.getFaceFarRatio()) {
                status = FaceStatusNewEnum.DetectRemindCodeTooFar;
                this.checkTimeout(status, faceConfig);
                return status;
            } else if (faceInfos[0].getLandmarksOutOfDetectCount(detectRect) > 10) {
                status = FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame;
                this.checkTimeout(status, faceConfig);
                return status;
            } else {
                return status;
            }
        } else {
            status = FaceStatusNewEnum.DetectRemindCodeNoFaceDetected;
            this.checkTimeout(status, faceConfig);
            return status;
        }
    }

    private void checkTimeout(FaceStatusNewEnum status, FaceConfig faceConfig) {
        if (faceConfig != null) {
            if (this.mCurrentFaceStatus == null || this.mCurrentFaceStatus != status) {
                this.mCurrentFaceStatus = status;
                this.mDuration = System.currentTimeMillis();
                this.mTimeoutFlag = false;
            }

            long t = System.currentTimeMillis();
            if (this.mCurrentFaceStatus == status && t - this.mDuration > faceConfig.getTimeDetectModule()) {
                this.mTimeoutFlag = true;
            }

        }
    }

    public FaceStatusNewEnum getDetectState(FaceExtInfo[] faceInfos, Rect detectRect, boolean isMasked, FaceConfig faceConfig) {
        FaceStatusNewEnum status = FaceStatusNewEnum.OK;
        if (faceInfos != null && faceInfos.length > 0 && faceInfos[0] != null && faceConfig != null) {
            if (faceInfos[0].getLandmarksOutOfDetectCount(detectRect) > 10) {
                status = FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame;
                this.checkTimeout(status, faceConfig);
                return status;
            } else {
                return this.getModuleState(faceInfos, detectRect, isMasked, faceConfig);
            }
        } else {
            status = FaceStatusNewEnum.DetectRemindCodeNoFaceDetected;
            this.checkTimeout(status, faceConfig);
            return status;
        }
    }

    private FaceStatusNewEnum getModuleState(FaceExtInfo[] faceInfos, Rect detectRect, boolean isMasked, FaceConfig faceConfig) {
        FaceStatusNewEnum status = FaceStatusNewEnum.OK;
        FaceExtInfo faceInfo = faceInfos[0];
        this.mTotalCropScore = 0.0F;
        if (!isMasked) {
            if (faceInfo.getOcclusion().leftEye > faceConfig.getOcclusionLeftEyeValue()) {
                return FaceStatusNewEnum.DetectRemindCodeOcclusionLeftEye;
            }

            this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().leftEye;
            if (faceInfo.getOcclusion().rightEye > faceConfig.getOcclusionRightEyeValue()) {
                return FaceStatusNewEnum.DetectRemindCodeOcclusionRightEye;
            }

            this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().rightEye;
            if (faceInfo.getOcclusion().nose > faceConfig.getOcclusionNoseValue()) {
                return FaceStatusNewEnum.DetectRemindCodeOcclusionNose;
            }

            this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().nose;
            if (faceInfo.getOcclusion().mouth > faceConfig.getOcclusionMouthValue()) {
                return FaceStatusNewEnum.DetectRemindCodeOcclusionMouth;
            }

            this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().mouth;
            if (faceInfo.getOcclusion().leftCheek > faceConfig.getOcclusionLeftContourValue()) {
                return FaceStatusNewEnum.DetectRemindCodeOcclusionLeftContour;
            }

            this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().leftCheek;
            if (faceInfo.getOcclusion().rightCheek > faceConfig.getOcclusionRightContourValue()) {
                return FaceStatusNewEnum.DetectRemindCodeOcclusionRightContour;
            }

            this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().rightCheek;
            if (faceInfo.getOcclusion().chin > faceConfig.getOcclusionChinValue()) {
                return FaceStatusNewEnum.DetectRemindCodeOcclusionChinContour;
            }

            this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().chin;
        }

        if ((float)faceInfos[0].getFaceWidth() < (float)detectRect.width() * faceConfig.getFaceFarRatio()) {
            status = FaceStatusNewEnum.DetectRemindCodeTooFar;
            this.checkTimeout(status, faceConfig);
            return status;
        } else if ((float)faceInfos[0].getFaceWidth() > (float)detectRect.width() * faceConfig.getFaceClosedRatio()) {
            status = FaceStatusNewEnum.DetectRemindCodeTooClose;
            this.checkTimeout(status, faceConfig);
            return status;
        } else if (faceInfo.getPitch() < (float)(-faceConfig.getHeadPitchValue() - 2)) {
            return FaceStatusNewEnum.DetectRemindCodePitchOutofDownRange;
        } else if (faceInfo.getPitch() > (float)(faceConfig.getHeadPitchValue() - 2)) {
            return FaceStatusNewEnum.DetectRemindCodePitchOutofUpRange;
        } else {
            this.mTotalCropScore += (45.0F - Math.abs(faceInfo.getPitch())) / 45.0F;
            if (faceInfo.getYaw() > (float)faceConfig.getHeadYawValue()) {
                status = FaceStatusNewEnum.DetectRemindCodeYawOutofLeftRange;
                return status;
            } else if (faceInfo.getYaw() < (float)(-faceConfig.getHeadYawValue())) {
                status = FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange;
                return status;
            } else {
                this.mTotalCropScore += (45.0F - Math.abs(faceInfo.getYaw())) / 45.0F;
                if (faceInfo.getRoll() > (float)faceConfig.getHeadRollValue()) {
                    return FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange;
                } else if (faceInfo.getRoll() < (float)(-faceConfig.getHeadRollValue())) {
                    return FaceStatusNewEnum.DetectRemindCodeYawOutofLeftRange;
                } else {
                    this.mTotalCropScore += (45.0F - Math.abs(faceInfo.getRoll())) / 45.0F;
                    if (faceInfo.getBluriness() > faceConfig.getBlurnessValue()) {
                        return FaceStatusNewEnum.DetectRemindCodeImageBlured;
                    } else {
                        this.mTotalCropScore += 1.0F - faceInfo.getBluriness();
                        if ((float)faceInfo.getIllum() < faceConfig.getBrightnessValue()) {
                            return FaceStatusNewEnum.DetectRemindCodePoorIllumination;
                        } else if ((float)faceInfo.getIllum() > faceConfig.getBrightnessMaxValue()) {
                            return FaceStatusNewEnum.DetectRemindCodeMuchIllumination;
                        } else if (faceInfo.getLeftEyeClose() > faceConfig.getEyeClosedValue()) {
                            return FaceStatusNewEnum.DetectRemindCodeLeftEyeClosed;
                        } else {
                            this.mTotalCropScore += 1.0F - faceInfo.getLeftEyeClose();
                            if (faceInfo.getRightEyeClose() > faceConfig.getEyeClosedValue()) {
                                return FaceStatusNewEnum.DetectRemindCodeRightEyeClosed;
                            } else {
                                this.mTotalCropScore += 1.0F - faceInfo.getRightEyeClose();
                                return status;
                            }
                        }
                    }
                }
            }
        }
    }

    public FaceStatusNewEnum getCropStatus(FaceExtInfo faceInfo, FaceConfig faceConfig) {
        if (faceInfo != null && faceConfig != null) {
            this.mTotalCropScore = 0.0F;
            if (faceInfo.getOcclusion().leftEye > faceConfig.getOcclusionLeftEyeValue()) {
                return FaceStatusNewEnum.DetectRemindCodeOcclusionLeftEye;
            } else {
                this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().leftEye;
                if (faceInfo.getOcclusion().rightEye > faceConfig.getOcclusionRightEyeValue()) {
                    return FaceStatusNewEnum.DetectRemindCodeOcclusionRightEye;
                } else {
                    this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().rightEye;
                    if (faceInfo.getOcclusion().nose > faceConfig.getOcclusionNoseValue()) {
                        return FaceStatusNewEnum.DetectRemindCodeOcclusionNose;
                    } else {
                        this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().nose;
                        if (faceInfo.getOcclusion().mouth > faceConfig.getOcclusionMouthValue()) {
                            return FaceStatusNewEnum.DetectRemindCodeOcclusionMouth;
                        } else {
                            this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().mouth;
                            if (faceInfo.getOcclusion().leftCheek > faceConfig.getOcclusionLeftContourValue()) {
                                return FaceStatusNewEnum.DetectRemindCodeOcclusionLeftContour;
                            } else {
                                this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().leftCheek;
                                if (faceInfo.getOcclusion().rightCheek > faceConfig.getOcclusionRightContourValue()) {
                                    return FaceStatusNewEnum.DetectRemindCodeOcclusionRightContour;
                                } else {
                                    this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().rightCheek;
                                    if (faceInfo.getOcclusion().chin > faceConfig.getOcclusionChinValue()) {
                                        return FaceStatusNewEnum.DetectRemindCodeOcclusionChinContour;
                                    } else {
                                        this.mTotalCropScore += 1.0F - faceInfo.getOcclusion().chin;
                                        if (faceInfo.getPitch() < (float)(-faceConfig.getHeadPitchValue() - 2)) {
                                            return FaceStatusNewEnum.DetectRemindCodePitchOutofDownRange;
                                        } else if (faceInfo.getPitch() > (float)(faceConfig.getHeadPitchValue() - 2)) {
                                            return FaceStatusNewEnum.DetectRemindCodePitchOutofUpRange;
                                        } else {
                                            this.mTotalCropScore += (45.0F - Math.abs(faceInfo.getPitch())) / 45.0F;
                                            if (faceInfo.getYaw() < (float)(-faceConfig.getHeadYawValue())) {
                                                return FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange;
                                            } else if (faceInfo.getYaw() > (float)faceConfig.getHeadYawValue()) {
                                                return FaceStatusNewEnum.DetectRemindCodeYawOutofLeftRange;
                                            } else {
                                                this.mTotalCropScore += (45.0F - Math.abs(faceInfo.getYaw())) / 45.0F;
                                                if (faceInfo.getRoll() > (float)faceConfig.getHeadRollValue()) {
                                                    return FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange;
                                                } else if (faceInfo.getRoll() < (float)(-faceConfig.getHeadRollValue())) {
                                                    return FaceStatusNewEnum.DetectRemindCodeYawOutofLeftRange;
                                                } else {
                                                    this.mTotalCropScore += (45.0F - Math.abs(faceInfo.getRoll())) / 45.0F;
                                                    if (faceInfo.getBluriness() > faceConfig.getBlurnessValue()) {
                                                        return FaceStatusNewEnum.DetectRemindCodeImageBlured;
                                                    } else {
                                                        this.mTotalCropScore += 1.0F - faceInfo.getBluriness();
                                                        if ((float)faceInfo.getIllum() < faceConfig.getBrightnessValue()) {
                                                            return FaceStatusNewEnum.DetectRemindCodePoorIllumination;
                                                        } else if ((float)faceInfo.getIllum() > faceConfig.getBrightnessMaxValue()) {
                                                            return FaceStatusNewEnum.DetectRemindCodeMuchIllumination;
                                                        } else if (faceInfo.getLeftEyeClose() > faceConfig.getEyeClosedValue()) {
                                                            return FaceStatusNewEnum.DetectRemindCodeLeftEyeClosed;
                                                        } else {
                                                            this.mTotalCropScore += 1.0F - faceInfo.getLeftEyeClose();
                                                            if (faceInfo.getRightEyeClose() > faceConfig.getEyeClosedValue()) {
                                                                return FaceStatusNewEnum.DetectRemindCodeRightEyeClosed;
                                                            } else {
                                                                this.mTotalCropScore += 1.0F - faceInfo.getRightEyeClose();
                                                                return FaceStatusNewEnum.OK;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            return FaceStatusNewEnum.DetectRemindCodeNoFaceDetected;
        }
    }

    private boolean isDefaultDetectStatus(FaceStatusNewEnum status) {
        boolean flag = false;
        switch(status) {
            case DetectRemindCodePoorIllumination:
            case DetectRemindCodeImageBlured:
            case DetectRemindCodeOcclusionLeftEye:
            case DetectRemindCodeOcclusionRightEye:
            case DetectRemindCodeOcclusionNose:
            case DetectRemindCodeOcclusionMouth:
            case DetectRemindCodeOcclusionLeftContour:
            case DetectRemindCodeOcclusionRightContour:
            case DetectRemindCodeOcclusionChinContour:
            case DetectRemindCodeTooFar:
            case DetectRemindCodeTooClose:
            case DetectRemindCodeNoFaceDetected:
            case DetectRemindCodePitchOutofUpRange:
            case DetectRemindCodePitchOutofDownRange:
            case DetectRemindCodeYawOutofLeftRange:
            case DetectRemindCodeYawOutofRightRange:
                flag = true;
            default:
                return flag;
        }
    }

    public float getTotalCropScore() {
        return this.mTotalCropScore;
    }

    public void reset() {
        this.mDuration = 0L;
        this.mTimeoutFlag = false;
        this.mCurrentFaceStatus = null;
        this.mTotalCropScore = 0.0F;
    }
}