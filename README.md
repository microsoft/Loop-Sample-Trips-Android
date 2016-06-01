# Loop Android Sample - Setting up test users

These instructions will get you a copy of a Loop sample app for pulling user trips and display them in the UI

  0. If you haven’t already, signup for a loop account and create an app on the Loop developer portal. It takes seconds - [Loop Developer Portal](https://developer.dev.loop.ms)
  0. Create a new project or use existing one
  0. Get the sample app
    0. Clone this sample app `git clone https://github.com/Microsoft/Loop-Sample-Trips-Android.git`
    0. Open it in Android Studio
    0. Add your appId and appToken in `SampleApplication.java OnCreate`

    ```
        String appId = "YOUR_APP_ID";
        String appToken = "YOUR_APP_TOKEN";
    ```
  0. Create test users in your user dashboard (user link in the left navigation)
  0. Replace the userId and deviceId with your test user id and device id in `SampleApplication.java OnCreate`

    ```
        String userId = "TEST_USER_USER_ID";
        String deviceId = "TEST_USER_DEVICE_ID"
        
        LoopSDK.initialize(this, appId, appToken, userId, deviceId);
    ```
  0. Build and run the app

You will see test user's drives data in the app
