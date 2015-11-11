package com.philips.cdp.ui.catalog.activity;

import android.app.FragmentManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.philips.cdp.ui.catalog.R;
import com.philips.cdp.ui.catalog.hamburgerfragments.HamburgerFragment;
import com.philips.cdp.uikit.com.philips.cdp.uikit.utils.HamburgerUtil;
import com.philips.cdp.uikit.com.philips.cdp.uikit.utils.OnDataNotified;
import com.philips.cdp.uikit.costumviews.PhilipsBadgeView;
import com.philips.cdp.uikit.costumviews.VectorDrawableImageView;
import com.philips.cdp.uikit.drawable.VectorDrawable;
import com.philips.cdp.uikit.hamburger.HamburgerItem;
import com.philips.cdp.uikit.hamburger.PhilipsExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class HamburgerMenuExpandableDemo extends CatalogActivity implements OnDataNotified {

    private String[] hamburgerMenuTitles;
    private TypedArray hamburgerMenuIcons;
    private List<String> listDataHeader;
    private HashMap<String, List<HamburgerItem>> listDataChild;
    private DrawerLayout philipsDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ExpandableListView drawerListView;
    private TextView actionBarTitle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private VectorDrawableImageView footerView;
    private HamburgerUtil hamburgerUtil;
    private PhilipsBadgeView actionBarCount;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uikit_hamburger_menu_expandable_basic);
        initViews();
        initActionBar(getSupportActionBar());
        philipsDrawerLayout = (DrawerLayout) findViewById(R.id.philips_drawer_layout);
        drawerListView = (ExpandableListView) findViewById(R.id.hamburger_list);
        loadSlideMenuItems();
        prepareListData();
        setHamburgerAdaptor();
        if (savedInstanceState == null) {
            displayView(0, 0);
        }

        drawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id) {
                displayView(groupPosition, childPosition);
                return false;
            }
        });
        configureDrawer();
        hamburgerUtil = new HamburgerUtil(this, drawerListView);
        hamburgerUtil.updateSmartFooter(footerView);
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        footerView = (VectorDrawableImageView) findViewById(R.id.philips_logo);
        philipsDrawerLayout = (DrawerLayout) findViewById(R.id.philips_drawer_layout);
        drawerListView = (ExpandableListView) findViewById(R.id.hamburger_list);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        setSupportActionBar(toolbar);
    }

    private void setHamburgerAdaptor() {
        final PhilipsExpandableListAdapter listAdapter = new PhilipsExpandableListAdapter(this, listDataHeader, listDataChild);
        drawerListView.setAdapter(listAdapter);
    }

    private void loadSlideMenuItems() {
        hamburgerMenuTitles = getResources().getStringArray(R.array.hamburger_drawer_items);
        hamburgerMenuIcons = getResources()
                .obtainTypedArray(R.array.hamburger_drawer_icons);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_reload:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayView(int groupPosition, final int childPosition) {
        final HamburgerFragment fragment = new HamburgerFragment();
            FragmentManager fragmentManager = getFragmentManager();
        List<HamburgerItem> child = listDataChild.get(listDataHeader.get(groupPosition));
        HamburgerItem hamburgerItem = child.get(childPosition);
        Bundle bundle = getBundle(hamburgerItem.getTitle(), hamburgerMenuIcons.getResourceId(groupPosition, -1));
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        setTitle(hamburgerItem.getTitle());
        philipsDrawerLayout.closeDrawer(navigationView);
    }

    @NonNull
    private Bundle getBundle(final String navMenuTitle, final int resourceId) {
        Bundle bundle = new Bundle();
        bundle.putString("data", navMenuTitle);
        bundle.putInt("resId", resourceId);
        return bundle;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataHeader.add("Title Long");
        listDataHeader.add("Title long");

        int feature = getIntent().getIntExtra("feature", -1);

        List<HamburgerItem> Title_Long = new ArrayList<>();
        List<HamburgerItem> Title_long = new ArrayList<>();
        if (feature == 1) {
            Title_Long.add(new HamburgerItem(hamburgerMenuTitles[0], 0));
            Title_Long.add(new HamburgerItem(hamburgerMenuTitles[1], 0));
            Title_Long.add(new HamburgerItem(hamburgerMenuTitles[2], 0));
            Title_Long.add(new HamburgerItem(hamburgerMenuTitles[3], 0));
            Title_Long.add(new HamburgerItem(hamburgerMenuTitles[4], 0));

            Title_long.add(new HamburgerItem(hamburgerMenuTitles[5], 0));
            Title_long.add(new HamburgerItem(hamburgerMenuTitles[6], 0));
        } else if (feature == 2) {
            Title_Long.add(new HamburgerItem(hamburgerMenuTitles[0], hamburgerMenuIcons.getResourceId(0, -1)));
            Title_Long.add(new HamburgerItem(hamburgerMenuTitles[1], hamburgerMenuIcons.getResourceId(1, -1)));
            Title_Long.add(new HamburgerItem(hamburgerMenuTitles[2], hamburgerMenuIcons.getResourceId(2, -1)));
            Title_Long.add(new HamburgerItem(hamburgerMenuTitles[3], hamburgerMenuIcons.getResourceId(3, -1)));
            Title_Long.add(new HamburgerItem(hamburgerMenuTitles[4], hamburgerMenuIcons.getResourceId(4, -1)));

            Title_long.add(new HamburgerItem(hamburgerMenuTitles[5], hamburgerMenuIcons.getResourceId(5, -1)));
            Title_long.add(new HamburgerItem(hamburgerMenuTitles[6], hamburgerMenuIcons.getResourceId(6, -1)));
        }

        listDataChild.put(listDataHeader.get(0), Title_Long);
        listDataChild.put(listDataHeader.get(1), Title_long);
    }

    private void configureDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, philipsDrawerLayout, com.philips.cdp.uikit.R.string.app_name, com.philips.cdp.uikit.R.string.app_name) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        philipsDrawerLayout.setDrawerListener(drawerToggle);
    }

    private void initActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(com.philips.cdp.uikit.R.layout.uikit_action_bar_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(VectorDrawable.create(this, com.philips.cdp.uikit.R.drawable.uikit_hamburger_icon));
        actionBarTitle = (TextView) actionBar.getCustomView().findViewById(com.philips.cdp.uikit.R.id.hamburger_title);
        actionBarCount = (PhilipsBadgeView) actionBar.getCustomView().findViewById(com.philips.cdp.uikit.R.id.hamburger_count);
        actionBarCount.setText("0");
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        actionBarTitle.setText(title);
    }

    @Override
    public void onDataSetChanged(String dataCount) {
        hamburgerUtil.updateSmartFooter(footerView);
    }
}
