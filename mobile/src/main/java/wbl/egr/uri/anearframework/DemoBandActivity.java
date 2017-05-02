package wbl.egr.uri.anearframework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.microsoft.band.BandInfo;

import java.io.File;
import java.lang.ref.WeakReference;

import wbl.egr.uri.anear.AnEar;
import wbl.egr.uri.anear.band.enums.BandSensor;
import wbl.egr.uri.anear.band.enums.BandState;
import wbl.egr.uri.anear.models.BandObject;
import wbl.egr.uri.anear.models.CsvObject;
import wbl.egr.uri.anear.models.StorageObject;
import wbl.egr.uri.anear.band.receivers.BandInfoReceiver;
import wbl.egr.uri.anear.band.receivers.BandStateReceiver;
import wbl.egr.uri.anear.band.services.BandCollectionService;
import wbl.egr.uri.anear.band.tasks.RequestHeartRateTask;

public class DemoBandActivity extends AppCompatActivity {

    private Context mContext;
    private Button mRequestHRButton, mConnectButton, mStartStreamButton, mStopStreamButton,
                   mDisconnectButton;

    private BandStateReceiver mBandStateReceiver = new BandStateReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch ((BandState) intent.getSerializableExtra(EXTRA_STATE)) {
                case UNINITIALIZED:
                    Log.d("Band Example", "Uninitialized");
                    break;
                case INITIALIZED:
                    Log.d("Band Example", "Initialized");
                    break;
                case CONNECTING:
                    Log.d("Band Example", "Connecting");
                    break;
                case CONNECTED:
                    Log.d("Band Example", "Connected");
                    break;
                case STREAMING:
                    Log.d("Band Example", "Streaming");
                    break;
                case PAUSED:
                    Log.d("Band Example", "Paused");
                    break;
                case NOT_WORN:
                    Log.d("Band Example", "Not Worn");
                    break;
                case DISCONNECTING:
                    Log.d("Band Example", "Disconnecting");
                    break;
                case DISCONNECTED:
                    Log.d("Band Example", "Disconnected");
                    break;
            }
        }
    };
    private BandInfoReceiver mBandInfoReceiver = new BandInfoReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String[] info = intent.getStringArrayExtra(EXTRA_INFO);
            Log.d("Band Info Receiver", "Received Band Info:");
            Log.d("Band Info Receiver", "Band Name :\t" + info[0]);
            Log.d("Band Info Receiver", "Band Address:\t" + info[1]);
            Log.d("Band Info Receiver", "HW Version:\t" + info[2]);
            Log.d("Band Info Receiver", "FW Version:\t" + info[3]);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_demo);

        mContext = this;

        mRequestHRButton = (Button) findViewById(R.id.consent_btn);
        mRequestHRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestHeartRateTask().execute(new WeakReference<Activity>((Activity) mContext));
            }
        });
        mConnectButton = (Button) findViewById(R.id.connect_btn);
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BandCollectionService.connect(mContext);
            }
        });
        mStartStreamButton = (Button) findViewById(R.id.start_stream_btn);
        mStartStreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BandCollectionService.requestInfo(mContext);
                BandCollectionService.startStream(mContext);
            }
        });
        mStopStreamButton = (Button) findViewById(R.id.stop_stream_btn);
        mStopStreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BandCollectionService.stopStream(mContext);
            }
        });
        mDisconnectButton = (Button) findViewById(R.id.disconnect_btn);
        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BandCollectionService.disconnect(mContext);
            }
        });

        // Register Receivers
        registerReceiver(mBandStateReceiver, BandStateReceiver.INTENT_FILTER);
        registerReceiver(mBandInfoReceiver, BandInfoReceiver.INTENT_FILTER);

        // Get Desired Band
        BandInfo[] pairedBands = BandObject.getPairedBands();
        BandInfo band = pairedBands[0];

        // Get Sensors to Record
        BandSensor[] sensors = {BandSensor.ACCELEROMETER, BandSensor.CONTACT, BandSensor.SKIN_TEMPERATURE};

        // Configure Band Object
        BandObject bandObject = new BandObject(band);
        bandObject.setSensorsToRecord(sensors);
        bandObject.enableAutoStream(true);
        bandObject.enablePeriodic(false);
        bandObject.enableHapticFeedback(true);

        // Get Destination Directory
        File directory = AnEar.ROOT_FILE;

        // Configure Storage Object
        StorageObject storageObject = new CsvObject(directory);

        // Initialize Band Collection Service
        BandCollectionService.initialize(mContext, bandObject, storageObject);
    }
}
