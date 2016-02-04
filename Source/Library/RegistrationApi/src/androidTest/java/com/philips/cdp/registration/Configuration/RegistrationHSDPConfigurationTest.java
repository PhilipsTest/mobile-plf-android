package com.philips.cdp.registration.Configuration;

import android.test.ActivityInstrumentationTestCase2;

import com.philips.cdp.registration.configuration.Configuration;
import com.philips.cdp.registration.configuration.HSDPInfo;
import com.philips.cdp.registration.configuration.HSDPConfiguration;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.configuration.RegistrationDynamicConfiguration;
import com.philips.cdp.registration.configuration.RegistrationStaticConfiguration;
import com.philips.cdp.registration.ui.traditional.RegistrationActivity;

/**
 * Created by vinayak on 28/01/16.
 */
public class RegistrationHSDPConfigurationTest extends ActivityInstrumentationTestCase2<RegistrationActivity> {

    //Constructor for instrimental test case
    public RegistrationHSDPConfigurationTest() {
        super(RegistrationActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String CONFIGURATION_JSON_PATH = "registration/configuration/configuration_for_test_cases.json";
        RegistrationStaticConfiguration.getInstance().parseConfigurationJson(getInstrumentation().getTargetContext(), CONFIGURATION_JSON_PATH);
    }


    public void testHSDPConfigurationFlieldsWithStatic() {


        HSDPConfiguration hsdpConfiguration = RegistrationConfiguration.getInstance().getHsdpConfiguration();


        if(hsdpConfiguration.getHsdpInfos().size()==0){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.EVALUATION).getApplicationName().equalsIgnoreCase("uGrowApplication")) {
            assertTrue(false);
        }
        if(!hsdpConfiguration.getHSDPInfo(Configuration.EVALUATION).getSecretId().equalsIgnoreCase("2eaec60a-1a2e-11e5-b60b-1697f925ec7b")){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.EVALUATION).getSharedId().equalsIgnoreCase("2eaec11e-1a2e-11e5-b60b-1697f925ec7b")){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.EVALUATION).getBaseURL().equalsIgnoreCase("http://ugrowuserregistration.cloud.pcftest.com/")){
            assertTrue(false);
        }


        assertTrue(true);
    }

    public void testHSDPConfigurationFlieldsWithDynamicReplace() {

        HSDPConfiguration hsdpConfiguration = new HSDPConfiguration();
        //Override value
        HSDPInfo hsdpInfo = new HSDPInfo();
        hsdpInfo.setApplicationName("Appname");
        hsdpInfo.setBaseURL("url");
        hsdpInfo.setSecretId("secreteid");
        hsdpInfo.setSharedId("sharedid");

        hsdpConfiguration.setHSDPInfo(Configuration.EVALUATION, hsdpInfo);

        RegistrationDynamicConfiguration.getInstance().setHsdpConfiguration(hsdpConfiguration);

         hsdpConfiguration = RegistrationConfiguration.getInstance().getHsdpConfiguration();

        if(hsdpConfiguration.getHsdpInfos().size()==0){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.EVALUATION).getApplicationName().equalsIgnoreCase("Appname")) {
            assertTrue(false);
        }
        if(!hsdpConfiguration.getHSDPInfo(Configuration.EVALUATION).getSecretId().equalsIgnoreCase("secreteid")){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.EVALUATION).getSharedId().equalsIgnoreCase("sharedid")){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.EVALUATION).getBaseURL().equalsIgnoreCase("url")){
            assertTrue(false);
        }


        assertTrue(true);

        RegistrationDynamicConfiguration.getInstance().resetDynamicConfiguration();


    }

    public void testHSDPConfigurationFlieldsWithDynamicAdd() {

        HSDPConfiguration hsdpConfiguration = new HSDPConfiguration();
        //Override value
        HSDPInfo hsdpInfo = new HSDPInfo();
        hsdpInfo.setApplicationName("Appname");
        hsdpInfo.setBaseURL("url");
        hsdpInfo.setSecretId("secreteid");
        hsdpInfo.setSharedId("sharedid");

        hsdpConfiguration.setHSDPInfo(Configuration.STAGING, hsdpInfo);

        RegistrationDynamicConfiguration.getInstance().setHsdpConfiguration(hsdpConfiguration);

        hsdpConfiguration = RegistrationConfiguration.getInstance().getHsdpConfiguration();

        if(hsdpConfiguration.getHsdpInfos().size()==0){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.STAGING).getApplicationName().equalsIgnoreCase("Appname")) {
            assertTrue(false);
        }
        if(!hsdpConfiguration.getHSDPInfo(Configuration.STAGING).getSecretId().equalsIgnoreCase("secreteid")){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.STAGING).getSharedId().equalsIgnoreCase("sharedid")){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.STAGING).getBaseURL().equalsIgnoreCase("url")){
            assertTrue(false);
        }

        if(hsdpConfiguration.getHsdpInfos().size()!=2){
            assertTrue(false);
        }


        assertTrue(true);
        RegistrationDynamicConfiguration.getInstance().resetDynamicConfiguration();

    }


    public void testFlowConfigurationFlieldsOnlyDynamic() {

        RegistrationStaticConfiguration.getInstance().setHsdpConfiguration(null);

        HSDPConfiguration hsdpConfiguration = new HSDPConfiguration();
        //Override value
        HSDPInfo hsdpInfo = new HSDPInfo();
        hsdpInfo.setApplicationName("Appname");
        hsdpInfo.setBaseURL("url");
        hsdpInfo.setSecretId("secreteid");
        hsdpInfo.setSharedId("sharedid");

        hsdpConfiguration.setHSDPInfo(Configuration.STAGING, hsdpInfo);

        RegistrationDynamicConfiguration.getInstance().setHsdpConfiguration(hsdpConfiguration);

        hsdpConfiguration = RegistrationConfiguration.getInstance().getHsdpConfiguration();

        if(hsdpConfiguration.getHsdpInfos().size()==0){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.STAGING).getApplicationName().equalsIgnoreCase("Appname")) {
            assertTrue(false);
        }
        if(!hsdpConfiguration.getHSDPInfo(Configuration.STAGING).getSecretId().equalsIgnoreCase("secreteid")){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.STAGING).getSharedId().equalsIgnoreCase("sharedid")){
            assertTrue(false);
        }

        if(!hsdpConfiguration.getHSDPInfo(Configuration.STAGING).getBaseURL().equalsIgnoreCase("url")) {
            assertTrue(false);
        }

        if(hsdpConfiguration.getHsdpInfos().size()>1){
            assertTrue(false);
        }

        assertTrue(true);
        RegistrationDynamicConfiguration.getInstance().resetDynamicConfiguration();
    }

}
