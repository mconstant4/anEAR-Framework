package wbl.egr.uri.anear.band.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandConnectionCallback;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandResultCallback;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.notifications.VibrationType;
import com.microsoft.band.sensors.BandSensorManager;
import com.microsoft.band.sensors.GsrSampleRate;
import com.microsoft.band.sensors.SampleRate;

import wbl.egr.uri.anear.band.enums.BandAction;
import wbl.egr.uri.anear.band.enums.BandSensor;
import wbl.egr.uri.anear.band.enums.BandState;
import wbl.egr.uri.anear.band.listeners.BandAccelerometerListener;
import wbl.egr.uri.anear.band.listeners.BandAltimeterListener;
import wbl.egr.uri.anear.band.listeners.BandAmbientLightListener;
import wbl.egr.uri.anear.band.listeners.BandBarometerListener;
import wbl.egr.uri.anear.band.listeners.BandCaloriesListener;
import wbl.egr.uri.anear.band.listeners.BandContactListener;
import wbl.egr.uri.anear.band.listeners.BandDistanceListener;
import wbl.egr.uri.anear.band.listeners.BandGsrListener;
import wbl.egr.uri.anear.band.listeners.BandGyroscopeListener;
import wbl.egr.uri.anear.band.listeners.BandHeartRateListener;
import wbl.egr.uri.anear.band.listeners.BandPedometerListener;
import wbl.egr.uri.anear.band.listeners.BandRrIntervalListener;
import wbl.egr.uri.anear.band.listeners.BandSkinTemperatureListener;
import wbl.egr.uri.anear.band.listeners.BandUvListener;
import wbl.egr.uri.anear.band.receivers.BandAlarmReceiver;
import wbl.egr.uri.anear.models.BandObject;
import wbl.egr.uri.anear.models.StorageObject;
import wbl.egr.uri.anear.band.receivers.BandContactReceiver;
import wbl.egr.uri.anear.band.receivers.BandInfoReceiver;
import wbl.egr.uri.anear.band.receivers.BandStateReceiver;

/**
 * Created by root on 4/26/17.
 */

public class BandCollectionService extends AnEarService {
    private static final String BAND_ACTION = "wbl.egr.uri.anear.band.action";
    private static final String BAND_INPUT = "wbl.egr.uri.anear.band.input";
    private static final String BAND_OUTPUT = "wbl.egr.uri.anear.band.output";

    public static final String ACTION_SET_ALARM = "wbl.egr.uri.anear.band.alarm.set";
    public static final String ACTION_CANCEL_ALARM = "wbl.egr.uri.anear.band.alarm.cancel";

    /**
     * This method is used to setup this service to connect to a specific BandObject (a Microsoft
     * Band) and send the information to a specific StorageObject (i.e. CsvObject). This must be
     * called before any other actions can be executed.
     *
     * @param context The Context of the Caller.
     * @param bandObject The desired Microsoft Band (compatible with both MB 1 and MB 2).
     * @param storageObject The desired destination for the Sensor Data.
     */
    public static void initialize(Context context, BandObject bandObject, StorageObject storageObject) {
        Intent intent = new Intent(context, BandCollectionService.class);
        intent.putExtra(BAND_ACTION, BandAction.INITIALIZE);
        intent.putExtra(BAND_INPUT, bandObject);
        intent.putExtra(BAND_OUTPUT, storageObject);
        context.startService(intent);
    }

    /**
     * This method connects the Android device to the BandObject setup in the initialize method.
     * @param context The Context of the Caller.
     */
    public static void connect(Context context) {
        Intent intent = new Intent(context, BandCollectionService.class);
        intent.putExtra(BAND_ACTION, BandAction.CONNECT);
        context.startService(intent);
    }

    /**
     * This method begins recordings sensor data from the sensors selected in the BandObject.
     * @param context The Context of the Caller.
     */
    public static void startStream(Context context) {
        Intent intent = new Intent(context, BandCollectionService.class);
        intent.putExtra(BAND_ACTION, BandAction.START_STREAM);
        context.startService(intent);
    }

    /**
     * This method stops recordings sensor data from the BandObject
     * @param context The Context of the Caller.
     */
    public static void stopStream(Context context) {
        Intent intent = new Intent(context, BandCollectionService.class);
        intent.putExtra(BAND_ACTION, BandAction.STOP_STREAM);
        context.startService(intent);
    }

    public static void toggle(Context context) {
        Intent intent = new Intent(context, BandCollectionService.class);
        intent.putExtra(BAND_ACTION, BandAction.TOGGLE);
        context.startService(intent);
    }

    /**
     * This method requests information from the connected BandObject. The returned info is in a
     * String array with the following format:<br/>
     *          [Band Name, Band Address, Band HW Version, Band FW Version]
     * @param context The Context of the Caller.
     */
    public static void requestInfo(Context context) {
        Intent intent = new Intent(context, BandCollectionService.class);
        intent.putExtra(BAND_ACTION, BandAction.REQUEST_INFO);
        context.startService(intent);
    }

    /**
     * This method disconnects the Android device from the connected BandObject.
     * @param context The Context of the Caller.
     */
    public static void disconnect(Context context) {
        Intent intent = new Intent(context, BandCollectionService.class);
        intent.putExtra(BAND_ACTION, BandAction.DISCONNECT);
        context.startService(intent);
    }

    /**
     * This method safely destroys this Service. It handles all disconnects properly if
     * necessary.
     * @param context The Context of the Caller.
     */
    public static void stopService(Context context) {
        Intent intent = new Intent(context, BandCollectionService.class);
        intent.putExtra(BAND_ACTION, BandAction.DESTROY);
        context.startService(intent);
    }

    private BandResultCallback<ConnectionState> mBandResultCallback = new BandResultCallback<ConnectionState>() {
        @Override
        public void onResult(ConnectionState connectionState, Throwable throwable) {
            if (connectionState == ConnectionState.CONNECTED) {
                log("Connected to " + mBandObject.getBandName(), Log.INFO);
                if (mBandObject.isHapticFeedback()) {
                    try {
                        mBandClient.getNotificationManager().vibrate(VibrationType.RAMP_UP);
                    } catch (BandIOException e) {
                        e.printStackTrace();
                    }
                }
                updateState(BandState.CONNECTED);
                mBandClient.registerConnectionCallback(mBandConnectionCallback);

                if (mBandObject.isAutoStream()) {
                    if (mBandObject.isPeriodic()) {
                        updateState(BandState.PAUSED);
                        setAlarm();
                        mWakeLock.acquire();
                    } else {
                        startStream();
                    }
                }
            } else {
                log("Connect Failed (Could not Connect)", Log.ERROR);
                if (mBandObject.isHapticFeedback()) {
                    try {
                        mBandClient.getNotificationManager().vibrate(VibrationType.THREE_TONE_HIGH);
                    } catch (BandIOException e) {
                        e.printStackTrace();
                    }
                }
                mBandState = BandState.INITIALIZED;
            }
        }
    };

    private BandResultCallback<Void> mDisconnectBandResultCallback = new BandResultCallback<Void>() {
        @Override
        public void onResult(Void aVoid, Throwable throwable) {
            log("Disconnected from " + mBandObject.getBandName(), Log.INFO);
            updateState(BandState.DISCONNECTED);
            if (mDestroying) {
                stopSelf();
            }
        }
    };

    private BandConnectionCallback mBandConnectionCallback = new BandConnectionCallback() {
        @Override
        public void onStateChanged(ConnectionState connectionState) {
            switch (connectionState) {
                case BOUND:
                    updateState(BandState.DISCONNECTED);
                    break;
                case CONNECTED:
                    if (mBandObject.isHapticFeedback()) {
                        try {
                            mBandClient.getNotificationManager().vibrate(VibrationType.RAMP_UP);
                        } catch (BandIOException e) {
                            e.printStackTrace();
                        }
                    }
                    updateState(BandState.CONNECTED);
                    if (mBandObject.isAutoStream()) {
                        startStream();
                    }
                    break;
                case UNBOUND:
                    updateState(BandState.DISCONNECTED);
                    break;
            }
        }
    };

    private BandContactReceiver mBandContactReceiver = new BandContactReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(EXTRA_CONTACT, true)) {
                if (mBandState == BandState.NOT_WORN) {
                    leaveDynamicBlackout();
                }
            } else {
                if (mBandState == BandState.STREAMING || mBandState == BandState.PAUSED) {
                    updateState(BandState.NOT_WORN);
                    enterDynamicBlackout();
                }
            }
        }
    };

    private final int NOTIFICATION_ID = 333;

    private Context mContext;
    private BandState mBandState;
    private BandObject mBandObject;
    private StorageObject mStorageObject;
    private BandClientManager mBandClientManager;
    private BandClient mBandClient;
    private PowerManager.WakeLock mWakeLock;
    private boolean mDestroying;

    @Override
    public void onCreate() {
        super.onCreate();
        log("Service Created", Log.INFO);

        mContext = this;
        mDestroying = false;
        updateState(BandState.UNINITIALIZED);

        //Declare as Foreground Service
        Notification notification = new Notification.Builder(this)
                .setContentTitle("ED EAR Active")
                .setContentText("EAR is Starting")
                .setContentIntent(generateNotificationPendingIntent())
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .build();
        startForeground(NOTIFICATION_ID, notification);

        mBandClientManager = BandClientManager.getInstance();
        mWakeLock = ((PowerManager) getSystemService(POWER_SERVICE) ).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BandCollectionServiceWakeLock");

        registerReceiver(mBandContactReceiver, BandContactReceiver.INTENT_FILTER);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Validate Intent
        if (intent == null || !intent.hasExtra(BAND_ACTION)) {
            log("Action Ignored (Invalid Intent Received)", Log.WARN);
            return START_STICKY;
        }

        // Handle Action
        switch ((BandAction) intent.getSerializableExtra(BAND_ACTION)) {
            case INITIALIZE:
                BandObject bandObject = (BandObject) intent.getSerializableExtra(BAND_INPUT);
                StorageObject storageObject = (StorageObject) intent.getSerializableExtra(BAND_OUTPUT);
                initialize(bandObject, storageObject);
                break;
            case CONNECT:
                connect();
                break;
            case START_STREAM:
                if (mBandObject != null && mBandObject.isPeriodic() && mBandState == BandState.CONNECTED) {
                    updateState(BandState.PAUSED);
                    setAlarm();
                    mWakeLock.acquire();
                } else {
                    startStream();
                }
                break;
            case STOP_STREAM:
                stopStream();
                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
                break;
            case REQUEST_INFO:
                requestInfo();
                break;
            case TOGGLE:
                toggle();
                break;
            case DISCONNECT:
                disconnect();
                break;
            case DESTROY:
                mDestroying = true;
                stopStream();
                disconnect();
                break;
            default:
                log("Action Ignored (Invalid Action Received)", Log.WARN);
                break;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        log("Service Destroyed", Log.INFO);
        unregisterReceiver(mBandContactReceiver);
        super.onDestroy();
    }

    private void initialize(BandObject bandObject, StorageObject storageObject) {
        if (mBandState != BandState.UNINITIALIZED) {
            log("Cannot Call Initialize from Current State (" + mBandState.toString() + ")",
                    Log.WARN);
        } else {
            mBandObject = bandObject;
            mStorageObject = storageObject;

            if (mBandObject == null) {
                log("Initialize Failed (Band Object is Null)", Log.ERROR);
                return;
            } else {
                log("Initialize Band: " + mBandObject.getBandName(), Log.INFO);
            }
            if (mStorageObject == null) {
                log("Initialize Failed (Storage Object is Null", Log.ERROR);
                return;
            }

            log("Service Initialized", Log.INFO);
            updateState(BandState.INITIALIZED);
        }
    }

    private void connect() {
        if (mBandState != BandState.INITIALIZED && mBandState != BandState.DISCONNECTED) {
            log("Cannot Call Connect from Current State (" + mBandState.toString() + ")",
                    Log.WARN);
        } else {
            BandInfo[] pairedBands = mBandClientManager.getPairedBands();
            boolean connecting = false;
            if (mBandObject.getBandAddress() == null) {
                log("No Band Address Specified", Log.WARN);
                mBandClient = mBandClientManager.create(mContext, pairedBands[0]);
                mBandClient.connect().registerResultCallback(mBandResultCallback);
                mBandState = BandState.CONNECTING;
                connecting = true;
            } else {
                for (BandInfo band : pairedBands) {
                    if (band.getMacAddress().equals(mBandObject.getBandAddress())) {
                        mBandClient = mBandClientManager.create(mContext, band);
                        mBandClient.connect().registerResultCallback(mBandResultCallback);
                        log("Connecting to Band...", Log.INFO);
                        mBandState = BandState.CONNECTING;
                        connecting = true;
                    }
                }
            }

            if (!connecting) {
                log("Connect Failed (Configured Band is Not Paired)", Log.WARN);
            } else {
                updateState(BandState.CONNECTING);
            }
        }
    }

    private void startStream() {
        if (mBandState != BandState.CONNECTED && mBandState != BandState.PAUSED && mBandState != BandState.NOT_WORN) {
            log("Cannot Call Start Stream from Current State (" + mBandState.toString() + ")",
                    Log.WARN);
        } else {
            try {
                BandSensorManager bandSensorManager = mBandClient.getSensorManager();
                if (mBandObject.getSensorsToRecord() == null) {
                    log("No Sensors Selected", Log.WARN);
                } else {
                    for (BandSensor sensor : mBandObject.getSensorsToRecord()) {
                        switch (sensor) {
                            case ACCELEROMETER:
                                BandAccelerometerListener bandAccelerometerListener =
                                        new BandAccelerometerListener(mContext, mStorageObject);
                                bandSensorManager.registerAccelerometerEventListener(
                                        bandAccelerometerListener, SampleRate.MS128);
                                break;
                            case ALTIMETER:
                                try {
                                    BandAltimeterListener bandAltimeterListener =
                                            new BandAltimeterListener(mContext, mStorageObject);
                                    bandSensorManager.registerAltimeterEventListener(bandAltimeterListener);
                                } catch (InvalidBandVersionException e) {
                                    log("Altimeter is only available on the Microsoft Band 2", Log.WARN);
                                    break;
                                }
                                break;
                            case AMBIENT_LIGHT:
                                try {
                                    BandAmbientLightListener bandAmbientLightListener =
                                            new BandAmbientLightListener(mContext, mStorageObject);
                                    bandSensorManager.registerAmbientLightEventListener(bandAmbientLightListener);
                                } catch (InvalidBandVersionException e) {
                                    log("Ambient Light is only available on the Microsoft Band 2", Log.WARN);
                                    break;
                                }
                                break;
                            case BAROMETER:
                                try {
                                    BandBarometerListener bandBarometerListener =
                                            new BandBarometerListener(mContext, mStorageObject);
                                    bandSensorManager.registerBarometerEventListener(bandBarometerListener);
                                } catch (InvalidBandVersionException e) {
                                    log("Barometer is only available on the Microsoft Band 2", Log.WARN);
                                    break;
                                }
                                break;
                            case CALORIES:
                                BandCaloriesListener bandCaloriesListener =
                                        new BandCaloriesListener(mContext, mStorageObject);
                                bandSensorManager.registerCaloriesEventListener(bandCaloriesListener);
                                break;
                            case CONTACT:
                                BandContactListener bandContactListener =
                                        new BandContactListener(mContext, mStorageObject);
                                bandSensorManager.registerContactEventListener(bandContactListener);
                                break;
                            case DISTANCE:
                                BandDistanceListener bandDistanceListener =
                                        new BandDistanceListener(mContext, mStorageObject);
                                bandSensorManager.registerDistanceEventListener(bandDistanceListener);
                                break;
                            case GSR:
                                try {
                                    BandGsrListener bandGsrListener =
                                            new BandGsrListener(mContext, mStorageObject);
                                    bandSensorManager.registerGsrEventListener(bandGsrListener, GsrSampleRate.MS5000);
                                } catch (InvalidBandVersionException e) {
                                    log("GSR is only available on the Microsoft Band 2", Log.WARN);
                                    break;
                                }
                                break;
                            case GYROSCOPE:
                                BandGyroscopeListener bandGyroscopeListener =
                                        new BandGyroscopeListener(mContext, mStorageObject);
                                bandSensorManager.registerGyroscopeEventListener(bandGyroscopeListener, SampleRate.MS128);
                                break;
                            case HEART_RATE:
                                BandHeartRateListener bandHeartRateListener =
                                        new BandHeartRateListener(mContext, mStorageObject);
                                bandSensorManager.registerHeartRateEventListener(bandHeartRateListener);
                                break;
                            case PEDOMETER:
                                BandPedometerListener bandPedometerListener =
                                        new BandPedometerListener(mContext, mStorageObject);
                                bandSensorManager.registerPedometerEventListener(bandPedometerListener);
                                break;
                            case RR_INTERVAL:
                                try {
                                    BandRrIntervalListener bandRrIntervalListener =
                                            new BandRrIntervalListener(mContext, mStorageObject);
                                    bandSensorManager.registerRRIntervalEventListener(bandRrIntervalListener);
                                } catch (InvalidBandVersionException e) {
                                    log("RR Interval is only available on the Microsoft Band 2", Log.WARN);
                                    break;
                                }
                                break;
                            case SKIN_TEMPERATURE:
                                BandSkinTemperatureListener bandSkinTemperatureListener =
                                        new BandSkinTemperatureListener(mContext, mStorageObject);
                                bandSensorManager.registerSkinTemperatureEventListener(bandSkinTemperatureListener);
                                break;
                            case UV:
                                BandUvListener bandUvListener =
                                        new BandUvListener(mContext, mStorageObject);
                                bandSensorManager.registerUVEventListener(bandUvListener);
                                break;
                            default:
                                log("Invalid Sensor Name (" + sensor + ")", Log.WARN);
                                break;
                        }
                    }
                }
            } catch (BandException e) {
                e.printStackTrace();
            } finally {
                updateState(BandState.STREAMING);
            }
        }
    }

    private void stopStream() {
        if (mBandState != BandState.STREAMING && mBandState != BandState.NOT_WORN &&
                mBandState != BandState.PAUSED && !mDestroying) {
            log("Cannot Call Stop Stream from Current State (" + mBandState.toString() + ")",
                    Log.WARN);
        } else {
            if (mBandClient != null && mBandClient.isConnected()) {
                try {
                    cancelAlarm();
                    mBandClient.getSensorManager().unregisterAllListeners();
                    updateState(BandState.CONNECTED);
                } catch (BandIOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void toggle() {
        if (mBandState == BandState.STREAMING || mBandState == BandState.NOT_WORN) {
            // Pause Sensor Recordings
            try {
                mBandClient.getSensorManager().unregisterAllListeners();
                updateState(BandState.PAUSED);
            } catch (BandIOException e) {
                e.printStackTrace();
            }
        } else if (mBandState == BandState.PAUSED) {
            // Resume Sensor Recordings
            startStream();
        } else {
            log("Cannot Call Toggle from Current State (" + mBandState.toString() + ")",
                    Log.WARN);
        }

        setAlarm();
    }

    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + (3 * 60 * 1000),
                    generatePendingIntent(ACTION_SET_ALARM));
        } else {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + (3 * 60 * 1000),
                    generatePendingIntent(ACTION_SET_ALARM));
        }
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(generatePendingIntent(ACTION_SET_ALARM));
    }

    private void enterDynamicBlackout() {
        if (mBandState == BandState.NOT_WORN) {
            try {
                mBandClient.getSensorManager().unregisterAllListeners();
                mBandClient.getSensorManager().registerContactEventListener(new BandContactListener(mContext, mStorageObject));
            } catch (BandIOException e) {
                e.printStackTrace();
            }
        } else {
            log("Cannot Enter Dynamic Blackout from Current State (" + mBandState.toString() + ")",
                    Log.WARN);
        }
    }

    private void leaveDynamicBlackout() {
        if (mBandState == BandState.NOT_WORN) {
            try {
                mBandClient.getSensorManager().unregisterContactEventListeners();
                startStream();
            } catch (BandIOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestInfo() {
        if (mBandState != BandState.CONNECTED && mBandState != BandState.STREAMING &&
                mBandState != BandState.NOT_WORN && mBandState != BandState.PAUSED) {
            log("Cannot Call Request Info from Current State (" + mBandState.toString() + ")",
                    Log.WARN);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String[] info = new String[4];
                        info[0] = mBandObject.getBandName();
                        info[1] = mBandObject.getBandAddress();
                        info[2] = mBandClient.getFirmwareVersion().await();
                        info[3] = mBandClient.getHardwareVersion().await();

                        // Send Broadcast
                        Intent intent = new Intent(BandInfoReceiver.INTENT_FILTER.getAction(0));
                        intent.putExtra(BandInfoReceiver.EXTRA_INFO, info);
                        sendBroadcast(intent);
                    } catch (BandException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void disconnect() {
        switch (mBandState) {
            case CONNECTED:
                if (mBandObject.isHapticFeedback()) {
                    try {
                        mBandClient.getNotificationManager().vibrate(VibrationType.RAMP_DOWN);
                    } catch (BandIOException e) {
                        e.printStackTrace();
                    }
                }
                mBandClient.disconnect().registerResultCallback(mDisconnectBandResultCallback);
                mBandClient.unregisterConnectionCallback();
                updateState(BandState.DISCONNECTING);
                break;
            case STREAMING:
            case PAUSED:
            case NOT_WORN:
                stopStream();
                if (mBandObject.isHapticFeedback()) {
                    try {
                        mBandClient.getNotificationManager().vibrate(VibrationType.RAMP_DOWN);
                    } catch (BandIOException e) {
                        e.printStackTrace();
                    }
                }
                mBandClient.disconnect().registerResultCallback(mDisconnectBandResultCallback);
                mBandClient.unregisterConnectionCallback();
                updateState(BandState.DISCONNECTING);
                break;
            default:
                if (mDestroying) {
                    stopSelf();
                } else {
                    log("Cannot Call Disconnect from Current State (" + mBandState.toString() + ")",
                            Log.WARN);
                }
                break;
        }
    }

    private PendingIntent generatePendingIntent(String action) {
        Intent intent = new Intent(mContext, BandAlarmReceiver.class);
        intent.putExtra(ACTION_SET_ALARM, action);
        return PendingIntent.getBroadcast(mContext, 347, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent generateNotificationPendingIntent() {
        Intent intent = new Intent(mContext, BandCollectionService.class);
        intent.putExtra(BAND_ACTION, BandAction.DESTROY);
        return PendingIntent.getService(mContext, 474, intent, 0);
    }

    private void updateState(BandState bandState) {
        log("State: " + bandState.toString(), Log.DEBUG);

        mBandState = bandState;
        updateNotification(bandState.toString());

        Intent intent = new Intent(BandStateReceiver.INTENT_FILTER.getAction(0));
        intent.putExtra(BandStateReceiver.EXTRA_STATE, bandState);
        sendBroadcast(intent);
    }

    private void updateNotification(String status) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setContentTitle("EAR is Active")
                .setContentIntent(generateNotificationPendingIntent())
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentText("Band Status: " + status);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
