package com.philips.cdp.ui.catalog.dot_navigation;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.philips.cdp.ui.catalog.R;
import com.philips.cdp.ui.catalog.activity.CatalogActivity;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class DotNavigationActivity extends CatalogActivity {

    private ViewPagerAdaptor adaptor;
    private ViewPager pager;
    private PageIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dot_navigation);

        adaptor = new ViewPagerAdaptor(getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adaptor);

        indicator = (CirclePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

}