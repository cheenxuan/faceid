package com.baidu.idl.face.platform.stat;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import com.baidu.idl.face.platform.stat.NetUtil.RequestAdapter;
import com.baidu.idl.main.facesdk.FaceInfo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author: xuan
 * Created on 2021/9/7 09:22.
 * <p>
 * Describe:
 */
public class Ast {
    private static final String AS_FILE_NAME = "ast";
    private static final long UPADTE_DEFUALT_DELAY_TIME = 15000L;
    private static Ast instance;
    private Context context;
    private boolean isInit;
    private File asFile;
    private Properties properties;
    private Dev dev;
    private String faceHitKey = "";
    private String faceHitKeyLasttime = "FACE_HIT_KEY_LASSTTIME";
    private long lastSavetime;
    private SparseArray<Integer> faceIds = new SparseArray();
    private String scene;

    private Ast() {
    }

    public static Ast getInstance() {
        if (instance == null) {
            Class var0 = Ast.class;
            synchronized(Ast.class) {
                instance = new Ast();
            }
        }

        return instance;
    }

    public boolean init(Context context, String sdkVersion, String scene) {
        if (this.isInit) {
            return true;
        } else {
            if (context != null) {
                this.context = context.getApplicationContext();
                this.dev = new Dev();
                this.dev.init(context);
                this.dev.setSdkVersion(sdkVersion);
                this.scene = scene;
                this.initFile();
            }

            return true;
        }
    }

    private String generateFaceHitKey(String indicator) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH");
        sb.append(simpleDateFormat.format(new Date())).append("_");
        sb.append(indicator);
        return sb.toString();
    }

    private boolean initFile() {
        this.asFile = new File(this.context.getFilesDir(), "ast");
        this.properties = new Properties();
        return !FileUtil.createFile(this.asFile) ? false : FileUtil.loadPropertiesFile(this.asFile, this.properties);
    }

    public void faceHit(String indicator, int daleyUpload, FaceInfo[] faceInfos) {
        int hitCount = 0;
        if (faceInfos == null) {
            this.faceIds.clear();
        } else {
            FaceInfo[] var5 = faceInfos;
            int var6 = faceInfos.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                FaceInfo faceInfo = var5[var7];
                Integer faceId = (Integer)this.faceIds.get(faceInfo.faceID);
                if (faceId == null) {
                    this.faceIds.put(faceInfo.faceID, faceInfo.faceID);
                    ++hitCount;
                }
            }

            if (hitCount != 0) {
                this.faceHit(indicator, (long)daleyUpload, hitCount);
            }
        }
    }

    public void faceHit() {
        this.faceHit("liveness", 15000L, 1);
    }

    public void faceHit(String indicator) {
        this.faceHit(indicator, 15000L, 1);
    }

    public void faceHit(String indicator, int count) {
        this.faceHit(indicator, 15000L, count);
    }

    public void faceHit(String indicator, long daleyUpload, int count) {
        if (this.properties != null) {
            String faceHitKey = this.generateFaceHitKey(indicator);
            String value = this.properties.getProperty(faceHitKey);
            if (TextUtils.isEmpty(value)) {
                this.properties.setProperty(faceHitKey, String.valueOf(count));
                this.properties.setProperty(this.faceHitKeyLasttime, String.valueOf(System.currentTimeMillis()));
            } else {
                int val = Integer.parseInt(value);
                this.properties.setProperty(faceHitKey, String.valueOf(val + count));
            }

            FileUtil.savePropertiesFile(this.asFile, this.properties);
            String lasttimeStr = this.properties.getProperty(this.faceHitKeyLasttime);
            long lasttime = System.currentTimeMillis();

            try {
                lasttime = Long.parseLong(lasttimeStr);
            } catch (Exception var11) {
                var11.printStackTrace();
            }

            if (this.dev.getFirstRun() || System.currentTimeMillis() - lasttime >= daleyUpload) {
                this.sendData();
            }
        }
    }

    public void immediatelyUpload() {
        this.sendData();
    }

    private void clear() {
        this.properties.clear();
    }

    private void sendData() {
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(new Runnable() {
            public void run() {
                Ast.this.netRequest();
            }
        });
    }

    private void netRequest() {
        if (this.properties.size() != 0) {
            NetUtil.uploadData(new RequestAdapter<Object>() {
                public String getURL() {
                    return "https://brain.baidu.com/record/api";
                }

                public String getRequestString() {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("mh", "faceSdkStatistic");
                        Properties asCopy = (Properties)Ast.this.properties.clone();
                        Iterator iter = asCopy.entrySet().iterator();
                        JSONArray dt = new JSONArray();

                        while(iter.hasNext()) {
                            Map.Entry entry = (Map.Entry)iter.next();
                            String key = (String)entry.getKey();
                            String value = (String)entry.getValue();
                            if (!key.equalsIgnoreCase(Ast.this.faceHitKeyLasttime)) {
                                JSONObject data = new JSONObject();
                                data.put("type", "facesdk");
                                data.put("scene", Ast.this.scene);
                                data.put("appid", Ast.this.dev.getPackagename());
                                data.put("device", Ast.this.dev.getBrand());
                                data.put("imei", Ast.this.dev.getUniqueID());
                                data.put("os", "Android");
                                data.put("system", Ast.this.dev.getSysVersion());
                                data.put("version", Ast.this.dev.getSdkVersion());
                                if (key.contains("liveness")) {
                                    data.put("isliving", "true");
                                } else {
                                    data.put("isliving", "false");
                                }

                                data.put("finish", "1");
                                String[] keySplit = key.split("_");
                                if (keySplit.length > 4) {
                                    data.put("year", keySplit[0]);
                                    data.put("month", keySplit[1]);
                                    data.put("day", keySplit[2]);
                                    data.put("hour", keySplit[3]);
                                }

                                data.put("num", value);
                                dt.put(data);
                            }
                        }

                        json.put("dt", dt);
                        return json.toString();
                    } catch (JSONException var10) {
                        var10.printStackTrace();
                        return "";
                    }
                }

                public void parseResponse(InputStream in) throws IOException, JSONException {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];

                    try {
                        int len1;
                        while((len1 = in.read(buffer)) > 0) {
                            out.write(buffer, 0, len1);
                        }

                        out.flush();
                        JSONObject json = new JSONObject(new String(out.toByteArray(), "UTF-8"));
                        int code = json.optInt("code");
                        if (code == 0) {
                            Ast.this.properties.clear();
                            Ast.this.dev.setFirstRun(false);
                            FileUtil.savePropertiesFile(Ast.this.asFile, Ast.this.properties);
                        }
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException var12) {
                                var12.printStackTrace();
                            }
                        }

                    }

                }
            });
        }
    }
}