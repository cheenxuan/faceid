package com.baidu.idl.face.platform;

/**
 * Author: xuan
 * Created on 2021/9/7 09:34.
 * <p>
 * Describe:
 */
public enum LicenseStatusEnum {
    StateSuccess,
    StateWarningValidityComing,
    StateErrorBegin,
    StateErrorNotFindLicense,
    StateErrorExpired,
    StateErrorAuthorized,
    StateErrorNetwork,
    StateNotInit,
    StateInitializing,
    StateUnknown;

    private LicenseStatusEnum() {
    }

    public static LicenseStatusEnum getLicenseStatus(int statusCode) {
        LicenseStatusEnum status = StateUnknown;
        switch(statusCode) {
            default:
                status = StateUnknown;
                return status;
        }
    }
}
