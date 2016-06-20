# Loop Sample Trips/Drives

These instructions will get you a copy of a Loop sample app that will download user trips generated in the Loop platform and display them.

  0. If you haven’t already, signup for a Loop account and create an app on the [Loop Developer Site](https://www.loop.ms)
  0. Get the sample app
    0. Clone this sample app `https://github.com/Microsoft/Loop-Sample-Trips-Android.git`
    0. Open it in Android Studio
    0. Add your appId and appToken in `SampleApplication.java OnCreate`

    ```
        String appId = "YOUR_APP_ID";
        String appToken = "YOUR_APP_TOKEN";
    ```
  0. Create test users in your user dashboard (user link in the left navigation)
  0. Fill in the userId and deviceId in `LoopTestUserApplication.java OnCreate` with a test user's userId and deviceId obtained from the [Loop Developer Site](https://www.loop.ms)

    ```
        String userId = "TEST_USER_USER_ID";
        String deviceId = "TEST_USER_DEVICE_ID"
        
        LoopSDK.initialize(this, appId, appToken, userId, deviceId);
    ```
  0. If you don't require a specific userId and deviceId you can skip the previous step and instead initialize the Loop SDK with the following line:

    ```
        LoopSDK.initialize(this, appId, appToken);
    ```
  0. Build and run the app

After the app runs for a while you will see your user's trips and drives. This should only take a few hours but no longer than 24 hours as you move between locations.
