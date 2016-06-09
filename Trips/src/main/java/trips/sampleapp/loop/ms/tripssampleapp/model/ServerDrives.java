package trips.sampleapp.loop.ms.tripssampleapp.model;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ms.loop.loopsdk.api.ILoopApi;
import ms.loop.loopsdk.api.LoopApiHelper;
import ms.loop.loopsdk.api.LoopHttpError;
import ms.loop.loopsdk.api.RetrofitException;
import ms.loop.loopsdk.core.ILoopServiceCallback;
import ms.loop.loopsdk.core.LoopSDK;
import ms.loop.loopsdk.profile.Drive;
import ms.loop.loopsdk.profile.IProfileDownloadCallback;
import ms.loop.loopsdk.util.JSONHelper;
import ms.loop.loopsdk.util.Logger;
import ms.loop.loopsdk.util.LoopError;
import retrofit.client.Response;

public class ServerDrives {

    private static final String TAG = ServerDrives.class.getSimpleName();
    private static final String CLINET_TRIPS_FILTER = "method eq device.processor";
    private ArrayList<String> drives = new ArrayList<>();
    public static ServerDrives Instance = new ServerDrives();

    private ServerDrives() {}

    public boolean hasDrive(String entityId) {
        return drives.contains(entityId);
    }

    public void download(final boolean overwrite, final IProfileDownloadCallback callback) {

        downloadProfileItem("drives", overwrite, new ILoopServiceCallback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray tripArray) {
                try {
                    if (tripArray != null) {
                        if (overwrite) drives.clear();
                        for (int i = 0; i < tripArray.length(); i++) {
                            final JSONObject trip = tripArray.getJSONObject(i);
                            String entityId = JSONHelper.safeGetString(trip, "entityId");
                            drives.add(entityId);
                        }
                        callback.onProfileDownloadComplete(tripArray.length());
                    }
                } catch (JSONException ex) {
                    Logger.log(TAG, Logger.ERROR, ex.toString());
                }
            }

            @Override
            public void onError(LoopError error) {
                callback.onProfileDownloadFailed(error);
            }
        });
    }

    public boolean tripExists(String entityId)
    {
        return drives.contains(entityId);
    }

    public static void downloadProfileItem(final String profilePath, final boolean overwrite, final ILoopServiceCallback<JSONArray> callback) {
        new Thread(new Runnable() {
            public void run() {
                if (!TextUtils.isEmpty(profilePath)) {
                    try {
                        final ILoopApi loopApiClient = LoopApiHelper.getApiClient(LoopSDK.userToken);
                        // reset context id to a new context ID can flow with this transaction
                        LoopSDK.resetContextId();

                        Map<String, String> options = new HashMap<>();
                        options.put("$orderby", "updatedAt asc");
                        options.put("$filter", CLINET_TRIPS_FILTER);

                        final Response response = loopApiClient.getUserProfileItem(LoopSDK.appId, LoopSDK.userId, profilePath, options);

                        if (response.getStatus() == 204) {
                            // nothing returned
                        }

                        if (response.getStatus() == 200) {
                            final JSONArray results = LoopApiHelper.getJsonArrayFromResponse(response);

                            if (results.length() > 0) {
                                try {
                                    final JSONObject lastResult = results.getJSONObject(results.length() - 1);
                                } catch (JSONException ex) {
                                    Logger.log(TAG, Logger.ERROR, ex.toString());
                                }
                            }
                            callback.onSuccess(results);
                        }

                    } catch (IllegalStateException | InvalidParameterException ex) {
                        Logger.log(TAG, Logger.ERROR, ex.toString());
                        callback.onError(new LoopError(ex.toString()));
                    } catch (RetrofitException ex) {
                        if (ex.error.getKind().name().equals("NETWORK")) {
                            Logger.logRetrofitFailure(TAG, ex.error, "get profile items: " + profilePath);
                            callback.onError(new LoopHttpError("get profile items: " + profilePath, ex.error));
                        } else {
                            // todo: error handling

                            callback.onError(new LoopHttpError("get profile items: " + profilePath, ex.error));
                        }
                    }
                } else {
                    callback.onError(new LoopError("profilePath is blank on call to getUserProfileItem()"));
                }
            }
        }).start();
    }

    public ArrayList<String> getItems() {
        return drives;
    }
}
