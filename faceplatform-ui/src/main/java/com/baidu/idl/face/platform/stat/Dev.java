package com.baidu.idl.face.platform.stat;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Author: xuan
 * Created on 2021/9/7 09:22.
 * <p>
 * Describe:
 */
public class Dev {
    private static final String INSTALLATION = "INSTALLATION";
    private String uniqueID = "";
    private String brand = "";
    private String sysVersion = "";
    private String packagename = "";
    private String sdkVersion = "2.1.0.0";
    private boolean firstRun = false;

    public Dev() {
    }

    public String getUniqueID() {
        return this.uniqueID;
    }

    public String getBrand() {
        return this.brand;
    }

    public String getSysVersion() {
        return this.sysVersion;
    }

    public String getPackagename() {
        return this.packagename;
    }

    public String getSdkVersion() {
        return this.sdkVersion;
    }

    public boolean getFirstRun() {
        return this.firstRun;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    public void init(Context context) {
        if (context != null) {
            this.brand = Build.MODEL.replace(" ", "");
            this.sysVersion = VERSION.RELEASE;
            this.packagename = context.getPackageName();
            this.uniqueID = this.getUniqueIdFromFile(context);
            if (TextUtils.isEmpty(this.uniqueID)) {
                this.firstRun = true;
                this.uniqueID = this.generateUniquePsuedoID(context);
                this.writeInstallationFile(context, this.uniqueID);
            }

        }
    }

    public synchronized String getUniqueIdFromFile(Context context) {
        if (TextUtils.isEmpty(this.uniqueID)) {
            File installation = new File(context.getFilesDir(), INSTALLATION);

            try {
                this.uniqueID = this.readInstallationFile(installation);
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        return this.uniqueID;
    }

    private String readInstallationFile(File installation) throws IOException {
        String uniqueId = "";
        RandomAccessFile rf = null;

        try {
            rf = new RandomAccessFile(installation, "r");
            byte[] bytes = new byte[(int)rf.length()];
            rf.readFully(bytes);
            uniqueId = new String(bytes);
        } catch (Exception var8) {
            var8.printStackTrace();
        } finally {
            if (rf != null) {
                rf.close();
            }

        }

        return uniqueId;
    }

    private void writeInstallationFile(Context context, String uniqueID) {
        FileOutputStream out = null;

        try {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            out = new FileOutputStream(installation);
            out.write(uniqueID.getBytes());
        } catch (FileNotFoundException var15) {
            var15.printStackTrace();
        } catch (IOException var16) {
            var16.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }

        }

    }

    private String generateUniquePsuedoID(Context context) {
        String m_szDevIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10;
        String serial = null;

        try {
            serial = Build.class.getField("SERIAL").get((Object)null).toString();
        } catch (Exception var6) {
            serial = UUID.randomUUID().toString();
        }

        String androidId = Secure.getString(context.getContentResolver(), "android_id");
        if ("9774d56d682e549c".equals(androidId)) {
            androidId = UUID.randomUUID().toString();
        }

        String join = serial + androidId + m_szDevIDShort;
        return md5(join);
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);

            String hashtext;
            for(hashtext = number.toString(16); hashtext.length() < 32; hashtext = "0" + hashtext) {
            }

            return hashtext;
        } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException(var5);
        }
    }
}

