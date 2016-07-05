package com.philips.platform.modularui.navigatorimpl;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.philips.platform.appframework.introscreen.IntroductionScreenActivity;
import com.philips.platform.modularui.statecontroller.UIBaseNavigator;

/**
 * Created by 310240027 on 7/4/2016.
 */
public class IntroductionScreenNavigator implements UIBaseNavigator {
    @Override
    public void loadActivity(Context context) {
        context.startActivity(new Intent(context, IntroductionScreenActivity.class));
    }

    @Override
    public Fragment loadFragment() {
        return null;
    }
}
