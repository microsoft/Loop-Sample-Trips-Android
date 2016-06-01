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
        String appId = "shuaib-sample-ap-dev-4a780486";
        String appToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6InNodWFpYi1zYW1wbGUtYXAtZGV2LTRhNzgwNDg2IiwiYXBwS2V5IjoiZGQxMzIxZjQ3MDAzLTRmMjYtODVmOC04OWNhNTM2ODhhYjQiLCJhbGxvd2VkUm91dGVzIjpbeyJtZXRob2QiOiJwb3N0IiwicGF0aCI6Ii92Mi4wL2FwcC9zaHVhaWItc2FtcGxlLWFwLWRldi00YTc4MDQ4Ni91c2VyIn0seyJtZXRob2QiOiJkZWxldGUiLCJwYXRoIjoiL3YyLjAvYXBwL3NodWFpYi1zYW1wbGUtYXAtZGV2LTRhNzgwNDg2L3VzZXIifSx7Im1ldGhvZCI6InBvc3QiLCJwYXRoIjoiL3YyLjAvYXBwL3NodWFpYi1zYW1wbGUtYXAtZGV2LTRhNzgwNDg2L2xvZ2luIn0seyJtZXRob2QiOiJnZXQiLCJwYXRoIjoiL3YyLjAvYXBwL3NodWFpYi1zYW1wbGUtYXAtZGV2LTRhNzgwNDg2L3VzZXIifV0sImlhdCI6MTQ2NDAyNjY5NCwiaXNzIjoiTG9vcCBBdXRoIHYyIiwic3ViIjoic2h1YWliLXNhbXBsZS1hcC1kZXYtNGE3ODA0ODYifQ.9zTjOKSf2hQLljmbJOnDZEJeSLabtWkq-V9r7H7TxHI";

        //replace your user id and device id
        String userId = "5a698df3-bb3b-4f78-a091-9fcdcc97eb00";
        String deviceId = "182313a7-e4cb-4802-b512-2ff69d92c95e";

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
