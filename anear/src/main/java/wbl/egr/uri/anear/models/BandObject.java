package wbl.egr.uri.anear.models;

import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandInfo;

import java.io.Serializable;

import wbl.egr.uri.anear.band.enums.BandSensor;

/**
 * Created by root on 4/26/17.
 */

public class BandObject extends SensorObject implements Serializable {
    public static BandInfo[] getPairedBands() {
        BandClientManager bandClientManager = BandClientManager.getInstance();
        return bandClientManager.getPairedBands();
    }

    private String mBandName;
    private String mBandAddress;
    private boolean mAutoStream;
    private BandSensor[] mSensorsToRecord;
    private boolean mPeriodic;
    private boolean mHapticFeedback;
    private boolean mTrigger;

    public BandObject(BandInfo bandInfo) {
        mBandName = bandInfo.getName();
        mBandAddress = bandInfo.getMacAddress();
        // Defaults
        mAutoStream = false;
        mSensorsToRecord = null;
        mPeriodic = false;
        mHapticFeedback = false;
        mTrigger = false;
    }

    public BandObject(BandInfo bandInfo, boolean autoStream, BandSensor[] sensorsToRecord, boolean periodic, boolean hapticFeedback, boolean trigger) {
        mBandName = bandInfo.getName();
        mBandAddress = bandInfo.getMacAddress();
        mAutoStream = autoStream;
        mSensorsToRecord = sensorsToRecord;
        mPeriodic = periodic;
        mHapticFeedback = hapticFeedback;
        mTrigger = trigger;
    }

    public String getBandName() {
        return mBandName;
    }

    public String getBandAddress() {
        return mBandAddress;
    }

    public boolean isAutoStream() {
        return mAutoStream;
    }

    public void enableAutoStream(boolean autoStream) {
        mAutoStream = autoStream;
    }

    public BandSensor[] getSensorsToRecord() {
        return mSensorsToRecord;
    }

    public void setSensorsToRecord(BandSensor[] sensorsToRecord) {
        mSensorsToRecord = sensorsToRecord;
    }

    public boolean isTrigger() {
        return mTrigger;
    }

    public void setTrigger(boolean trigger) {
        mTrigger = trigger;
    }

    public boolean isPeriodic() {
        return mPeriodic;
    }

    public void enablePeriodic(boolean periodic) {
        mPeriodic = periodic;
    }

    public boolean isHapticFeedback() {
        return mHapticFeedback;
    }

    public void enableHapticFeedback(boolean hapticFeedback) {
        mHapticFeedback = hapticFeedback;
    }
}
