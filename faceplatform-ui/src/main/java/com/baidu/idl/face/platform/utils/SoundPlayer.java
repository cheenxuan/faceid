package com.baidu.idl.face.platform.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.SoundPool;
import android.util.SparseIntArray;

/**
 * Author: xuan
 * Created on 2021/9/7 09:13.
 * <p>
 * Describe:
 */
public final class SoundPlayer {
    private static final boolean DEBUG = false;
    private static final String TAG = SoundPlayer.class.getSimpleName();
    private SoundPool mSoundPool = new SoundPool(5, 3, 0);
    private SparseIntArray mSoundPoolCache = new SparseIntArray();
    public static final int MAX_STREAMS = 5;
    private static SoundPlayer sSoundPlayer;
    private static final long LOAD_SOUND_MILLIS = 100L;
    public static long playTime = 0L;

    private SoundPlayer() {
        playTime = 0L;
    }

    @SuppressLint({"NewApi"})
    public static void play(Context context, int resId) {
        if (null == sSoundPlayer) {
            sSoundPlayer = new SoundPlayer();
        }

        int id = sSoundPlayer.mSoundPoolCache.get(resId);
        if (0 == id) {
            final int soundId = sSoundPlayer.mSoundPool.load(context, resId, 1);
            sSoundPlayer.mSoundPoolCache.put(resId, soundId);
            if (APIUtils.hasFroyo()) {
                sSoundPlayer.mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        if (0 == status && soundId == sampleId) {
                            try {
                                SoundPlayer.playTime = System.currentTimeMillis();
                                SoundPlayer.sSoundPlayer.mSoundPool.play(soundId, 1.0F, 1.0F, 5, 0, 1.0F);
                            } catch (Exception var5) {
                                var5.printStackTrace();
                            }
                        }

                    }
                });
            } else {
                try {
                    Thread.currentThread().join(LOAD_SOUND_MILLIS);
                } catch (InterruptedException var6) {
                    var6.printStackTrace();
                }

                playTime = System.currentTimeMillis();
                sSoundPlayer.mSoundPool.play(soundId, 1.0F, 1.0F, 5, 0, 1.0F);
            }
        } else {
            try {
                sSoundPlayer.mSoundPool.play(id, 1.0F, 1.0F, 5, 0, 1.0F);
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

    }

    public static void release() {
        if (null != sSoundPlayer) {
            int i = 0;

            for(int n = sSoundPlayer.mSoundPoolCache.size(); i < n; ++i) {
                sSoundPlayer.mSoundPool.unload(sSoundPlayer.mSoundPoolCache.valueAt(i));
            }

            sSoundPlayer.mSoundPool.release();
            sSoundPlayer.mSoundPool = null;
            sSoundPlayer.mSoundPoolCache.clear();
            sSoundPlayer.mSoundPoolCache = null;
            sSoundPlayer = null;
        }

        playTime = 0L;
    }
}
