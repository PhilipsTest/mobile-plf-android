#Android support library
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.support.v8.renderscript.** { *; }

-verbose

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

#check if somethings below can be removed?
-dontwarn org.slf4j.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.commons.codec.binary.**
-dontwarn javax.persistence.**
-dontwarn javax.lang.**
-dontwarn javax.annotation.**
-dontwarn javax.tools.**



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


#HSDP Lib
-keep  class com.philips.dhpclient.** {*;}
-keep  class com.fasterxml.jackson.annotation.** {*;}
-keep  class com.fasterxml.jackson.core.** {*;}
-keep  class com.fasterxml.jackson.databind.** {*;}

#Tagging

-keep public class com.adobe.mobile.** {*;}
-keep public class com.philips.cdp.tagging.** {*;}


#LocaleMatch

-keep public class com.philips.cdp.localematch.** {*;}


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
-dontwarn com.philips.platform.appinfra.**

-dontwarn org.apache.**
-keep class org.apache.http.** { *; }
-keep class android.net.http.** { *; }



#Tagging lib and jar
-keep public class com.adobe.mobile.** {*;}
-keep public class com.philips.platform.appinfra.tagging.** {*;}
-keep class com.android.volley.** { *; }
-keep interface com.android.volley.** { *; }
-keep class org.apache.commons.logging.**


-keep public class com.philips.platform.appinfra.AppInfra { *; }
-keep public class com.philips.platform.appinfra.AppInfra.Builder { *; }
-keepnames public class com.philips.platform.appinfra.AppInfra.Builder
-keep public class com.philips.platform.appinfra.AppInfraSingleton { *; }
-keep public class com.philips.platform.appinfra.GlobalStore { *; }

-keepnames public class com.philips.platform.appinfra.AppInfra
-keep public class  com.philips.platform.appinfra.AppInfra$* {
        *;
 }
-keepclassmembers  public class com.philips.platform.appinfra.AppInfra
-keep public class com.philips.platform.appinfra.AppInfraLibraryApplication.**


-keep public class com.philips.platform.appinfra.appidentity.** {
  public protected *;
}
-keep public class com.philips.platform.appinfra.securestorage.** {
  public protected *;
}
-keep public class com.philips.platform.appinfra.logging.** {
   public protected *;
 }
 -keep public class com.philips.platform.appinfra.servicediscovery.** {
    public protected *;
  }
-keep public class com.philips.platform.appinfra.timesync.** {
    public protected *;
  }



-keep public interface com.philips.platform.appinfra.appidentity.AppIdentityInterface {*;}

-keep public interface com.philips.platform.appinfra.AppInfraInterface {*;}

#-----------------------------app infra ends here-----------------------------------

-keep class com.americanwell.sdksample.** { *; }
-dontwarn com.americanwell.sdksample.**

# Butterknife
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(...); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

# Icepick
-dontwarn icepick.**
-keep class icepick.** { *; }
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
-keepnames class * { @icepick.State *;}

#foo

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\ken.rothman\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

# Butterknife
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(...); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

# Icepick
-dontwarn icepick.**
-keep class icepick.** { *; }
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
-keepnames class * { @icepick.State *;}

#foo

-dontwarn com.google.common.base.**
-keep class com.google.common.base.** {*;}
-dontwarn com.google.errorprone.annotations.**
-keep class com.google.errorprone.annotations.** {*;}
-dontwarn   com.google.j2objc.annotations.**
-keep class com.google.j2objc.annotations.** { *; }
-dontwarn   java.lang.ClassValue
-keep class java.lang.ClassValue { *; }
-dontwarn   org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement