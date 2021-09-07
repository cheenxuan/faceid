package com.baidu.idl.face.platform.model;

/**
 * Author: xuan
 * Created on 2021/9/7 09:32.
 * <p>
 * Describe:
 */
public class ImageInfo {
    private String base64;
    private String secBase64;

    public ImageInfo() {
    }

    public String getBase64() {
        return this.base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getSecBase64() {
        return this.secBase64;
    }

    public void setSecBase64(String secBase64) {
        this.secBase64 = secBase64;
    }
}
