package wbl.egr.uri.anear;

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
        // Do not change this! Works on targeted devices for WBL. Dynamically retrieving the
        // SD Card path is tricky on Android since it is different depending on the physical device.
        File root = new File("/storage/sdcard1");
        if (!root.exists() || !root.canWrite()) {
            // If no external SD Card mounted, use the Documents directory
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                root = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
            } else {
                root = new File(Environment.getExternalStorageDirectory(), "Documents");
            }
        }
        // Save all files in the .anear root directory
        File directory = new File(root, ".anear");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                // Error Creating Files, check your permissions
            }
        }
        return directory;
    }
}
