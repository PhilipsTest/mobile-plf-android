/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.appinfra.logging.rest;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.AppInfraLogging;
import com.philips.platform.appinfra.logging.database.AILCloudLogData;
import com.philips.platform.appinfra.logging.model.AILCloudLogMetaData;
import com.philips.platform.appinfra.logging.rest.model.CloudLogs;
import com.philips.platform.appinfra.logging.rest.model.Resource;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class CloudLogRequestBodyBuilderTest {

    private CloudLogRequestBodyBuilder cloudLogRequestBodyBuilder;


    @Mock
    private AppInfraInterface appInfra;

    @Mock
    private AppInfraLogging appInfraLogging;

    @Mock
    private AILCloudLogMetaData ailCloudLogMetaData;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(appInfra.getLogging()).thenReturn(appInfraLogging);
        when(appInfraLogging.getAilCloudLogMetaData()).thenReturn(ailCloudLogMetaData);
        cloudLogRequestBodyBuilder = new CloudLogRequestBodyBuilder(appInfra, "product_key");
    }

    @Test
    public void testFormingCloudLogRequestBody() {
        when(ailCloudLogMetaData.getAppName()).thenReturn("AppInfra");
        AILCloudLogData ailCloudLogData = getAilCloudLogData();
        ArrayList<AILCloudLogData> ailCloudLogDataList = new ArrayList<>();
        ailCloudLogDataList.add(ailCloudLogData);
        JSONObject cloudLogRequestBody = cloudLogRequestBodyBuilder.getCloudLogRequestBody(ailCloudLogDataList);
        Gson gson = new Gson();
        CloudLogs cloudLogs = gson.fromJson(cloudLogRequestBody.toString(), CloudLogs.class);

        if (cloudLogs != null && cloudLogs.getEntry() != null) {
            Resource resource = cloudLogs.getEntry().get(0).getResource();
            assertEquals(resource.getApplicationName(), "AppInfra");
            assertEquals(resource.getApplicationVersion(), "version");
            assertEquals(resource.getEventId(), "eventId");
            assertEquals(resource.getServerName(), "serverName");
            assertEquals(resource.getOriginatingUser(), "userUUID");
        }

    }

    @NonNull
    private AILCloudLogData getAilCloudLogData() {
        AILCloudLogData ailCloudLogData = new AILCloudLogData();
        ailCloudLogData.status = "status";
        ailCloudLogData.appsId = "app_id";
        ailCloudLogData.appState = "appState";
        ailCloudLogData.appVersion = "version";
        ailCloudLogData.details = "details";
        ailCloudLogData.homecountry = "homeCountry";
        ailCloudLogData.component = "component";
        ailCloudLogData.userUUID = "userUUID";
        ailCloudLogData.logDescription = "description";
        ailCloudLogData.locale = "en";
        ailCloudLogData.localtime = 456789;
        ailCloudLogData.serverName = "serverName";
        ailCloudLogData.severity = "severity";
        ailCloudLogData.logId = "log_id";
        ailCloudLogData.eventId = "eventId";
        ailCloudLogData.networktype = "networkType";
        return ailCloudLogData;
    }
}