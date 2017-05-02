package wbl.egr.uri.anear.models;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import wbl.egr.uri.anear.AnEar;

/**
 * Created by root on 4/26/17.
 */

public class CsvObject extends StorageObject implements Serializable {
    private File mRootFile;

    public CsvObject(Context context) {
        //Default
        mRootFile = AnEar.getRoot(context);
    }

    public CsvObject(File rootFile) {
        mRootFile = rootFile;
    }

    public File getRootFile() {
        return mRootFile;
    }

    public void setFile(File rootFile) {
        mRootFile = rootFile;
    }
}
