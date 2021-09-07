package com.baidu.idl.face.platform.network;

import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Author: xuan
 * Created on 2021/9/7 09:24.
 * <p>
 * Describe:
 */
public class NoMotionRequest extends BaseRequest {
    private static final String TAG = NoMotionRequest.class.getSimpleName();
    public static final String URL_POST_NOMOTION_LIVENESS = "http://face.baidu.com/gate/api/userverifydemo";

    public NoMotionRequest() {
    }

    public static void sendMessage(final String image, final Handler uiHandler) {
        if (image != null && image.length() > 0) {
            (new Thread(new Runnable() {
                public void run() {
                    NoMotionRequest.httpUrlConnectionPost(image, uiHandler);
                }
            })).start();
        }

    }

    private static void httpUrlConnectionPost(String message, Handler uiHandler) {
        StringBuilder result = new StringBuilder("");
        HttpURLConnection urlConnection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        int responseCode = 0;

        try {
            String requestMessage = "pic_file=" + URLEncoder.encode(message, "UTF-8");
            URL url = new URL("http://face.baidu.com/gate/api/userverifydemo");
            urlConnection = (HttpURLConnection)url.openConnection();
            System.setProperty("sun.net.client.defaultConnectTimeout", "8000");
            System.setProperty("sun.net.client.defaultReadTimeout", "8000");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.connect();
            outputStream = urlConnection.getOutputStream();
            outputStream.write(requestMessage.getBytes());
            outputStream.flush();
            outputStream.close();
            responseCode = urlConnection.getResponseCode();
            if (200 == responseCode) {
                inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                baos = new ByteArrayOutputStream();
                boolean var11 = true;

                int len;
                while((len = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }

                byte[] b = baos.toByteArray();
                result.append(new String(b, "utf-8"));
                baos.flush();
            }
        } catch (MalformedURLException var29) {
            var29.printStackTrace();
        } catch (UnsupportedEncodingException var30) {
            var30.printStackTrace();
        } catch (ProtocolException var31) {
            var31.printStackTrace();
        } catch (IOException var32) {
            var32.printStackTrace();
        } catch (Exception var33) {
            var33.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }

                if (baos != null) {
                    baos.close();
                }

                if (inputStream != null) {
                    inputStream.close();
                }

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            } catch (IOException var28) {
                var28.printStackTrace();
            }

            if (uiHandler != null) {
                Message msg = uiHandler.obtainMessage(0);
                msg.arg1 = responseCode;
                msg.obj = result.toString();
                uiHandler.sendMessage(msg);
            }

        }

    }
}
