package com.baidu.idl.face.platform.utils;

import android.os.Build.VERSION;
/**
 * Author: xuan
 * Created on 2021/9/7 09:17.
 * <p>
 * Describe:
 */
public final class APIUtils {
    private APIUtils() {
    }

    public static boolean hasFroyo() {
        return VERSION.SDK_INT >= 8;
    }

    public static boolean hasGingerbread() {
        return VERSION.SDK_INT >= 9;
    }

    public static boolean hasHoneycomb() {
        return VERSION.SDK_INT >= 11;
    }

    public static boolean hasHoneycombMR1() {
        return VERSION.SDK_INT >= 12;
    }

    public static boolean hasICS() {
        return VERSION.SDK_INT >= 14;
    }

    public static boolean hasJellyBean() {
        return VERSION.SDK_INT >= 16;
    }

    public static boolean hasJellyBeanMR1() {
        return VERSION.SDK_INT >= 17;
    }
}

