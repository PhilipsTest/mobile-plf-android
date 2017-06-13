package com.philips.cdp.registration.controller;

import android.content.Context;

import com.janrain.android.capture.CaptureApiError;
import com.philips.cdp.registration.RegistrationApiInstrumentationBase;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.handlers.ResendVerificationEmailHandler;
import com.philips.cdp.registration.ui.utils.RegConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;


public class ResendVerificationEmailTest extends RegistrationApiInstrumentationBase {

    Context context;
    @Mock
    ResendVerificationEmail mResendVerificationEmail;
    ResendVerificationEmailHandler mResendVerificationEmailHandler;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getTargetContext();
        mResendVerificationEmailHandler = new ResendVerificationEmailHandler() {
            @Override
            public void onResendVerificationEmailSuccess() {

            }

            @Override
            public void onResendVerificationEmailFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {

            }
        };
        assertNotNull(mResendVerificationEmailHandler);
        mResendVerificationEmail = new ResendVerificationEmail(context,mResendVerificationEmailHandler);
        assertNotNull(mResendVerificationEmail);

    }
    @Test
    public void testmResendVerificationEmail(){
        CaptureApiError error = new CaptureApiError();
        mResendVerificationEmail.onFailure(error);
        assertSame(mResendVerificationEmail.mResendVerificationEmail,mResendVerificationEmailHandler );
    }
    public void testResendVerificationMail(){

        synchronized(this){//synchronized block

            try{
                mResendVerificationEmail.resendVerificationMail("emailAddress@sample.com");

            }catch(Exception e){System.out.println(e);}
        }

        Method method = null;
        String s= "";
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("hdll");
        try {
            method = ResendVerificationEmail.class.getDeclaredMethod("getErrorMessage", JSONArray.class);
            method.setAccessible(true);
            method.invoke(mResendVerificationEmail, jsonArray);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
         jsonArray = null;
        try {
            method = ResendVerificationEmail.class.getDeclaredMethod("getErrorMessage", JSONArray.class);
            method.setAccessible(true);
            method.invoke(mResendVerificationEmail, jsonArray);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void testHandleInvalidInputs(){

        synchronized(this){//synchronized block

            try{
                mResendVerificationEmail.resendVerificationMail("emailAddress@sample.com");

            }catch(Exception e){System.out.println(e);}
        }

        Method method = null;
        JSONObject response = new JSONObject();
        try {
            response.put("error",RegConstants.INVALID_CREDENTIALS);
            response.put("code",200);
            response.put("error_description","sample");
            response.put(RegConstants.INVALID_FIELDS,RegConstants.INVALID_FIELDS);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        CaptureApiError error=new CaptureApiError( response,  "engageToken",  "conflictingProvider");

        UserRegistrationFailureInfo userRegistrationFailureInfo= new UserRegistrationFailureInfo();
        try {
            method = ResendVerificationEmail.class.getDeclaredMethod("handleInvalidInputs", CaptureApiError.class,UserRegistrationFailureInfo.class);
            method.setAccessible(true);
            method.invoke(mResendVerificationEmail, error,userRegistrationFailureInfo);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}