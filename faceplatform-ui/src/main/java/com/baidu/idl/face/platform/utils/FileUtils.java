package com.baidu.idl.face.platform.utils;

import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Author: xuan
 * Created on 2021/9/7 09:04.
 * <p>
 * Describe:
 */
public final class FileUtils {
    public static final int S_IRWXU = 448;
    public static final int S_IRUSR = 256;
    public static final int S_IWUSR = 128;
    public static final int S_IXUSR = 64;
    public static final int S_IRWXG = 56;
    public static final int S_IRGRP = 32;
    public static final int S_IWGRP = 16;
    public static final int S_IXGRP = 8;
    public static final int S_IRWXO = 7;
    public static final int S_IROTH = 4;
    public static final int S_IWOTH = 2;
    public static final int S_IXOTH = 1;
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");
    private static final Pattern RESERVED_CHARS_PATTERN = Pattern.compile("[\\\\/:\\*\\?\\\"<>|]");

    private FileUtils() {
    }

    public static boolean isFilenameSafe(File file) {
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    public static boolean isFilenameValid(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        } else {
            return !RESERVED_CHARS_PATTERN.matcher(name).find();
        }
    }

    public static void copyFile(String src, String dest) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(src);
            IoUtils.copyStream(fis, new File(dest));
        } catch (IOException var7) {
            var7.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fis);
        }

    }

    public static void copyFile(File src, File dest) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;

        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dest);
            in = fis.getChannel();
            out = fos.getChannel();
            in.transferTo(0L, in.size(), out);
        } catch (IOException var10) {
            var10.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fis);
            IoUtils.closeQuietly(in);
            IoUtils.closeQuietly(fos);
            IoUtils.closeQuietly(out);
        }

    }

    public static void copyDirectory(File src, File dest) throws IOException {
        if (src.exists()) {
            dest.mkdirs();
            File[] files = src.listFiles();
            if (files == null) {
                return;
            }

            File[] var3 = files;
            int var4 = files.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File file = var3[var5];
                if (file.isDirectory()) {
                    copyDirectory(file, new File(dest, file.getName()));
                } else {
                    copyFile(file, new File(dest, file.getName()));
                }
            }
        }

    }

    public static void ensureDir(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
                file.mkdirs();
            }
        } else {
            file.mkdirs();
        }

    }

    public static boolean ensureMkdir(File dir) {
        if (dir == null) {
            return false;
        } else {
            File tempDir = dir;

            for(int i = 1; tempDir.exists(); ++i) {
                tempDir = new File(dir.getParent(), dir.getName() + "(" + i + ")");
            }

            return tempDir.mkdir();
        }
    }

    public static void ensureParent(File file) {
        if (null != file) {
            File parentFile = file.getParentFile();
            if (null != parentFile && !parentFile.exists()) {
                parentFile.mkdirs();
            }
        }

    }

    public static void cleanDir(File dir) {
        deleteDir(dir, false);
    }

    public static void cleanDir(File dir, FilenameFilter filter) {
        deleteDir(dir, false, filter);
    }

    public static void cleanDir(File dir, FileFilter filter) {
        deleteDir(dir, false, filter);
    }

    public static void deleteDir(String dir) {
        deleteDir(new File(dir));
    }

    public static void deleteDir(File dir) {
        deleteDir(dir, true);
    }

    public static void deleteDir(File dir, FileFilter filter) {
        deleteDir(dir, true, filter);
    }

    public static void deleteDir(File dir, FilenameFilter filter) {
        deleteDir(dir, true, filter);
    }

    public static void deleteDir(File dir, boolean removeDir) {
        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                File[] var3 = files;
                int var4 = files.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    File file = var3[var5];
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir);
                    } else {
                        file.delete();
                    }
                }
            }

            if (removeDir) {
                dir.delete();
            }
        }

    }

    public static void deleteDir(File dir, boolean removeDir, FileFilter filter) {
        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles(filter);
            if (files != null) {
                File[] var4 = files;
                int var5 = files.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    File file = var4[var6];
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir, filter);
                    } else {
                        file.delete();
                    }
                }
            }

            if (removeDir) {
                dir.delete();
            }
        }

    }

    public static void deleteDir(File dir, boolean removeDir, FilenameFilter filter) {
        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles(filter);
            if (files != null) {
                File[] var4 = files;
                int var5 = files.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    File file = var4[var6];
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir, filter);
                    } else {
                        file.delete();
                    }
                }
            }

            if (removeDir) {
                dir.delete();
            }
        }

    }

    public static long computeFolderSize(File dir) {
        if (dir == null) {
            return 0L;
        } else {
            long dirSize = 0L;
            File[] files = dir.listFiles();
            if (null != files) {
                for(int i = 0; i < files.length; ++i) {
                    File file = files[i];
                    if (file.isFile()) {
                        dirSize += file.length();
                    } else if (file.isDirectory()) {
                        dirSize += file.length();
                        dirSize += computeFolderSize(file);
                    }
                }
            }

            return dirSize;
        }
    }

    public static String getFileNameWithoutExtensionByPath(String path) {
        return TextUtils.isEmpty(path) ? null : getFileNameWithoutExtension(new File(path));
    }

    public static String getFileNameWithoutExtension(String fileName) {
        String name = fileName;
        int index = fileName.lastIndexOf(46);
        if (index != -1) {
            name = fileName.substring(0, index);
        }

        return name;
    }

    public static String getFileNameWithoutExtension(File file) {
        if (null == file) {
            return null;
        } else {
            String fileName = file.getName();
            int index = fileName.lastIndexOf(46);
            if (index >= 0) {
                fileName = fileName.substring(0, index);
            }

            return fileName;
        }
    }

    public static String getExtension(String path) {
        return TextUtils.isEmpty(path) ? null : getExtension(new File(path));
    }

    public static String getExtension(File file) {
        if (null == file) {
            return null;
        } else {
            String fileName = file.getName();
            int index = fileName.lastIndexOf(46);
            String extension = "";
            if (index >= 0) {
                extension = fileName.substring(index + 1);
            }

            return extension;
        }
    }

    public static boolean existsFile(String path) {
        return TextUtils.isEmpty(path) ? false : existsFile(new File(path));
    }

    public static boolean existsFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

    public static boolean deleteFileIfExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        } else {
            File file = new File(path);
            return file.exists() ? file.delete() : false;
        }
    }

    public static boolean deleteFileIfExist(File file) {
        if (file == null) {
            return false;
        } else {
            return file.exists() ? file.delete() : false;
        }
    }

    public static void writeToFile(File file, String content) {
        writeToFile(file, content, false, "utf-8");
    }

    public static void writeToFile(File file, String content, boolean append) {
        writeToFile(file, content, append, "utf-8");
    }

    public static void writeToFile(File file, String content, String encoding) {
        writeToFile(file, content, false, encoding);
    }

    public static void writeToFile(File file, String content, boolean append, String encoding) {
        if (file != null && !TextUtils.isEmpty(content)) {
            ensureParent(file);
            OutputStreamWriter writer = null;

            try {
                writer = new OutputStreamWriter(new FileOutputStream(file, append), encoding);
                writer.write(content);
            } catch (IOException var9) {
                var9.printStackTrace();
            } finally {
                IoUtils.closeQuietly(writer);
            }

        }
    }

    public static final void writeToFile(File file, byte[] data) {
        if (file != null && data != null) {
            ensureParent(file);
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(file);
                fos.write(data);
            } catch (Exception var7) {
                var7.printStackTrace();
            } finally {
                IoUtils.closeQuietly(fos);
            }

        }
    }

    public static void writeToFileNio(InputStream is, File target) {
        FileOutputStream fo = null;
        ReadableByteChannel src = null;
        FileChannel out = null;

        try {
            int len = is.available();
            src = Channels.newChannel(is);
            fo = new FileOutputStream(target);
            out = fo.getChannel();
            out.transferFrom(src, 0L, (long)len);
        } catch (IOException var9) {
            var9.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fo);
            IoUtils.closeQuietly(src);
            IoUtils.closeQuietly(out);
        }

    }

    public static void writeToFileNio(File target, byte[] data) {
        FileOutputStream fo = null;
        ReadableByteChannel src = null;
        FileChannel out = null;

        try {
            src = Channels.newChannel(new ByteArrayInputStream(data));
            fo = new FileOutputStream(target);
            out = fo.getChannel();
            out.transferFrom(src, 0L, (long)data.length);
        } catch (IOException var9) {
            var9.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fo);
            IoUtils.closeQuietly(src);
            IoUtils.closeQuietly(out);
        }

    }

    public static String readFileText(String path) {
        return TextUtils.isEmpty(path) ? null : readFileText(new File(path));
    }

    public static String readFileText(File file) {
        if (existsFile(file)) {
            FileInputStream fis = null;

            String var2;
            try {
                fis = new FileInputStream(file);
                var2 = IoUtils.loadContent(fis);
            } catch (IOException var6) {
                var6.printStackTrace();
                return null;
            } finally {
                IoUtils.closeQuietly(fis);
            }

            return var2;
        } else {
            return null;
        }
    }

    public static String readFileText(String path, String charsetName) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(path);
            String var3 = IoUtils.loadContent(fis, charsetName);
            return var3;
        } catch (IOException var7) {
            var7.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fis);
        }

        return null;
    }

    public static byte[] readFileBytes(File file) {
        if (existsFile(file)) {
            FileInputStream fis = null;

            byte[] var2;
            try {
                fis = new FileInputStream(file);
                var2 = IoUtils.loadBytes(fis);
            } catch (IOException var6) {
                var6.printStackTrace();
                return null;
            } finally {
                IoUtils.closeQuietly(fis);
            }

            return var2;
        } else {
            return null;
        }
    }

    public static Map<String, String> readConfig(File file) {
        Map<String, String> map = new HashMap();
        String text = readFileText(file);
        if (text != null && !TextUtils.isEmpty(text)) {
            String[] lines = text.split("\n");
            String[] var4 = lines;
            int var5 = lines.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String line = var4[var6];
                line = line.trim();
                if (!TextUtils.isEmpty(line) && !line.startsWith("#")) {
                    String[] array = line.split("=", 2);
                    if (array.length >= 2) {
                        map.put(array[0].trim(), array[1].trim());
                    }
                }
            }

            return map;
        } else {
            return map;
        }
    }

    public static FileOutputStream openNewFileOutput(File file) throws IOException {
        deleteFileIfExist(file);
        ensureParent(file);
        file.createNewFile();
        return new FileOutputStream(file);
    }

    public static File getUserDir() {
        String path = System.getProperty("user.dir");
        return new File(path);
    }

    public static File getUserHome() {
        String path = System.getProperty("user.home");
        return new File(path);
    }

    public static boolean isSdCardAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static File getSDRootFile() {
        return isSdCardAvailable() ? Environment.getExternalStorageDirectory() : null;
    }

    public static File createCollectDirectory() {
        File sdRootFile = getSDRootFile();
        File file = null;
        if (sdRootFile != null && sdRootFile.exists()) {
            file = new File(sdRootFile, "CollectBest");
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        return file;
    }

    public static String readAssetFileUtf8String(AssetManager assetManager, String filename) throws IOException {
        byte[] bytes = readAssetFileContent(assetManager, filename);
        return new String(bytes, Charset.forName("UTF-8"));
    }

    public static byte[] readAssetFileContent(AssetManager assetManager, String filename) throws IOException {
        Log.i("FileUtil", " try to read asset file :" + filename);
        InputStream is = assetManager.open(filename);
        int size = is.available();
        byte[] buffer = new byte[size];
        int realSize = is.read(buffer);
        if (realSize != size) {
            throw new IOException("realSize is not equal to size: " + realSize + " : " + size);
        } else {
            is.close();
            return buffer;
        }
    }
}