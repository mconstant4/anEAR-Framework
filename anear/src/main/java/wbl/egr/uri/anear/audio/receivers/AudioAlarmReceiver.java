package wbl.egr.uri.anear.audio.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import wbl.egr.uri.anear.audio.services.AudioRecorderService;

/**
 * Created by root on 5/2/17.
 */

public class AudioAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AudioRecorderService.start(context);
    }
}
