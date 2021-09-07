package com.baidu.idl.face.platform.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Author: xuan
 * Created on 2021/9/7 09:30.
 * <p>
 * Describe:
 */
public final class SharedPrefHelper {
    private static final String SHARED_PREFERENCES_NAME_FACE_VALUE = "face_sdk_value";

    private SharedPrefHelper() {
    }

    private static SharedPreferences getPref(Context context) {
        return context.getSharedPreferences("face_sdk_value", 0);
    }
}
