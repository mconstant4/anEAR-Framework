package wbl.egr.uri.anear.band.enums;

/**
 * Created by root on 4/26/17.
 */

public enum BandAction {
    INITIALIZE("init"),
    CONNECT("con"),
    START_STREAM("str"),
    STOP_STREAM("stop"),
    REQUEST_INFO("info"),
    DISCONNECT("dis"),
    DESTROY("des");

    private String mAction;

    BandAction(String action) {
        mAction = action;
    }

    @Override
    public String toString() {
        return mAction;
    }
}
