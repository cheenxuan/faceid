package com.baidu.idl.face.platform.utils;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.util.zip.ZipFile;

/**
 * Author: xuan
 * Created on 2021/9/7 09:11.
 * <p>
 * Describe:
 */
public class IoUtils {
    private static final int EOF = -1;
    private static final int BUFFER_SIZE = 1024;

    public IoUtils() {
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        int len;
        while ((len = in.read(buffer)) != EOF) {
            out.write(buffer, 0, len);
        }

    }

    public static void copyStream(InputStream in, File outFile) throws IOException {
        FileOutputStream fos = null;

        try {
            fos = FileUtils.openNewFileOutput(outFile);
            copyStream(in, (OutputStream) fos);
        } finally {
            closeQuietly((Closeable) fos);
        }

    }

    public static void copyStream(InputStream in, File outFile, long total, IoUtils.ProgressListener l) throws IOException {
        FileOutputStream fos = null;

        try {
            fos = FileUtils.openNewFileOutput(outFile);
            copyStream(in, (OutputStream) fos, total, l);
        } finally {
            closeQuietly((Closeable) fos);
        }

    }

    public static void copyStream(InputStream in, OutputStream out, long total, IoUtils.ProgressListener l) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        long current = 0L;
        boolean var8 = false;

        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
            current += (long) len;
            if (l != null) {
                l.progress(current, total);
            }
        }

    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    public static void closeQuietly(ServerSocket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    public static void closeQuietly(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    public static String loadContent(InputStream stream) throws IOException {
        return loadContent(stream, (String) null);
    }

    public static String loadContent(InputStream stream, String charsetName) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("stream may not be null.");
        } else {
            String encoding = charsetName;
            if (TextUtils.isEmpty(charsetName)) {
                encoding = System.getProperty("file.encoding", "utf-8");
            }

            InputStreamReader reader = new InputStreamReader(stream, encoding);
            StringWriter writer = new StringWriter();
            char[] buffer = new char[4 * BUFFER_SIZE];

            for (int len = reader.read(buffer); len > 0; len = reader.read(buffer)) {
                writer.write(buffer, 0, len);
            }

            return writer.toString();
        }
    }

    public static byte[] loadBytes(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = null;

        try {
            copyStream(in, (OutputStream) out);
            data = out.toByteArray();
        } catch (IOException var7) {
            var7.printStackTrace();
        } finally {
            closeQuietly((Closeable) out);
        }

        return data;
    }

    public interface ProgressListener {
        void progress(long var1, long var3);
    }
}
