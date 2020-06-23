package com.philips.platform.appinfra.logging.database;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.AppInfraLogging;
import com.philips.platform.appinfra.logging.CloudLoggingConstants;
import com.philips.platform.appinfra.logging.LoggingUtils;
import com.philips.platform.appinfra.logging.model.AILCloudLogMetaData;

import java.util.Arrays;
import java.util.List;
import java.util.logging.LogRecord;

/**
 * Created by abhishek on 4/26/18.
 */

public class AILCloudLogDataBuilder {


    public static final String UUID_PREFIX = "hsdp_";
    private AppInfraInterface appInfra;

    public AILCloudLogDataBuilder(AppInfraInterface appInfra) {
        this.appInfra = appInfra;
    }

    public AILCloudLogData buildCloudLogModel(LogRecord logRecord) throws MessageSizeExceedsException {
        AILCloudLogData ailCloudLogData = new AILCloudLogData();

        if (logRecord.getParameters() != null) {
            List<Object> parameters = Arrays.asList(logRecord.getParameters());
            //Size 4 indicates that additional key value pairs has been passed with log.
            //Size 3 indicates that no additional key value pairs has been passed with log
            if (parameters.size() == AppInfraLogging.PARAM_SIZE_WITH_METADATA) {
                if (parameters.get(AppInfraLogging.LOG_METADATA_INDEX) != null) {
                    ailCloudLogData.details = LoggingUtils.convertObjectToJsonString(parameters.get(AppInfraLogging.LOG_METADATA_INDEX));
                }
            }
            //Getting log message from index 0.
            ailCloudLogData.logDescription = (String) parameters.get(AppInfraLogging.LOG_MESSAGE_INDEX);
            //Getting component Id and Component version from 1 and 2 index respectively.
            ailCloudLogData.component = (String) parameters.get(AppInfraLogging.COMPONENT_ID_INDEX) + "/" + (String) parameters.get(AppInfraLogging.COMPONENT_VERSION_INDEX);
            if (LoggingUtils.getStringLengthInBytes(ailCloudLogData.logDescription + ailCloudLogData.details) > CloudLoggingConstants.MAX_LOG_SIZE) {
                throw new MessageSizeExceedsException();
            }
        }
        ailCloudLogData.logId = LoggingUtils.getUUID();
        ailCloudLogData.severity = LoggingUtils.getAILLogLevel(logRecord.getLevel().toString());
        ailCloudLogData.eventId = logRecord.getMessage();
        if (appInfra.getLogging() != null) {
            AILCloudLogMetaData ailCloudLogMetaData = ((AppInfraLogging) appInfra.getLogging()).getAilCloudLogMetaData();
            if (ailCloudLogMetaData != null) {
                ailCloudLogData.homecountry = ailCloudLogMetaData.getHomeCountry();
                ailCloudLogData.locale = ailCloudLogMetaData.getLocale();
                ailCloudLogData.appState = ailCloudLogMetaData.getAppState();
                ailCloudLogData.appVersion = ailCloudLogMetaData.getAppVersion();
                ailCloudLogData.appsId = ailCloudLogMetaData.getAppId();
                if(ailCloudLogMetaData.getUserUUID()!=null){
                    ailCloudLogData.userUUID = UUID_PREFIX +ailCloudLogMetaData.getUserUUID();
                }
            }
        }

        if (appInfra.getTime() != null && appInfra.getTime().getUTCTime() != null) {
            ailCloudLogData.logTime = appInfra.getTime().getUTCTime().getTime();
        }
        if (appInfra.getRestClient() != null && appInfra.getRestClient().getNetworkReachabilityStatus() != null) {
            ailCloudLogData.networktype = appInfra.getRestClient().getNetworkReachabilityStatus().toString();
        }
        ailCloudLogData.localtime = logRecord.getMillis();

        ailCloudLogData.serverName = "Android/" + LoggingUtils.getOSVersion();
        ailCloudLogData.status= AILCloudLogDBManager.DBLogState.NEW.getState();
        return ailCloudLogData;
    }


    /**
     * Created by abhishek on 5/7/18.
     */

    public static class MessageSizeExceedsException extends Exception{
        private static final long serialVersionUID = -9089015001914125564L;
    }
}
