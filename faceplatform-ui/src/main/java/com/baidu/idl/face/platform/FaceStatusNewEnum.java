package com.baidu.idl.face.platform;

/**
 * Author: xuan
 * Created on 2021/9/7 09:34.
 * <p>
 * Describe:
 */
public enum FaceStatusNewEnum {
    OK,
    DetectRemindCodeBeyondPreviewFrame,
    DetectRemindCodeNoFaceDetected,
    DetectRemindCodeMuchIllumination,
    DetectRemindCodePoorIllumination,
    DetectRemindCodeImageBlured,
    DetectRemindCodeTooFar,
    DetectRemindCodeTooClose,
    DetectRemindCodePitchOutofDownRange,
    DetectRemindCodePitchOutofUpRange,
    DetectRemindCodeYawOutofLeftRange,
    DetectRemindCodeYawOutofRightRange,
    DetectRemindCodeOcclusionLeftEye,
    DetectRemindCodeOcclusionRightEye,
    DetectRemindCodeOcclusionNose,
    DetectRemindCodeOcclusionMouth,
    DetectRemindCodeOcclusionLeftContour,
    DetectRemindCodeOcclusionRightContour,
    DetectRemindCodeOcclusionChinContour,
    DetectRemindCodeTimeout,
    FaceLivenessActionTypeLiveEye,
    FaceLivenessActionTypeLiveMouth,
    FaceLivenessActionTypeLiveYawRight,
    FaceLivenessActionTypeLiveYawLeft,
    FaceLivenessActionTypeLivePitchUp,
    FaceLivenessActionTypeLivePitchDown,
    FaceLivenessActionTypeLiveYaw,
    FaceLivenessActionComplete,
    FaceLivenessActionCodeTimeout,
    DetectRemindCodeLeftEyeClosed,
    DetectRemindCodeRightEyeClosed;

    private FaceStatusNewEnum() {
    }
}
