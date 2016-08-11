package trips.sampleapp.loop.ms.tripssampleapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import ms.loop.loopsdk.core.ILoopSDKCallback;
import ms.loop.loopsdk.core.ISignalListener;
import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.processors.DriveProcessor;
import ms.loop.loopsdk.processors.KnownLocationProcessor;
import ms.loop.loopsdk.processors.TripProcessor;
import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.providers.LoopLocationProvider;
import ms.loop.loopsdk.signal.Signal;
import ms.loop.loopsdk.signal.SignalConfig;
import ms.loop.loopsdk.util.LoopError;

public class SampleAppApplication extends MultiDexApplication implements ILoopSDKCallback {

    private static final String TAG = SampleAppApplication.class.getSimpleName();
    private static KnownLocationProcessor knownLocationProcessor ;
    private static Context applicationContext;
    private static boolean sdkInitialized = false;
    private static String DAYS_IN_APP_KEY = "days_in_app_key";
    private static String MIX_PANEL_DATE_FORMAT = "yyyy-MM-dd'T'00:00:00";  // mixpanel dateformat
    private static String MIX_PANEL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";  // mixpanel dateformat

    public static TripProcessor tripProcessor;
    public static DriveProcessor driveProcessor;
    public static MixpanelAPI mixpanel;

    @Override
    public void onCreate() {
        super.onCreate();
        
        // initialize the Loop SDK. create an account to get your appId and appToken

        String appId = BuildConfig.APP_ID; // Or replace your id here
        String appToken = BuildConfig.APP_TOKEN; // or replace your app token here

        String userId = "YOUR USER ID";
        String deviceId = "YOUR DEVICE ID";

        LoopSDK.initialize(this, appId, appToken);
        applicationContext = this;

        String projectToken = BuildConfig.MIXPANEL_TOKEN;
        mixpanel = MixpanelAPI.getInstance(this, projectToken);

        if (getLongSharedPrefValue(this, DAYS_IN_APP_KEY) == 0) {
            setSharedPrefValue(this, DAYS_IN_APP_KEY, System.currentTimeMillis());
            MixpanelAPI.People people = mixpanel.getPeople();
            people.identify(mixpanel.getDistinctId());
            String dateFirstSeen = convertDateFormat(new Date(), false);
            people.setOnce("FirstSeen", dateFirstSeen);
            mixpanel.track("New User");
        }
        mixpanel.track("App Launched");
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
            knownLocationProcessor.initialize();

            sdkInitialized = true;

            // register signal listeners
            LoopSDK.registerSignalListener("drives", "*", new ISignalListener() {
                @Override
                public void onSignal(Signal signal) {}
            });

            LoopLocationProvider.registerCallback("location", new LoopLocationProvider.ILocationProviderCallback() {
                @Override
                public void onLocationChanged(Location location) {
                }

                @Override
                public void onModeChanged(int modeFrom, int modeTo, Location location) {}
                @Override
                public void onKnownLocationEntered(KnownLocation location) {}
                @Override
                public void onKnownLocationExited(KnownLocation location) {}
            });

            LoopSDK.enableLogging("loggly", BuildConfig.LOGGLY_TOKEN);
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
    public void onDebug(String debugString) {}

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

    public static void setSharedPrefValue(Context context, String key, long value)
    {
        context.getSharedPreferences("TripsApp",0).edit().putLong(key, value).apply();
        context.getSharedPreferences("TripsApp",0).edit().commit();
    }

    public static long getLongSharedPrefValue(Context context, String key)
    {
        return context.getSharedPreferences("TripsApp",0).getLong(key, 0);
    }

    public static void setSharedPrefValue(Context context, String key, boolean value)
    {
        context.getSharedPreferences("TripsApp",0).edit().putBoolean(key, value).apply();
        context.getSharedPreferences("TripsApp",0).edit().commit();
    }

    public static boolean getBooleanSharedPrefValue(Context context, String key, boolean defValue)
    {
        return context.getSharedPreferences("TripsApp",0).getBoolean(key, defValue);
    }

    public static String convertDateFormat(Date localdate, boolean useTime) {
        DateFormat df = new SimpleDateFormat(useTime ? MIX_PANEL_DATE_TIME_FORMAT : MIX_PANEL_DATE_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = df.format(localdate);
        return gmtTime;
    }

}
