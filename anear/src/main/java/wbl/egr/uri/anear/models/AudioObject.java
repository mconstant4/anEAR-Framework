package wbl.egr.uri.anear.models;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.Serializable;

/**
 * Created by root on 5/1/17.
 */

public class AudioObject implements Serializable {
    private boolean mNotification;
    private int mDuration;
    private int mDelay;
    private boolean mPeriodic;
    private boolean mLog;
    private int mSampleRate;
    private int mAudioSource;
    private int mChannelConfig;
    private int mAudioFormat;
    private int mBufferSize;

    public AudioObject(int duration) {
        mDuration = duration;

        // Defaults
        mDelay = 0; // Not Periodic
        mSampleRate = 44100;
        mAudioSource = MediaRecorder.AudioSource.MIC;
        mChannelConfig = AudioFormat.CHANNEL_IN_MONO;
        mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
        mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannelConfig, mAudioFormat);
        mNotification = false;
        mPeriodic = false;
        mLog = false;
    }

    public AudioObject(int duration, int sampleRate, int audioSource, int channelConfig, int audioFormat) {
        mDuration = duration;
        mSampleRate = sampleRate;
        mAudioSource = audioSource;
        mChannelConfig = channelConfig;
        mAudioFormat = audioFormat;
        mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannelConfig, mAudioFormat);

        // Defaults
        mNotification = false;
        mPeriodic = false;
        mLog = false;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    public int getAudioSource() {
        return mAudioSource;
    }

    public int getChannelConfig() {
        return mChannelConfig;
    }

    public int getAudioFormat() {
        return mAudioFormat;
    }

    public int getBufferSize() {
        return mBufferSize;
    }

    public boolean isNotificationEnabled() {
        return mNotification;
    }

    public void enableNotification(boolean notification) {
        mNotification = notification;
    }

    public boolean isPeriodicEnabled() {
        return mPeriodic;
    }

    public void enablePeriodic(int delay) {
        mPeriodic = true;
        mDelay = delay;
    }

    public void disablePeriodic() {
        mPeriodic = false;
        mDelay = 0;
    }

    public int getDelay() {
        return mDelay;
    }

    public boolean isLogEnabled() {
        return mLog;
    }

    public void enableLog(boolean log) {
        mLog = log;
    }
}
