/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package package2.component2;

import android.content.Context;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.logging.LoggingInterface;

/**
 * Created by 310238114 on 5/13/2016.
 */
public class Component2 {
    private final AppInfra mAppInfra ;
    private LoggingInterface AILoggingInterface;
    Context mContext;

    public Component2(AppInfra pAppInfra){

      mAppInfra=pAppInfra;

        AILoggingInterface = mAppInfra.getLogging().createInstanceForComponent("package2.component2", "2.0.1"); //this.getClass().getPackage().toString()
       /* AILoggingInterface.enableConsoleLog(true);
        AILoggingInterface.enableFileLog(true);*/
        showLog();
    }

    public void showLog(){
        AILoggingInterface.log(LoggingInterface.LogLevel.ERROR,"c2 er","c2 msg");
       /* AILoggingInterface.log(LoggingInterface.LogLevel.WARNING,"c2 er","c2 msg");

        AILoggingInterface.log(LoggingInterface.LogLevel.INFO,"c2 er","c2 msg");
        AILoggingInterface.log(LoggingInterface.LogLevel.DEBUG,"c2 er","c2 msg");
        AILoggingInterface.log(LoggingInterface.LogLevel.VERBOSE,"c2 er","c2 msg");*/


    }
}
