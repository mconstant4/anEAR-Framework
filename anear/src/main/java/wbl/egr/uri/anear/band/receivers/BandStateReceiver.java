package wbl.egr.uri.anear.band.receivers;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by root on 4/26/17.
 */

public abstract class BandStateReceiver extends BroadcastReceiver {
    public static final IntentFilter INTENT_FILTER = new IntentFilter("wbl.egr.uri.anear.filter.state");
    public static final String EXTRA_STATE = "wbl.egr.uri.anear.state";
}
