package wbl.egr.uri.anear.audio.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioRecord;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import wbl.egr.uri.anear.AnEar;
import wbl.egr.uri.anear.audio.enums.AudioAction;
import wbl.egr.uri.anear.audio.enums.AudioState;
import wbl.egr.uri.anear.audio.receivers.AudioAlarmReceiver;
import wbl.egr.uri.anear.models.AudioObject;
import wbl.egr.uri.anear.models.AudioStorageObject;

/**
 * Created by root on 5/1/17.
 */

public class AudioRecorderService extends Service {
    private static final String AUDIO_ACTION = "wbl.egr.uri.anear.audio.action";
    private static final String AUDIO_INPUT = "wbl.egr.uri.anear.audio.input";
    private static final String AUDIO_OUTPUT = "wbl.egr.uri.anear.audio.output";

    public static void initialize(Context context, AudioObject audioObject, AudioStorageObject audioStorageObject) {
        Intent intent = new Intent(context, AudioRecorderService.class);
        intent.putExtra(AUDIO_ACTION, AudioAction.INITIALIZE);
        intent.putExtra(AUDIO_INPUT, audioObject);
        intent.putExtra(AUDIO_OUTPUT, audioStorageObject);
        context.startService(intent);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AudioRecorderService.class);
        intent.putExtra(AUDIO_ACTION, AudioAction.START);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, AudioRecorderService.class);
        intent.putExtra(AUDIO_ACTION, AudioAction.STOP);
        context.startService(intent);
    }

    public static void destroy(Context context) {
        Intent intent = new Intent(context, AudioRecorderService.class);
        intent.putExtra(AUDIO_ACTION, AudioAction.DESTROY);
        context.startService(intent);
    }

    private AudioRecord mAudioRecord;
    private boolean mRecording;
    private AudioObject mAudioObject;
    private AudioStorageObject mStorageObject;
    private AudioState mAudioState;
    private File mTempFile;
    private CountDownTimer mTimer;
    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();

        mWakeLock = ((PowerManager) getSystemService(POWER_SERVICE) ).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AudioRecorderServiceWakeLock");
        mRecording = false;
        updateState(AudioState.UNINITIALIZED);
        mTempFile = new File(AnEar.ROOT_FILE, "temp.tmp");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int startId, int flags) {
        if (!intent.hasExtra(AUDIO_ACTION)) {
            return START_STICKY;
        }

        switch ((AudioAction) intent.getSerializableExtra(AUDIO_ACTION)) {
            case INITIALIZE:
                AudioObject audioObject = (AudioObject) intent.getSerializableExtra(AUDIO_INPUT);
                AudioStorageObject storageObject = (AudioStorageObject) intent.getSerializableExtra(AUDIO_OUTPUT);
                initialize(audioObject, storageObject);
                break;
            case START:
                startRecording();
                break;
            case STOP:
                stopRecording();
                cancelAlarm();
                break;
            case DESTROY:
                if (mRecording) {
                    stopRecording();
                }
                stopSelf();
                break;
            default:

                break;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mWakeLock.release();
    }

    private void initialize(AudioObject audioObject, AudioStorageObject storageObject) {
        mAudioObject = audioObject;
        mStorageObject = storageObject;

        mWakeLock.acquire();
        updateState(AudioState.INITIALIZED);
    }

    private void startRecording() {
        if (mAudioState != AudioState.INITIALIZED && mAudioState != AudioState.WAITING) {
            log("Cannot call Start Recording from this state (" + mAudioState.toString() + ")",
                    Log.WARN);
            return;
        }

        // Setup Temp File
        try {
            if (!mTempFile.exists()) {
                mTempFile.getParentFile().mkdirs();
            } else {
                mTempFile.delete();
            }
            mTempFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        prepareAudioRecord();

        if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            log("Audio Record not initialized properly", Log.WARN);
            return;
        }

        mAudioRecord.startRecording();
        log("Recording Started", Log.INFO);
        startTimer(mAudioObject.getDuration());
        mRecording = true;
        updateState(AudioState.RECORDING);
        saveRawAudio();
    }

    private void stopRecording() {
        if (mAudioState == AudioState.RECORDING) {
            mTimer.cancel();
            mRecording = false;
            releaseAudioRecord();
            log("Recording Stopped", Log.INFO);
            mStorageObject.processRawAudio(mTempFile);
            if (mAudioObject.isPeriodicEnabled()) {
                updateState(AudioState.WAITING);
                setAlarm();
            } else {
                updateState(AudioState.INITIALIZED);
            }
        }
    }

    private void prepareAudioRecord() {
        if (mAudioRecord != null) {
            releaseAudioRecord();
        }

        mAudioRecord = new AudioRecord(mAudioObject.getAudioSource(), mAudioObject.getSampleRate(),
                mAudioObject.getChannelConfig(), mAudioObject.getAudioFormat(), mAudioObject.getBufferSize());
    }

    private void releaseAudioRecord() {
        if (mAudioRecord == null) {
            return;
        }

        mAudioRecord.stop();
        mAudioRecord.release();
        mAudioRecord = null;
    }

    private void saveRawAudio() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(mTempFile);
                    byte[] audioData = new byte[mAudioObject.getBufferSize()];
                    while (mRecording && mAudioRecord.read(audioData, 0, mAudioObject.getBufferSize()) != -1) {
                        fileOutputStream.write(audioData);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "Audio Streaming Thread").start();
    }

    private void startTimer(int duration) {
        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new CountDownTimer(duration, duration) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                // Toggle Recording
                if (mAudioState == AudioState.RECORDING) {
                    stopRecording();
                }
            }
        }.start();
    }

    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + (mAudioObject.getDelay()),
                    generatePendingIntent());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + (mAudioObject.getDelay()),
                    generatePendingIntent());
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + (mAudioObject.getDelay()),
                    generatePendingIntent());
        }
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(generatePendingIntent());
    }

    private PendingIntent generatePendingIntent() {
        Intent intent = new Intent(this, AudioAlarmReceiver.class);
        return PendingIntent.getBroadcast(this, 387, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void updateState(AudioState audioState) {
        mAudioState = audioState;
    }

    private void log(String message, int type) {
        switch (type) {
            case Log.DEBUG:
                Log.d("Audio Recorder Service", message);
                break;
            case Log.ERROR:
                Log.e("Audio Recorder Service", message);
                break;
            case Log.INFO:
                Log.i("Audio Recorder Service", message);
                break;
            case Log.VERBOSE:
                Log.v("Audio Recorder Service", message);
                break;
            case Log.WARN:
                Log.w("Audio Recorder Service", message);
                break;
            default:
                Log.d("Audio Recorder Service", message);
                break;
        }
    }
}