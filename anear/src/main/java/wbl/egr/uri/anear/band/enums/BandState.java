package wbl.egr.uri.anear.band.enums;

/**
 * Created by root on 4/26/17.
 */

public enum BandState {
    UNINITIALIZED("uninitialized"),
    INITIALIZED("initialized"),
    CONNECTING("connecting"),
    CONNECTED("connected"),
    STREAMING("streaming"),
    NOT_WORN("not worn"),
    PAUSED("paused"),
    DISCONNECTING("disconnecting"),
    DISCONNECTED("disconnected");


    private String mState;

    BandState(String state) {
        mState = state;
    }

    @Override
    public String toString() {
        return mState;
    }
}
