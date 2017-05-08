package wbl.egr.uri.anear.band.listeners;

import android.content.Context;

import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;

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
 * Created by Matt Constant on 4/26/17.
 *
 * From Microsoft Band SDK:
 *          Details:
 *              Provides X, Y, and Z acceleration in g units. 1g = 9.81 meters per second squared (m/s*s)
 *          Frequency:
 *              62/31/8 HZ
 * Data Extracted:
 *          Date, Time, X-Acceleration, Y-Acceleration, Z-Acceleration
 */

public class BandAccelerometerListener implements BandAccelerometerEventListener {
    private final String HEADER = "Date,Time,X-Acceleration (m/s*s),Y-Acceleration (m/s*s),Z-Acceleration (m/s*s)";

    private Context mContext;
    private StorageObject mStorageObject;

    public BandAccelerometerListener(Context context, StorageObject storageObject) {
        mContext = context;
        mStorageObject = storageObject;
    }

    @Override
    public void onBandAccelerometerChanged(BandAccelerometerEvent bandAccelerometerEvent) {
        if (mStorageObject instanceof CsvObject) {
            // Store Info as CSV
            Date date = Calendar.getInstance().getTime();
            String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
            String timeString = new SimpleDateFormat("kk:mm:ss.SSS", Locale.US).format(date);
            String[] contents = {dateString, timeString,
                    String.valueOf(bandAccelerometerEvent.getAccelerationX() * (long)9.81),
                    String.valueOf(bandAccelerometerEvent.getAccelerationY() * (long)9.81),
                    String.valueOf(bandAccelerometerEvent.getAccelerationZ() * (long)9.81)};
            String content = CsvLogService.generateContents(contents);
            CsvLogService.logData(mContext, new File(AnEar.getRoot(mContext), "acceleration.csv"), HEADER, content);
        }
    }
}
