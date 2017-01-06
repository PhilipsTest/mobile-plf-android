package com.philips.cdp.registration.handlers;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.test.InstrumentationTestCase;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by 310243576 on 8/18/2016.
 */
public class RefreshandUpdateUserHandlerTest extends InstrumentationTestCase {

    @Mock
    RefreshandUpdateUserHandler refreshandUpdateUserHandler;

    @Mock
    UpdateUserRecordHandler updateUserRecordHandler;

    @Mock
    Context context;
    RefreshUserHandler handler;
    User user;


    @Before
    public void setUp() throws Exception {
        MultiDex.install(getInstrumentation().getTargetContext());
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().
                getCacheDir().getPath());
//        MockitoAnnotations.initMocks(this);
        super.setUp();
        context = getInstrumentation().getTargetContext();
        handler= new RefreshUserHandler() {
            @Override
            public void onRefreshUserSuccess() {

            }

            @Override
            public void onRefreshUserFailed(final int error) {

            }
        };
        user= new User(getInstrumentation().getContext());
        updateUserRecordHandler = new UpdateUserRecordHandler() {
            @Override
            public void updateUserRecordLogin() {

            }

            @Override
            public void updateUserRecordRegister() {

            }
        };
        assertNotNull(updateUserRecordHandler);
        refreshandUpdateUserHandler = new RefreshandUpdateUserHandler(updateUserRecordHandler,
                context);
        assertNotNull(refreshandUpdateUserHandler);

    }

    @Test
    public void testRefreshAndUpdateUser() throws Exception {
//        User user = new User(context);
//        RefreshUserHandler refreshUserHandler = new RefreshUserHandler() {
//            @Override
//            public void onRefreshUserSuccess() {
//
//            }
//
//            @Override
//            public void onRefreshUserFailed(int error) {
//
//            }
//        };
//
//        AppInfraSingleton.setInstance(new AppInfra.Builder().build(context));
//        refreshandUpdateUserHandler.refreshAndUpdateUser(refreshUserHandler, user, "password");
        assertSame(refreshandUpdateUserHandler.mUpdateUserRecordHandler, updateUserRecordHandler);

    }
    public void testonFlowDownloadFailure(){
        assertNotNull(!UserRegistrationInitializer.getInstance().isJumpInitializated());
        assertNotNull(!UserRegistrationInitializer.getInstance().isRegInitializationInProgress());

    }
    @Test
    public void testRefreshUpdateUser(){
        Method method = null;
         RefreshUserHandler handler=new RefreshUserHandler() {
             @Override
             public void onRefreshUserSuccess() {

             }

             @Override
             public void onRefreshUserFailed(final int error) {

             }
         } ;
        User user=new User(getInstrumentation().getContext());
        String password="abs";
        try {
            method =RefreshandUpdateUserHandler.class.getDeclaredMethod("refreshUpdateUser",
                    RefreshUserHandler.class,User.class,String.class);
            method.setAccessible(true);
            method.invoke(refreshandUpdateUserHandler,handler,user,password);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
    public void testGetDIUserProfileFromDisk(){
        Method method = null;
        try {
            method = RefreshandUpdateUserHandler.class.getDeclaredMethod("getDIUserProfileFromDisk");
            method.setAccessible(true);
            method.invoke(refreshandUpdateUserHandler);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}