package wbl.egr.uri.anear.band.listeners;

import android.content.Context;
import android.content.Intent;

import com.microsoft.band.sensors.BandContactEvent;
import com.microsoft.band.sensors.BandContactEventListener;
import com.microsoft.band.sensors.BandContactState;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wbl.egr.uri.anear.AnEar;
import wbl.egr.uri.anear.models.CsvObject;
import wbl.egr.uri.anear.models.StorageObject;
import wbl.egr.uri.anear.band.receivers.BandContactReceiver;
import wbl.egr.uri.anear.io.services.CsvLogService;

/**
 * Created by root on 4/27/17.
 */

public class BandContactListener implements BandContactEventListener {
    private final String HEADER = "Date,Time,Contact (Worn / Not Worn)";

    private Context mContext;
    private StorageObject mStorageObject;

    public BandContactListener(Context context, StorageObject storageObject) {
        mContext = context;
        mStorageObject = storageObject;
    }

    @Override
    public void onBandContactChanged(BandContactEvent bandContactEvent) {
        Intent intent = new Intent(BandContactReceiver.INTENT_FILTER.getAction(0));
        intent.putExtra(BandContactReceiver.EXTRA_CONTACT, bandContactEvent.getContactState() == BandContactState.WORN);
        mContext.sendBroadcast(intent);

        if (mStorageObject instanceof CsvObject) {
            // Store Info as CSV
            Date date = Calendar.getInstance().getTime();
            String dateString = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
            String timeString = new SimpleDateFormat("hh:mm:ss.SSS", Locale.US).format(date);
            String[] contents = {dateString, timeString,
                    String.valueOf(bandContactEvent.getContactState())};
            String content = CsvLogService.generateContents(contents);
            CsvLogService.logData(mContext, new File(AnEar.ROOT_FILE, "contact.csv"), HEADER, content);
        }
    }
}
