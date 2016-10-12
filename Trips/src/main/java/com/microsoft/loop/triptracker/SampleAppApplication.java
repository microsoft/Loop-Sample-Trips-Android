package com.microsoft.loop.triptracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.microsoft.loop.triptracker.utils.LoopUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import ms.loop.loopsdk.core.ILoopSDKCallback;
import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.processors.DriveProcessor;
import ms.loop.loopsdk.processors.KnownLocationProcessor;
import ms.loop.loopsdk.processors.TripProcessor;
import ms.loop.loopsdk.providers.LoopLocationProvider;
import ms.loop.loopsdk.signal.SignalConfig;
import ms.loop.loopsdk.util.LoopError;

@ReportsCrashes(
        formUri = BuildConfig.ACRA_URL,
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = BuildConfig.ACRA_LOGIN,
        formUriBasicAuthPassword = BuildConfig.ACRA_PWD,
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE,
                ReportField.USER_APP_START_DATE,
                ReportField.USER_CRASH_DATE,
                ReportField.CUSTOM_DATA,
                ReportField.INSTALLATION_ID,
                ReportField.DEVICE_ID,
                ReportField.STACK_TRACE_HASH,
        },
        mode = ReportingInteractionMode.SILENT
)
public class SampleAppApplication extends MultiDexApplication implements ILoopSDKCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SampleAppApplication.class.getSimpleName();
    private static KnownLocationProcessor knownLocationProcessor;

    private static boolean sdkInitialized = false;
    private static String DAYS_IN_APP_KEY = "days_in_app_key";
    private static String MIX_PANEL_DATE_FORMAT = "yyyy-MM-dd'T'00:00:00";  // mixpanel dateformat
    private static String MIX_PANEL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";  // mixpanel dateformat

    private static final String sharedPrefName = "TripsApp";

    public static SampleAppApplication instance;
    public static TripProcessor tripProcessor;
    public static MixpanelAPI mixpanel;
    private static GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ACRA.init(this);

        initializeLoopSDK();

        String projectToken = BuildConfig.MIXPANEL_TOKEN;
        mixpanel = MixpanelAPI.getInstance(this, projectToken);

        if (getLongSharedPrefValue(this, DAYS_IN_APP_KEY) == 0) {
            setSharedPrefValue(this, DAYS_IN_APP_KEY, System.currentTimeMillis());
            mixpanel.track("New User");
        }
        mixpanel.track("App Launched");
    }

    public void initializeLoopSDK() {

        // initialize the Loop SDK. create an account to get your appId and appToken
        String appId = BuildConfig.APP_ID;
        String appToken = BuildConfig.APP_TOKEN;

        LoopSDK.initialize(this, appId, appToken);

        String userId = "TEST_USER_USER_ID";
        String deviceId = "TEST_USER_DEVICE_ID";

        //LoopSDK.initialize(this, appId, appToken, userId, deviceId);
    }

    public void unInitializeLoopSDK() {
        sdkInitialized = false;
        LoopSDK.unInitialize();
    }

    @Override
    public void onInitialized() {

        if (sdkInitialized) return;

        // start any required Providers
        if (isTrackingEnabled()) {
            enableLocationTracking();
        }

        sdkInitialized = true;

        LoopUtils.initialize();
        LoopSDK.enableLogging("loggly", BuildConfig.LOGGLY_TOKEN);

        // send intent to activity to update
        Intent intent = new Intent("android.intent.action.onInitialized").putExtra("status", "initialized");
        this.sendBroadcast(intent);
    }

    @Override
    public void onInitializeFailed(LoopError loopError) {
    }

    @Override
    public void onServiceStatusChanged(String provider, String status, Bundle bundle) {
    }

    @Override
    public void onDebug(String debugString) {
    }

    public void enableLocationTracking(){
        if (!LoopSDK.isInitialized()) return;
        LoopLocationProvider.start(SignalConfig.SIGNAL_SEND_MODE_BATCH);
        tripProcessor = new TripProcessor();
        knownLocationProcessor = new KnownLocationProcessor();

        // initialize signal processors
        tripProcessor.initialize();
        knownLocationProcessor.initialize();
    }
    public void disableLocationTracking(){
        if (!LoopSDK.isInitialized()) return;
        LoopLocationProvider.stop();
    }

    public static boolean isLocationTurnedOn(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean locationEnabled = false;

        try {
            locationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (locationEnabled) {
                locationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception ex) {
        }
        return locationEnabled;
    }

    public void openLocationServiceSettingPage(final Activity activity) {

        if (isLocationTurnedOn(activity)) return;
        try {
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(activity)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this).build();
                googleApiClient.connect();
            }

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult((Activity) activity, 0x1);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });

        } catch (Exception e) {
        }
    }

    public static void setSharedPrefValue(Context context, String key, long value) {
        context.getSharedPreferences(sharedPrefName, 0).edit().putLong(key, value).apply();
        context.getSharedPreferences(sharedPrefName, 0).edit().apply();
    }

    public static long getLongSharedPrefValue(Context context, String key) {
        return context.getSharedPreferences(sharedPrefName, 0).getLong(key, 0);
    }

    public static void setSharedPrefValue(Context context, String key, boolean value) {
        context.getSharedPreferences(sharedPrefName, 0).edit().putBoolean(key, value).apply();
        context.getSharedPreferences(sharedPrefName, 0).edit().apply();
    }

    public boolean getBooleanSharedPrefValue(String key, boolean defValue) {
        return getSharedPreferences(sharedPrefName, 0).getBoolean(key, defValue);
    }

    public static boolean getBooleanSharedPrefValue(Context context, String key, boolean defValue) {
        return context.getSharedPreferences(sharedPrefName, 0).getBoolean(key, defValue);
    }
    public  void setSharedPrefValue(String key, boolean value) {
        getSharedPreferences(sharedPrefName, 0).edit().putBoolean(key, value).apply();
    }


    public static String convertDateFormat(Date localdate, boolean useTime) {
        DateFormat df = new SimpleDateFormat(useTime ? MIX_PANEL_DATE_TIME_FORMAT : MIX_PANEL_DATE_FORMAT, Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = df.format(localdate);
        return gmtTime;
    }

    private boolean isTrackingEnabled() {
        return getBooleanSharedPrefValue(this, "AppTracking", true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }

    public void sendDebugEvent(String eventName) {
        HashMap<String, Object> logParams = new HashMap<>();
        logParams.put("Type", eventName);
        mixpanel.trackMap("Debug", logParams);
    }

}
