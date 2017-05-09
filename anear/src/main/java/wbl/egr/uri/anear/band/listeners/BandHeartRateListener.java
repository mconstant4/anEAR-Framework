package wbl.egr.uri.anear.band.listeners;

import android.content.Context;

import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wbl.egr.uri.anear.AnEar;
import wbl.egr.uri.anear.audio.services.AudioRecorderService;
import wbl.egr.uri.anear.models.CsvObject;
import wbl.egr.uri.anear.models.StorageObject;
import wbl.egr.uri.anear.io.services.CsvLogService;

/**
 * Created by root on 4/27/17.
 */

public class BandHeartRateListener implements BandHeartRateEventListener {
    private final String HEADER = "Date,Time,Heart Rate (BPM),Quality (Acquiring / Locked)";

    private Context mContext;
    private StorageObject mStorageObject;
    private boolean mTrigger;

    public BandHeartRateListener(Context context, StorageObject storageObject, boolean trigger) {
        mContext = context;
        mStorageObject = storageObject;
        mTrigger = trigger;
    }

    @Override
    public void onBandHeartRateChanged(BandHeartRateEvent bandHeartRateEvent) {
        if (mTrigger && bandHeartRateEvent.getHeartRate() > 100) {
            // Trigger Additional Audio Recording
            AudioRecorderService.trigger(mContext);
        }

        if (mStorageObject instanceof CsvObject) {
            // Store Info as CSV
            Date date = Calendar.getInstance().getTime();
            String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
            String timeString = new SimpleDateFormat("kk:mm:ss.SSS", Locale.US).format(date);
            String[] contents = {dateString, timeString,
                    String.valueOf(bandHeartRateEvent.getHeartRate()),
                    String.valueOf(bandHeartRateEvent.getQuality())};
            String content = CsvLogService.generateContents(contents);
            CsvLogService.logData(mContext, new File(AnEar.getRoot(mContext), "heart_rate.csv"), HEADER, content);
        }
    }
}
