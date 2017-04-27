package wbl.egr.uri.anear.band.services;

import android.app.Service;
import android.util.Log;

/**
 * Created by root on 4/26/17.
 */

public abstract class AnEarService extends Service {
    protected void log(String message, int type) {
        switch (type) {
            case Log.DEBUG:
                Log.d("Band Collection Service", message);
                break;
            case Log.ERROR:
                Log.e("Band Collection Service", message);
                break;
            case Log.INFO:
                Log.i("Band Collection Service", message);
                break;
            case Log.VERBOSE:
                Log.v("Band Collection Service", message);
                break;
            case Log.WARN:
                Log.w("Band Collection Service", message);
                break;
            default:
                Log.d("Band Collection Service", message);
                break;
        }
    }
}
