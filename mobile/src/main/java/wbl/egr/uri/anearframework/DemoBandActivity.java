package wbl.egr.uri.anearframework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;

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
                case INITIALIZED:
                    Log.d("Band Example", "Initialized");
                    break;
                case CONNECTED:
                    Log.d("Band Example", "Connected");
                    break;
            }
        }
    };
    private BandInfoReceiver mBandInfoReceiver = new BandInfoReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String[] info = intent.getStringArrayExtra(EXTRA_INFO);
            Log.d("Band Info Receiver", "Received Band Info:");
            for (String s : info) {
                Log.d("\tBand Info Receiver", s);
            }
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

        /*
        Register Receivers
                Band State Receiver: Receives the new state from Band Collection Service whenever
                                        it is updated.
                Band Info Receiver: Receives the Band's info whenever it is requested.
         */
        registerReceiver(mBandStateReceiver, BandStateReceiver.INTENT_FILTER);
        registerReceiver(mBandInfoReceiver, BandInfoReceiver.INTENT_FILTER);

        // Configure Band Object
        BandSensor[] sensors = {BandSensor.ACCELEROMETER, BandSensor.CONTACT, BandSensor.SKIN_TEMPERATURE};
        BandObject bandObject = new BandObject(BandObject.getPairedBands()[0]);
        bandObject.setSensorsToRecord(sensors);
        bandObject.setAutoStream(true);

        // Configure Storage Object
        StorageObject storageObject = new CsvObject();

        // Initialize Band Collection Service
        BandCollectionService.initialize(mContext, bandObject, storageObject);
    }
}
