package wbl.egr.uri.anear.band.listeners;

import android.content.Context;

import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;

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

public class BandCaloriesListener implements BandCaloriesEventListener {
    private final String HEADER = "Date,Time,Calories Burned (kcals)";

    private Context mContext;
    private StorageObject mStorageObject;

    public BandCaloriesListener(Context context, StorageObject storageObject) {
        mContext = context;
        mStorageObject = storageObject;
    }

    @Override
    public void onBandCaloriesChanged(BandCaloriesEvent bandCaloriesEvent) {
        if (mStorageObject instanceof CsvObject) {
            // Store Info as CSV
            try {
                Date date = Calendar.getInstance().getTime();
                String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
                String timeString = new SimpleDateFormat("hh:mm:ss.SSS", Locale.US).format(date);
                String[] contents = {dateString, timeString,
                        String.valueOf(bandCaloriesEvent.getCaloriesToday())};
                String content = CsvLogService.generateContents(contents);
                CsvLogService.logData(mContext, new File(AnEar.getRoot(mContext), "calories.csv"), HEADER, content);
            } catch (InvalidBandVersionException e) {
                e.printStackTrace();
            }
        }
    }
}
