package com.baidu.idl.face.platform.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author: xuan
 * Created on 2021/9/7 09:16.
 * <p>
 * Describe:
 */
public class MD5Utils {
    public MD5Utils() {
    }

    public static String encryption(byte[] message) {
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(message);
            StringBuilder hex = new StringBuilder(hash.length * 2);
            byte[] var3 = hash;
            int var4 = hash.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                byte b = var3[var5];
                int i = b & 255;
                if (i < 16) {
                    hex.append('0');
                }

                hex.append(Integer.toHexString(i));
            }

            return hex.toString();
        } catch (NoSuchAlgorithmException var8) {
            var8.printStackTrace();
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return "";
    }
}
