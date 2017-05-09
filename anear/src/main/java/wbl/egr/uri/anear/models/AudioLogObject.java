package wbl.egr.uri.anear.models;

import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wbl.egr.uri.anear.AnEar;
import wbl.egr.uri.anear.io.services.CsvLogService;

/**
 * Created by root on 5/8/17.
 */

public class AudioLogObject {
    private Context mContext;
    private Date mStartDate, mEndDate;
    private boolean mTriggered;

    public AudioLogObject(Context context, boolean triggered) {
        mContext = context;
        mTriggered = triggered;
    }

    public void start() {
        mStartDate = Calendar.getInstance().getTime();
    }

    public void stop(File file) {
        mEndDate = Calendar.getInstance().getTime();

        logAudioRecord(file);
    }

    private void logAudioRecord(File file) {
        String header = "Start Date,Start Time,End Date,End Time,File Size (bytes),Triggered?";
        String startDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(mStartDate);
        String startTime = new SimpleDateFormat("kk:mm:ss", Locale.US).format(mStartDate);
        String endDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(mEndDate);
        String endTime = new SimpleDateFormat("kk:mm:ss", Locale.US).format(mEndDate);
        long fileSize = file.length();

        String[] contents = {startDate, startTime, endDate, endTime, String.valueOf(fileSize), String.valueOf(mTriggered)};
        String content = CsvLogService.generateContents(contents);
        CsvLogService.logData(mContext, new File(AnEar.getRoot(mContext), "AudioRecordLog.csv"), header, content);
    }
}
