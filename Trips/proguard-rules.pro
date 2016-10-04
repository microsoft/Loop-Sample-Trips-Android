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
-dontwarn  org.acra.**
#ACRA specifics
# Restore some Source file names and restore approximate line numbers in the stack traces,
# otherwise the stack traces are pretty useless
-keepattributes SourceFile,LineNumberTable

# ACRA needs "annotations" so add this...
# Note: This may already be defined in the default "proguard-android-optimize.txt"
# file in the SDK. If it is, then you don't need to duplicate it. See your
# "project.properties" file to get the path to the default "proguard-android-optimize.txt".
-keepattributes *Annotation*

# keep this class so that logging will show 'ACRA' and not a obfuscated name like 'a'.
# Note: if you are removing log messages elsewhere in this file then this isn't necessary
-keep class org.acra.ACRA {*;}

# keep this around for some enums that ACRA needs
-keep class org.acra.ReportingInteractionMode {*;}
-keepnames class org.acra.sender.HttpSender$** {*;}
-keepnames class org.acra.ReportField {*;}

# keep this otherwise it is removed by ProGuard
-keep public class org.acra.ErrorReporter{
    public void addCustomData(java.lang.String,java.lang.String);
    public void putCustomData(java.lang.String,java.lang.String);
    public void removeCustomData(java.lang.String);
}

# keep this otherwise it is removed by ProGuard
-keep public class org.acra.ErrorReporter{
    public void handleSilentException(java.lang.Throwable);
}
##---------------End: proguard configuration for ACRA  ----------

