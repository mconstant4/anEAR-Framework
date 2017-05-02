package wbl.egr.uri.anear.band.listeners;

import android.content.Context;

import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandUVEvent;
import com.microsoft.band.sensors.BandUVEventListener;

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

public class BandUvListener implements BandUVEventListener {
    private final String HEADER = "Date,Time,UV Exposure,UV Index Level";

    private Context mContext;
    private StorageObject mStorageObject;

    public BandUvListener(Context context, StorageObject storageObject) {
        mContext = context;
        mStorageObject = storageObject;
    }

    @Override
    public void onBandUVChanged(BandUVEvent bandUVEvent) {
        if (mStorageObject instanceof CsvObject) {
            // Store Info as CSV
            try {
                Date date = Calendar.getInstance().getTime();
                String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
                String timeString = new SimpleDateFormat("hh:mm:ss.SSS", Locale.US).format(date);
                String[] contents = {dateString, timeString,
                        String.valueOf(bandUVEvent.getUVExposureToday()),
                        String.valueOf(bandUVEvent.getUVIndexLevel())};
                String content = CsvLogService.generateContents(contents);
                CsvLogService.logData(mContext, new File(AnEar.ROOT_FILE, "uv.csv"), HEADER, content);
            } catch (InvalidBandVersionException e) {
                e.printStackTrace();
            }
        }
    }
}
