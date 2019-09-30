/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.screens;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.activity.ECSActivity;
import com.ecs.demouapp.ui.adapters.ImageAdapter;
import com.ecs.demouapp.ui.analytics.ECSAnalytics;
import com.ecs.demouapp.ui.analytics.ECSAnalyticsConstant;
import com.ecs.demouapp.ui.cart.AbstractShoppingCartPresenter;
import com.ecs.demouapp.ui.cart.ECSCartListener;
import com.ecs.demouapp.ui.cart.ShoppingCartAPI;
import com.ecs.demouapp.ui.cart.ShoppingCartData;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.controller.ControllerFactory;
import com.ecs.demouapp.ui.eventhelper.EventHelper;
import com.ecs.demouapp.ui.eventhelper.EventListener;
import com.ecs.demouapp.ui.session.IAPNetworkError;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.stock.ECSStockAvailabilityHelper;
import com.ecs.demouapp.ui.utils.AlertListener;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.ECSLog;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.ecs.demouapp.ui.utils.NetworkUtility;
import com.ecs.demouapp.ui.utils.Utility;
import com.ecs.demouapp.ui.view.CountDropDown;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.asset.Asset;
import com.philips.cdp.di.ecs.model.asset.Assets;
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart;
import com.philips.cdp.di.ecs.model.cart.ECSEntries;
import com.philips.cdp.di.ecs.model.disclaimer.Disclaimer;
import com.philips.cdp.di.ecs.model.products.ECSProduct;
import com.philips.cdp.di.ecs.model.retailers.ECSRetailer;
import com.philips.cdp.di.ecs.model.summary.Data;
import com.philips.cdp.di.ecs.util.ECSConfiguration;

import com.philips.platform.uid.view.widget.DotNavigationIndicator;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.UIPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import static com.ecs.demouapp.ui.utils.ECSConstant.IAP_UPDATE_PRODUCT_COUNT;



public class ProductDetailFragment extends InAppBaseFragment implements
        View.OnClickListener, EventListener, ErrorDialogFragment.ErrorDialogListener,
         AbstractShoppingCartPresenter.ShoppingCartListener<ShoppingCartData>, AlertListener{



    private static final int RTP = 1;
    private static final int APP = 2;
    private static final int DPP = 3;
    private static final int MI1 = 4;
    private static final int PID = 5;


    public static final String TAG = ProductDetailFragment.class.getName();
    private Context mContext;
    private Bundle mBundle;
    private Data mProductSummary;
    private ShoppingCartAPI mShoppingCartAPI;
    private ImageAdapter mImageAdapter;
    private ViewPager mViewPager;

    private TextView mProductDiscountedPrice;
    private TextView mProductDescription;
    private TextView mCTN;
    private TextView mPrice;
    private TextView mProductOverview;
    private ProgressBarButton mAddToCart;
    private ProgressBarButton mBuyFromRetailers;
    private TextView mProductStockInfo;
    private ScrollView mDetailLayout;
    private LinearLayout mCheckutAndCountinue;
    private LinearLayout mQuantityAndDelete;
    private TextView mQuantity;
    private Button mDeleteProduct;
    private Label mProductDisclaimer;
    private ArrayList<String> mAsset;
    private boolean mLaunchedFromProductCatalog = false;
    private String mCTNValue;
    private String mProductTitle;
    private ErrorDialogFragment mErrorDialogFragment;
    private boolean mIsFromVertical;
    private ArrayList<ECSRetailer> mUpdtedStoreEntity;
    private RelativeLayout mParentLayout;
    private ProgressBar mProgresImage;


    private ECSCartListener mBuyProductListener = new ECSCartListener() {
        @Override
        public void onSuccess(ECSShoppingCart ecsShoppingCart) {
            hideProgressBar();
            mAddToCart.hideProgressIndicator();
            if (mIapListener != null) {
                mIapListener.onUpdateCartCount(ECSUtility.getInstance().getQuantity(ecsShoppingCart));
            }

            startShoppingCartFragment();
        }

        @Override
        public void onFailure(final Message msg) {
            hideProgressBar();
            mAddToCart.hideProgressIndicator();
            if(null!=msg && null!=msg.obj ) {
                ECSUtility.showECSAlertDialog(mContext, "Error", msg.obj.toString());
            }
        }
    };
    private ECSProduct product;


    public static ProductDetailFragment createInstance(Bundle args, AnimationType animType) {
        final ProductDetailFragment fragment = new ProductDetailFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mShoppingCartAPI = ControllerFactory.
                getInstance().getShoppingCartPresenter(mContext, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventHelper.getInstance().registerEventNotification(ECSConstant.IAP_LAUNCH_SHOPPING_CART, this);
        EventHelper.getInstance().registerEventNotification(IAP_UPDATE_PRODUCT_COUNT, this);
        EventHelper.getInstance().registerEventNotification(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED, this);
        EventHelper.getInstance().registerEventNotification(ECSConstant.IAP_DELETE_PRODUCT, this);
        final View rootView = inflater.inflate(R.layout.ecs_product_details_screen, container, false);
        initializeViews(rootView);

        return rootView;
    }

    void initializeViews(View rootView) {
        mDetailLayout = rootView.findViewById(R.id.scrollView);
        mProductDescription = rootView.findViewById(R.id.iap_productDetailScreen_productDescription_lebel);
        mProgresImage = rootView.findViewById(R.id.progress_image);
        mCTN = rootView.findViewById(R.id.iap_productDetailsScreen_ctn_lebel);
        mPrice = rootView.findViewById(R.id.iap_productDetailsScreen_individualPrice_lebel);
        mProductOverview = rootView.findViewById(R.id.iap_productDetailsScreen_productOverview);
        mAddToCart = rootView.findViewById(R.id.iap_productDetailsScreen_addToCart_button);
        mBuyFromRetailers = rootView.findViewById(R.id.iap_productDetailsScreen_buyFromRetailor_button);
        mCheckutAndCountinue = rootView.findViewById(R.id.iap_productDetailsScreen_btn_ll);
        mQuantityAndDelete = rootView.findViewById(R.id.iap_productDetailsScreen_quantity_delete_btn_ll);
        mProductDiscountedPrice = rootView.findViewById(R.id.iap_productCatalogItem_discountedPrice_lebel);
        mProductStockInfo = rootView.findViewById(R.id.iap_productDetailsScreen_outOfStock_label);
        mDeleteProduct = rootView.findViewById(R.id.delete_btn);
        if (getContext() != null) {
            mDeleteProduct.setTextColor(ContextCompat.getColor(getContext(), R.color.uid_signal_red_level_45));
        }
        mDeleteProduct.setOnClickListener(this);
        mQuantity = rootView.findViewById(R.id.quantity_val);
        mViewPager = rootView.findViewById(R.id.pager);
        mParentLayout = rootView.findViewById(R.id.product_details_container);
        final DotNavigationIndicator indicator = rootView.findViewById(R.id.indicator);
        mImageAdapter = new ImageAdapter(mContext, new ArrayList<String>());
        mViewPager.setAdapter(mImageAdapter);
        indicator.setViewPager(mViewPager);
        mProductDisclaimer = rootView.findViewById(R.id.iap_productDetailsScreen_productDisclaimer);

        mBundle = getArguments();
        if (mBundle != null) {

            ECSProduct productCatalogData = (ECSProduct) mBundle.getSerializable("ProductCatalogData");
            product = productCatalogData;

            if(mBundle.getSerializable(ECSConstant.SHOPPING_CART_CODE)!=null){
              ECSEntries entriesEntity = (ECSEntries) mBundle.getSerializable(ECSConstant.SHOPPING_CART_CODE);
                product = entriesEntity.getProduct();
            }

            if (mBundle.containsKey(ECSConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL)) {
                mIsFromVertical = true;
                mCTNValue = mBundle.getString(ECSConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL);
                if(product == null){
                    product = new ECSProduct();
                }
                product.setCode(mCTNValue);
                if (isNetworkConnected()) {
                    if (!ControllerFactory.getInstance().isPlanB()) {

                        ECSUtility.getInstance().getEcsServices().fetchProduct(mCTNValue, new ECSCallback<ECSProduct, Exception>() {
                            @Override
                            public void onResponse(ECSProduct result) {
                                product = result;
                                hideProgressBar();
                                fetchProductDetailFromPrx();
                                mDetailLayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFailure(Exception error, ECSError ecsError) {
                                hideProgressBar();
                                mDetailLayout.setVisibility(View.GONE);
                                ECSUtility.showECSAlertDialog(mContext,"Error",error.getMessage());
                                //showErrorDialog(new Message());
                            }
                        });

                    } else {
                        fetchProductDetailFromPrx();
                        if (mIapListener != null)
                            mIapListener.onSuccess();
                    }
                }
            } else {
                mCTNValue = mBundle.getString(ECSConstant.PRODUCT_CTN);
                mLaunchedFromProductCatalog = mBundle.getBoolean(ECSConstant.IS_PRODUCT_CATALOG, false);
                mProductTitle = mBundle.getString(ECSConstant.PRODUCT_TITLE);
                if(product!=null)
                populateData(product.getSummary());
            }

        }

    }

    private void fetchProductDetailFromPrx() {
        makeSummaryRequest();
    }

    private void tagProduct() {
        String productPrice = "";
        final HashMap<String, String> contextData = new HashMap<>();
        StringBuilder product = new StringBuilder();
        if (mBundle.getString(ECSConstant.PRODUCT_VALUE_PRICE) != null) {
            productPrice = mBundle.getString(ECSConstant.PRODUCT_VALUE_PRICE);
        }
        product = product.append(ECSConfiguration.INSTANCE.getRootCategory()).append(";")
                .append(mProductTitle).append(";").append(";")
                .append(productPrice);
        contextData.put(ECSAnalyticsConstant.SPECIAL_EVENTS, ECSAnalyticsConstant.PROD_VIEW);
        contextData.put(ECSAnalyticsConstant.PRODUCTS, product.toString());
        ECSAnalytics.trackMultipleActions(ECSAnalyticsConstant.SEND_DATA, contextData);
    }

    private void makeSummaryRequest() {

        populateData(product.getSummary());
        makeAssetsAndDisclaimerRequest();
    }

    private void makeAssetsAndDisclaimerRequest() {
        createCustomProgressBar(mParentLayout, BIG);
        ECSUtility.getInstance().getEcsServices().fetchProductDetails(product, new ECSCallback<ECSProduct, Exception>() {
            @Override
            public void onResponse(ECSProduct result) {

                if(result.getDisclaimers()!=null || !result.getDisclaimers().getDisclaimer().isEmpty()) {
                    showDisclaimer(result.getDisclaimers().getDisclaimer());
                }
                if(result.getAssets()!=null) {
                    processAssets(result.getAssets());
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                hideProgressBar();
                mProgresImage.setVisibility(View.GONE);
                ECSLog.d(ECSConstant.PRODUCT_DETAIL_FRAGMENT, "Failure");
                if (mBuyFromRetailers.isActivated()) {
                    mBuyFromRetailers.hideProgressIndicator();
                }
                if (!isNetworkConnected()) {
                    return;
                }
                Message msg = new Message();

                if (msg.obj instanceof IAPNetworkError) {
                    ECSUtility.showECSAlertDialog(mContext,"Error", ((IAPNetworkError) msg.obj).getMessage());
                    final IAPNetworkError obj = (IAPNetworkError) msg.obj;
                    mIapListener.onFailure(obj.getIAPErrorCode());
                }
            }
        });
    }

    private void processAssets(Assets assets) {

        if (mContext == null)
            return;
        mProgresImage.setVisibility(View.GONE);
        ECSLog.d(ECSConstant.PRODUCT_DETAIL_FRAGMENT, "Success");
        mAsset = fetchImageUrlsFromPRXAssets(assets.getAsset());
        CartModelContainer.getInstance().addProductAsset(mCTNValue, mAsset);
        if(mAsset.size() == 0){
            mAsset.add("default image");
        }
        mImageAdapter = new ImageAdapter(mContext, mAsset);
        mViewPager.setAdapter(mImageAdapter);
        mImageAdapter.notifyDataSetChanged();
        if (mIapListener != null)
            mIapListener.onSuccess();
        if (mBuyFromRetailers.isActivated())
            mBuyFromRetailers.hideProgressIndicator();
    }

    private ArrayList<String> fetchImageUrlsFromPRXAssets(List<Asset> assets) {
        ArrayList<String> mAssetsFromPRX = new ArrayList<>();
        TreeMap<Integer, String> sortedAssetsFromPRX = new TreeMap<>();
        GetHeightAndWidth getHeightAndWidth = new GetHeightAndWidth().invoke();
        int width = getHeightAndWidth.getWidth();
        int height = getHeightAndWidth.getHeight();

        for (Asset asset : assets) {
            int assetType = getAssetType(asset);
            if (assetType != -1) {
                String imagepath = asset.getAsset() + "?wid=" + width +
                        "&hei=" + height + "&$pnglarge$" + "&fit=fit,1";
                sortedAssetsFromPRX.put(assetType, imagepath);
            }
            mAssetsFromPRX = new ArrayList<>(sortedAssetsFromPRX.values());
        }

        return mAssetsFromPRX;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBundle != null) {
            if (!mBundle.containsKey(ECSConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL)) {
                tagProduct();
                if (mBundle != null && mLaunchedFromProductCatalog) {
                    ECSAnalytics.trackPage(ECSAnalyticsConstant.PRODUCT_DETAIL_PAGE_NAME);
                    handleViews();
                } else {
                    mQuantityAndDelete.setVisibility(View.VISIBLE);
                    ECSAnalytics.trackPage(ECSAnalyticsConstant.SHOPPING_CART_ITEM_DETAIL_PAGE_NAME);
                    setCartIconVisibility(false);
                    final int quantity = getArguments().getInt(ECSConstant.PRODUCT_QUANTITY);
                    if (quantity != 0) {
                        setCountArrow(mContext, true);
                        mCheckutAndCountinue.setVisibility(View.GONE);
                        mQuantityAndDelete.setVisibility(View.VISIBLE);
                        mQuantity.setCompoundDrawables(null, null, countArrow, null);

                        mQuantity.setText(quantity + "");
                        final int stock = getArguments().getInt(ECSConstant.PRODUCT_STOCK);
                        bindCountView(mQuantity, stock, quantity);
                    } else {
                        mQuantityAndDelete.setVisibility(View.GONE);
                    }
                }
            } else {
                handleViews();
            }
        }

        makeAssetsAndDisclaimerRequest();
        setTitleAndBackButtonVisibility(R.string.iap_product_detail_title, true);
    }

    private Drawable countArrow;
    private int mQuantityStatus;
    private int mNewCount;
    private UIPicker mPopupWindow;

    private void setCountArrow(final Context context, final boolean isEnable) {
        if (isEnable) {
            countArrow = context.getDrawable(R.drawable.ecs_product_count_drop_down);
            countArrow.setColorFilter(new
                    PorterDuffColorFilter(mContext.getResources().getColor(R.color.uid_quiet_button_icon_selector), PorterDuff.Mode.MULTIPLY));
        } else {
            countArrow = VectorDrawableCompat.create(context.getResources(), R.drawable.ecs_product_disable_count_drop_down, mContext.getTheme());
        }
        int width = (int) mContext.getResources().getDimension(R.dimen.iap_count_drop_down_icon_width);
        int height = (int) mContext.getResources().getDimension(R.dimen.iap_count_drop_down_icon_height);
        countArrow.setBounds(0, 0, width, height);
    }

    private void bindCountView(final View view, final int stockLevel, final int quantity) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final CountDropDown countPopUp = new CountDropDown(v, v.getContext(), stockLevel, quantity, new CountDropDown.CountUpdateListener() {
                    @Override
                    public void countUpdate(final int oldCount, final int newCount) {
                        mQuantityStatus = getQuantityStatus(newCount, oldCount);
                        mNewCount = newCount;
                        EventHelper.getInstance().notifyEventOccurred(IAP_UPDATE_PRODUCT_COUNT);
                    }
                });
                countPopUp.createPopUp(v, stockLevel);
                mPopupWindow = countPopUp.getPopUpWindow();
                countPopUp.show();
            }
        });
    }

    public int getNewCount() {
        return mNewCount;
    }

    private int getQuantityStatus(int newCount, int oldCount) {
        if (newCount > oldCount) {
            return 1;
        } else if (newCount < oldCount) {
            return 0;
        } else {
            return -1;
        }
    }

    public int getQuantityStatusInfo() {
        return mQuantityStatus;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventHelper.getInstance().unregisterEventNotification(ECSConstant.IAP_LAUNCH_SHOPPING_CART, this);
        EventHelper.getInstance().unregisterEventNotification(IAP_UPDATE_PRODUCT_COUNT, this);
        EventHelper.getInstance().unregisterEventNotification(ECSConstant.IAP_DELETE_PRODUCT, this);
        EventHelper.getInstance().unregisterEventNotification(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED, this);
        if (mErrorDialogFragment != null) {
            mErrorDialogFragment.onDestroyView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void handleViews() {

        if (ControllerFactory.getInstance().isPlanB()) {
            mAddToCart.setVisibility(View.GONE);
            setCartIconVisibility(false);
        } else {
            mAddToCart.setVisibility(View.VISIBLE);
            mAddToCart.setOnClickListener(this);
            setCartIconVisibility(true);
            if(isUserLoggedIn())
            mShoppingCartAPI.getProductCartCount(mContext, mProductCountListener);
        }
        mBuyFromRetailers.setOnClickListener(this);
        mBuyFromRetailers.setVisibility(View.VISIBLE);

        mProductDiscountedPrice.setVisibility(View.VISIBLE);
        mProductStockInfo.setVisibility(View.VISIBLE);
        mCheckutAndCountinue.setVisibility(View.VISIBLE);
        mQuantityAndDelete.setVisibility(View.GONE);
    }

    private void getRetailersInformation() {
        final ShoppingCartAPI presenter = ControllerFactory.
                getInstance().getShoppingCartPresenter(mContext, this);

        if (!mBuyFromRetailers.isActivated()) {
            mBuyFromRetailers.showProgressIndicator();
            presenter.getRetailersInformation(mCTNValue);
        }
    }

    private void buyFromRetailers(ArrayList<ECSRetailer> storeEntities) {
        if (!isNetworkConnected())
            return;
        Bundle bundle = new Bundle();
        final ArrayList<ECSRetailer> removedBlacklistedRetailers = removedBlacklistedRetailers(storeEntities);
        if (removedBlacklistedRetailers.size() == 1 && (removedBlacklistedRetailers.get(0).getIsPhilipsStore().equalsIgnoreCase("Y"))) {
            bundle.putString(ECSConstant.IAP_BUY_URL, storeEntities.get(0).getBuyURL());
            bundle.putString(ECSConstant.IAP_STORE_NAME, storeEntities.get(0).getName());
            bundle.putBoolean(ECSConstant.IAP_IS_PHILIPS_SHOP, new Utility().isPhilipsShop(storeEntities.get(0)));
            addFragment(WebBuyFromRetailers.createInstance(bundle, AnimationType.NONE), WebBuyFromRetailers.TAG, true);
        } else {

            bundle.putStringArrayList(ECSConstant.IAP_IGNORE_RETAILER_LIST, getArguments().getStringArrayList(ECSConstant.IAP_IGNORE_RETAILER_LIST));
            bundle.putSerializable(ECSConstant.IAP_RETAILER_INFO, removedBlacklistedRetailers);
            if (removedBlacklistedRetailers.isEmpty()) {
                onRetailerError(NetworkUtility.getInstance().
                        createIAPErrorMessage("", mContext.getString(R.string.iap_no_retailer_message)));
            } else {
                addFragment(BuyFromRetailersFragment.createInstance(bundle, AnimationType.NONE),
                        BuyFromRetailersFragment.TAG, true);
            }
        }
    }



    void buyProduct() {

        if(isUserLoggedIn()) {
            if (!mAddToCart.isActivated()) {
                mAddToCart.showProgressIndicator();
            }
            mShoppingCartAPI.addProductToCart(product, mBuyProductListener);
        }else{
            showLogInDialog();
        }
    }
    @Override
    public void onClick(View v) {
        if (!isNetworkConnected()) {
            return;
        }
        if (v == mAddToCart) {
            buyProduct();
        }
        if (v == mBuyFromRetailers) {
            getRetailersInformation();
        }
        if (v == mDeleteProduct) {
            deleteProduct();
        }
    }

    private void showDisclaimer(List<Disclaimer> disclaimerList) {
        try {
            mProductDisclaimer.setVisibility(View.VISIBLE);
            if (null != disclaimerList && disclaimerList.size() > 0) {
                final StringBuilder disclaimerStringBuilder = new StringBuilder();
                for (Disclaimer disclaimer : disclaimerList) {
                    disclaimerStringBuilder.append("- ").append(disclaimer.getDisclaimerText()).append(System.getProperty("line.separator"));
                }
                mProductDisclaimer.setText(disclaimerStringBuilder.toString());
            }
        } catch (Exception e) {
            ECSLog.v("DISCLAIMER_REQ", e.getMessage());
        }
    }

    private void deleteProduct() {
        Utility.showActionDialog(mContext, getString(R.string.iap_remove_product), getString(R.string.iap_cancel)
                , null, getString(R.string.iap_product_remove_description), getFragmentManager(), this);
    }


    private void populateData(Data data) {
        String actualPrice = null;
        String discountedPrice = null;
        String stockLevelStatus = null;
        int stockLevel;
       // if (mBundle.containsKey(ECSConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL)) {
            if (data != null) {
                mProductTitle = data.getProductTitle();
                if (mProductTitle == null) {
                    trackErrorTag(ECSAnalyticsConstant.PRX + mCTNValue + "_" + ECSAnalyticsConstant.PRODUCT_TITLE_MISSING);
                }
                mProductDescription.setText(mProductTitle);
                mCTN.setText(mCTNValue);
                mProductOverview.setText(data.getMarketingTextHeader());
                trackErrorTag(ECSAnalyticsConstant.PRX + mCTNValue + "_" + ECSAnalyticsConstant.PRODUCT_DESCRIPTION_MISSING);
                if (product != null) {
                    actualPrice = product.getPrice().getFormattedValue();
                    if(null!=product.getDiscountPrice() && null!= product.getDiscountPrice().getFormattedValue()) {
                        discountedPrice = product.getDiscountPrice().getFormattedValue();
                    }
                    setPrice(actualPrice, discountedPrice);
                    setStockInfo(product.getStock().getStockLevelStatus(), product.getStock().getStockLevel());
                } else {
                    mPrice.setVisibility(View.GONE);
                    mProductDiscountedPrice.setVisibility(View.GONE);
                    mProductStockInfo.setVisibility(View.GONE);
                }
            }
       // }
        else {
            actualPrice = mBundle.getString(ECSConstant.PRODUCT_PRICE);
            discountedPrice = mBundle.getString(ECSConstant.IAP_PRODUCT_DISCOUNTED_PRICE);
            stockLevelStatus = mBundle.getString(ECSConstant.STOCK_LEVEL_STATUS);
            stockLevel = mBundle.getInt(ECSConstant.STOCK_LEVEL);

            mProductDescription.setText(mBundle.getString(ECSConstant.PRODUCT_TITLE));
            mCTN.setText(mBundle.getString(ECSConstant.PRODUCT_CTN));
            mProductOverview.setText(mBundle.getString(ECSConstant.PRODUCT_OVERVIEW));

            if (mBundle.getString(ECSConstant.PRODUCT_TITLE) == null) {
                trackErrorTag(ECSAnalyticsConstant.PRX + mCTNValue + "_" + ECSAnalyticsConstant.PRODUCT_TITLE_MISSING);
            }
            if (mBundle.getString(ECSConstant.PRODUCT_OVERVIEW) == null) {
                trackErrorTag(ECSAnalyticsConstant.PRX + mCTNValue + "_" + ECSAnalyticsConstant.PRODUCT_DESCRIPTION_MISSING);
            }

            if (mLaunchedFromProductCatalog) {
                setPrice(actualPrice, discountedPrice);
                setStockInfo(stockLevelStatus, stockLevel);
            } else {
                mPrice.setVisibility(View.GONE);
                mProductDiscountedPrice.setText(actualPrice);
            }
        }
    }

    private void setStockInfo(String stockLevelStatus, int stockLevel) {
        final ECSStockAvailabilityHelper ECSStockAvailabilityHelper = new ECSStockAvailabilityHelper();
        if (ECSStockAvailabilityHelper.isStockAvailable(stockLevelStatus, stockLevel)) {
            mAddToCart.setEnabled(true);
            mProductStockInfo.setVisibility(View.GONE);
        } else {
            mAddToCart.setEnabled(false);
            mProductStockInfo.setText(mContext.getString(R.string.iap_out_of_stock));
            mProductStockInfo.setTextColor(ContextCompat.getColor(mContext, R.color.uid_signal_red_level_60));
            ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                    ECSAnalyticsConstant.OUT_OF_STOCK, stockLevelStatus);
        }
    }

    private void trackErrorTag(String value) {
        ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                ECSAnalyticsConstant.ERROR, value);
    }

    private void setPrice(String actualPrice, String discountedPrice) {
        if (!ControllerFactory.getInstance().isPlanB()) {
            setCartIconVisibility(true);
        }
        mPrice.setText(actualPrice);
        if (discountedPrice == null || discountedPrice.equalsIgnoreCase("")) {
            mProductDiscountedPrice.setVisibility(View.GONE);
        } else if (discountedPrice.equalsIgnoreCase(actualPrice)) {
            mPrice.setVisibility(View.GONE);
            mProductDiscountedPrice.setVisibility(View.VISIBLE);
            mProductDiscountedPrice.setText(discountedPrice);
        } else {
            mProductDiscountedPrice.setVisibility(View.VISIBLE);
            mProductDiscountedPrice.setText(discountedPrice);
            mPrice.setPaintFlags(mPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public void onLoadFinished(ArrayList<?> data) {
        hideProgressBar();
        mAddToCart.hideProgressIndicator();

        if (data != null && data.get(0) instanceof ShoppingCartData) {
            final ShoppingCartData shoppingCartData = getShoppingCartDataFromCTN(data);
            if (shoppingCartData != null) {
                mQuantity.setText(shoppingCartData.getQuantity() + "");
            } else {
                getFragmentManager().popBackStack();
            }

        }

        if (!data.isEmpty() && data.get(0) instanceof ECSRetailer) {
            mBuyFromRetailers.hideProgressIndicator();
            buyFromRetailers((ArrayList<ECSRetailer>) data);
        }
    }

    private ShoppingCartData getShoppingCartDataFromCTN(ArrayList<?> data) {
        final String lCtn = mBundle.getString(ECSConstant.PRODUCT_CTN);

        for (ShoppingCartData shoppingCartData : (ArrayList<ShoppingCartData>) data) {
            if (shoppingCartData.getCtnNumber().equalsIgnoreCase(lCtn))
                return shoppingCartData;
        }
        return null;
    }

    @Override
    public void onLoadError(Message msg) {
        ECSLog.d(ECSLog.LOG, "onLoadError == ProductDetailFragment ");
        mBuyFromRetailers.hideProgressIndicator();
        if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, ((FragmentActivity) mContext).getSupportFragmentManager(), mContext);
        }
        else if(msg.obj instanceof String){
            NetworkUtility.getInstance().showErrorDialog(getActivity(), getFragmentManager(), getActivity().getString(R.string.iap_ok),
                    getActivity().getString(R.string.iap_server_error), (String) msg.obj);
        }else {
            NetworkUtility.getInstance().showErrorDialog(mContext, ((FragmentActivity) mContext).getSupportFragmentManager(), mContext.getString(R.string.iap_ok),
                    mContext.getString(R.string.iap_server_error), mContext.getString(R.string.iap_something_went_wrong));
        }
    }

    @Override
    public void onRetailerError(IAPNetworkError errorMsg) {
        mBuyFromRetailers.hideProgressIndicator();
        NetworkUtility.getInstance().showErrorDialog(mContext,
                getFragmentManager(), mContext.getString(R.string.iap_ok),
                mContext.getString(R.string.iap_retailer_title_for_no_retailers), errorMsg.getMessage());
    }

    @Override
    public void onLoadFinished(ECSShoppingCart data) {

        product = getProductFromCTN(data);

        if(product ==null){
            onEventReceived(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED);
            return;
        }

        populateData(product.getSummary());

        hideProgressBar();

    }

    private ECSProduct getProductFromCTN(ECSShoppingCart data) {

        if(data.getEntries()!=null && data.getEntries().size() >0) {

            for (ECSEntries entriesEntity : data.getEntries()) {

                if (mCTNValue.equalsIgnoreCase(entriesEntity.getProduct().getCode())) {
                    mQuantity.setText(entriesEntity.getQuantity() + "");
                    return entriesEntity.getProduct();
                }
            }
        }
        return null;
    }

    @Override
    public void onEventReceived(String event) {
        if (event.equalsIgnoreCase(String.valueOf(ECSConstant.IAP_LAUNCH_SHOPPING_CART))) {
            startShoppingCartFragment();
        } else if (event.equalsIgnoreCase(IAP_UPDATE_PRODUCT_COUNT)) {
            createCustomProgressBar(mParentLayout, BIG);
            final ECSEntries entriesEntity = (ECSEntries) mBundle.getSerializable(ECSConstant.SHOPPING_CART_CODE);
            mShoppingCartAPI.updateProductQuantity(entriesEntity, getNewCount());

        } else if (event.equalsIgnoreCase(ECSConstant.IAP_DELETE_PRODUCT)) {
            hideProgressBar();
            createCustomProgressBar(mParentLayout, BIG);
            final ECSEntries entriesEntity = (ECSEntries) mBundle.getSerializable(ECSConstant.SHOPPING_CART_CODE);
            mShoppingCartAPI.deleteProduct(entriesEntity);

        } else if (event.equalsIgnoreCase(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED)) {
            hideProgressBar();
            addFragment(EmptyCartFragment.createInstance(new Bundle(), AnimationType.NONE), EmptyCartFragment.TAG, true);
        }
    }

    private void startShoppingCartFragment() {
        mAddToCart.hideProgressIndicator();
        Bundle bundle = new Bundle();

        addFragment(ShoppingCartFragment.createInstance(bundle, AnimationType.NONE), ShoppingCartFragment.TAG, true);
    }

    @Override
    public void onDialogOkClick() {
        if (getActivity() != null && getActivity() instanceof ECSActivity) {
            int count = getFragmentManager().getBackStackEntryCount();
            for (int i = 0; i < count; i++) {
                getFragmentManager().popBackStack();
            }
            finishActivity();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private void showErrorDialog(Message msg) {
        Bundle bundle = new Bundle();
        if (msg.obj instanceof IAPNetworkError) {
            IAPNetworkError error = (IAPNetworkError) msg.obj;
            bundle.putString(ECSConstant.SINGLE_BUTTON_DIALOG_TITLE,
                    NetworkUtility.getInstance().getErrorTitleMessageFromErrorCode(mContext, error.getIAPErrorCode()));
            bundle.putString(ECSConstant.SINGLE_BUTTON_DIALOG_DESCRIPTION,
                    NetworkUtility.getInstance().getErrorDescriptionMessageFromErrorCode(mContext, error));
        } else {
            bundle.putString(ECSConstant.SINGLE_BUTTON_DIALOG_DESCRIPTION, (String) msg.obj);
        }

        bundle.putString(ECSConstant.SINGLE_BUTTON_DIALOG_TEXT, mContext.getString(R.string.iap_ok));

        if (mErrorDialogFragment == null) {
            mErrorDialogFragment = new ErrorDialogFragment();
            mErrorDialogFragment.setErrorDialogListener(this);
            mErrorDialogFragment.setArguments(bundle);
            mErrorDialogFragment.setShowsDialog(false);
        }
        try {
            mErrorDialogFragment.show(getFragmentManager(), "NetworkErrorDialog");
            mErrorDialogFragment.setShowsDialog(true);
        } catch (Exception e) {
            ECSLog.e(ECSLog.LOG, e.getMessage());
        }
    }

    @Override
    public boolean handleBackEvent() {
        if (mIsFromVertical) {
            if (getActivity() != null && getActivity() instanceof ECSActivity) {
                int count = getFragmentManager().getBackStackEntryCount();
                for (int i = 0; i < count; i++) {
                    getFragmentManager().popBackStack();
                }
                finishActivity();
            } else {
                getFragmentManager().popBackStack();
            }
        }
        return super.handleBackEvent();
    }

    private ArrayList<ECSRetailer> removedBlacklistedRetailers(ArrayList<ECSRetailer> pStoreEntity) {
        final ArrayList<String> list = getArguments().getStringArrayList(ECSConstant.IAP_IGNORE_RETAILER_LIST);
        mUpdtedStoreEntity = new ArrayList<>();
        mUpdtedStoreEntity.addAll(pStoreEntity);
        for (ECSRetailer storeEntity : pStoreEntity) {
            final String retailerName = storeEntity.getName().replaceAll("\\s+", "");
            for (int i = 0; i < (list != null ? list.size() : 0); i++) {
                if (Utility.indexOfSubString(true, retailerName, list.get(i)) >= 0) {
                    mUpdtedStoreEntity.remove(storeEntity);
                }
            }
        }
        return mUpdtedStoreEntity;
    }

    @Override
    public void onPositiveBtnClick() {
        EventHelper.getInstance().notifyEventOccurred(ECSConstant.IAP_DELETE_PRODUCT);
    }

    @Override
    public void onNegativeBtnClick() {

    }

    private void showLogInDialog(){

        new NetworkUtility().showDialogMessage(getContext().getString(R.string.iap_shopping_cart_dls), "Please Register or Login to easily order your products", getFragmentManager(), getContext(), new AlertListener() {
            @Override
            public void onPositiveBtnClick() {
                new NetworkUtility().dismissErrorDialog();
            }

            @Override
            public void onNegativeBtnClick() {
                new NetworkUtility().dismissErrorDialog();
            }
        });
    }

    private class GetHeightAndWidth {
        private int width;
        private int height;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        GetHeightAndWidth invoke() {
            width = 0;
            height = 0;
            width = mContext.getResources().getDisplayMetrics().widthPixels;
            height = (int) mContext.getResources().getDimension(R.dimen.iap_product_detail_image_height);

            return this;
        }
    }

    private int getAssetType(Asset asset) {
        switch (asset.getType()) {
            case "RTP":
                return RTP;
            case "APP":
                return APP;
            case "DPP":
                return DPP;
            case "MI1":
                return MI1;
            case "PID":
                return PID;
            default:
                return -1;
        }
    }
}
