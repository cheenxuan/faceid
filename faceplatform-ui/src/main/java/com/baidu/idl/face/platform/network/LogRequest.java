package com.baidu.idl.face.platform.network;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Author: xuan
 * Created on 2021/9/7 09:27.
 * <p>
 * Describe:
 */
public class LogRequest extends BaseRequest {
    public static final String URL_GET_LOG = "http://face.baidu.com/openapi/v2/stat/sdkdata";

    public LogRequest() {
    }

    public static void sendLogMessage(final String message) {
        if (message != null && message.length() > 0) {
            (new Thread(new Runnable() {
                public void run() {
                    LogRequest.httpUrlConnectionPost(message);
                }
            })).start();
        }

    }

    private static void httpUrlConnectionPost(String message) {
        StringBuffer result = new StringBuffer();
        HttpURLConnection urlConnection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;

        try {
            JSONObject json = new JSONObject(message);
            URL url = new URL("http://face.baidu.com/openapi/v2/stat/sdkdata");
            urlConnection = (HttpURLConnection)url.openConnection();
            System.setProperty("sun.net.client.defaultConnectTimeout", "8000");
            System.setProperty("sun.net.client.defaultReadTimeout", "8000");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setRequestProperty("contentType", "application/json");
            urlConnection.connect();
            outputStream = urlConnection.getOutputStream();
            outputStream.write(json.toString().getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();
            int responseCode = urlConnection.getResponseCode();
            if (200 == responseCode) {
                inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                baos = new ByteArrayOutputStream();
                boolean var10 = true;

                int len;
                while((len = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }

                byte[] b = baos.toByteArray();
                result.append(new String(b, "utf-8"));
                baos.flush();
            }
        } catch (MalformedURLException var28) {
            var28.printStackTrace();
        } catch (UnsupportedEncodingException var29) {
            var29.printStackTrace();
        } catch (ProtocolException var30) {
            var30.printStackTrace();
        } catch (IOException var31) {
            var31.printStackTrace();
        } catch (Exception var32) {
            var32.printStackTrace();
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
            } catch (IOException var27) {
                var27.printStackTrace();
            }

        }

    }
}

