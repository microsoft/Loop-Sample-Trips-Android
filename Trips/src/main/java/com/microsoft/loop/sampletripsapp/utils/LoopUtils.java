package com.microsoft.loop.sampletripsapp.utils;


import com.microsoft.loop.sampletripsapp.SampleAppApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.Drive;
import ms.loop.loopsdk.profile.Drives;
import ms.loop.loopsdk.profile.IProfileDownloadCallback;
import ms.loop.loopsdk.profile.IProfileItemChangedCallback;
import ms.loop.loopsdk.profile.KnownLocation;
import ms.loop.loopsdk.profile.Locations;
import ms.loop.loopsdk.profile.Trip;
import ms.loop.loopsdk.profile.Trips;
import ms.loop.loopsdk.providers.LoopLocationProvider;
import ms.loop.loopsdk.signal.SignalConfig;
import ms.loop.loopsdk.util.LoopError;

public class LoopUtils {

    private static Locations knownLocations;
    private static Trips localTrips;

    public static void initialize() {
        if (!LoopSDK.isInitialized()) return;
        knownLocations = Locations.createAndLoad(Locations.class, KnownLocation.class);
        localTrips = Trips.createAndLoad(Trips.class, Trip.class);

        localTrips.registerItemChangedCallback("Trips", new IProfileItemChangedCallback() {
            @Override
            public void onItemChanged(String entityId) {}

            @Override
            public void onItemAdded(String entityId) {
                SampleAppApplication.mixpanel.track("Trip created");
            }

            @Override
            public void onItemRemoved(String entityId) {}
        });

    }

    public static void registerItemChangedCallback(final IProfileItemChangedCallback callback){

        localTrips.registerItemChangedCallback("Trips", new IProfileItemChangedCallback() {
            @Override
            public void onItemChanged(String entityId) {
                callback.onItemChanged(entityId);
            }

            @Override
            public void onItemAdded(String entityId) {
                SampleAppApplication.mixpanel.track("Trip created");
            }

            @Override
            public void onItemRemoved(String entityId) {}
        });
    }



    public static List<Trip> getTrips() {
        if (LoopSDK.isInitialized()) {
            return localTrips.sortedByStartedAt();
        } else {
            return new ArrayList<>();
        }
    }

    public static void downloadTrips(final IProfileDownloadCallback callback) {

        if (!LoopSDK.isInitialized()) {
            callback.onProfileDownloadFailed(new LoopError("Loop not initialized"));
            return;
        }
        localTrips.download(true, new IProfileDownloadCallback() {
            @Override
            public void onProfileDownloadComplete(int itemCount) {

                if (itemCount == 0) {
                    loadSampleTrips();
                }
                callback.onProfileDownloadComplete(itemCount);
            }

            @Override
            public void onProfileDownloadFailed(LoopError error) {
                if (localTrips.size() == 0) {
                    loadSampleTrips();
                }
            }
        });
    }


    public static void loadItems() {
        if (LoopSDK.isInitialized()) {
            localTrips.load();
        }
    }

    public static void deleteItems() {
        if (LoopSDK.isInitialized()) {
            //localTrips.deleteAll();
        }
    }

    public static void loadSampleTrips() {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset("sample_trips.json"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                localTrips.createAndAddItem(jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static KnownLocation getLocation(String id) {
        if (LoopSDK.isInitialized()) {
            KnownLocation knownLocation = knownLocations.byEntityId(id);
            return knownLocation;
        }
        return null;
    }

    public static Trip getTrip(String id) {
        if (LoopSDK.isInitialized()) {
            Trip trip = localTrips.byEntityId(id);
            return trip;
        }
        return null;
    }

    public static void startLocationProvider() {
        if (LoopSDK.isInitialized()) {
            LoopLocationProvider.start(SignalConfig.SIGNAL_SEND_MODE_BATCH);
        }
    }

    public static void stopLocationProvider() {
        if (LoopSDK.isInitialized()) {
            LoopLocationProvider.stop();
        }
    }

    public static String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = SampleAppApplication.instance.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
