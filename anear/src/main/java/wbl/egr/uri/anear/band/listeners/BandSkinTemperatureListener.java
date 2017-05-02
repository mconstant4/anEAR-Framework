package wbl.egr.uri.anear.band.listeners;

import android.content.Context;

import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;

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

public class BandSkinTemperatureListener implements BandSkinTemperatureEventListener {
    private final String HEADER = "Date,Time,Skin Temperature (Celsius)";

    private Context mContext;
    private StorageObject mStorageObject;

    public BandSkinTemperatureListener(Context context, StorageObject storageObject) {
        mContext = context;
        mStorageObject = storageObject;
    }

    @Override
    public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent bandSkinTemperatureEvent) {
        if (mStorageObject instanceof CsvObject) {
            // Store Info as CSV
            Date date = Calendar.getInstance().getTime();
            String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
            String timeString = new SimpleDateFormat("hh:mm:ss.SSS", Locale.US).format(date);
            String[] contents = {dateString, timeString,
                    String.valueOf(bandSkinTemperatureEvent.getTemperature())};
            String content = CsvLogService.generateContents(contents);
            CsvLogService.logData(mContext, new File(AnEar.ROOT_FILE, "skin_temperature.csv"), HEADER, content);
        }
    }
}
