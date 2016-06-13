package trips.sampleapp.loop.ms.tripssampleapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.util.Patterns;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

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

    private String projectToken = "d416de690e6d8a004ef95c5f6e9e17b5"; // e.g.: "1ef7e30d2a58d27f4b90c42e31d6d7ad"

    public static MixpanelAPI mixpanelAPI;
    public static TripProcessor tripProcessor;
    public static DriveProcessor driveProcessor;


    @Override
    public void onCreate() {
        super.onCreate();

        String appId = "shuaib-sample-ap-dev-4a780486";
        String appToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6InNodWFpYi1zYW1wbGUtYXAtZGV2LTRhNzgwNDg2IiwiYXBwS2V5IjoiZGQxMzIxZjQ3MDAzLTRmMjYtODVmOC04OWNhNTM2ODhhYjQiLCJhbGxvd2VkUm91dGVzIjpbeyJtZXRob2QiOiJwb3N0IiwicGF0aCI6Ii92Mi4wL2FwcC9zaHVhaWItc2FtcGxlLWFwLWRldi00YTc4MDQ4Ni91c2VyIn0seyJtZXRob2QiOiJkZWxldGUiLCJwYXRoIjoiL3YyLjAvYXBwL3NodWFpYi1zYW1wbGUtYXAtZGV2LTRhNzgwNDg2L3VzZXIifSx7Im1ldGhvZCI6InBvc3QiLCJwYXRoIjoiL3YyLjAvYXBwL3NodWFpYi1zYW1wbGUtYXAtZGV2LTRhNzgwNDg2L2xvZ2luIn0seyJtZXRob2QiOiJnZXQiLCJwYXRoIjoiL3YyLjAvYXBwL3NodWFpYi1zYW1wbGUtYXAtZGV2LTRhNzgwNDg2L3VzZXIifV0sImlhdCI6MTQ2NDAyNjY5NCwiaXNzIjoiTG9vcCBBdXRoIHYyIiwic3ViIjoic2h1YWliLXNhbXBsZS1hcC1kZXYtNGE3ODA0ODYifQ.9zTjOKSf2hQLljmbJOnDZEJeSLabtWkq-V9r7H7TxHI";

        SignalConfig.add(TAG, "/system", "/test", "*", SignalConfig.SIGNAL_SEND_MODE_REALTIME);

        LoopSDK.initialize(this, appId, appToken);
        applicationContext = this;
        mixpanelAPI = MixpanelAPI.getInstance(applicationContext, projectToken);
        setMixpanelUser();
        mixpanelAPI.track("App Started");
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
                    trackSignal(signal);
                }
            });

            LoopLocationProvider.registerMainThreadCallback("location", new LoopLocationProvider.ILocationProviderCallback() {
                @Override
                public void onLocationChanged(LoopLocation location) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("debug", location.getDebugString());
                    mixpanelAPI.track("onLocationChanged");
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

        if (debugString.contains("receivedDrivingMotion cleared")) {
            mixpanelAPI.track("Drive points cleared");
        }

    }



    public static void setPeopleProperty(String key, Object obj) {
        mixpanelAPI.getPeople().set(key, obj);
        mixpanelAPI.flush();
    }

    public static void trackSignal(Signal signal) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("createdAt", signal.createdAt);
        map.put("entityId", signal.entityId);
        map.put("method", signal.method);
        map.put("namespace", signal.namespace);
        map.put("data", signal.data.toString());

        Iterator it = signal.data.keys();
        while (it.hasNext()) {
            String key = (String)it.next();
            try {
                map.put(key, signal.data.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        map.put("id", LoopSDK.userId);
        mixpanelAPI.identify(getEmail());

        mixpanelAPI.trackMap(signal.name, map);
        mixpanelAPI.flush();
    }

    public void setMixpanelUser()
    {
        MixpanelAPI.People people = mixpanelAPI.getPeople();

        if (people != null) {
            people.identify(getEmail());
            people.set("$email", getEmail());
            people.set("appVersion", BuildConfig.VERSION_NAME);
            mixpanelAPI.flush();
        }
    }

    public static String getEmail() {
        String possibleEmail = "";
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(applicationContext).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmail = account.name;
                if (possibleEmail.contains("gmail.com")) {
                    return possibleEmail;
                }
            }
        }
        return possibleEmail;
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

    public static void openLocationServiceSettingPage(Context context)
    {
        final Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        locationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (locationIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(locationIntent);
        }
    }
}
