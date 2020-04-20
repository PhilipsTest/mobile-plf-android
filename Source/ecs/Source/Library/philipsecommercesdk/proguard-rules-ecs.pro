


 #ECS
 -keep class com.philips.cdp.ecs.ECSServices {*;}
 -keep class com.philips.cdp.ecs.ECSManager {*;}
 -keep class com.philips.cdp.ecs.ECSCallValidator {*;}
 -keep class com.philips.cdp.ecs.ApiInputValidator {*;}
-keep class com.philips.cdp.ecs.model** {*;}
-keep class com.philips.cdp.ecs.integration** {*;}
-keep class com.philips.cdp.ecs.error** {*;}
-keep interface com.philips.cdp.ecs.ECSServiceProvider** {*;}


#JSACKSON
-keep  class com.fasterxml.jackson.annotation.** {*;}
-keep  class com.fasterxml.jackson.core.** {*;}
-keep  class com.fasterxml.jackson.databind.** {*;}
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
-keep class org.codehaus.** { *; }
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
    public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
-keep public class your.class.** {
    public void set*(***);
    public *** get*();
    }

