package com.philips.appinfra;

import android.content.Context;

import com.philips.appinfra.securestorage.SecureStorage;

/**
 * Created by 310238655 on 4/29/2016.
 */
public class TaggingTest extends MockitoTestCase {

    private Context context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getContext();

        assertNotNull(context);
       
    }


}
