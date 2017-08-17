# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\310240027\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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
#demouapp
-dontwarn com.philips.cdp2.demouapp.fragment.MismatchedPinAppliancesFragment
-dontwarn com.philips.cdp2.demouapp.fragment.port.DevicePortFragment$2

#Android support library
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.support.v8.renderscript.** { *; }

-verbose

#Volley
-keep class com.android.volley.** { *; }
-keep interface com.android.volley.** { *; }
-keep class org.apache.commons.logging.**
-keep class com.squareup.okhttp.** { *; }
-keep class okio.** { *; }

#Gson
-keep class sun.misc.Unsafe { *; }
#Prx
-keep class com.philips.cdp.prxclient.** {*;}
-keep interface com.philips.cdp.prxclient.** { *; }

#Tagging
-keep class com.adobe.mobile.** {*;}
-keep class com.philips.cdp.tagging.** {*;}

#Hockey
-keepclassmembers class net.hockeyapp.android.UpdateFragment {*;}

#Registration
-keep class com.philips.cdp.registration.** {*;}

-keep public class javax.net.ssl.**
-keepclassmembers public class javax.net.ssl.** {*;}
-keepclassmembers public class org.apache.http.** {*;}
-keep class org.apache.http.** { *; }
-keep class android.net.http.** { *; }

#GMS (Registration)
-keep  class com.google.android.gms.* { public *; }

#Webkit (Registration)
-keep  class android.net.http.SslError
-keep  class android.webkit.WebViewClient


#Janrain (Registration)
-keep public class com.janrain.android.** {*;}
-keep  class com.janrain.android.Jump$* {*;}
-keep class com.philips.cdp.registration.User$*{*;}
-keep  class com.janrain.android.capture.Capture$* {*;}

-keep public class * extends android.view.View {
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
public void set*(...);
*** get*();
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet, int);
}

#Enumeration
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

#Static
-keepclassmembers class **.R$* {
public static <fields>;
}


-dontwarn com.google.gson.**

-dontwarn com.android.volley.**
-dontwarn com.squareup.okhttp.**
-dontwarn java.nio.file.**
-dontwarn okio.**

-dontwarn com.facebook.android.BuildConfig

-dontwarn android.support.**
-dontwarn android.support.v8.**
-dontwarn com.philips.cdp.registration.**
-dontwarn com.philips.cdp.platform.**
-dontwarn org.apache.**
#-dontwarn com.philips.cdp.digitalcare.**
#-dontwarn com.philips.cdp.prxclient.**


-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

#notification (Registration)
-dontwarn android.app.Notification

-keepclasseswithmembernames class * {
    native <methods>;
}
-dontwarn com.google.android.gms.**
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

# -------------WeChat---------------------------------------------------

-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}

-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}

-keep class com.janrainphilips.philipsregistration.wxapi.** {*;}

-keep class com.philips.platform.baseapp.base.wxapi.** {*;}

-keep class com.philips.platform.referenceapp.wxapi.** {*;}

-keep class com.philips.platform.referenceapp.wxapi.WXEntryActivity

-keep class com.tencent.mm.sdk.** {
      *;
  }

#-------------------------Consumer Care Starts -------------------------


-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-keepattributes *Annotation*
-keepattributes Signature


# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** {*;}
-keep class com.philips.cdp.prxclient.** {*;}
-keep class com.philips.cdp.prxclient.prxdatamodels.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

#Product Registration library
-keep class com.philips.cdp.prodreg.** {*;}
-keep interface com.philips.cdp.prodreg.** {*;}
-keep enum com.philips.cdp.prodreg.** {*;}

# App-framework
# -keep public class com.philips.platform.appframework.** {*;}


##--------------- ORMLite  ----------

# Keep ORMLite specifics
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

-keep @com.j256.ormlite.table.DatabaseTable class * { *; }




-dontwarn org.slf4j.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.commons.codec.binary.**
-dontwarn javax.persistence.**
-dontwarn javax.lang.**
-dontwarn javax.annotation.**
-dontwarn javax.tools.**


##--------------- Eventbus  ----------

-keepclassmembers class ** {
    !private void onEvent*(**);}
#     @org.greenrobot.eventbus.Subscribe <methods>;
#     public void onEvent(**);
#     public void onEventMainThread(**);
#     public void onEventBackgroundThread(**);
#     public void onEvent*(***);
#     void onEvent*(**);
#     void onEvent*(***);

-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keep class de.greenrobot.event.** { *; }
-keep class de.greenrobot.** { *; }
#-keepclassmembers,includedescriptorclasses class ** { public void onEvent*(**); }

#-keep class * {
#    @de.greenrobot.event.* <methods>;
#}

# Only required if you use AsyncExecutor
#-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
#    <init>(java.lang.Throwable);
#}

##--------------- Jodatime  ----------

-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

##-------------- Retrofit -------------

-keep class com.squareup.** { *; }
-keep interface com.squareup.** { *; }
-keep class retrofit.** { *; }
-keep interface retrofit.** { *;}

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-dontwarn com.squareup.okhttp.**
-dontwarn retrofit.**
-dontwarn okio.**
-dontwarn rx.**
-dontwarn android.app.Notification

#Data-Services
-keep class com.philips.platform.core.** {*;}
-keep interface com.philips.platform.core.** {*;}

##----------------- Gson -------------------
-keep class com.philips.platform.datasync.moments.UCoreMoment { *; }
-keep class com.philips.platform.datasync.moments.UCoreDetail { *; }
-keep class com.philips.platform.datasync.moments.UCoreMeasurement { *; }
-keep class com.philips.platform.datasync.moments.UCoreMomentsHistory { *; }
-keep class com.philips.platform.datasync.moments.UCoreMomentSaveResponse { *; }
-keep class com.philips.platform.datasync.moments.UCoreMeasurementGroupDetail { *; }
-keep class com.philips.platform.datasync.moments.UCoreMeasurementGroups { *; }
-keep class com.philips.platform.datasync.consent.UCoreConsentDetail { *; }
-keep class com.philips.platform.datasync.characteristics.UCoreCharacteristics { *; }
-keep class com.philips.platform.datasync.characteristics.UCoreUserCharacteristics { *; }
-keep class com.philips.platform.datasync.settings.UCoreSettings { *; }
#Insight
-keep class com.philips.platform.datasync.insights.UCoreInsight { *; }
-keep class com.philips.platform.datasync.insights.UCoreInsightList { *; }

#Push notification
-keep class com.philips.platform.datasync.PushNotification.UCorePushNotification { *; }

#Device pairing
-keep class com.philips.platform.datasync.devicePairing.UCoreDevicePair { *; }

#Subject Profile
-keep class com.philips.platform.datasync.subjectProfile.UCoreCreateSubjectProfileRequest { *; }
-keep class com.philips.platform.datasync.subjectProfile.UCoreCreateSubjectProfileResponse { *; }
-keep class com.philips.platform.datasync.subjectProfile.UCoreSubjectProfile { *; }
-keep class com.philips.platform.datasync.subjectProfile.UCoreSubjectProfileList { *; }

-keep class com.philips.platform.baseapp.screens.dataservices.pojo.AppUserCharacteristics { *; }
-keep class com.philips.platform.baseapp.screens.dataservices.pojo.AppCharacteristics { *; }

#HSDP Lib
-keep  class com.philips.dhpclient.** {*;}
-keep  class com.fasterxml.jackson.annotation.** {*;}
-keep  class com.fasterxml.jackson.core.** {*;}
-keep  class com.fasterxml.jackson.databind.** {*;}

#Tagging

-keep public class com.adobe.mobile.** {*;}
-keep public class com.philips.cdp.tagging.** {*;}


#Network
-keep class org.apache.http.** { *; }
-keep class android.net.http.** { *; }

#UIKit
-keep class com.shamanland.** {*;}
-keep class uk.co.chrisjenx.** {*;}


#ConsumerCare
-keep class com.philips.cdp.digitalcare.** {*;}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.content.BroadcastReceiver


-keep public class * extends android.app.Fragment {
    <init>(...);
}
-keep public class * extends android.support.v4.app.Fragment {
    <init>(...);
}

    -keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
    }

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class *{
  @android.webkit.JavascriptInterface <methods>;
}


#GooglePLayServices

-keep class android.support.** {*;}
-keep class android.view.** {*;}

-keep interface android.support.v13.app.** { *; }
-keep public class * extends android.support.v13.**
-keep public class * extends android.app.Fragment
-keep class com.philips.cdp.uikit.customviews.**
-keep class com.philips.cdp.productselection.**
-keep class com.philips.cdp.productselection.utils.ProductSelectionLogger.**
-keep class com.philips.cdp.productselection.ProductModelSelectionHelper.**


#-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-dontwarn  org.w3c.dom.bootstrap.DOMImplementationRegistry
#-dontwarn  com.philips.cdp.productselection.R$id
-dontwarn android.view.**
-dontwarn android.media.session
-dontwarn android.app.**

-dontwarn com.philips.cdp.digitalcare.**
#-dontwarn com.philips.cdp.productselection.**
-dontwarn android.support.**
-dontwarn com.adobe.mobile.**
-dontwarn org.apache.**


-dontwarn com.shamanland.**
-dontwarn uk.co.chrisjenx.**


#-------------------------Consumer Care Ends -------------------------

#InAppPurchase
-keep class com.philips.cdp.di.iap.store** {*;}
-keep interface com.philips.cdp.di.iap.store** {*;}
-keep class com.philips.cdp.di.iap.model** {*;}
-keep interface com.philips.cdp.di.iap.model** {*;}
-keep class com.philips.cdp.di.iap.response** {*;}
-keep interface com.philips.cdp.di.iap.response** {*;}
-keep class com.philips.cdp.di.iap.session.** {*;}
-keep interface com.philips.cdp.di.iap.session.** {*;}
-dontwarn com.philips.cdp.di.iap.analytics.**

#Prx
-keep class com.philips.cdp.prxclient.** {*;}
-keep interface com.philips.cdp.prxclient.** { *; }


#--------------------------AppInfra starts here-----------
-keep public class javax.net.ssl.**
-keepclassmembers public class javax.net.ssl.** {*;}
-keepclassmembers public class org.apache.http.** {*;}
-keepattributes InnerClasses,Exceptions

-dontwarn org.apache.**
-keep class org.apache.http.** { *; }
-keep class android.net.http.** { *; }



#Tagging lib and jar
-keep public class com.adobe.mobile.** {*;}
-keep class com.android.volley.** { *; }
-keep interface com.android.volley.** { *; }
-keep class org.apache.commons.logging.**

-keep public class com.philips.platform.appinfra.rest.request.GsonCustomRequest.** { *; }
-keep public class com.philips.platform.appinfra.languagepack.model.** { *; }

#-----------------------------app infra ends here-----------------------------------


#-----------------------------Apeligent starts here-----------------------------------
-dontwarn com.crittercism.**
-keep public class com.crittercism.**
-keepclassmembers public class com.crittercism.**{*;}

-keepattributes LineNumberTable
#------------------------------Apeligent ends here------------------------------------
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.support.v8.renderscript.** { *; }
-keep interface android.support.v13.app.** { *; }
-keep public class * extends android.support.v13.**
-keep class com.philips.platform.appframework.flowmanager.** { *; }
-keep interface com.philips.platform.appframework.flowmanager.** { *; }

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Remove Logging
-assumenosideeffects class android.util.Log {

public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-assumenosideeffects class java.io.PrintStream {
     public void println(%);
     public void println(**);
 }

 -keepclasseswithmembers class * {
      public <init>(java.util.Map);
 }

 ##----- Model classes should not be offusticated
 -keepclassmembers class * implements java.io.Serializable {
     static final long serialVersionUID;
     private static final java.io.ObjectStreamField[] serialPersistentFields;
     !static !transient <fields>;
     !private <fields>;
     !private <methods>;
     private void writeObject(java.io.ObjectOutputStream);
     private void readObject(java.io.ObjectInputStream);
     java.lang.Object writeReplace();
     java.lang.Object readResolve();
 }

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

#------- Keep activity, application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# Keep fragments
-keep public class * extends android.support.v4.app.FragmentStackActivity
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

-keep public class * extends android.app.Fragment {
    <init>(...);
}
-keep public class * extends android.support.v4.app.Fragment {
    <init>(...);
}

-keep public class * extends android.view.View {
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
public void set*(...);
*** get*();
}

-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet, int);
}

#Enumeration
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

#Static
-keepclassmembers class **.R$* {
public static <fields>;
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}