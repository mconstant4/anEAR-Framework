package wbl.egr.uri.anear.audio.receivers;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by root on 5/2/17.
 */

public abstract class AudioStateReceiver extends BroadcastReceiver {
    public static final IntentFilter INTENT_FILTER = new IntentFilter("wbl.egr.uri.anear.audio.filter.state");
    public static final String EXTRA_STATE = "wbl.egr.uri.anear.audio.state";
}
