package loop.ms.looptrips;

import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import ms.loop.loopsdk.core.ILoopSDKCallback;
import ms.loop.loopsdk.core.LoopSDK;
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
        String appId = "Your App Id";
        String appToken = "Your App Token";

        //replace your user id and device id
        String userId = "Your User Id";
        String deviceId = "Your Device Id";

        LoopSDK.initialize(this, appId, appToken, userId, deviceId);
    }

    @Override
    public void onInitialized() {

        trips = Trips.createAndLoad(Trips.class, Trip.class);
        trips.download(true, new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {}
            @Override
            public void onProfileDownloadFailed(LoopError error) {}
        });
    }
    @Override
    public void onInitializeFailed(LoopError loopError) {}

    @Override
    public void onServiceStatusChanged(String provider, String status, Bundle bundle) {}
}
