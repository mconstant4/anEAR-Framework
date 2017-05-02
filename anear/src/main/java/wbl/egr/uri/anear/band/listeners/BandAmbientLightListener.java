package wbl.egr.uri.anear.band.listeners;

import android.content.Context;

import com.microsoft.band.sensors.BandAmbientLightEvent;
import com.microsoft.band.sensors.BandAmbientLightEventListener;

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
 * Created by root on 4/26/17.
 */

public class BandAmbientLightListener implements BandAmbientLightEventListener {
    private final String HEADER = "Date,Time,Brightness (LUX)";

    private Context mContext;
    private StorageObject mStorageObject;

    public BandAmbientLightListener(Context context, StorageObject storageObject) {
        mContext = context;
        mStorageObject = storageObject;
    }

    @Override
    public void onBandAmbientLightChanged(BandAmbientLightEvent bandAmbientLightEvent) {
        if (mStorageObject instanceof CsvObject) {
            // Store Info as CSV
            Date date = Calendar.getInstance().getTime();
            String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
            String timeString = new SimpleDateFormat("hh:mm:ss.SSS", Locale.US).format(date);
            String[] contents = {dateString, timeString,
                    String.valueOf(bandAmbientLightEvent.getBrightness())};
            String content = CsvLogService.generateContents(contents);
            CsvLogService.logData(mContext, new File(AnEar.ROOT_FILE, "ambient_light.csv"), HEADER, content);
        }
    }
}
