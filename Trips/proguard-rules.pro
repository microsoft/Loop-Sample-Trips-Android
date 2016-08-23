-keep class ms.loop.** {*;}

## the following is for loop sdk, we don't pro-guard the 3rd party library which loop sdk used
-keep class com.squareup.** {*;} ## com.squareup.retrofit:retrofit:1.9.0 and com.squareup.okhttp:okhttp:2.4.0
-keep class retrofit.** {*;} ## de.greenrobot:eventbus:2.4.0 use it
-keep class com.google.** {*;} ## com.google.appengine.**
-keep class de.** {*;} ## de.greenrobot:eventbus:2.4.0
-keep class rx.** {*;}
-keep class org.codehaus.** {*;}
-keep class java.nio.** {*;}
-keep class org.joda.** {*;}
-keep class org.apache.commons.** {*;}
-keep class com.j256.ormlite.** {*;}

-keepattributes Exceptions ## will make sure that your Throwable remains in your code after obfuscation.
## Custom Error Handling with Retrofit when obfuscated using proguard gives java.lang.reflect.UndeclaredThrowableException

## for loop sdk
-dontwarn ms.loop.**
-dontwarn ms.loop.library.**
-dontwarn com.squareup.**
-dontwarn com.google.**
-dontwarn de.greenrobot.**
-dontwarn rx.**
-dontwarn okio.**
-dontwarn org.apache.commons.**
-dontwarn org.joda.**
-dontwarn org.junit.**
-dontwarn android.test.**
-dontwarn com.mixpanel.**
