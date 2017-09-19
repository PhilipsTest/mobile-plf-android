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

#------------------------------------Registration Start----------------------------------
#Wechat
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}
-keep class com.janrainphilips.philipsregistration.wxapi.** {*;}
## New rules for EventBus 3.0.x ##
# http://greenrobot.org/eventbus/documentation/proguard/
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#Tagging lib and jar
-keep public class com.adobe.mobile.** {*;}
-keep public class com.philips.cdp.tagging.** {*;}
#Janrain lib
-keep public class com.philips.cdp.security.SecurityHelper {
    public static void init(android.content.Context);
}
-keep public class com.philips.cdp.security.SecurityHelper {
    public static java.lang.String objectToString(java.io.Serializable);
}
-keep public class com.philips.cdp.security.SecurityHelper {
    public static java.lang.Object stringToObject(java.lang.String);
}
-keep public class com.philips.cdp.security.SecurityHelper {
    public static void migrateUserData(java.lang.String);
}
-keep public class com.philips.cdp.security.SecurityHelper {
    public static byte[] encrypt(java.lang.String);
}
-keep public class com.philips.cdp.security.SecurityHelper {
    public static byte[] decrypt(byte[]);
}
-keep public class com.philips.cdp.security.SecurityHelper {
    public static void generateSecretKey();
}
#Locale match
-keep public class com.philips.cdp.localematch.** {*;}
#HSDP Lib
-keep  class com.fasterxml.jackson.annotation.** {*;}
-keep  class com.fasterxml.jackson.core.** {*;}
-keep  class com.fasterxml.jackson.databind.** {*;}
#GSM
-keep  class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
#webkit
-keep  class android.net.http.SslError
-keep  class android.webkit.WebViewClient
-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient
#notification
-dontwarn android.app.Notification
-dontwarn okio.**
-keep class com.squareup.** { *; }
-keep class java.nio.**
-keep class org.codehaus.**
-dontwarn com.janrain.android.**
-dontwarn java.nio.**
-keepattributes Signature
-keepattributes InnerClasses,EnclosingMethod
#--------------------------------------------------Registration End------------------------
#----------------------------Product Registration library Start Here -----------------------

-keep class com.philips.cdp.prodreg.model.** {*;}
-keep class com.philips.cdp.prodreg.register.** {*;}
-keep class com.philips.cdp.prodreg.localcache.** {*;}
#----------------------------Product Registration library End Here -----------------------

#----------------------------Product Registration DemoApp Start Here -----------------------

-keepclassmembers enum * { *; }

#Webkit (Registration)
-keep  class android.net.http.SslError
-keep  class android.webkit.WebViewClient

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#----------------------------Product Registration DemoApp End Here -----------------------






#---------------------------Dataservices starts here-----------------------------------------
#Pojo classes required by Retorfit to reflect the response

#Data-Services Moments
-keep class com.philips.platform.datasync.moments.UCoreMoment { *; }
-keep class com.philips.platform.datasync.moments.UCoreDetail { *; }
-keep class com.philips.platform.datasync.moments.UCoreMeasurement { *; }
-keep class com.philips.platform.datasync.moments.UCoreMomentsHistory { *; }
-keep class com.philips.platform.datasync.moments.UCoreMomentSaveResponse { *; }
-keep class com.philips.platform.datasync.moments.UCoreMeasurementGroupDetail { *; }
-keep class com.philips.platform.datasync.moments.UCoreMeasurementGroups { *; }

#Data-Services Consent
-keep class com.philips.platform.datasync.consent.UCoreConsentDetail { *; }

#Data-Services Characteristics
-keep class com.philips.platform.datasync.characteristics.UCoreCharacteristics { *; }
-keep class com.philips.platform.datasync.characteristics.UCoreUserCharacteristics { *; }

#For Dataservices Demo Micro App
-keep class com.philips.platform.dscdemo.pojo.AppCharacteristics { *; }
-keep class com.philips.platform.dscdemo.pojo.AppUserCharacteristics { *; }

#Data-Services Settings
-keep class com.philips.platform.datasync.settings.UCoreSettings { *; }

#Data-Services Insight
-keep class com.philips.platform.datasync.insights.UCoreInsight { *; }
-keep class com.philips.platform.datasync.insights.UCoreInsightList { *; }

#Data-Services Push notification
-keep class com.philips.platform.datasync.PushNotification.UCorePushNotification { *; }

#Data-Services Device pairing
-keep class com.philips.platform.datasync.devicePairing.UCoreDevicePair { *; }

#Data-Services Subject Profile
-keep class com.philips.platform.datasync.subjectProfile.UCoreCreateSubjectProfileRequest { *; }
-keep class com.philips.platform.datasync.subjectProfile.UCoreCreateSubjectProfileResponse { *; }
-keep class com.philips.platform.datasync.subjectProfile.UCoreSubjectProfile { *; }
-keep class com.philips.platform.datasync.subjectProfile.UCoreSubjectProfileList { *; }

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

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-dontwarn com.squareup.okhttp.**
-dontwarn retrofit.**
-dontwarn okio.**
-dontwarn rx.**
-dontwarn android.app.Notification

#Green Robot Eventbus
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keep class de.greenrobot.event.** { *; }
-keep class de.greenrobot.** { *; }
-keepclassmembers class ** {
    public void onEvent(**);
}
-keepclassmembers,includedescriptorclasses class ** { public void onEvent*(**); }

# Jodatime

-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

# Retrofit

-keep class com.squareup.** { *; }
-keep interface com.squareup.** { *; }
-keep class retrofit.** { *; }
-keep interface retrofit.** { *;}

# Sqlcipher
-keep class net.sqlcipher.** {*;}
-keep interface net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** {*;}
-keep interface net.sqlcipher.database.** { *; }
-keep enum net.sqlcipher.**
-keepclassmembers enum net.sqlcipher.** { *; }


#------------------Data Services ends here----------------------------------------------------



#------------------------- Consumer Care starts -------------------------

#need for javascript enabled webviews
-keepclassmembers class *{
  @android.webkit.JavascriptInterface <methods>;
}

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-dontoptimize

#attributes
-keepattributes *Annotation*
-keepattributes Signature


#-------------------------Consumer Care Ends -------------------------



#------------------------------InAppPurchase starts here------------------------------------
#all below classes are model classes
-keep class com.philips.cdp.di.iap.store.AbstractStore {*;}
-keep class com.philips.cdp.di.iap.store.HybrisStore {*;}
-keep class com.philips.cdp.di.iap.store.IAPUser {*;}
-keep class com.philips.cdp.di.iap.store.LocalStore {*;}
-keep class com.philips.cdp.di.iap.store.StoreConfiguration {*;}
-keep class com.philips.cdp.di.iap.store.StoreController {*;}
-keep interface com.philips.cdp.di.iap.store.StoreListener{*;}

-keep class com.philips.cdp.di.iap.model** {*;}
-keep interface com.philips.cdp.di.iap.model** {*;}

-keep class com.philips.cdp.di.iap.response** {*;}
-keep interface com.philips.cdp.di.iap.response** {*;}

#------------------------------InAppPurchase ends here------------------------------------

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


#app-infra
-keep public class com.philips.platform.appinfra.rest.request.GsonCustomRequest.** { *; }
-keep public class com.philips.platform.appinfra.languagepack.model.** { *; }

#UappFramework
-keep public class com.philips.platform.appframework.flowmanager.models.** { *; }

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** {*;}
-keep class com.google.gson.examples.android.model.** { *; }
-dontwarn com.google.gson.**


#-----------------------------app infra ends here-----------------------------------

#-----------------------------PRX starts here-----------------------------------

-keep class com.philips.cdp.prxclient.datamodels.** { *; }

#-----------------------------PRX ends here-----------------------------------


#-----------------------------Apeligent starts here-----------------------------------
-dontwarn com.crittercism.**
-keep public class com.crittercism.**
-keepclassmembers public class com.crittercism.**{*;}

-keepattributes LineNumberTable
#------------------------------Apeligent ends here------------------------------------

#------------------------------Application specific rules start here------------------------------------
#Detail info at https://www.guardsquare.com/en/proguard/manual/examples#application

-verbose

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembers class * {
      public <init>(java.util.Map);
 }

# Keep fragments
-keep public class * extends android.support.v4.app.FragmentStackActivity
-keep public class * extends android.app.Fragment {
    <init>(...);
}
-keep public class * extends android.support.v4.app.Fragment {
    <init>(...);
}

-dontwarn com.google.android.gms.**

# Keep android view

-keep public class * extends android.view.View {
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
public void set*(...);
*** get*();
}

# Model classes for test microapp should not be obfuscated
-keep class com.philips.platform.appframework.models.** {*;}


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

#Needed for Registration
-keep class com.philips.platform.baseapp.base.wxapi.** {*;}
-keep class com.philips.platform.referenceapp.wxapi.** {*;}
-keep class com.philips.platform.referenceapp.wxapi.WXEntryActivity
-keep class com.tencent.mm.sdk.** {
      *;
  }

#------------------------------Application specific rules end  here------------------------------------
