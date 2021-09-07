package com.baidu.idl.face.platform.utils;

import android.text.TextUtils;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Author: xuan
 * Created on 2021/9/7 09:15.
 * <p>
 * Describe:
 */
public class EncodeUtil {
    public EncodeUtil() {
    }

    public static String base64Encode(byte[] bytes) {
        return Base64.encodeToString(bytes, 2);
    }

    public static byte[] base64Decode(String base64Code) throws Exception {
        return TextUtils.isEmpty(base64Code) ? null : Base64.decode(base64Code, 2);
    }

    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(1, new SecretKeySpec(base64Decode(encryptKey), "AES"));
        return cipher.doFinal(content.getBytes("utf-8"));
    }

    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(2, new SecretKeySpec(base64Decode(decryptKey), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return TextUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }
}
