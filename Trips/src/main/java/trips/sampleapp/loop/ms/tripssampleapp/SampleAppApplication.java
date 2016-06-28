package trips.sampleapp.loop.ms.tripssampleapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.widget.TextView;

import ms.loop.loopsdk.core.ILoopSDKCallback;
import ms.loop.loopsdk.core.ISignalListener;
import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.processors.DriveProcessor;
import ms.loop.loopsdk.processors.KnownLocationProcessor;
import ms.loop.loopsdk.processors.TripProcessor;
import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.providers.LoopLocation;
import ms.loop.loopsdk.providers.LoopLocationProvider;
import ms.loop.loopsdk.signal.Signal;
import ms.loop.loopsdk.signal.SignalConfig;
import ms.loop.loopsdk.util.Logger;
import ms.loop.loopsdk.util.LoopError;

/**
 * Created by on 5/24/16.
 */
public class SampleAppApplication extends MultiDexApplication implements ILoopSDKCallback {

    private static final String TAG = SampleAppApplication.class.getSimpleName();
    private static KnownLocationProcessor knownLocationProcessor ;
    private static Context applicationContext;
    private static boolean sdkInitialized = false;

    public static TripProcessor tripProcessor;
    public static DriveProcessor driveProcessor;


    @Override
    public void onCreate() {
        super.onCreate();

        //replace appId and device id below

        String appId = "sample-trips-app-dev-23dfcb96";
        String appToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb3V0ZXMiOlt7Im1ldGhvZCI6InBvc3QiLCJwYXRoIjoiL3YyLjAvYXBwL3NhbXBsZS10cmlwcy1hcHAtZGV2LTIzZGZjYjk2L3VzZXIifSx7Im1ldGhvZCI6ImRlbGV0ZSIsInBhdGgiOiIvdjIuMC9hcHAvc2FtcGxlLXRyaXBzLWFwcC1kZXYtMjNkZmNiOTYvdXNlciJ9LHsibWV0aG9kIjoicG9zdCIsInBhdGgiOiIvdjIuMC9hcHAvc2FtcGxlLXRyaXBzLWFwcC1kZXYtMjNkZmNiOTYvbG9naW4ifSx7Im1ldGhvZCI6ImdldCIsInBhdGgiOiIvdjIuMC9hcHAvc2FtcGxlLXRyaXBzLWFwcC1kZXYtMjNkZmNiOTYvdXNlciJ9LHsibWV0aG9kIjoiZ2V0IiwicGF0aCI6Ii92Mi4wL2FwcC9zYW1wbGUtdHJpcHMtYXBwLWRldi0yM2RmY2I5Ni91c2VyL1teLy5dKyJ9XSwiaXNzIjoiTWljcm9zb2Z0IExPT1AgQXV0aCBWMiIsInN1YiI6InNhbXBsZS10cmlwcy1hcHAtZGV2LTIzZGZjYjk2In0.EJGdkh0Eg15kAmLFXsGh5lujbqHU8j1SzDe_5FqosTI";

        SignalConfig.add(TAG, "/system", "/test", "*", SignalConfig.SIGNAL_SEND_MODE_REALTIME);

        String userId = "";
        String deviceId = "";

        LoopSDK.initialize(this, appId, appToken);
        applicationContext = this;
    }
    @Override
    public void onInitialized() {

        // start any required Providers
        LoopLocationProvider.start(SignalConfig.SIGNAL_SEND_MODE_BATCH);

        if (!sdkInitialized) {

            tripProcessor = new TripProcessor();
            driveProcessor = new DriveProcessor();
            knownLocationProcessor = new KnownLocationProcessor();

            // initialize signal processors
            tripProcessor.initialize();
            driveProcessor.initialize();

            sdkInitialized = true;

            // register signal listeners
            LoopSDK.registerSignalListener("drives", "*", new ISignalListener() {
                @Override
                public void onSignal(Signal signal) {
                }
            });

            LoopLocationProvider.registerMainThreadCallback("location", new LoopLocationProvider.ILocationProviderCallback() {
                @Override
                public void onLocationChanged(LoopLocation location) {
                }

                @Override
                public void onModeChanged(int modeFrom, int modeTo, LoopLocation location) {}
                @Override
                public void onKnownLocationEntered(KnownLocation location) {}
                @Override
                public void onKnownLocationExited(KnownLocation location) {}
            });
        }

       // knownLocationProcessor.initialize();
        Intent i = new Intent("android.intent.action.onInitialized").putExtra("status", "initialized");
        this.sendBroadcast(i);
    }

    @Override
    public void onInitializeFailed(LoopError loopError) {}

    @Override
    public void onServiceStatusChanged(String provider, String status, Bundle bundle) {}

    @Override
    public void onDebug(String debugString) {
    }

    public static boolean isLocationTurnedOn(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean locationEnbaled = false;

        try {
            locationEnbaled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (locationEnbaled) {
                locationEnbaled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception ex) {
        }
        return locationEnbaled;
    }

    public static void openLocationServiceSettingPage(Context context) {
        final Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        locationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (locationIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(locationIntent);
        }
    }
}
