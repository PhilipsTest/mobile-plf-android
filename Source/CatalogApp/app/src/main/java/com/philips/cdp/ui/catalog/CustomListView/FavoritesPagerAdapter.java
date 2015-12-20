package com.philips.cdp.ui.catalog.CustomListView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.philips.cdp.ui.catalog.activity.AllItems;
import com.philips.cdp.ui.catalog.activity.Favorites;
import com.philips.cdp.ui.catalog.activity.TabFragmentListOption;
import com.philips.cdp.ui.catalog.activity.TabFragmentListProduct;
import com.philips.cdp.ui.catalog.activity.TabFragmentListWithoutIcon1;
import com.philips.cdp.ui.catalog.activity.TabFragmentListicon;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class FavoritesPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public FavoritesPagerAdapter(final FragmentManager fm) {
        super(fm);
    }


    public FavoritesPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(final int position) {
        switch (position) {
            case 0:
                AllItems tab1 = new AllItems();
                return tab1;
            case 1:
                Favorites tab2 = new Favorites();
                return tab2;
            default:
                AllItems tabdefault = new AllItems();
                return tabdefault;

        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
