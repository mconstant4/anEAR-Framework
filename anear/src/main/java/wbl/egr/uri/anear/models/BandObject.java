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

    public BandObject(BandInfo bandInfo) {
        mBandName = bandInfo.getName();
        mBandAddress = bandInfo.getMacAddress();
        // Defaults
        mAutoStream = false;
        mSensorsToRecord = null;
        mPeriodic = false;
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

    public void setAutoStream(boolean autoStream) {
        mAutoStream = autoStream;
    }

    public BandSensor[] getSensorsToRecord() {
        return mSensorsToRecord;
    }

    public void setSensorsToRecord(BandSensor[] sensorsToRecord) {
        mSensorsToRecord = sensorsToRecord;
    }

    public boolean isPeriodic() {
        return mPeriodic;
    }

    public void setPeriodic(boolean periodic) {
        mPeriodic = periodic;
    }
}
