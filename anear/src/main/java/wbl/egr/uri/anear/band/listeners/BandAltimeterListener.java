package wbl.egr.uri.anear.band.listeners;

import android.content.Context;

import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandAltimeterEvent;
import com.microsoft.band.sensors.BandAltimeterEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wbl.egr.uri.anear.band.enums.AnEar;
import wbl.egr.uri.anear.models.CsvObject;
import wbl.egr.uri.anear.models.StorageObject;
import wbl.egr.uri.anear.io.services.CsvLogService;

/**
 * Created by Matt Constant on 4/27/17.
 *
 * NOTE: Only compatible with Microsoft Band 2
 *
 * From Microsoft Band SDK:
 *          Details:
 *              Provides current elevation data like total gain/loss, steps ascended/descended,
 *              flights ascended/descended, and elevation rate.
 *          Frequency:
 *              1 HZ
 *
 * Data Extracted:
 *      Date, Time, Elevation Gain, Elevation Loss
 */

public class BandAltimeterListener implements BandAltimeterEventListener {
    private final String HEADER = "Date,Time,Total Daily Elevation Gain (cm),Total Daily Elevation Loss (cm)";

    private Context mContext;
    private StorageObject mStorageObject;

    public BandAltimeterListener(Context context, StorageObject storageObject) {
        mContext = context;
        mStorageObject = storageObject;
    }

    @Override
    public void onBandAltimeterChanged(BandAltimeterEvent bandAltimeterEvent) {
        if (mStorageObject instanceof CsvObject) {
            // Store Info as CSV
            try {
                Date date = Calendar.getInstance().getTime();
                String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
                String timeString = new SimpleDateFormat("hh:mm:ss.SSS", Locale.US).format(date);
                String[] contents = {dateString, timeString,
                        String.valueOf(bandAltimeterEvent.getTotalGainToday()),
                        String.valueOf(bandAltimeterEvent.getTotalLoss())};
                String content = CsvLogService.generateContents(contents);
                CsvLogService.logData(mContext, new File(AnEar.ROOT_FILE, "altimeter.csv"), HEADER, content);
            } catch (InvalidBandVersionException e) {
                e.printStackTrace();
            }
        }
    }
}
