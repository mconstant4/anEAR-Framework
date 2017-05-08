package wbl.egr.uri.anearframework;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;

import wbl.egr.uri.anear.AnEar;
import wbl.egr.uri.anear.audio.services.AudioRecorderService;
import wbl.egr.uri.anear.models.AudioObject;
import wbl.egr.uri.anear.models.WavObject;

/**
 * Created by root on 5/2/17.
 */

public class DemoAudioActivity extends AppCompatActivity {
    private Context mContext;
    private Button mStartServiceButton, mStartRecordingButton, mStopRecordingButton, mStopServiceButton;
    private AudioObject mAudioObject;
    private WavObject mWavObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_demo);

        mContext = this;
        mStartServiceButton = (Button) findViewById(R.id.init_btn);
        mStartRecordingButton = (Button) findViewById(R.id.start_recording_btn);
        mStopRecordingButton = (Button) findViewById(R.id.stop_recording_btn);
        mStopServiceButton = (Button) findViewById(R.id.destroy_btn);

        mAudioObject = new AudioObject(10000);
        mAudioObject.enablePeriodic(15000);

        AnEar.setRoot(mContext, "wbl app");
        mWavObject = new WavObject(new File(AnEar.getRoot(mContext), "audio.wav"));

        mStartServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecorderService.initialize(mContext, mAudioObject, mWavObject);
            }
        });
        mStartRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecorderService.start(mContext);
            }
        });
        mStopRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecorderService.stop(mContext);
            }
        });
        mStopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioRecorderService.destroy(mContext);
            }
        });
    }
}
