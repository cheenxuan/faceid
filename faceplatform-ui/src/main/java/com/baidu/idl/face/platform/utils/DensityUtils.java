package com.baidu.idl.face.platform.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

/**
 * Author: xuan
 * Created on 2021/9/7 09:14.
 * <p>
 * Describe:
 */
public final class DensityUtils {
    private static final float DOT_FIVE = 0.5F;
    private static final int PORTRAIT_DEGREE_90 = 90;
    private static final int PORTRAIT_DEGREE_270 = 270;
    private static final String[] BUILD_MODELS = new String[]{"i700v", "A862W", "V8526"};

    private DensityUtils() {
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + DOT_FIVE);
    }

    public static int dip2px(Context context, float dip) {
        float density = getDensity(context);
        return (int) (dip * density + DOT_FIVE);
    }

    public static int px2dip(Context context, float px) {
        float density = getDensity(context);
        return (int) (px / density + DOT_FIVE);
    }

    public static int getDisplayWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static boolean supportCameraPortrait() {
        return APIUtils.hasFroyo() && !TextUtils.equals("GT-S5830i", Build.PRODUCT);
    }

    public static int getPortraitDegree() {
        int degree = PORTRAIT_DEGREE_90;

        int length = BUILD_MODELS.length;
        for (int i = 0; i < length; ++i) {
            String model = BUILD_MODELS[i];
            if (TextUtils.equals(model, Build.MODEL)) {
                degree = PORTRAIT_DEGREE_270;
                break;
            }
        }

        return degree;
    }
}
