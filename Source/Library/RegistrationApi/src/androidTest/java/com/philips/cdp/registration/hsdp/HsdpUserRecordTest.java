package com.philips.cdp.registration.hsdp;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by 310243576 on 9/6/2016.
 */
public class HsdpUserRecordTest extends InstrumentationTestCase {

    @Mock
    HsdpUserRecord mHsdpUserRecord;

    @Mock
    HsdpUserRecord.Profile profile ;
    @Mock
    Context context;

    @Before
    public void setUp() throws Exception {
        MultiDex.install(getInstrumentation().getTargetContext());
        // Necessary to get Mockito framework working
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
//        MockitoAnnotations.initMocks(this);
        super.setUp();
        context = getInstrumentation().getTargetContext();
        mHsdpUserRecord = new HsdpUserRecord(context);
    }

    public void testHsdpUserRecord(){
        mHsdpUserRecord.setLoginId("sample");
        assertEquals("sample",mHsdpUserRecord.getLoginId());
        assertEquals(null,mHsdpUserRecord.getProfile());
        assertEquals(null,mHsdpUserRecord.getUserUUID());
    //    mHsdpUserRecord.setRefreshSecret("refreshSecret");
      //  assertEquals("refreshSecret",mHsdpUserRecord.getRefreshSecret());
        assertNotEquals(-1,mHsdpUserRecord.getUserIsActive());
    }

    public void testAccessCredential(){
        HsdpUserRecord.AccessCredential accessCredential = new HsdpUserRecord(context).new AccessCredential();

        accessCredential.setRefreshToken("refreshToken");
        assertEquals("refreshToken",accessCredential.getRefreshToken());

        accessCredential.setAccessToken("accessToken");
        assertEquals("accessToken",accessCredential.getAccessToken());

        accessCredential.setExpiresIn(1);
        assertEquals(1,accessCredential.getExpiresIn());
    }
    public void testProfile(){
        profile = new HsdpUserRecord(context).new Profile();
        profile.setGivenName("givenName");
        assertEquals("givenName",profile.getGivenName());

        profile.setMiddleName("middleName");
        assertEquals("middleName",profile.getMiddleName());

        profile.setGender("gender");
        assertEquals("gender",profile.getGender());


        profile.setBirthday("birthday");
        assertEquals("birthday",profile.getBirthday());

        profile.setPreferredLanguage("preferredLanguage");
        assertEquals("preferredLanguage",profile.getPreferredLanguage());

        profile.setReceiveMarketingEmail("receiveMarketingEmail");
        assertEquals("receiveMarketingEmail",profile.getReceiveMarketingEmail());

        profile.setCurrentLocation("currentLocation");
        assertEquals("currentLocation",profile.getCurrentLocation());

        profile.setDisplayName("displayName");
        assertEquals("displayName",profile.getDisplayName());

        profile.setFamilyName("familyName");
        assertEquals("familyName",profile.getFamilyName());

        profile.setLocale("en-EN");
        assertEquals("en-EN",profile.getLocale());


        profile.setTimeZone("timeZone");
        assertEquals("timeZone",profile.getTimeZone());


        profile.setHeight("height");
        assertEquals("height",profile.getHeight());


        profile.setWeight("weight");
        assertEquals("weight",profile.getWeight());


        HsdpUserRecord.Profile.PrimaryAddress primaryAddress = new HsdpUserRecord(context).new Profile().new PrimaryAddress();

        primaryAddress.setCountry("sample");
        primaryAddress.getCountry();
        profile.setPrimaryAddress(primaryAddress);
        assertEquals(primaryAddress,profile.getPrimaryAddress());

        ArrayList<HsdpUserRecord.Profile.Photo> photoList = new ArrayList<HsdpUserRecord.Profile.Photo>();
        HsdpUserRecord.Profile.Photo photo = new HsdpUserRecord(context).new Profile().new Photo("type", "value");
        photo.setType("type");
        assertEquals("type", photo.getType());
        photo.setValue("value");
        assertEquals("value", photo.getValue());
        photoList.add(photo);
        profile.setPhotos(photoList);
        assertEquals(photoList, profile.getPhotos());
    }

}

