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

    public BandHeartRateListener(Context context, StorageObject storageObject) {
        mContext = context;
        mStorageObject = storageObject;
    }

    @Override
    public void onBandHeartRateChanged(BandHeartRateEvent bandHeartRateEvent) {
        if (mStorageObject instanceof CsvObject) {
            // Store Info as CSV
            Date date = Calendar.getInstance().getTime();
            String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
            String timeString = new SimpleDateFormat("hh:mm:ss.SSS", Locale.US).format(date);
            String[] contents = {dateString, timeString,
                    String.valueOf(bandHeartRateEvent.getHeartRate()),
                    String.valueOf(bandHeartRateEvent.getQuality())};
            String content = CsvLogService.generateContents(contents);
            CsvLogService.logData(mContext, new File(AnEar.ROOT_FILE, "heart_rate.csv"), HEADER, content);
        }
    }
}
