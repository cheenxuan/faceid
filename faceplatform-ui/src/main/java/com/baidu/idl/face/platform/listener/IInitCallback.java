package com.baidu.idl.face.platform.listener;

/**
 * Author: xuan
 * Created on 2021/9/7 09:40.
 * <p>
 * Describe:
 */
public interface IInitCallback {
    void initSuccess();

    void initFailure(int var1, String var2);
}
