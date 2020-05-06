


 #ECS


-keep class com.philips.platform.ecs.ECSManager {*;}
-keep class com.philips.platform.ecs.ECSCallValidator {*;}
-keep class com.philips.platform.ecs.ApiInputValidator {*;}

-keep class com.philips.platform.ecs.model** {*;}
-keep class com.philips.platform.ecs.integration** {*;}
-keep class com.philips.platform.ecs.error** {*;}
-keep interface com.philips.platform.ecs.ECSServiceProvider** {*;}




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

