package wbl.egr.uri.anear.ble.receivers;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/**
 * Created by root on 4/27/17.
 */

public abstract class BleDeviceInfoReceiver extends BroadcastReceiver {
    public static final IntentFilter INTENT_FILTER = new IntentFilter("uri.egr.wbl.library.ble_device_info_receiver");
    public static final String EXTRA_DEVICE_ADDRESS = "uri.egr.wbl.library.ble_device_address";
    public static final String EXTRA_DEVICE_NAME = "uri.egr.wbl.library.ble_device_name";
    public static final String EXTRA_DEVICE_TYPE = "uri.egr.wbl.library.ble_device_type";
}