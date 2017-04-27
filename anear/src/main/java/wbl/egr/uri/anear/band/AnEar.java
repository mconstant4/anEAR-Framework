package wbl.egr.uri.anear.band.enums;

import android.app.Application;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by root on 4/26/17.
 */

public class AnEar extends Application {
    public static final File ROOT_FILE = getRoot();

    private static File getRoot() {
        File root = new File("/storage/sdcard1");
        if (!root.exists() || !root.canWrite()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                root = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
            } else {
                root = new File(Environment.getExternalStorageDirectory(), "Documents");
            }
        }
        File directory = new File(root, ".anear");
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                Log.d("MAIN", "Made parent directories");
            }
        }
        return directory;
    }
}
