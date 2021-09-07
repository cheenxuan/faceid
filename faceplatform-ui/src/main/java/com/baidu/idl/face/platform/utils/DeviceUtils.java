package com.baidu.idl.face.platform.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.UUID;

/**
 * Author: xuan
 * Created on 2021/9/7 09:14.
 * <p>
 * Describe:
 */
public class DeviceUtils {
    private static final String TAG = DeviceUtils.class.getSimpleName();

    public DeviceUtils() {
    }

    public static String getDeviceCode(Context context) {
        String code = "";

        try {
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            code = tm.getDeviceId();
            code = MD5Utils.encryption(code.getBytes());
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return code;
    }

    public static String getAndroidID(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), "android_id");

        try {
            androidId = MD5Utils.encryption(androidId.getBytes());
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return androidId;
    }

    public static String getSerialNumber(Context context) {
        return Build.SERIAL;
    }

    public static String getUUID() {
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }
}