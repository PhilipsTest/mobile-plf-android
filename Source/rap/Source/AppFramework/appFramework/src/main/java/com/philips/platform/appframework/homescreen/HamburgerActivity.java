/*
 *  Copyright (c) Koninklijke Philips N.V., 2017
 *  All rights are reserved. Reproduction or dissemination
 *  in whole or in part is prohibited without the prior written
 *  consent of the copyright holder.
 */
package com.philips.platform.appframework.homescreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cdp.di.iap.screens.InAppBaseFragment;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.logout.URLogout;
import com.philips.platform.appframework.logout.URLogoutInterface;
import com.philips.platform.appframework.models.HamburgerMenuItem;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;
import com.philips.platform.appinfra.consentmanager.ConsentManagerInterface;
import com.philips.platform.appinfra.consentmanager.ConsentStatusChangedListener;
import com.philips.platform.baseapp.base.AbstractAppFrameworkBaseActivity;
import com.philips.platform.baseapp.base.AbstractUIBasePresenter;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.FragmentView;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.baseapp.screens.utility.IndexSelectionListener;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.baseapp.screens.utility.SharedPreferenceUtility;
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.themesettings.ThemeSelectionActivity;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ColorRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;
import com.philips.platform.uid.view.widget.SideBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.philips.platform.prdemoapp.activity.MainActivity.THEMESETTINGS_ACTIVITY_RESTART;

/**
 * This is the Main activity which host the main hamburger menu
 * This activity is the container of all the other fragment for the app
 * ActionbarListener is implemented by this activty and all the logic related to handleBack handling and actionar is contained in this activity
 */
public class HamburgerActivity extends AbstractAppFrameworkBaseActivity implements IndexSelectionListener, FragmentManager.OnBackStackChangedListener, FragmentView, HamburgerMenuItemClickListener, View.OnClickListener, URLogoutInterface.URLogoutListener {
    private static final long serialVersionUID = -8631395631531369815L;
    private static String TAG = HamburgerActivity.class.getSimpleName();
    private String[] hamburgerMenuTitles;
    private LinearLayout navigationView;
    private Toolbar toolbar;
    private int selectedIndex = 0;

    private SharedPreferenceUtility sharedPreferenceUtility;
    private boolean isBackButtonVisible = false;
    Handler handler = new Handler();
    private SideBar sideBar;
    private ActionBarDrawerToggle drawerToggle;
    private int START_THEME_SELECTOR = 101;

    private URLogoutInterface urLogoutInterface;

    @BindView(R.id.rap_avatar_name)
    Label avatarName;

    @BindView(R.id.rap_env_name)
    Label envInfo;


    @BindView(R.id.hamburger_menu_header_container)
    LinearLayout hamburgerHeaderParent;

    @BindView(R.id.hamburger_list)
    RecyclerView hamburgerMenuRecyclerView;

    private ImageView cartIcon;
    private TextView cartCount;
    private FrameLayout shoppingCartLayout;

    private HamburgerMenuAdapter hamburgerMenuAdapter;

    private static final String NAVIGATION_CONTENT_DESC_HAMBURGER = "hamburger";

    private static final String NAVIGATION_CONTENT_DESC_BACK = "back";

    private ConsentStatusChangedListener clickStreamStatusChanegListener = this::onClickStreamConsentToggled;

    /**
     * For instantiating the view and actionabar and hamburger menu initialization
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RALog.d(TAG, " OnCreate ");
        /*
         * Setting Philips UI KIT standard BLUE theme.
         */
        super.onCreate(savedInstanceState);
        RALog.d(TAG, "App initialization status:" + AppFrameworkApplication.isAppDataInitialized());
        if (!(savedInstanceState != null && !AppFrameworkApplication.isAppDataInitialized())) {
            presenter = getActivityPresenter();
            sharedPreferenceUtility = new SharedPreferenceUtility(this);
            setContentView(R.layout.af_uid_hamburger_menu);
            ButterKnife.bind(this);
            initializeActivityContents();
            SharedPreferenceUtility sharedPreferenceUtility = new SharedPreferenceUtility(getFragmentActivity().getApplicationContext());
            boolean isTHSDeeplinkingFlow = sharedPreferenceUtility.getPreferenceBoolean(Constants.THS_DEEP_LINK_FLOW);
            if (isTHSDeeplinkingFlow) {
                sharedPreferenceUtility.writePreferenceBoolean(Constants.THS_DEEP_LINK_FLOW, false);
                presenter.onEvent(Constants.THS_DEEP_LINKING_EVENT_ID); // THS deep linking flow
            }
            registerClickStreamStatusChanges();
        }

    }

    private void registerClickStreamStatusChanges() {
        AppInfraInterface appInfra = ((AppFrameworkApplication) getApplicationContext()).getAppInfra();
        ConsentManagerInterface consentManager = appInfra.getConsentManager();
        if (consentManager != null) {
            ConsentDefinition clickStreamDefinition = consentManager.getConsentDefinitionForType(appInfra.getTagging().getClickStreamConsentIdentifier());
            if (clickStreamDefinition != null) {
                consentManager.addConsentStatusChangedListener(clickStreamDefinition, clickStreamStatusChanegListener);
            }
        }
    }

    private void onClickStreamConsentToggled(ConsentDefinition consentDefinition, ConsentError consentError, boolean status) {
        String type = consentDefinition.getTypes().get(0);
        String message = consentError == null ? type + "Consent changed to " + status : type + "Consent change request to " + status + "failed with error " + consentError.getError();
        RALog.d(TAG, "onClickStreamConsentToggled: " + message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.catalog_view_menu, menu);

        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.badge).getActionView();
        cartCount = badgeLayout.findViewById(R.id.item_count);
        shoppingCartLayout = badgeLayout.findViewById(R.id.cart_container);
        cartIcon = badgeLayout.findViewById(R.id.cart_icon);

        cartCount.setVisibility(View.GONE);
        cartIcon.setVisibility(View.GONE);

        shoppingCartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEvent(Constants.SHOPPING_CART_CLICK);
            }
        });

        super.onCreateOptionsMenu(menu);
        return true;
    }

    protected AbstractUIBasePresenter getActivityPresenter() {
        return new HamburgerActivityPresenter(this);
    }

    protected void initializeActivityContents() {
        initViews();

        urLogoutInterface = getURLogoutInterface();
        urLogoutInterface.setUrLogoutListener(this);

        presenter.onEvent(0);
        initLeftSidebarRecyclerViews();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    protected URLogoutInterface getURLogoutInterface() {
        return new URLogout();
    }

    private void initLeftSidebarRecyclerViews() {
        RecyclerViewSeparatorItemDecoration hamburgerSeparatorItemDecoration = new RecyclerViewSeparatorItemDecoration(UIDHelper.getContentThemedContext(this));
        ArrayList<HamburgerMenuItem> hamburgerMenuItems = getIconDataHolderView(UIDHelper.getContentThemedContext(this));

        hamburgerMenuAdapter = new HamburgerMenuAdapter(hamburgerMenuItems);
        hamburgerMenuAdapter.setMenuItemClickListener(this);
        hamburgerMenuRecyclerView.setAdapter(hamburgerMenuAdapter);
        hamburgerMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        hamburgerMenuRecyclerView.addItemDecoration(hamburgerSeparatorItemDecoration);
    }

    @NonNull
    private ArrayList<HamburgerMenuItem> getIconDataHolderView(Context context) {
        hamburgerMenuTitles = getResources().getStringArray(R.array.hamburger_drawer_items);
        ArrayList<HamburgerMenuItem> hamburgerMenuItems = new ArrayList<HamburgerMenuItem>();
        TypedArray typedArray = getResources().obtainTypedArray(R.array.hamburger_drawer_items_res);
        for (int i = 0; i < hamburgerMenuTitles.length; i++) {
            hamburgerMenuItems.add(new HamburgerMenuItem(typedArray.getResourceId(i, R.drawable.rap_question_mark), hamburgerMenuTitles[i], context));
        }
        return hamburgerMenuItems;
    }

    @Override
    public void onMenuItemClicked(int position) {
        if (position == selectedIndex) {
            sideBar.closeDrawer(navigationView);
            return;
        }
        selectedIndex = position;
        sharedPreferenceUtility.writePreferenceInt(Constants.HOME_FRAGMENT_PRESSED, position);
        showNavigationDrawerItem(position);
    }

    /**
     * To show navigation Drawer
     *
     * @param position : Pass the position of hamburger item to be shown
     */
    private void showNavigationDrawerItem(int position) {
        sideBar.closeDrawer(navigationView);
        presenter.onEvent(position);
    }

    public HamburgerMenuAdapter getHamburgerAdapter() {
        return this.hamburgerMenuAdapter;
    }

    private void initViews() {
        RALog.d(TAG, " initViews");
        toolbar = (Toolbar) findViewById(R.id.uid_toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        hamburgerHeaderParent.setOnClickListener(this);
        setUserNameAndLogoutText();
        initSidebarComponents();
        navigationView = (LinearLayout) findViewById(R.id.navigation_view);
        UIDHelper.setupToolbar(this);
        toolbar.setNavigationIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_hamburger_icon, getTheme()));
        toolbar.setNavigationContentDescription(NAVIGATION_CONTENT_DESC_HAMBURGER);
    }

    public void setUserNameAndLogoutText() {
        UserDataInterface userDataInterface = ((AppFrameworkApplication)getApplicationContext()).getUserRegistrationState().getUserDataInterface();
        if (userDataInterface.getUserLoggedInState() != UserLoggedInState.USER_LOGGED_IN && userDataInterface.getUserLoggedInState() != UserLoggedInState.PENDING_HSDP_LOGIN) {
            avatarName.setText(getString(R.string.RA_DLSS_avatar_default_text));
        } else {
            AppIdentityInterface.AppState appState = ((AppFrameworkApplication) getApplicationContext()).getAppState();
            ArrayList<String> detailskey = new ArrayList<>();
            detailskey.add(UserDetailConstants.GIVEN_NAME);
            try {
                avatarName.setText(userDataInterface.getUserDetails(detailskey).get(UserDetailConstants.GIVEN_NAME).toString());
            } catch (UserDataInterfaceException e) {
                RALog.e(TAG,"Error in set avatarName : "+e.getMessage());
            }
            if (!appState.name().equalsIgnoreCase(AppIdentityInterface.AppState.STAGING.name()))
                envInfo.setText(appState.name());

        }
    }

    private void initSidebarComponents() {
        sideBar = (SideBar) findViewById(R.id.sidebar_layout);
        RALog.d(TAG, "initSidebarComponents : drawer");
        drawerToggle = configureDrawer();
        drawerToggle.setDrawerIndicatorEnabled(false);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        UIDHelper.setTitle(this, title);
    }

    protected ActionBarDrawerToggle configureDrawer() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, sideBar,
                R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                setUserNameAndLogoutText();
            }
        };
        sideBar.addDrawerListener(drawerToggle);
        return drawerToggle;
    }


    @Override
    public void onBackPressed() {
        RALog.d(TAG, " on Back Pressed  ");
        if (sideBar.isDrawerOpen(navigationView)) {
            sideBar.closeDrawer(navigationView);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFrag = fragmentManager.findFragmentById(R.id.frame_container);
            boolean backState = false;
            if (fragmentManager.getBackStackEntryCount() == 1) {
                finishAffinity();
            } else if (currentFrag instanceof BackEventListener) {
                backState = ((BackEventListener) currentFrag).handleBackEvent();
                if (!backState) {

                    if (fragmentManager.getBackStackEntryCount() == 2) {
                        updateSelectionIndex(0);
                    }
                    super.onBackPressed();
                }
            } else {
                if (fragmentManager.getBackStackEntryCount() == 2) {
                    updateSelectionIndex(0);
                }

                super.onBackPressed();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getFragmentActivity().getWindow().getDecorView().requestLayout();
    }

    @Override
    protected void onDestroy() {
        RALog.d(TAG, " onDestroy ");
        removeClickStreamStatusChanges();
        super.onDestroy();
        removeListeners();
    }

    private void removeClickStreamStatusChanges() {
        AppInfraInterface appInfra = ((AppFrameworkApplication) getApplicationContext()).getAppInfra();
        if(appInfra != null) {
            ConsentManagerInterface consentManager = appInfra.getConsentManager();
            if (consentManager != null) {
                ConsentDefinition clickStreamDefinition = consentManager.getConsentDefinitionForType(appInfra.getTagging().getClickStreamConsentIdentifier());
                if (clickStreamDefinition != null) {
                    consentManager.removeConsentStatusChangedListener(clickStreamDefinition, clickStreamStatusChanegListener);
                }
            }
        }
    }

    protected void removeListeners() {
        if (hamburgerMenuAdapter != null) {
            hamburgerMenuAdapter.removeMenuItemClickListener();
        }
        if (urLogoutInterface != null) {
            urLogoutInterface.removeListener();
        }
    }


    /**
     * For Updating the actionbar title as coming from other components
     *
     * @param i String res ID
     * @param b Whether handleBack is handled by them or not
     */
    @Override
    public void updateActionBar(@StringRes int i, boolean b) {
        UIDHelper.setTitle(this, i);
        updateActionBarIcon(b);
    }

    /**
     * For Updating the actionbar title as coming from other components
     *
     * @param s String to be updated on actionbar title
     * @param b Whether handleBack is handled by them or not
     */
    @Override
    public void updateActionBar(String s, boolean b) {
        UIDHelper.setTitle(this, s);
        updateActionBarIcon(b);
    }

    public String getActionbarTag() {
        return (String) toolbar.getNavigationContentDescription();
    }

    /**
     * Method for showing the hamburger Icon or Back key on home fragments
     */
    public void updateActionBarIcon(boolean isBackButtonVisible) {
        RALog.d(TAG, " updateActionBarIcon : " + isBackButtonVisible);
        int navigationDrawableId = isBackButtonVisible ? R.drawable.back_arrow : R.drawable.ic_hamburger_icon;
        toolbar.setNavigationIcon(VectorDrawableCompat.create(getResources(), navigationDrawableId, getTheme()));
        toolbar.setNavigationContentDescription(isBackButtonVisible ? NAVIGATION_CONTENT_DESC_BACK : NAVIGATION_CONTENT_DESC_HAMBURGER);
        this.isBackButtonVisible = isBackButtonVisible;
        cartIconVisibility(0);
    }

    public boolean isIAPInstance() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
            String str = backEntry.getName();
            if (null != str) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(str);
                if (fragment instanceof InAppBaseFragment) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public void cartIconVisibility(int count) {

        try {
            final boolean isShopingCartVisible = ((AppFrameworkApplication) getApplication()).isShopingCartVisible;
            if (!isShopingCartVisible) {
                cartIcon.setVisibility(View.GONE);
                cartCount.setVisibility(View.GONE);
                shoppingCartLayout.setVisibility(View.GONE);
                return;
            }

            if (isIAPInstance()) {
                cartIcon.setVisibility(View.VISIBLE);
                int cartItemsCount = count;
                if (cartItemsCount > 0) {
                    cartCount.setVisibility(View.VISIBLE);
                    cartCount.setText(String.valueOf(cartItemsCount));
                    shoppingCartLayout.setVisibility(View.VISIBLE);
                } else {
                    cartCount.setVisibility(View.GONE);
                }
            } else {
                cartIcon.setVisibility(View.GONE);
                cartCount.setVisibility(View.GONE);
                shoppingCartLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }

    /*private void showToast(int errorCode) {
        String errorText = getResources().getString(R.string.af_iap_server_error);
        if (IAPConstant.IAP_ERROR_NO_CONNECTION == errorCode) {
            errorText = getResources().getString(R.string.af_iap_no_connection);
        } else if (IAPConstant.IAP_ERROR_CONNECTION_TIME_OUT == errorCode) {
            errorText = getResources().getString(R.string.af_iap_connection_time_out);
        } else if (IAPConstant.IAP_ERROR_AUTHENTICATION_FAILURE == errorCode) {
            errorText = getResources().getString(R.string.af_iap_authentication_failure);
        } else if (IAPConstant.IAP_ERROR_INSUFFICIENT_STOCK_ERROR == errorCode) {
            errorText = getResources().getString(R.string.af_iap_prod_out_of_stock);
        }
        Toast toast = Toast.makeText(this, errorText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }*/
    @Override
    public void onBackStackChanged() {
        /*if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
            String str = backEntry.getName();
            if(null != str){
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(str);
                if(fragment instanceof InAppBaseFragment){
                    cartIconVisibility(isCartVisible);
                }
                else {
                    cartIconVisibility(true);
                }
            }
        }*/
    }

    @Override
    public ActionBarListener getActionBarListener() {
        return this;
    }

    @Override
    public int getContainerId() {
        return R.id.frame_container;
    }

    @Override
    public FragmentActivity getFragmentActivity() {
        return this;
    }


    @Override
    public void updateSelectionIndex(final int position) {
        RALog.d(TAG, " setting selection index to 0  hamburger menu ");

        if (handler != null)
            handler.post(new Runnable() {
                @Override
                public void run() {
                    hamburgerMenuAdapter.setSelectedPosition(position);
                    selectedIndex = position;
                }
            });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isBackButtonVisible) {
                    onBackPressed();
                } else {
                    sideBar.openDrawer(navigationView);
                }
                break;

            case R.id.menu_theme_settings:
                Intent intent = new Intent(this, ThemeSelectionActivity.class);
                startActivityForResult(intent, START_THEME_SELECTOR);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == START_THEME_SELECTOR) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                colorRange = (ColorRange) data.getSerializableExtra("CLR");
                navigationColor = (NavigationColor) data.getSerializableExtra("NR");
                contentColor = (ContentColor) data.getSerializableExtra("CR");
                accentColorRange = (AccentRange) data.getSerializableExtra("AR");
            }
            restartActivity();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        sideBar.closeDrawer(navigationView);
        switch (view.getId()) {
            case R.id.hamburger_menu_header_container:
                if (((AppFrameworkApplication) getApplicationContext()).getUserRegistrationState().getUserDataInterface().getUserLoggedInState().ordinal() >= UserLoggedInState.PENDING_HSDP_LOGIN.ordinal()) {
                    selectedIndex = Constants.HAMBURGER_MY_ACCOUNT_CLICK;
                    hamburgerMenuAdapter.setSelectedPosition(Constants.HAMBURGER_MY_ACCOUNT_CLICK);
                    presenter.onEvent(Constants.HAMBURGER_MY_ACCOUNT_CLICK);
                } else {
                    selectedIndex = Constants.LOGIN_BUTTON_CLICK_CONSTANT;
                    hamburgerMenuAdapter.setSelectedPosition(Constants.LOGIN_BUTTON_CLICK_CONSTANT);
                    presenter.onEvent(Constants.LOGIN_BUTTON_CLICK_CONSTANT);
                }
                break;
        }
    }

    @Override
    public void onLogoutResultFailure(int i, String errorMessage) {
        RALog.d(TAG, " UserRegistration onLogoutFailure  - " + errorMessage);
        Toast.makeText(HamburgerActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        hideProgressBar();

    }

    @Override
    public void onLogoutResultSuccess() {
        RALog.d(TAG, " UserRegistration onLogoutSuccess  - ");
        setUserNameAndLogoutText();
        hideProgressBar();
        presenter.onEvent(Constants.LOGOUT_BUTTON_CLICK_CONSTANT);
        updateSelectionIndex(0);
    }


    @Override
    public void onNetworkError(String errorMessage) {
        RALog.d(TAG, " UserRegistration onNetworkError  - " + errorMessage);
        Toast.makeText(HamburgerActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        hideProgressBar();
    }

    private void restartActivity() {
        Intent intent = new Intent(this, HamburgerActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(THEMESETTINGS_ACTIVITY_RESTART, true);
        startActivity(intent);
    }
}
