/*
 * SupportHomeFragment is the first screen of Support app. This class will give
 * all the possible options to navigate within digital support app.
 *
 * @author : Ritesh.jha@philips.com
 * @creation Date : 5 Dec 2014
 * <p/>
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.homefragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.philips.cdp.digitalcare.ConsumerProductInfo;
import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.contactus.fragments.ContactUsFragment;
import com.philips.cdp.digitalcare.faq.fragments.FaqListFragment;
import com.philips.cdp.digitalcare.fragments.rateandreview.RateThisAppFragment;
import com.philips.cdp.digitalcare.listeners.PrxFaqCallback;
import com.philips.cdp.digitalcare.listeners.PrxSummaryListener;
import com.philips.cdp.digitalcare.productdetails.ProductDetailsFragment;
import com.philips.cdp.digitalcare.productdetails.model.ViewProductDetailsModel;
import com.philips.cdp.digitalcare.prx.PrxWrapper;
import com.philips.cdp.digitalcare.prx.subcategorymodel.SubcategoryModel;
import com.philips.cdp.digitalcare.request.RequestData;
import com.philips.cdp.digitalcare.request.ResponseCallback;
import com.philips.cdp.digitalcare.util.CommonRecyclerViewAdapter;
import com.philips.cdp.digitalcare.util.DigiCareLogger;
import com.philips.cdp.digitalcare.util.DigitalCareConstants;
import com.philips.cdp.digitalcare.util.MenuItem;
import com.philips.cdp.digitalcare.util.Utils;
import com.philips.cdp.digitalcare.view.ProgressAlertDialog;
import com.philips.cdp.productselection.ProductModelSelectionHelper;
import com.philips.cdp.productselection.listeners.ProductSelectionListener;
import com.philips.cdp.productselection.productselectiontype.ProductModelSelectionType;
import com.philips.cdp.prxclient.datamodels.summary.Data;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.cdp.prxclient.datamodels.support.SupportModel;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;
import com.shamanland.fonticon.FontIconTypefaceHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * The main feature enable screen opens once the ConsumerCare Component is triggered.
 */
@SuppressWarnings("serial")
public class SupportHomeFragment extends DigitalCareBaseFragment implements PrxSummaryListener {

    private static final String TAG = SupportHomeFragment.class.getSimpleName();
    private static final String USER_SELECTED_PRODUCT_CTN = "mCtnFromPreference";
    private static final String DCC_SAVED_COUNTRY = "mCountryPreference";
    private static final String USER_PREFERENCE = "user_product";
    private static final String USER_SELECTED_PRODUCT_CTN_CALL = "contact_call";
    private static final String USER_SELECTED_PRODUCT_CTN_HOURS = "contact_hours";
    private static boolean isSupportScreenLaunched;
    private SharedPreferences prefs = null;
    private LinearLayout mOptionParent = null;
    private RecyclerView mOptionContainer = null;
    private LinearLayout.LayoutParams mParams = null;
    private boolean mIsFirstScreenLaunch = false;
    private boolean mSupportButtonClickable = true;
    private CommonRecyclerViewAdapter<MenuItem> mAdapter;
    private ProgressAlertDialog mProgressDialog = null;
    protected ResponseCallback categoryResponseCallbak = new ResponseCallback() {
        @Override
        public void onResponseReceived(String response) {
            if (getActivity() != null) {
                SubcategoryModel subcategoryModel = new Gson().fromJson(response,
                        SubcategoryModel.class);
                if (subcategoryModel != null && subcategoryModel.getSuccess() != null && subcategoryModel.getSuccess()) {
                    com.philips.cdp.digitalcare.prx.subcategorymodel.Data data =
                            subcategoryModel.getData();
                    if ((data != null) && (data.getCode() != null)) {
                        DigitalCareConfigManager digitalCareConfigManager =
                                DigitalCareConfigManager.getInstance();
                        ConsumerProductInfo consumerProductInfo = digitalCareConfigManager.
                                getConsumerProductInfo();
                        consumerProductInfo.setCategory(data.getCode());
                        digitalCareConfigManager.setConsumerProductInfo(consumerProductInfo);
                    }
                }

                if (mProgressDialog != null && mProgressDialog.isShowing() &&
                        !getActivity().isFinishing()) {
                    try {
                        mProgressDialog.cancel();
                        mProgressDialog = null;
                    } catch (IllegalArgumentException e) {
                        DigiCareLogger.e(TAG, "Progress Dialog got IllegalArgumentException - ResponseCallback");
                    }
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSupportScreenLaunched = true;
        FontIconTypefaceHolder.init(getActivity().getAssets(), "fonts/iconfont.ttf");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DigiCareLogger.v(TAG, "SupportScreen Launched");
        View mView = inflater.inflate(R.layout.consumercare_fragment_support, container,
                false);
        mIsFirstScreenLaunch = true;


        prefs = getActivity().getSharedPreferences(
                USER_PREFERENCE, Context.MODE_PRIVATE);

        initializeCountry();

        if(DigitalCareConfigManager.getInstance().getConsumerProductInfo() == null){
            ConsumerProductInfo mProductInfo = new ConsumerProductInfo();
            DigitalCareConfigManager.getInstance().setConsumerProductInfo(mProductInfo);
        }
        updateConsumerProductInfo();



        if (mIsFirstScreenLaunch || DigitalCareConfigManager.getInstance().
                getProductModelSelectionType().getHardCodedProductList().length < 2) {
            synchronized (this) {
                if (DigitalCareConfigManager.getInstance().
                        getProductModelSelectionType().getHardCodedProductList().length == 1) {
                    ProductModelSelectionType modelSelectionType =
                            DigitalCareConfigManager.getInstance().
                                    getProductModelSelectionType();
                    DigitalCareConfigManager.getInstance().getConsumerProductInfo().
                            setCtn(modelSelectionType.getHardCodedProductList()[0]);
                    DigitalCareConfigManager.getInstance().getConsumerProductInfo().
                            setSector(modelSelectionType.getSector().toString());
                    DigitalCareConfigManager.getInstance().getConsumerProductInfo().
                            setCatalog(modelSelectionType.getCatalog().toString());
                }

                DigiCareLogger.v(TAG, "Sending PRX Request");
                PrxWrapper mPrxWrapper = new PrxWrapper(getActivity(), this);
                mPrxWrapper.executeRequests();

            }
        } else {
            createMainMenu();
        }
        return mView;
    }

    private void updateConsumerProductInfo() {
        if (DigitalCareConfigManager.getInstance().getProductModelSelectionType() != null) {
            DigitalCareConfigManager.getInstance().getConsumerProductInfo().setSector
                    (DigitalCareConfigManager.getInstance().getProductModelSelectionType().
                            getSector().toString());
            DigitalCareConfigManager.getInstance().getConsumerProductInfo().setCatalog
                    (DigitalCareConfigManager.getInstance().getProductModelSelectionType().
                            getCatalog().toString());
        }

        String mCtnFromPreference = prefs.getString(USER_SELECTED_PRODUCT_CTN, "");

        if (!mCtnFromPreference.equals(""))
            DigitalCareConfigManager.getInstance().getConsumerProductInfo().
                    setCtn(mCtnFromPreference);
    }

    private boolean isProductSelected() {
        String ctn = prefs.getString(USER_SELECTED_PRODUCT_CTN, "");
        DigiCareLogger.i(TAG, "isProductSelected ?" + ctn);
        return !(ctn != null && !ctn.isEmpty());
    }

    private void enableSupportButtonClickable() {
        mSupportButtonClickable = true;
    }

    private void disableSupportButtonClickable() {
        mSupportButtonClickable = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mOptionParent = getActivity().findViewById(
                R.id.optionParent);
        mOptionContainer = getActivity().findViewById(
                R.id.supportMenuContainer);
        mParams = (LinearLayout.LayoutParams) mOptionParent.getLayoutParams();

        Configuration config = getResources().getConfiguration();
        setViewParams(config);

        if (!(mIsFirstScreenLaunch)) {
            createMainMenu();
        }
        try {
            if (DigitalCareConfigManager.getInstance().getPreviousPageNameForTagging() != null
                    && mIsFirstScreenLaunch) {
                DigitalCareConfigManager.getInstance().getTaggingInterface().trackPageWithInfo
                        (AnalyticsConstants.PAGE_HOME,
                                DigitalCareConfigManager.
                                        getInstance().getPreviousPageNameForTagging(),
                                DigitalCareConfigManager.getInstance().
                                        getPreviousPageNameForTagging());
                mIsFirstScreenLaunch = false;
            } else {
                DigitalCareConfigManager.getInstance().getTaggingInterface().trackPageWithInfo
                        (AnalyticsConstants.PAGE_HOME,
                                getPreviousName(), getPreviousName());
            }
        } catch (Exception e) {
            DigiCareLogger.e(TAG, "LocaleMatch Crash Controlled : " + e);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);

        setViewParams(config);
    }

    public void setViewParams(Configuration config) {
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mParams.leftMargin = mParams.rightMargin = mLeftRightMarginPort;

        } else {
            mParams.leftMargin = mParams.rightMargin = mLeftRightMarginLand;
        }
        mOptionParent.setLayoutParams(mParams);
    }


    private void launchProductSelectionComponent() {
        DigitalCareConfigManager digitalCareConfigManager = DigitalCareConfigManager.getInstance();

        if (digitalCareConfigManager.getUiLauncher() instanceof ActivityLauncher) {
            DigiCareLogger.i(TAG, "Launching the ProductSelection as Activity");
            launchProductSelectionActivityComponent();
        } else if (digitalCareConfigManager.getUiLauncher() instanceof FragmentLauncher) {
            DigiCareLogger.i(TAG, "Launching ProductSelection as Fragment");
            launchProductSelectionFragmentComponent();
        }
    }

    protected boolean isContactNumberCached() {
        String customerSupportNumber;
        customerSupportNumber = prefs.getString(USER_SELECTED_PRODUCT_CTN_CALL, "");
        return (!customerSupportNumber.equals(""));
    }


    protected boolean isContactHoursCached() {
        String contactHours;
        contactHours = prefs.getString(USER_SELECTED_PRODUCT_CTN_HOURS, "");
        return (!contactHours.equals(""));
    }


    @Override
    public void onClick(View view) {
        if (!mSupportButtonClickable) {
            return;
        }

        String tag = (String) view.getTag();

        boolean actionTaken = false;
        if (DigitalCareConfigManager.getInstance()
                .getCcListener() != null) {
            actionTaken = DigitalCareConfigManager.getInstance()
                    .getCcListener().onMainMenuItemClicked(tag);
        }

        if (actionTaken) {
            return;
        }

        if (tag.equals(getStringKey(R.string.contact_us))) {
            DigiCareLogger.i(TAG, "Clicked on ContactUs button");
            if (isProductSelected() && isSupportScreenLaunched) {
                if (isConnectionAvailable()) {
                    disableSupportButtonClickable();
                    launchProductSelectionComponent();
                }
            } else {

                if (isInternetAvailable) {
                    showFragment(new ContactUsFragment());
                } else if (isContactHoursCached() || isContactNumberCached()) {
                    showFragment(new ContactUsFragment());
                } else isConnectionAlertDisplayed();
            }
        } else if (tag.equals(getStringKey(R.string.product_info))) {
            DigiCareLogger.i(TAG, "Clicked on View Product Details button");
            if (isConnectionAvailable())
                if (isProductSelected() && isSupportScreenLaunched) {
                    disableSupportButtonClickable();
                    launchProductSelectionComponent();
                } else
                    showFragment(new ProductDetailsFragment());
        }  else if (tag.equals(getStringKey(R.string.FAQ_KEY))) {
            DigiCareLogger.i(TAG, "Clicked on ReadFaq button");
            if (isConnectionAvailable())
                if (isProductSelected() && isSupportScreenLaunched) {
                    disableSupportButtonClickable();
                    launchProductSelectionComponent();
                } else
                    launchFaqScreen();
        } else if (tag.equals(getStringKey(R.string.dcc_tellUs_header))) {
            DigiCareLogger.i(TAG, "Clicked on TellUs what you think button");
            if (isConnectionAvailable())
                if (isProductSelected() && isSupportScreenLaunched) {
                    disableSupportButtonClickable();
                    launchProductSelectionComponent();
                } else
                    showFragment(new RateThisAppFragment());
        } else if (tag.equals(getStringKey(R.string.Change_Selected_Product))) {
            DigiCareLogger.i(TAG, "Clicked on Change Selected Product Button");
            if (isConnectionAvailable()) {
                disableSupportButtonClickable();
                DigitalCareConfigManager digitalCareConfigManager =
                        DigitalCareConfigManager.getInstance();

                if (digitalCareConfigManager.getUiLauncher() instanceof ActivityLauncher) {
                    launchProductSelectionActivityComponent();
                } else if (digitalCareConfigManager.getUiLauncher() instanceof FragmentLauncher) {
                    Configuration configuration = getResources().getConfiguration();
                    ProductModelSelectionHelper.getInstance().setCurrentOrientation(configuration);
                    launchProductSelectionFragmentComponent();
                }
            }
        }
    }

    private void launchFaqScreen() {

        DigiCareLogger.i(TAG, "Requesting the Su");
        PrxWrapper mPrxWrapper = new PrxWrapper(getActivity(), new PrxFaqCallback() {
            @Override
            public void onResponseReceived(SupportModel supportModel) {
                if (supportModel == null && getActivity() != null) {
                    showAlert(getString(R.string.NO_SUPPORT_KEY));
                } else {

                    if (isAdded()) {
                        FaqListFragment faqFragment = new FaqListFragment(getActivity());
                        faqFragment.setSupportModel(supportModel);
                        showFragment(faqFragment);
                    }
                }
            }
        });
        mPrxWrapper.executeFaqSupportRequest();
    }

    private void launchProductSelectionFragmentComponent() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressAlertDialog(getActivity(), R.style.loaderTheme);
        }

        mProgressDialog.setCancelable(false);
        if (!(getActivity().isFinishing()) && isVisible()) {
            mProgressDialog.show();
        }

        final FragmentLauncher fragmentLauncher = (FragmentLauncher) DigitalCareConfigManager.
                getInstance().getUiLauncher();

        /*Initialize product selection tagging*/
        DigitalCareConfigManager ccConfigManager = DigitalCareConfigManager.getInstance();
        AppTaggingInterface aiAppTaggingInterface = ProductModelSelectionHelper.getInstance().
                getAPPInfraInstance().getTagging();
        aiAppTaggingInterface.setPreviousPage(ccConfigManager.getPreviousPageNameForTagging());

        ProductModelSelectionHelper.getInstance().
                setProductSelectionListener(new ProductSelectionListener() {
                    @Override
                    public void onProductModelSelected(SummaryModel summaryModel) {
                        isSupportScreenLaunched = false;
                        if (summaryModel != null) {
                            enableSupportButtonClickable();
                            updateSummaryData(summaryModel);
                        } else {
                            if (!getActivity().isFinishing()) showAlert(getString(R.string.
                                    NO_PRODUCT_KEY));
                            disablePrxDependentButtons();
                            enableSupportButtonClickable();
                            disableProgressDialog();
                        }
                    }
                });
        // to resolve progress dialog now showing,
        // run invoke product selection in another thread to avoid blocking UI thread,
        new Thread() {
            @Override
            public void run() {
                ProductModelSelectionHelper.getInstance().invokeProductSelection(fragmentLauncher,
                        DigitalCareConfigManager.getInstance().getProductModelSelectionType());
            }
        }.start();
    }

    private void launchProductSelectionActivityComponent() {

        DigiCareLogger.d(TAG, "Launching ProductSelection as Activity Instance");

        if (mProgressDialog == null) mProgressDialog = new ProgressAlertDialog
                (getActivity(), R.style.loaderTheme);

        mProgressDialog.setCancelable(false);
        if (!(getActivity().isFinishing()) && isVisible()) {
            mProgressDialog.show();
        }

        /*Initialize product selection tagging*/
        DigitalCareConfigManager ccConfigManager = DigitalCareConfigManager.getInstance();
        /*AppInfraSingleton.setInstance(new AppInfra.Builder().build(getActivity()));*/
        AppTaggingInterface aiAppTaggingInterface = ProductModelSelectionHelper.getInstance().
                getAPPInfraInstance().getTagging();
        aiAppTaggingInterface.setPreviousPage(ccConfigManager.getPreviousPageNameForTagging());

        ActivityLauncher uiLauncher = (ActivityLauncher) DigitalCareConfigManager.getInstance().
                getUiLauncher();
        uiLauncher = new ActivityLauncher(getActivity(),uiLauncher.getScreenOrientation(), uiLauncher.getDlsThemeConfiguration(), uiLauncher.getUiKitTheme(), null);
        uiLauncher.setCustomAnimation(DigitalCareConfigManager.getInstance().getUiLauncher().
                        getEnterAnimation(),
                DigitalCareConfigManager.getInstance().getUiLauncher().getExitAnimation());
        ProductModelSelectionHelper.getInstance().
                setProductSelectionListener(new ProductSelectionListener() {
                    @Override
                    public void onProductModelSelected(SummaryModel summaryModel) {
                        isSupportScreenLaunched = false;
                        if (summaryModel != null) {
                            enableSupportButtonClickable();
                            updateSummaryData(summaryModel);
                        } else {
                            if (!getActivity().isFinishing())
                                showAlert(getString(R.string.NO_PRODUCT_KEY));
                            disablePrxDependentButtons();
                            enableSupportButtonClickable();
                            disableProgressDialog();
                        }
                    }
                });

        // to resolve progress dialog now showing,
        // run invoke product selection in another thread to avoid blocking UI thread,
        final ActivityLauncher finalUiLauncher = uiLauncher;
        new Thread() {
            @Override
            public void run() {
                ProductModelSelectionHelper.getInstance().invokeProductSelection(finalUiLauncher,
                        DigitalCareConfigManager.getInstance().getProductModelSelectionType());
            }
        }.start();
    }

    private void disableChangeProductButton() {
        DigiCareLogger.i(TAG, "Removing the PRX dependent Buttons from the SupportScreen");
        ArrayList<Integer> disabledButtons = new ArrayList<>();
        disabledButtons.add(R.string.Change_Selected_Product);

        if (!(DigitalCareConfigManager.getInstance().getProductModelSelectionType().
                getHardCodedProductList().length < 2)) {
            disabledButtons.remove(Integer.valueOf(R.string.Change_Selected_Product));
        }
        updateMenus(disabledButtons);
    }


    private void disablePrxDependentButtons() {
        DigiCareLogger.i(TAG, "Removing the PRX dependent Buttons from the SupportScreen");
        ArrayList<Integer> disabledButtons = new ArrayList<>();
        disabledButtons.add(R.string.product_info);
        disabledButtons.add(R.string.FAQ_KEY);
        disabledButtons.add(R.string.Change_Selected_Product);

        if (!isProductReviewLinkAvailable() && Utils.isCountryChina()) {
            disabledButtons.add(R.string.dcc_tellUs_header);
        }
        if (!(DigitalCareConfigManager.getInstance().getProductModelSelectionType().
                getHardCodedProductList().length < 2)) {
            disabledButtons.remove(Integer.valueOf(R.string.Change_Selected_Product));
        }
        updateMenus(disabledButtons);
    }

    private void updateMenus(ArrayList<Integer> disabledButtons) {
        ArrayList<MenuItem> menus = getMenuItems();
        if (disabledButtons != null) {
            for (Iterator<MenuItem> iterator = menus.iterator(); iterator.hasNext(); ) {
                MenuItem item = iterator.next();
                if (disabledButtons.contains(item.mText)) {
                    iterator.remove();
                }
            }
        }
        mAdapter.swap(menus);
    }

    protected void updateSummaryData(SummaryModel productSummaryModel) {
        if (productSummaryModel != null) {
            mViewProductSummaryModel = productSummaryModel;
            SummaryModel summaryModel = productSummaryModel;
            DigitalCareConfigManager.getInstance().getConsumerProductInfo().
                    setCtn(summaryModel.getData().getCtn());
            updateMenus(null);

            setDataToModels(productSummaryModel);
            initialiseServiceDiscoveryRequests();
        }
    }

    private void setDataToModels(SummaryModel productSummaryModel) {
        Data summaryData = productSummaryModel.getData();
        List<String> filterKeys = summaryData.getFilterKeys();
        String productGroup = null;
        String productCategory = null;
        String productSubCategoryKey = null;
        for (String filterData : filterKeys) {

            if (filterData != null && filterData.endsWith("GR"))
                productGroup = filterData;

            if (filterData != null && filterData.endsWith("CA"))
                productCategory = filterData;
        }
        if (summaryData.getSubcategory() != null)
            productSubCategoryKey = summaryData.getSubcategory();

        DigiCareLogger.d(TAG, "Subcategory Key : " + productSubCategoryKey);
        DigitalCareConfigManager.getInstance().getConsumerProductInfo().
                setCtn(summaryData.getCtn());
        DigitalCareConfigManager.getInstance().getConsumerProductInfo().
                setSubCategory(productSubCategoryKey);
        DigitalCareConfigManager.getInstance().getConsumerProductInfo().
                setProductReviewUrl(summaryData.getProductURL());
        DigitalCareConfigManager.getInstance().getConsumerProductInfo().setGroup(productGroup);

        ViewProductDetailsModel productDetailsModel = new ViewProductDetailsModel();
        productDetailsModel.setProductName(summaryData.getProductTitle());
        productDetailsModel.setCtnName(summaryData.getCtn());
        productDetailsModel.setProductImage(summaryData.getImageURL());
        productDetailsModel.setProductInfoLink(summaryData.getProductURL());
        productDetailsModel.setDomain(summaryData.getDomain());
        DigitalCareConfigManager.getInstance().setViewProductDetailsData(productDetailsModel);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_SELECTED_PRODUCT_CTN, summaryData.getCtn());
        editor.putString(DCC_SAVED_COUNTRY, ProductModelSelectionHelper.getInstance().getAPPInfraInstance().getServiceDiscovery().getHomeCountry());
        editor.apply();
    }

    protected void executeSubcategoryRequest() {

        if (getActivity() != null) {
            String subCategoryUrl = getSubCategoryURL();
            DigiCareLogger.d(TAG, "Sub Category URL : " + subCategoryUrl);

            RequestData subCategoryRequest = new RequestData();

            subCategoryRequest.setRequestUrl(subCategoryUrl);
            subCategoryRequest.setResponseCallback(categoryResponseCallbak);

            if (mProgressDialog == null) mProgressDialog = new ProgressAlertDialog
                    (getActivity(), R.style.loaderTheme);

            mProgressDialog.setCancelable(false);
            if (!(getActivity().isFinishing()) && isVisible()) {
                mProgressDialog.show();
            }
            subCategoryRequest.execute();
        }
    }

    protected String getSubCategoryURL() {

        return DigitalCareConfigManager.getInstance().getSubCategoryUrl();
    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.dcc_Help_Support);
    }

    private ArrayList<MenuItem> getMenuItems() {
        TypedArray titles = getResources().obtainTypedArray(R.array.main_menu_title);
        TypedArray resources = getResources().obtainTypedArray(R.array.main_menu_resources);
        ArrayList<MenuItem> menus = new ArrayList<>();
        for (int i = 0; i < titles.length(); i++) {
            ProductModelSelectionType productModelSelectionType = DigitalCareConfigManager.getInstance().getProductModelSelectionType();
            if ((titles.getResourceId(i, 0) == R.string.Change_Selected_Product) && isProductSelected()) {
                if (productModelSelectionType == null || productModelSelectionType.getHardCodedProductList() == null || productModelSelectionType.getHardCodedProductList().length > 1) {
                    continue;
                }
            }
            menus.add(new MenuItem(resources.getResourceId(i, 0), titles.getResourceId(i, 0)));
        }
        titles.recycle();
        resources.recycle();
        return menus;
    }

    /*
     * This method will parse, how many features are available at DigitalCare
     * level.
     */
    private void createMainMenu() {
        DigiCareLogger.i(TAG, "Dynamically creating the SupportScreen Buttons");

        final SupportHomeFragment context = this;
        RecyclerView recyclerView = mOptionContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new RecyclerViewSeparatorItemDecoration(getContext()));
        mAdapter = new CommonRecyclerViewAdapter<MenuItem>(getMenuItems(), R.layout.consumercare_icon_button) {
            @Override
            public void bindData(RecyclerView.ViewHolder holder, MenuItem item) {
                View container = holder.itemView.findViewById(R.id.icon_button);
                Label label = container.findViewById(R.id.icon_button_text);
                label.setText(item.mText);
                TextView icon = container.findViewById(R.id.icon_button_icon);
                icon.setText(item.mIcon);
                container.setTag(getResources().getResourceEntryName(item.mText));
                container.setOnClickListener(context);
            }
        };
        recyclerView.setAdapter(mAdapter);
        disableChangeProductButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableSupportButtonClickable();
    }

    @Override
    public void onResponseReceived(SummaryModel productSummaryModel) {
        if(getContext() == null) {
            return;
        }
        if (productSummaryModel == null) {
            DigiCareLogger.i(TAG, "Summary Response Not Received from PRX");
            createMainMenu();
            if (!isProductSelected()) {
                disablePrxDependentButtons();
            }
        } else {
            DigiCareLogger.i(TAG, "Summary Response Received from PRX");
            try {
                mViewProductSummaryModel = productSummaryModel;
                SummaryModel summaryModel = productSummaryModel;
                DigitalCareConfigManager.getInstance().getConsumerProductInfo().setCtn(summaryModel.getData().getCtn());
                setDataToModels(productSummaryModel);
                initialiseServiceDiscoveryRequests();

            } finally {
                createMainMenu();
            }
        }
    }

    @Override
    public void onDestroy() {
        disableProgressDialog();
        super.onDestroy();
    }

    private void disableProgressDialog() {
        if (mProgressDialog != null && isAdded()) {
            if (mProgressDialog.isShowing()) {
                try {
                    DigiCareLogger.i(TAG, "Removing the Progress View ");
                    mProgressDialog.dismiss();
                    mProgressDialog.cancel();
                    mProgressDialog = null;
                } catch (IllegalArgumentException e) {
                    DigiCareLogger.e(TAG, "Progress Dialog got IllegalArgumentException - disableProgressDialog 1");
                }
            }

        } else if (mProgressDialog != null) {
            try {
                mProgressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                DigiCareLogger.e(TAG, "Progress Dialog got IllegalArgumentException - disableProgressDialog 2");
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (mProgressDialog != null && isAdded()) {
            if (mProgressDialog.isShowing()) {
                try {
                    DigiCareLogger.i(TAG, "Removing the ProgressScreen");
                    mProgressDialog.dismiss();
                    mProgressDialog.cancel();
                    mProgressDialog = null;
                } catch (IllegalArgumentException e) {
                    DigiCareLogger.e(TAG, "Progress Dialog got IllegalArgumentException - onDestroyView 1");
                }
            }

        } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                DigiCareLogger.e(TAG, "Progress Dialog got IllegalArgumentException - onDestroyView 2");
            }
        }
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        if (mProgressDialog != null && isAdded()) {
            if (mProgressDialog.isShowing()) {
                try {
                    mProgressDialog.dismiss();
                    mProgressDialog.cancel();
                    mProgressDialog = null;
                } catch (IllegalArgumentException e) {
                    DigiCareLogger.e(TAG, "Progress Dialog got IllegalArgumentException - onPause 1");
                }
            }

        } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                DigiCareLogger.e(TAG, "Progress Dialog got IllegalArgumentException - onPause 2");
            }
        }
        super.onPause();
    }

    protected boolean isProductReviewLinkAvailable() {
        ViewProductDetailsModel productData = DigitalCareConfigManager.getInstance().getViewProductDetailsData();
        return (productData.getProductInfoLink() == null);
    }

    private String getStringKey(int resId) {
        return getResources().getResourceEntryName(resId);
    }

    @Override
    public String setPreviousPageName() {
        return AnalyticsConstants.PAGE_HOME;
    }

    private void initialiseServiceDiscoveryRequests() {

        //initializeCountry();
        ArrayList<String> var1 = new ArrayList<>();
        var1.add(DigitalCareConstants.SERVICE_ID_CC_CDLS);
        var1.add(DigitalCareConstants.SERVICE_ID_CC_EMAILFROMURL);
        var1.add(DigitalCareConstants.SERVICE_ID_CC_PRX_CATEGORY);
        var1.add(DigitalCareConstants.SERVICE_ID_CC_PRODUCTREVIEWURL);

        HashMap<String, String> hm = new HashMap<>();

        hm.put(DigitalCareConstants.KEY_PRODUCT_SECTOR, DigitalCareConfigManager.getInstance().getConsumerProductInfo().getSector());
        hm.put(DigitalCareConstants.KEY_PRODUCT_CATALOG, DigitalCareConfigManager.getInstance().getConsumerProductInfo().getCatalog());
        hm.put(DigitalCareConstants.KEY_PRODUCT_SUBCATEGORY, DigitalCareConfigManager.getInstance().getConsumerProductInfo().getSubCategory());
        hm.put(DigitalCareConstants.KEY_PRODUCT_REVIEWURL, DigitalCareConfigManager.getInstance().getViewProductDetailsData().getProductInfoLink());

        hm.put(DigitalCareConstants.KEY_APPNAME, getAppName());

        DigitalCareConfigManager.getInstance().getAPPInfraInstance().getServiceDiscovery().getServicesWithCountryPreference(var1, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> map) {

                ServiceDiscoveryService serviceDiscoveryService = map.get("cc.prx.category");
                if (serviceDiscoveryService != null) {
                    DigitalCareConfigManager.getInstance().setSubCategoryUrl(serviceDiscoveryService.getConfigUrls());
                    DigiCareLogger.d(TAG, "Response from Service Discovery : Service ID : 'cc.prx.category' - " + serviceDiscoveryService.getConfigUrls());
                }

                serviceDiscoveryService = map.get("cc.productreviewurl");
                if (serviceDiscoveryService != null) {
                    DigitalCareConfigManager.getInstance().setProductReviewUrl(serviceDiscoveryService.getConfigUrls());
                    DigiCareLogger.d(TAG, "Response from Service Discovery : Service ID : 'cc.productreviewurl' - " + serviceDiscoveryService.getConfigUrls());
                }

                executeSubcategoryRequest();

                if (mProgressDialog != null && isAdded()) {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.cancel();
                        mProgressDialog.dismiss();
                    }
                }

            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {
                DigiCareLogger.v(TAG, "Error Response from Service Discovery :" + s);
                DigitalCareConfigManager.getInstance().getTaggingInterface().trackActionWithInfo(AnalyticsConstants.ACTION_SET_ERROR, AnalyticsConstants.ACTION_KEY_TECHNICAL_ERROR, s);
            }
        }, hm);
    }

    private void initializeCountry() {


        DigitalCareConfigManager.getInstance().getAPPInfraInstance().getServiceDiscovery().getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
            @Override
            public void onSuccess(String s, SOURCE source) {
                String ccSavedCountry = prefs.getString(DCC_SAVED_COUNTRY, "");
                if(!TextUtils.isEmpty(ccSavedCountry)) {
                    if (!(s.equalsIgnoreCase(ccSavedCountry))) {
                        resetProductSelectionOnCountryChange();
                    }
                }
                DigitalCareConfigManager.getInstance().setCountry(s);
                DigiCareLogger.v(TAG, "Response from Service Discovery : Home Country - " + s);
            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {
                DigiCareLogger.v(TAG, "Error response from Service Discovery : Home Country - " + s);
            }
        });
    }

    private void resetProductSelectionOnCountryChange(){

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_SELECTED_PRODUCT_CTN, "");
        editor.putString(DCC_SAVED_COUNTRY, "");
        editor.apply();
        DigitalCareConfigManager.getInstance().setConsumerProductInfo(null);
    }

}