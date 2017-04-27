package wbl.egr.uri.anear.band.receivers;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by root on 4/27/17.
 */

public abstract class BandInfoReceiver extends BroadcastReceiver {
    public static final IntentFilter INTENT_FILTER = new IntentFilter("wbl.egr.uri.anear.band.filter.info");
    public static final String EXTRA_INFO = "wbl.egr.uri.anear.band.info";
}
