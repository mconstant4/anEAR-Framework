package wbl.egr.uri.anear;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;

/**
 * Created by root on 4/26/17.
 */

public class AnEar extends Application {
    private static final String FILE_PREF = "uri.egr.wbl.anear.pref_root_file";

    public static File getRoot(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String customPath = sharedPreferences.getString(FILE_PREF, null);
        if (customPath == null) {
            return getRootDirectory();
        } else {
            File file = new File(customPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        }
    }

    public static void setRoot(Context context, String id) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        File directory = getRootDirectory();
        if (directory == null) {
            Log.e("anEAR", "Fatal Error (Could not retrieve Root Directory)");
            return;
        }
        File file;
        if (id != null && !id.equals("")) {
            file = new File(directory, id);
        } else {
            file = directory;
        }
        editor.putString(FILE_PREF, file.getAbsolutePath());
        editor.apply();
    }

    private static File getRootDirectory() {
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
                return null;
            }
        }

        return directory;
    }
}
