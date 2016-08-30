# LOOP Sample Trips/Drives

## Prerequisites:
  * Android 4.4+

## Build instructions:
These instructions will get you a copy of the Location and Observation Platform (LOOP) sample app that will download and display trips.

  0. Signup for a Loop account and create an app on the [Loop Developer Site](https://www.loop.ms)
  0. Get the sample app
    0. Clone this sample app `https://github.com/Microsoft/Loop-Sample-Trips-Android.git`
    0. Open it in Android Studio
    0. Add your appId and appToken in `SampleApplication.java` in the `initializeLoopSDK()` method

    ```
        String appId = "YOUR_APP_ID";
        String appToken = "YOUR_APP_TOKEN";
    ```
  0. Create test users in the user dashboard at the [LOOP Developer Site](https://www.loop.ms)
  0. Fill in the userId and deviceId in `LoopTestUserApplication.java initializeLoopSDK()` with a test user's userId and deviceId obtained from the [Loop Developer Site](https://www.loop.ms)

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
