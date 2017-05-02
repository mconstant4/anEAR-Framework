package wbl.egr.uri.anear.band.listeners;

import android.content.Context;

import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;

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

public class BandGyroscopeListener implements BandGyroscopeEventListener {
    private final String HEADER = "Date,Time,X-Angular Velocity (degrees/sec)," +
            "Y-Angular Velocity (degrees/sec),Z-Angular Velocity (degrees/sec)";

    private Context mContext;
    private StorageObject mStorageObject;

    public BandGyroscopeListener(Context context, StorageObject storageObject) {
        mContext = context;
        mStorageObject = storageObject;
    }

    @Override
    public void onBandGyroscopeChanged(BandGyroscopeEvent bandGyroscopeEvent) {
        if (mStorageObject instanceof CsvObject) {
            // Store Info as CSV
            Date date = Calendar.getInstance().getTime();
            String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
            String timeString = new SimpleDateFormat("hh:mm:ss.SSS", Locale.US).format(date);
            String[] contents = {dateString, timeString,
                    String.valueOf(bandGyroscopeEvent.getAngularVelocityX()),
                    String.valueOf(bandGyroscopeEvent.getAngularVelocityY()),
                    String.valueOf(bandGyroscopeEvent.getAngularVelocityZ())};
            String content = CsvLogService.generateContents(contents);
            CsvLogService.logData(mContext, new File(AnEar.getRoot(mContext), "gyroscope.csv"), HEADER, content);
        }
    }
}
