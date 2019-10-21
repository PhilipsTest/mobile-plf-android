/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.philips.cdp.di.iap.address.AddressFields;
import com.philips.cdp.di.iap.response.addresses.Addresses;
import com.philips.cdp.di.iap.response.addresses.Country;
import com.philips.cdp.di.iap.response.addresses.Region;
import com.philips.platform.uid.view.widget.AlertDialogFragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static com.philips.cdp.di.iap.utils.NetworkUtility.ALERT_DIALOG_TAG;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class UtilityTest {

    @Mock
    private Activity activity;

    @Mock
    private Context contextMock;

    @Mock
    private View viewMock;

    @Mock
    private IBinder iBinderMock;

    @Mock
    private SharedPreferences.Editor editorMock;

    @Mock
    private SharedPreferences sharedPreferencesMock;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void shouldHideKeyboard() throws Exception {
        when(viewMock.getWindowToken()).thenReturn(iBinderMock);
        when(activity.getCurrentFocus()).thenReturn(viewMock);
        Utility.hideKeypad(activity);
    }

    @Test
    public void shouldFormatAddress() throws Exception {
        Utility.formatAddress("26 New york");
    }

    @Test
    public void shouldReturnNull_WhenNullIsPassedToFormatAddress() throws Exception {
        Assert.assertNull(Utility.formatAddress(null));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowException_WhenGetFormattedDateIsPassedWithWrongFormatString() throws Exception {
        Utility.getFormattedDate("26 New york");
    }

    @Test
    public void shouldAddCountryPreference() throws Exception {
        when(sharedPreferencesMock.edit()).thenReturn(editorMock);
        Utility.addCountryInPreference(sharedPreferencesMock, "hi", "Hi");
    }

    @Test
    public void shouldAppendAddressWithNewLineIfNotNull() throws Exception {

        StringBuilder stringBuilder = new StringBuilder("abcd");
        Utility.appendAddressWithNewLineIfNotNull(stringBuilder, "code");

    }

    @Mock
    private AddressFields addressFieldsMock;

    @Test
    public void shouldGetAddressToDisplay() throws Exception {
        Utility.getAddressToDisplayForOrderDetail(addressFieldsMock);
    }

    @Test
    public void shouldReturnIsNotNullNorEmpty() throws Exception {
        Assert.assertTrue(Utility.isNotNullNorEmpty("abcd"));
    }

    @Mock
    private Addresses addressesMock;

    @Mock
    private Country countryMock;

    @Mock
    private Region regionMock;

    @Test
    public void shouldPrepareAddressFields() throws Exception {
        when(countryMock.getIsocode()).thenReturn("091");
        when(regionMock.getIsocodeShort()).thenReturn("01");
        when(addressesMock.getRegion()).thenReturn(regionMock);
        when(addressesMock.getCountry()).thenReturn(countryMock);
        when(addressesMock.getTitleCode()).thenReturn("abcd");
        Utility.prepareAddressFields(addressesMock, "abc@gmail.com");

    }

    @Mock
    private FragmentManager fragmentManagerMock;

    @Mock
    private AlertListener alertListenerMock;

    @Test(expected = NullPointerException.class)
    public void shouldShowActionDialog() throws Exception {
        Utility.showActionDialog(contextMock, "HI", "HI", "HI", "HI", fragmentManagerMock, alertListenerMock);

    }

    @Mock
    private AlertDialogFragment alertDialogFragmentMock;

    @Test
    public void shouldDismissAlertDialog() throws Exception {
        Utility.dismissAlertFragmentDialog(alertDialogFragmentMock, fragmentManagerMock);
    }

    @Test
    public void shouldDismissAlertDialogWhenPassedNull() throws Exception {
        when(fragmentManagerMock.findFragmentByTag(ALERT_DIALOG_TAG)).thenReturn(alertDialogFragmentMock);
        Utility.dismissAlertFragmentDialog(null, fragmentManagerMock);
    }

    @Test
    public void shouldReturnIndexOfSubString() throws Exception {
        CharSequence hello = "hello";
        CharSequence world = "world";
        Utility.indexOfSubString(true, hello, world);

    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnImageArray() throws Exception {
        Utility.getImageArrow(contextMock);
    }
}