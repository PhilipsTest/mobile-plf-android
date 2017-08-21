/* Copyright (c) Koninklijke Philips N.V., 2017
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/

package com.philips.platform.baseapp.screens.introscreen.pager;

public class WelcomeVideoFragmentContract {

    interface View {
        void setVideoDataSource(String videoUrl);

        void onFetchError();
    }

    interface Presenter {
        void fetchVideoDataSource();
    }
}
