# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# This is a configuration file for ProGuard.
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
-verbose
-dontpreverify


# Enable Optimization. # Optimization is turned off by default.
-optimizations   code/simplification/arithmetic,!code/simplification/cast,!field
-optimizationpasses 5
-allowaccessmodification


#Disable Optimization
#-dontoptimize
#-dontpreverify

# Remove Log command from code
-assumenosideeffects class android.util.Log{
 public static *** d(...);
 public static *** i(...);
 public static *** v(...);
 public static *** e(...);
}

# -------------------------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference

-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.DialogFragment
-keep public class * extends android.app.Fragment
-keep public class * extends java.lang.Exception

# -------------------------------------------------
#-injars libs
#-keepattributes *Annotation*, Signature, Exception, EnclosingMethod, InnerClasses
#-keepattributes JavascriptInterface

# Keep source file name and line number
-keepattributes SourceFile,LineNumberTable

-keep class okhttp3.** {*;}
-keep interface okhttp3.** {*;}
-dontwarn okhttp3.**

-keep class okio.** { *; }
-keep interface okio.** { *; }
-dontwarn okio.**

-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-dontwarn org.apache.**

-keep class com.activeandroid.** { *; }
-keep class com.activeandroid.**.** { *; }
-dontwarn com.activeandroid.**

-keep class * extends com.activeandroid.Model
-keep class * extends com.activeandroid.serializer.TypeSerializer

-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-dontwarn android.support.v7.**

# Dontwarn-----------------------------------------
-dontwarn javax.**
-dontwarn java.lang.management.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn android.support.**
-dontwarn com.google.ads.**
-dontwarn org.slf4j.**
-dontwarn org.json.**

-ignorewarnings

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# -------------------------------------------------
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#Support Library
-keep class android.support.** {*;}
-keep interface android.support.** {*;}

#Needed when building against Marshmallow SDK.
  -dontwarn android.app.Notification

#Retrofit and GSON
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn com.squareup.okhttp3.**

-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

-keep class sun.misc.Unsafe.** { *; }
-dontwarn sun.misc.Unsafe.**

-keep public class com.google.gson.** {*;}
-keep class * implements com.google.gson.** {*;}
-keep class com.google.gson.stream.** { *; }
-dontwarn com.google.gson.**

#-keep class * implements com.google.gson.TypeAdapterFactory
#-keep class * implements com.google.gson.JsonSerializer
#-keep class * implements com.google.gson.JsonDeserializer

-keepclasseswithmembers class * {@retrofit2.http.* <methods>;}
-keepclasseswithmembers interface * { @retrofit2.* <methods>;}
-dontwarn com.google.appengine.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.**
-dontwarn org.codehaus.mojo.**
-dontwarn retrofit2.Platform$Java8
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor

#Volley
-keep class com.android.volley.** { *; }
-dontwarn com.android.volley.**

##Butterknife 8.0
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

# EventBus
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# OkHttp and Picasso
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep interface com.google.code.gson.** { *; }
-dontwarn com.squareup.okhttp.**

#todo for Otto
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

# todo for Slidingmenu
-keep class nl.psdcompany.** { *; }
# todo for Slidingmenu
-keep class com.afollestad.** { *; }
# todo for sharish
-keep class com.github.sharish.** { *; }
# todo for OTP pin view
-keep class com.github.GoodieBag.** { *; }
# todo for Glide library
-keep class com.github.bumptech.** { *; }
# todo for crashlytics
-keep class com.crashlytics.** { *; }

# Keep line number
-keepattributes SourceFile,LineNumberTable

# Google Play Admob
    -keep public class com.google.android.gms.ads.** {
        public *;
    }

    -keep public class com.google.ads.** {
         public *;
    }

# Facebook
#-keep class com.facebook.** {*;}
#-dontwarn com.facebook.**

# Dagger 2
 -dontwarn com.google.errorprone.annotations.**

# Rxjava
 -keep class rx.schedulers.Schedulers {
     public static <methods>;
 }
 -keep class rx.schedulers.ImmediateScheduler {
     public <methods>;
 }
 -keep class rx.schedulers.TestScheduler {
     public <methods>;
 }
 -keep class rx.schedulers.Schedulers {
     public static ** test();
 }
 -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
     long producerIndex;
     long consumerIndex;
 }
 -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
     long producerNode;
     long consumerNode;
 }

 # Realm
 -keepnames public class * extends io.realm.RealmObject
 -keep @io.realm.annotations.RealmModule class *
 -keep class io.realm.** { *; }
 -dontwarn io.realm.**

# todo for Picasso
-keep class com.squareup.picasso.** { *; }
-dontwarn com.squareup.picasso.**

#todo for Firebase
-keep class com.firebase.** { *; }
-dontwarn com.firebase.**

-keep class org.apache.** { *; }
-dontwarn org.apache.**

-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.ietf.jgss.**

-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.**

-keepnames class javax.servlet.** { *; }

-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontnote com.firebase.client.core.GaePlatform


