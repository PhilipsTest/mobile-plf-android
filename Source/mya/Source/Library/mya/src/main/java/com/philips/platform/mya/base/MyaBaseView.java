/*
 * Copyright (c) Koninklijke Philips N.V. 2017
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */
package com.philips.platform.mya.base;


import android.support.v4.app.FragmentActivity;

public interface MyaBaseView {
    FragmentActivity getFragmentActivity();
    boolean exitMyAccounts();
}
