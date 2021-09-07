package com.baidu.idl.face.platform.stat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Author: xuan
 * Created on 2021/9/7 09:24.
 * <p>
 * Describe:
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    private FileUtil() {
        throw new RuntimeException("This class instance can not be created.");
    }

    public static boolean savePropertiesFile(File file, Properties properties) {
        BufferedOutputStream out = null;

        boolean var4;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            properties.store(out, (String)null);
            boolean e = true;
            boolean var5 = e;
            return var5;
        } catch (Exception var15) {
            var15.printStackTrace();
            var4 = false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException var14) {
                }
            }

        }

        return var4;
    }

    public static boolean loadPropertiesFile(File file, Properties properties) {
        BufferedInputStream in = null;

        boolean var4;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            properties.load(in);
            boolean e = true;
            boolean var5 = e;
            return var5;
        } catch (Exception var15) {
            var15.printStackTrace();
            var4 = false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var14) {
                }
            }

        }

        return var4;
    }

    public static boolean createFile(File file) {
        if (file == null) {
            return false;
        } else {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (file.exists() && file.length() > 3145728L) {
                file.delete();
            }

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException var2) {
                    var2.printStackTrace();
                    return false;
                }
            }

            return true;
        }
    }
}