package com.philips.platform.csw.dialogs;

import android.view.View;

class MockOkayButtonListener implements View.OnClickListener {

    public boolean listenerInvoked;

    @Override
    public void onClick(View view) {
        listenerInvoked = true;
    }
}
