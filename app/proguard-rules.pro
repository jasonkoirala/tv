# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/samir/dev/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn javax.annotation.**

# proguard configuration for greenrobot
-keepclassmembers class ** {
    public void onEvent*(**);
}

# Only required if you use AsyncExecutor
#-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
#    <init>(java.lang.Throwable);
#}

# proguard configuration for crashlytics
-keepattributes SourceFile,LineNumberTable,*Annotation*
-keep class com.crashlytics.android.**

# no reason to obfuscate open source libraries due to which processing can be further reduced ultimately improving build time

-keep class com.crashlytics.** { *; }

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }


-keep class com.koushikdutta.ion.** { *; }

-keep class com.koushikdutta.async.** { *; }
-dontwarn com.koushikdutta.**
-keep class com.google.gson.** { *; }
-keep class com.readystatesoftware.systembartint..** { *; }
-keep class ..** { *; }

#The "Signature" attribute is required to be able to access generic types when compiling in JDK 5.0 and higher.
-keepattributes Signature
## GreenRobot EventBus specific rules ##
# https://github.com/greenrobot/EventBus/blob/master/HOWTO.md#proguard-configuration

-keepclassmembers class ** {
    public void onEvent*(***);
}

# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    public <init>(java.lang.Throwable);
}

# Don't warn for missing support classes
-dontwarn de.greenrobot.event.util.*$Support
-dontwarn de.greenrobot.event.util.*$SupportManagerFragment

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class android.support.v7.widget.** {*;}
-keep class android.support.v4.** { *; }
-dontwarn com.viewpagerindicator.**
