package com.philips.cdp.digitalcare.customview;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.platform.uid.view.widget.AlertDialogFragment;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by philips on 24/11/17.
 */

public class GpsAlertViewTest extends TestCase {

    @Mock
    Log mockLog;

    @Mock
    AlertDialogFragment alertDialogFragmentMock;

    @Mock
    DialogFragment dialogFragmentMock;

    @Mock
    Fragment fragmentMock;

    @Mock
    FragmentManager fragmentManagerMock;

    @Mock
    GpsAlertView gpsAlertViewMock;

    @Mock
    DigitalCareBaseFragment digitalCareBaseFragmentMock;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);


    }

    @Test
    public void testShowGpsAlert() {

        gpsAlertViewMock.showAlert(digitalCareBaseFragmentMock, -1, R.string.gps_disabled,
                android.R.string.yes, android.R.string.no);
    }

    @Test
    public void testRemoveAlert(){
        gpsAlertViewMock.removeAlert();
    }

}
