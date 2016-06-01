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

        String appId = "test-project-2-dev-87907e4c";
        String appToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6InRlc3QtcHJvamVjdC0yLWRldi04NzkwN2U0YyIsImFwcEtleSI6IjEyZjFkNGE4MWNkYi00MGMyLWFhMGItN2JkYWRmZmMwMjExIiwiYWxsb3dlZFJvdXRlcyI6W3sibWV0aG9kIjoicG9zdCIsInBhdGgiOiIvdjIuMC9hcHAvdGVzdC1wcm9qZWN0LTItZGV2LTg3OTA3ZTRjL3VzZXIifSx7Im1ldGhvZCI6ImRlbGV0ZSIsInBhdGgiOiIvdjIuMC9hcHAvdGVzdC1wcm9qZWN0LTItZGV2LTg3OTA3ZTRjL3VzZXIifSx7Im1ldGhvZCI6InBvc3QiLCJwYXRoIjoiL3YyLjAvYXBwL3Rlc3QtcHJvamVjdC0yLWRldi04NzkwN2U0Yy9sb2dpbiJ9LHsibWV0aG9kIjoiZ2V0IiwicGF0aCI6Ii92Mi4wL2FwcC90ZXN0LXByb2plY3QtMi1kZXYtODc5MDdlNGMvdXNlciJ9LHsibWV0aG9kIjoiZ2V0IiwicGF0aCI6Ii92Mi4wL2FwcC90ZXN0LXByb2plY3QtMi1kZXYtODc5MDdlNGMvdXNlci9bd2QtXSoifV0sImlhdCI6MTQ2NDgwMzcwNSwiaXNzIjoiTG9vcCBBdXRoIHYyIiwic3ViIjoidGVzdC1wcm9qZWN0LTItZGV2LTg3OTA3ZTRjIn0.rYbgJEBnl2qMZ2X-CXWI_o6KYR2Ff_33R1t56JlcY_s";

        //replace your user id and device id
        String userId = "9e2f26a0-9a83-402c-8939-b6f54a9b7e42";
        String deviceId = "6e2e7aa4-2649-44cb-8b44-2f71a16cee92";

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
