package wbl.egr.uri.anear.io.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by root on 4/26/17.
 */

public class CsvLogService extends IntentService {
    public static void logData(Context context, File file, String header, String contents) {
        Intent intent = new Intent(context, CsvLogService.class);
        intent.putExtra(EXTRA_FILE, file.getAbsolutePath());
        intent.putExtra(EXTRA_HEADER, header);
        intent.putExtra(EXTRA_CONTENTS, contents);
        context.startService(intent);
    }

    public static String generateContents(String[] contents) {
        String content = "";
        for (String s : contents) {
            content += s + ",";
        }
        return content.substring(1, content.length() - 1);
    }

    private static final String EXTRA_FILE = "wbl.egr.uri.anear.io.file";
    private static final String EXTRA_HEADER = "wbl.egr.uri.anear.io.header";
    private static final String EXTRA_CONTENTS = "wbl.egr.uri.anear.io.contents";

    public CsvLogService() {
        super("CSV Log Service Thread");
    }

    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.e("CSV Log Service", "App must have Write External Storage Permission!");
            }
        }
    }

    @Override
    public void onHandleIntent(Intent intent) {
        if (intent == null || !intent.hasExtra(EXTRA_FILE) ||
                !intent.hasExtra(EXTRA_HEADER) || !intent.hasExtra(EXTRA_CONTENTS)) {
            Log.d("CSV Log Service", "Invalid Intent");
            return;
        }

        File file = new File(intent.getStringExtra(EXTRA_FILE));
        boolean newFile = false;

        if (!file.exists()) {
            newFile = true;
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String header = intent.getStringExtra(EXTRA_HEADER);
        String contents = intent.getStringExtra(EXTRA_CONTENTS);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            if (newFile) {
                fileOutputStream.write(header.getBytes());
                fileOutputStream.write("\n".getBytes());
            }
            fileOutputStream.write(contents.getBytes());
            fileOutputStream.write("\n".getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
