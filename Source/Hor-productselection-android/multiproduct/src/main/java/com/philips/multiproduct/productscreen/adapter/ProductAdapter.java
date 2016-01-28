package com.philips.multiproduct.productscreen.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.philips.multiproduct.productscreen.DotNavigationTestFragment;

/**
 * This is the adapter class to keep all the image contents storing
 *
 * @author naveen@philips.com
 * @Date 28/01/2016
 */
public class ProductAdapter extends FragmentPagerAdapter {


    protected static final String[] CONTENT = new String[]{"Naveen", "Ritesha", "Krishna", "Sameera Reddy",};

    private int mCount = CONTENT.length;


    public ProductAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return DotNavigationTestFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ProductAdapter.CONTENT[position % CONTENT.length];
    }
}
