package loop.ms.looptrips;

import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import ms.loop.loopsdk.core.ILoopSDKCallback;
import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.Drives;
import ms.loop.loopsdk.profile.IProfileDownloadCallback;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Trips;
import ms.loop.loopsdk.signal.SignalConfig;
import ms.loop.loopsdk.util.LoopError;

/**
 * Created on 5/30/16.
 */
public class SampleApplication extends MultiDexApplication implements ILoopSDKCallback {

    private Trips trips;

    @Override
    public void onCreate()
    {
        super.onCreate();

        //replace appId and device id below

        String appId = "";
        String appToken = "";

        //replace your user id and device id
        String userId = "";
        String deviceId = "";

        LoopSDK.initialize(this, appId, appToken, userId, deviceId);
    }

    @Override
    public void onInitialized() {

        Intent i = new Intent("android.intent.action.onInitialized").putExtra("status", "initialized");
        this.sendBroadcast(i);
    }
    @Override
    public void onInitializeFailed(LoopError loopError) {}

    @Override
    public void onServiceStatusChanged(String provider, String status, Bundle bundle) {}
}
