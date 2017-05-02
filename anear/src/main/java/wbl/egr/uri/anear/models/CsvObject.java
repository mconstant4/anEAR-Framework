package wbl.egr.uri.anear.models;

import java.io.File;
import java.io.Serializable;

import wbl.egr.uri.anear.AnEar;

/**
 * Created by root on 4/26/17.
 */

public class CsvObject extends StorageObject implements Serializable {
    private File mRootFile;

    public CsvObject() {
        //Default
        mRootFile = AnEar.ROOT_FILE;
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
