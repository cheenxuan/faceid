package com.baidu.idl.face.platform.common;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.utils.SoundPlayer;

import java.util.HashMap;

/**
 * Author: xuan
 * Created on 2021/9/7 09:29.
 * <p>
 * Describe:
 */
public class SoundPoolHelper {
    private static final String TAG = SoundPoolHelper.class.getSimpleName();
    private Context mContext;
    private FaceStatusNewEnum mPlaySoundStatusNewEnum;
    private volatile long mPlayDuration = 0L;
    private volatile long mPlayTime = 0L;
    private volatile boolean mIsPlaying = false;
    private volatile boolean mIsEnableSound = true;
    private HashMap<Integer, Long> mPlayDurationMap = new HashMap();

    public SoundPoolHelper(Context context) {
        this.mContext = context;
    }

    public void setEnableSound(boolean flag) {
        this.mIsEnableSound = flag;
    }

    public boolean getEnableSound() {
        return this.mIsEnableSound;
    }

    public long getPlayDuration() {
        return this.mPlayDuration;
    }

    public boolean playSound(FaceStatusNewEnum status) {
        if (!this.mIsEnableSound) {
            SoundPlayer.release();
        }

        this.mIsPlaying = System.currentTimeMillis() - SoundPlayer.playTime < this.mPlayDuration;
        if (this.mIsPlaying || this.mPlaySoundStatusNewEnum == status && System.currentTimeMillis() - this.mPlayTime < FaceEnvironment.TIME_TIPS_REPEAT) {
            return false;
        } else {
            this.mIsPlaying = true;
            this.mPlaySoundStatusNewEnum = status;
            this.mPlayDuration = 0L;
            this.mPlayTime = System.currentTimeMillis();
            int resId = FaceEnvironment.getSoundId(status);
            if (resId > 0) {
                this.mPlayDuration = this.getSoundDuration(resId);
                SoundPlayer.playTime = System.currentTimeMillis();
                if (this.mIsEnableSound) {
                    SoundPlayer.play(this.mContext, resId);
                }
            }

            return this.mIsPlaying;
        }
    }

    private long getSoundDuration(int rawId) {
        long duration = 600L;
        long durationStep = 0L;
        if (this.mPlayDurationMap.containsKey(rawId)) {
            duration = (Long)this.mPlayDurationMap.get(rawId);
        } else {
            long time = System.currentTimeMillis();
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();

            try {
                Uri uri = Uri.parse("android.resource://" + this.mContext.getPackageName() + "/" + rawId);
                mmr.setDataSource(this.mContext, uri);
                String d = mmr.extractMetadata(9);
                duration = Long.valueOf(d) + durationStep;
                this.mPlayDurationMap.put(rawId, duration);
            } catch (IllegalArgumentException var11) {
                var11.printStackTrace();
            } catch (IllegalStateException var12) {
                var12.printStackTrace();
            } catch (Exception var13) {
                var13.printStackTrace();
            }
        }

        return duration;
    }

    public void release() {
        SoundPlayer.release();
        this.mPlayDuration = 0L;
        this.mPlayTime = 0L;
        this.mContext = null;
    }
}
