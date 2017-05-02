package wbl.egr.uri.anear.band.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import wbl.egr.uri.anear.band.services.BandCollectionService;

/**
 * Created by root on 5/1/17.
 */

public class BandAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Band alarm", "Alarm Received");
        if (intent.hasExtra(BandCollectionService.ACTION_SET_ALARM)) {
            BandCollectionService.toggle(context);
        }
    }
}
