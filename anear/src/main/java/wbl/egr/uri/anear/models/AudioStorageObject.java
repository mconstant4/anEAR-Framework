package wbl.egr.uri.anear.models;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * Created by root on 5/1/17.
 */

public abstract class AudioStorageObject extends StorageObject {
    public abstract File processRawAudio(File file);
}
