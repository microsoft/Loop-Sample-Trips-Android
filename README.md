# Trip Tracker

Trip Tracker, a Microsoft Garage project, that automatically records your drives, runs, walks, and bike rides. Trip Tracker is built with Microsoft's Location and Observation Platform. You can fork this project and build your own location tracking app.

## Prerequisites:
  * Android 4.4+

## Build instructions:

  0. Signup for a Loop account and create an app on the [Loop Developer Site](https://www.loop.ms)
  0. Get the sample app
    0. Clone this sample app `https://github.com/Microsoft/Loop-Sample-Trips-Android.git`
    0. Open it in Android Studio
    0. Update appId and app token in [gradle](gradle.properties)
       
       -TRIPTRACKER_APP_ID_PROP=
       
       -TRIPTRACKER_APP_TOKEN_PROP=
       
       In the code 'SampleAppApplication.java initializeLoopSDK()' appId and token will be referenced as 
       
       ```
        String appId = BuildConfig.APP_ID;
        String appToken = BuildConfig.APP_TOKEN;
       ```
    
    0. Get a google maps API key and update it TRIPTRACKER_APP_MAPS_KEY_PROP in [gradle](gradle.properties)
    0. (Optional) Update loggly, ACRA and mixpanel related keys in [gradle](gradle.properties)

  0. Create test users in the user dashboard at the [LOOP Developer Site](https://www.loop.ms)
  0. Fill in the userId and deviceId in `SampleAppApplication.java initializeLoopSDK()` with a test user's userId and deviceId obtained from the [Loop Developer Site](https://www.loop.ms)

    ```
        String userId = "TEST_USER_USER_ID";
        String deviceId = "TEST_USER_DEVICE_ID"
        LoopSDK.initialize(this, appId, appToken, userId, deviceId);
    ```
  0. (optional) If you don't require a specific userId and deviceId, skip the previous step. Instead, initialize the SDK with the following:

    ```
        LoopSDK.initialize(this, appId, appToken);
    ```
  0. Build and run the app

After the app runs for a while you will see your user's trips and drives. This should only take a few hours but no longer than 24 hours as you move between locations.

# Privacy
Information regarding privacy can be found in the [privacy.md](privacy.md).
# License
This source code is provided under the [MIT License](LICENSE.txt).

---
The Microsoft Garage is an outlet for experimental projects for you to try. Learn more at http://garage.microsoft.com. 
