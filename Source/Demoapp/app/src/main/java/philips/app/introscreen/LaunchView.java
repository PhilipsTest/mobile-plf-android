/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package philips.app.introscreen;


import philips.app.base.FragmentView;

public interface LaunchView extends FragmentView {

    void showActionBar();

    void hideActionBar();

    void finishActivityAffinity();
}
