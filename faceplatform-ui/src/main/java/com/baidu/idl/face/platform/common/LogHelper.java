package com.baidu.idl.face.platform.common;

import android.text.TextUtils;
import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.network.LogRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Author: xuan
 * Created on 2021/9/7 09:28.
 * <p>
 * Describe:
 */
public class LogHelper {
    private static final String TAG = LogHelper.class.getSimpleName();
    private static HashMap<String, Object> logMap = new HashMap();
    private static ArrayList<Integer> logLivenessLiveness = new ArrayList();
    private static HashMap<String, Integer> logTipsMap = new HashMap();

    public LogHelper() {
    }

    public static void addLogWithKey(String key, Object value) {
        if (logMap != null && !logMap.containsKey(key)) {
            logMap.put(key, value);
        }

    }

    public static void addLog(String key, Object value) {
        if (logMap != null) {
            logMap.put(key, value);
        }

    }

    public static void addLivenessLog(int livenessIndex) {
        if (logLivenessLiveness != null && !logLivenessLiveness.contains(livenessIndex)) {
            logLivenessLiveness.add(livenessIndex);
        }

    }

    public static void addTipsLogWithKey(String status) {
        if (logTipsMap != null && !logTipsMap.containsKey(status)) {
            logTipsMap.put(status, 1);
        } else if (logTipsMap != null && logTipsMap.containsKey(status)) {
            int count = (Integer)logTipsMap.get(status) + 1;
            logTipsMap.put(status, count);
        }

    }

    private static String getLog() {
        StringBuilder log = new StringBuilder();
        try {
            int index = 0;
            log.append("{");

            for(Iterator var2 = logMap.entrySet().iterator(); var2.hasNext(); ++index) {
                Entry<String, Object> entry = (Map.Entry)var2.next();
                if (index == logMap.size() - 1) {
                    if (entry.getValue() instanceof String) {
                        log.append((String)entry.getKey() + ":'" + entry.getValue() + "'");
                    } else {
                        log.append((String)entry.getKey() + ":" + entry.getValue());
                    }
                } else {
                    if (entry.getValue() instanceof String) {
                        log.append((String)entry.getKey() + ":'" + entry.getValue() + "'");
                    } else {
                        log.append((String)entry.getKey() + ":" + entry.getValue());
                    }

                    log.append(",");
                }
            }

            if (logLivenessLiveness != null && logLivenessLiveness.size() > 0) {
                log.append(",lv:[");

                for(int i = 0; i < logLivenessLiveness.size(); ++i) {
                    if (i == logLivenessLiveness.size() - 1) {
                        log.append(logLivenessLiveness.get(i));
                    } else {
                        log.append(logLivenessLiveness.get(i) + ",");
                    }
                }

                log.append("]");
            }

            if (logTipsMap != null && logTipsMap.size() > 0) {
                log.append(",msg:{");
                log.append(getTipsMessage());
                log.append("}");
            }

            log.append("}");
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        logMap = new HashMap();
        logLivenessLiveness = new ArrayList();
        logTipsMap = new HashMap();
        return log.toString();
    }

    private static String getTipsMessage() {
        StringBuilder log = new StringBuilder();
        int index = 0;
        String key = "";

        for(Iterator var3 = logTipsMap.entrySet().iterator(); var3.hasNext(); ++index) {
            Entry<String, Integer> entry = (Map.Entry)var3.next();
            key = getTipsKey((String)entry.getKey());
            if (!TextUtils.isEmpty(key)) {
                log.append(key + ":" + entry.getValue());
                log.append(",");
            }
        }

        if (log.length() > 0) {
            log.deleteCharAt(log.length() - 1);
        }

        return log.toString();
    }

    private static String getTipsKey(String key) {
        String tipsKey = "";
        if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeOcclusionLeftEye.name())) {
            tipsKey = "leftEyeOccusion";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeOcclusionRightEye.name())) {
            tipsKey = "rightEyeOccusion";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeOcclusionNose.name())) {
            tipsKey = "noseOccusion";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeOcclusionMouth.name())) {
            tipsKey = "mouthOccusion";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeOcclusionLeftContour.name())) {
            tipsKey = "leftFaceOccusion";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeOcclusionRightContour.name())) {
            tipsKey = "rightFaceOccusion";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeOcclusionChinContour.name())) {
            tipsKey = "chinOccusion";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodePoorIllumination.name())) {
            tipsKey = "lightUp";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeImageBlured.name())) {
            tipsKey = "stayStill";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeTooFar.name())) {
            tipsKey = "moveClose";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeTooClose.name())) {
            tipsKey = "moveFurther";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodePitchOutofDownRange.name())) {
            tipsKey = "headUp";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodePitchOutofUpRange.name())) {
            tipsKey = "headDown";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange.name())) {
            tipsKey = "turnLeft";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeYawOutofLeftRange.name())) {
            tipsKey = "turnRight";
        } else if (TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeNoFaceDetected.name()) || TextUtils.equals(key, FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame.name())) {
            tipsKey = "moveFace";
        }

        return tipsKey;
    }

    public static void sendLog() {
        String message = getLog();
        LogRequest.sendLogMessage(message);
    }

    public static void clear() {
        logMap = new HashMap();
        logLivenessLiveness = new ArrayList();
        logTipsMap = new HashMap();
    }
}
