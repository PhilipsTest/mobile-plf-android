/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

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

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.activity.IAPActivity;
import com.philips.cdp.di.iap.adapters.ImageAdapter;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.cart.AbstractShoppingCartPresenter.ShoppingCartListener;
import com.philips.cdp.di.iap.cart.IAPCartListener;
import com.philips.cdp.di.iap.cart.ShoppingCartAPI;
import com.philips.cdp.di.iap.cart.ShoppingCartData;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.controller.ControllerFactory;
import com.philips.cdp.di.iap.controller.ProductDetailController;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.eventhelper.EventListener;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.prx.PRXAssetExecutor;
import com.philips.cdp.di.iap.prx.PRXDisclaimerExecutor;
import com.philips.cdp.di.iap.prx.PRXSummaryListExecutor;
import com.philips.cdp.di.iap.response.products.ProductDetailEntity;
import com.philips.cdp.di.iap.response.retailers.StoreEntity;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.session.RequestCode;
import com.philips.cdp.di.iap.stock.IAPStockAvailabilityHelper;
import com.philips.cdp.di.iap.utils.AlertListener;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.di.iap.view.CountDropDown;
import com.philips.cdp.prxclient.datamodels.Disclaimer.Disclaimer;
import com.philips.cdp.prxclient.datamodels.Disclaimer.DisclaimerModel;
import com.philips.cdp.prxclient.datamodels.summary.Data;
import com.philips.platform.uid.view.widget.DotNavigationIndicator;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.UIPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.philips.cdp.di.iap.utils.IAPConstant.IAP_UPDATE_PRODUCT_COUNT;

public class ProductDetailFragment extends InAppBaseFragment implements
        PRXAssetExecutor.AssetListener, View.OnClickListener, EventListener,
        AbstractModel.DataLoadListener, ErrorDialogFragment.ErrorDialogListener,
        ProductDetailController.ProductSearchListener, ShoppingCartListener<ShoppingCartData>, AlertListener, PRXDisclaimerExecutor.ProductDisclaimerListener {


    public static final String TAG = ProductDetailFragment.class.getName();
    private Context mContext;
    private Bundle mBundle;
    private Data mProductSummary;
    private ShoppingCartAPI mShoppingCartAPI;
    private ProductDetailEntity mProductDetail;
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
    private ArrayList<StoreEntity> mUpdtedStoreEntity;
    private RelativeLayout mParentLayout;
    private ProgressBar mProgresImage;


    private IAPCartListener mBuyProductListener = new IAPCartListener() {
        @Override
        public void onSuccess(final int count) {
            hideProgressBar();
            mAddToCart.hideProgressIndicator();
            tagItemAddedToCart();
            if (mIapListener != null) {
                mIapListener.onUpdateCartCount();
            }
        }

        @Override
        public void onFailure(final Message msg) {
            hideProgressBar();
            mAddToCart.hideProgressIndicator();
            IAPNetworkError iapNetworkError = (IAPNetworkError) msg.obj;
            if (null != iapNetworkError.getServerError()) {
                if (iapNetworkError.getIAPErrorCode() == IAPConstant.IAP_ERROR_INSUFFICIENT_STOCK_ERROR) {
                    NetworkUtility.getInstance().showErrorDialog(mContext, getFragmentManager(),
                            mContext.getString(R.string.iap_ok),
                            mContext.getString(R.string.iap_out_of_stock), iapNetworkError.getMessage());

                }
            } else {
                NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
            }
        }
    };



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
        EventHelper.getInstance().registerEventNotification(IAPConstant.IAP_LAUNCH_SHOPPING_CART, this);
        EventHelper.getInstance().registerEventNotification(IAPConstant.IAP_UPDATE_PRODUCT_COUNT, this);
        EventHelper.getInstance().registerEventNotification(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED, this);
        EventHelper.getInstance().registerEventNotification(IAPConstant.IAP_DELETE_PRODUCT, this);
        final View rootView = inflater.inflate(R.layout.iap_product_details_screen, container, false);
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
            if (mBundle.containsKey(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL)) {
                mIsFromVertical = true;
                mCTNValue = mBundle.getString(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL);
                if (isNetworkConnected()) {
                    if (!ControllerFactory.getInstance().isPlanB()) {
                        ProductDetailController controller = new ProductDetailController(mContext, this);
                        controller.getProductDetail(mCTNValue);
                    } else {
                        fetchProductDetailFromPrx();
                        if (mIapListener != null)
                            mIapListener.onSuccess();
                    }
                }
            } else {
                mCTNValue = mBundle.getString(IAPConstant.PRODUCT_CTN);
                mLaunchedFromProductCatalog = mBundle.getBoolean(IAPConstant.IS_PRODUCT_CATALOG, false);
                mProductTitle = mBundle.getString(IAPConstant.PRODUCT_TITLE);
                populateData(mProductSummary);
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
        if (mBundle.getString(IAPConstant.PRODUCT_VALUE_PRICE) != null) {
            productPrice = mBundle.getString(IAPConstant.PRODUCT_VALUE_PRICE);
        }
        product = product.append("Tuscany_Campaign").append(";")
                .append(mProductTitle).append(";").append(";")
                .append(productPrice);
        contextData.put(IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.PROD_VIEW);
        contextData.put(IAPAnalyticsConstant.PRODUCTS, product.toString());
        IAPAnalytics.trackMultipleActions(IAPAnalyticsConstant.SEND_DATA, contextData);
    }

    private void makeAssetRequest() {
        if (!CartModelContainer.getInstance().isPRXAssetPresent(mCTNValue)) {
            mProgresImage.setVisibility(View.VISIBLE);
            final PRXAssetExecutor builder = new PRXAssetExecutor(mContext, mCTNValue, this);
            builder.build();
        } else {
            final HashMap<String, ArrayList<String>> prxAssetObjects =
                    CartModelContainer.getInstance().getPRXAssetList();
            for (Map.Entry<String, ArrayList<String>> entry : prxAssetObjects.entrySet()) {
                if (entry != null && entry.getKey().equalsIgnoreCase(mCTNValue)) {
                    mAsset = entry.getValue();
                    break;

                }
            }
            mImageAdapter = new ImageAdapter(mContext, mAsset);
            //This clause is added to populate the viewpager at least once , So that default image can be loaded .
            if(mAsset.size() == 0){
                mAsset.add("default image");
            }
            if (mAsset == null) {
                trackErrorTag(IAPAnalyticsConstant.PRX + mCTNValue + "_" + IAPAnalyticsConstant.No_IMAGES_FOUND);
            }
            mViewPager.setAdapter(mImageAdapter);
            mImageAdapter.notifyDataSetChanged();
            if (mBuyFromRetailers.isActivated()) {
                mBuyFromRetailers.hideProgressIndicator();
            }
        }
    }

    private void makeSummaryRequest() {
        ArrayList<String> ctnList = new ArrayList<>();
        ctnList.add(mCTNValue);
        if (!CartModelContainer.getInstance().isPRXSummaryPresent(mCTNValue)) {

            final PRXSummaryListExecutor builder = new PRXSummaryListExecutor(mContext, ctnList, this);
            builder.preparePRXDataRequest();
        } else {
            final ArrayList<Data> prxAssetObjects = CartModelContainer.getInstance().getPRXSummaryList();

            for (Data data : prxAssetObjects) {
                populateData(data);
            }

        }
        makeDisclaimerRequest();
    }

    private void makeDisclaimerRequest() {
        final ArrayList<String> ctnList = new ArrayList<>();
        ctnList.add(mCTNValue);
        final PRXDisclaimerExecutor builder = new PRXDisclaimerExecutor(mContext, ctnList, this);
        builder.preparePRXDataRequest();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBundle != null) {
            if (!mBundle.containsKey(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL)) {
                tagProduct();
                if (mBundle != null && mLaunchedFromProductCatalog) {
                    IAPAnalytics.trackPage(IAPAnalyticsConstant.PRODUCT_DETAIL_PAGE_NAME);
                    handleViews();
                } else {
                    mQuantityAndDelete.setVisibility(View.VISIBLE);
                    IAPAnalytics.trackPage(IAPAnalyticsConstant.SHOPPING_CART_ITEM_DETAIL_PAGE_NAME);
                    setCartIconVisibility(false);
                    final int quantity = getArguments().getInt(IAPConstant.PRODUCT_QUANTITY);
                    if (quantity != 0) {
                        setCountArrow(mContext, true);
                        mCheckutAndCountinue.setVisibility(View.GONE);
                        mQuantityAndDelete.setVisibility(View.VISIBLE);
                        mQuantity.setCompoundDrawables(null, null, countArrow, null);

                        mQuantity.setText(quantity + "");
                        final int stock = getArguments().getInt(IAPConstant.PRODUCT_STOCK);
                        bindCountView(mQuantity, stock, quantity);
                    } else {
                        mQuantityAndDelete.setVisibility(View.GONE);
                    }
                }
            } else {
                handleViews();
            }
        }
        makeAssetRequest();
        makeDisclaimerRequest();
        setTitleAndBackButtonVisibility(R.string.iap_product_detail_title, true);
    }

    private Drawable countArrow;
    private int mQuantityStatus;
    private int mNewCount;
    private UIPicker mPopupWindow;

    private void setCountArrow(final Context context, final boolean isEnable) {
        if (isEnable) {
            countArrow = context.getDrawable(R.drawable.iap_product_count_drop_down);
            countArrow.setColorFilter(new
                    PorterDuffColorFilter(mContext.getResources().getColor(R.color.uid_quiet_button_icon_selector), PorterDuff.Mode.MULTIPLY));
        } else {
            countArrow = VectorDrawableCompat.create(context.getResources(), R.drawable.iap_product_disable_count_drop_down, mContext.getTheme());
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
        EventHelper.getInstance().unregisterEventNotification(IAPConstant.IAP_LAUNCH_SHOPPING_CART, this);
        EventHelper.getInstance().unregisterEventNotification(IAP_UPDATE_PRODUCT_COUNT, this);
        EventHelper.getInstance().unregisterEventNotification(IAPConstant.IAP_DELETE_PRODUCT, this);
        EventHelper.getInstance().unregisterEventNotification(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED, this);
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

    private void buyFromRetailers(ArrayList<StoreEntity> storeEntities) {
        if (!isNetworkConnected())
            return;
        Bundle bundle = new Bundle();
        final ArrayList<StoreEntity> removedBlacklistedRetailers = removedBlacklistedRetailers(storeEntities);
        if (removedBlacklistedRetailers.size() == 1 && (removedBlacklistedRetailers.get(0).getIsPhilipsStore().equalsIgnoreCase("Y"))) {
            bundle.putString(IAPConstant.IAP_BUY_URL, storeEntities.get(0).getBuyURL());
            bundle.putString(IAPConstant.IAP_STORE_NAME, storeEntities.get(0).getName());
            bundle.putBoolean(IAPConstant.IAP_IS_PHILIPS_SHOP, new Utility().isPhilipsShop(storeEntities.get(0)));
            addFragment(WebBuyFromRetailers.createInstance(bundle, AnimationType.NONE), WebBuyFromRetailers.TAG, true);
        } else {

            bundle.putStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST, getArguments().getStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST));
            bundle.putSerializable(IAPConstant.IAP_RETAILER_INFO, removedBlacklistedRetailers);
            if (removedBlacklistedRetailers.isEmpty()) {
                onRetailerError(NetworkUtility.getInstance().
                        createIAPErrorMessage("", mContext.getString(R.string.iap_no_retailer_message)));
            } else {
                addFragment(BuyFromRetailersFragment.createInstance(bundle, AnimationType.NONE),
                        BuyFromRetailersFragment.TAG, true);
            }
        }
    }



    @SuppressWarnings("unchecked")
    @Override
    public void onFetchAssetSuccess(final Message msg) {
        if (mContext == null)
            return;
        mProgresImage.setVisibility(View.GONE);
        IAPLog.d(IAPConstant.PRODUCT_DETAIL_FRAGMENT, "Success");
        mAsset = (ArrayList<String>) msg.obj;
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

    @Override
    public void onFetchAssetFailure(final Message msg) {
        mProgresImage.setVisibility(View.GONE);
        IAPLog.d(IAPConstant.PRODUCT_DETAIL_FRAGMENT, "Failure");
        if (mBuyFromRetailers.isActivated()) {
            mBuyFromRetailers.hideProgressIndicator();
        }
        if (!isNetworkConnected()) {
            return;
        }
        NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);

        if (msg.obj instanceof IAPNetworkError) {
            final IAPNetworkError obj = (IAPNetworkError) msg.obj;
            mIapListener.onFailure(obj.getIAPErrorCode());
        }
    }

    void buyProduct(final String ctnNumber) {

        if(isUserLoggedIn()) {
            if (!mAddToCart.isActivated()) {
                mAddToCart.showProgressIndicator();
            }
            mShoppingCartAPI.buyProduct(mContext, ctnNumber, mBuyProductListener);
        }else{
            showLogInDialog();
        }
    }

    private void tagItemAddedToCart() {
        final HashMap<String, String> contextData = new HashMap<>();
        contextData.put(IAPAnalyticsConstant.ORIGINAL_PRICE, mPrice.getText().toString());
        if (mProductDiscountedPrice.getVisibility() == View.VISIBLE) {
            contextData.put(IAPAnalyticsConstant.DISCOUNTED_PRICE, mProductDiscountedPrice.getText().toString());
        }

        contextData.put(IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.ADD_TO_CART);
        IAPAnalytics.trackMultipleActions(IAPAnalyticsConstant.SEND_DATA, contextData);
    }

    @Override
    public void onClick(View v) {
        if (!isNetworkConnected()) {
            return;
        }
        if (v == mAddToCart) {
            buyProduct(mCTNValue);
        }
        if (v == mBuyFromRetailers) {
            getRetailersInformation();
        }
        if (v == mDeleteProduct) {
            deleteProduct();
        }
    }

    private void showDisclaimer(DisclaimerModel disclaimerModel) {
        try {
            final List<Disclaimer> disclaimerList = disclaimerModel.getData().getDisclaimers().getDisclaimer();
            mProductDisclaimer.setVisibility(View.VISIBLE);
            if (null != disclaimerList && disclaimerList.size() > 0) {
                final StringBuilder disclaimerStringBuilder = new StringBuilder();
                for (Disclaimer disclaimer : disclaimerList) {
                    disclaimerStringBuilder.append("- ").append(disclaimer.getDisclaimerText()).append(System.getProperty("line.separator"));
                }
                mProductDisclaimer.setText(disclaimerStringBuilder.toString());
            }
        } catch (Exception e) {
            IAPLog.v("DISCLAIMER_REQ", e.getMessage());
        }
    }

    private void deleteProduct() {
        Utility.showActionDialog(mContext, getString(R.string.iap_remove_product), getString(R.string.iap_cancel)
                , null, getString(R.string.iap_product_remove_description), getFragmentManager(), this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onModelDataLoadFinished(Message msg) {
        final HashMap<String, Data> msgObj = (HashMap<String, Data>) msg.obj;
        mProductSummary = msgObj.get(mCTNValue);
        populateData(mProductSummary);
        if (mBuyFromRetailers.isActivated()) {
            mBuyFromRetailers.hideProgressIndicator();
        }
        mDetailLayout.setVisibility(View.VISIBLE);
        if (mIapListener != null) {
            mIapListener.onSuccess();
        }
    }

    @Override
    public void onModelDataError(Message msg) {
        mDetailLayout.setVisibility(View.GONE);
        mBuyFromRetailers.setVisibility(View.GONE);
        showErrorDialog(msg);

        if (mBuyFromRetailers.isActivated()) {
            mBuyFromRetailers.hideProgressIndicator();
        }

        if (msg.obj instanceof IAPNetworkError) {
            IAPNetworkError obj = (IAPNetworkError) msg.obj;
            mIapListener.onFailure(obj.getIAPErrorCode());
        }
    }

    @Override
    public void onGetProductDetail(Message msg) {
        if (msg.obj instanceof IAPNetworkError) {
            hideProgressBar();
            mDetailLayout.setVisibility(View.GONE);
            showErrorDialog(msg);
        } else {
            if (msg.what == RequestCode.SEARCH_PRODUCT) {
                if (msg.obj instanceof ProductDetailEntity) {
                    mProductDetail = (ProductDetailEntity) msg.obj;
                    mCTNValue = mProductDetail.getCode();
                    fetchProductDetailFromPrx();
                    mDetailLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void populateData(Data data) {
        String actualPrice = null;
        String discountedPrice = null;
        String stockLevelStatus = null;
        int stockLevel;
        if (mBundle.containsKey(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL)) {
            if (data != null) {
                mProductTitle = data.getProductTitle();
                if (mProductTitle == null) {
                    trackErrorTag(IAPAnalyticsConstant.PRX + mCTNValue + "_" + IAPAnalyticsConstant.PRODUCT_TITLE_MISSING);
                }
                mProductDescription.setText(mProductTitle);
                mCTN.setText(mCTNValue);
                mProductOverview.setText(data.getMarketingTextHeader());
                trackErrorTag(IAPAnalyticsConstant.PRX + mCTNValue + "_" + IAPAnalyticsConstant.PRODUCT_DESCRIPTION_MISSING);
                if (mProductDetail != null) {
                    actualPrice = mProductDetail.getPrice().getFormattedValue();
                    discountedPrice = mProductDetail.getDiscountPrice().getFormattedValue();
                    setPrice(actualPrice, discountedPrice);
                    setStockInfo(mProductDetail.getStock().getStockLevelStatus(), mProductDetail.getStock().getStockLevel());
                } else {
                    mPrice.setVisibility(View.GONE);
                    mProductDiscountedPrice.setVisibility(View.GONE);
                    mProductStockInfo.setVisibility(View.GONE);
                }
            }
        } else {
            actualPrice = mBundle.getString(IAPConstant.PRODUCT_PRICE);
            discountedPrice = mBundle.getString(IAPConstant.IAP_PRODUCT_DISCOUNTED_PRICE);
            stockLevelStatus = mBundle.getString(IAPConstant.STOCK_LEVEL_STATUS);
            stockLevel = mBundle.getInt(IAPConstant.STOCK_LEVEL);

            mProductDescription.setText(mBundle.getString(IAPConstant.PRODUCT_TITLE));
            mCTN.setText(mBundle.getString(IAPConstant.PRODUCT_CTN));
            mProductOverview.setText(mBundle.getString(IAPConstant.PRODUCT_OVERVIEW));

            if (mBundle.getString(IAPConstant.PRODUCT_TITLE) == null) {
                trackErrorTag(IAPAnalyticsConstant.PRX + mCTNValue + "_" + IAPAnalyticsConstant.PRODUCT_TITLE_MISSING);
            }
            if (mBundle.getString(IAPConstant.PRODUCT_OVERVIEW) == null) {
                trackErrorTag(IAPAnalyticsConstant.PRX + mCTNValue + "_" + IAPAnalyticsConstant.PRODUCT_DESCRIPTION_MISSING);
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
        final IAPStockAvailabilityHelper iapStockAvailabilityHelper = new IAPStockAvailabilityHelper();
        if (iapStockAvailabilityHelper.isStockAvailable(stockLevelStatus, stockLevel)) {
            mAddToCart.setEnabled(true);
            mProductStockInfo.setVisibility(View.GONE);
        } else {
            mAddToCart.setEnabled(false);
            mProductStockInfo.setText(mContext.getString(R.string.iap_out_of_stock));
            mProductStockInfo.setTextColor(ContextCompat.getColor(mContext, R.color.uid_signal_red_level_60));
            IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                    IAPAnalyticsConstant.OUT_OF_STOCK, stockLevelStatus);
        }
    }

    private void trackErrorTag(String value) {
        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                IAPAnalyticsConstant.ERROR, value);
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

        if (!data.isEmpty() && data.get(0) instanceof StoreEntity) {
            mBuyFromRetailers.hideProgressIndicator();
            buyFromRetailers((ArrayList<StoreEntity>) data);
        }
    }

    private ShoppingCartData getShoppingCartDataFromCTN(ArrayList<?> data) {
        final String lCtn = mBundle.getString(IAPConstant.PRODUCT_CTN);

        for (ShoppingCartData shoppingCartData : (ArrayList<ShoppingCartData>) data) {
            if (shoppingCartData.getCtnNumber().equalsIgnoreCase(lCtn))
                return shoppingCartData;
        }
        return null;
    }

    @Override
    public void onLoadError(Message msg) {
        IAPLog.d(IAPLog.LOG, "onLoadError == ProductDetailFragment ");
        mBuyFromRetailers.hideProgressIndicator();
        if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, ((FragmentActivity) mContext).getSupportFragmentManager(), mContext);
        } else {
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
    public void onEventReceived(String event) {
        if (event.equalsIgnoreCase(String.valueOf(IAPConstant.IAP_LAUNCH_SHOPPING_CART))) {
            startShoppingCartFragment();
        } else if (event.equalsIgnoreCase(IAP_UPDATE_PRODUCT_COUNT)) {
            createCustomProgressBar(mParentLayout, BIG);
            final ShoppingCartData shoppingCartData = (ShoppingCartData) mBundle.getSerializable(IAPConstant.SHOPPING_CART_CODE);
            mShoppingCartAPI.updateProductQuantity(shoppingCartData, getNewCount(), getQuantityStatusInfo());

        } else if (event.equalsIgnoreCase(IAPConstant.IAP_DELETE_PRODUCT)) {
            hideProgressBar();
            createCustomProgressBar(mParentLayout, BIG);
            final ShoppingCartData shoppingCartData = (ShoppingCartData) mBundle.getSerializable(IAPConstant.SHOPPING_CART_CODE);
            mShoppingCartAPI.deleteProduct(shoppingCartData);

        } else if (event.equalsIgnoreCase(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED)) {
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
        if (getActivity() != null && getActivity() instanceof IAPActivity) {
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
            bundle.putString(IAPConstant.SINGLE_BUTTON_DIALOG_TITLE,
                    NetworkUtility.getInstance().getErrorTitleMessageFromErrorCode(mContext, error.getIAPErrorCode()));
            bundle.putString(IAPConstant.SINGLE_BUTTON_DIALOG_DESCRIPTION,
                    NetworkUtility.getInstance().getErrorDescriptionMessageFromErrorCode(mContext, error));
        } else {
            bundle.putString(IAPConstant.SINGLE_BUTTON_DIALOG_DESCRIPTION, (String) msg.obj);
        }

        bundle.putString(IAPConstant.SINGLE_BUTTON_DIALOG_TEXT, mContext.getString(R.string.iap_ok));

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
            IAPLog.e(IAPLog.LOG, e.getMessage());
        }
    }

    @Override
    public boolean handleBackEvent() {
        if (mIsFromVertical) {
            if (getActivity() != null && getActivity() instanceof IAPActivity) {
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

    private ArrayList<StoreEntity> removedBlacklistedRetailers(ArrayList<StoreEntity> pStoreEntity) {
        final ArrayList<String> list = getArguments().getStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST);
        mUpdtedStoreEntity = new ArrayList<>();
        mUpdtedStoreEntity.addAll(pStoreEntity);
        for (StoreEntity storeEntity : pStoreEntity) {
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
        EventHelper.getInstance().notifyEventOccurred(IAPConstant.IAP_DELETE_PRODUCT);
    }

    @Override
    public void onNegativeBtnClick() {

    }

    @Override
    public void onFetchProductDisclaimerSuccess(DisclaimerModel disclaimerModel) {
        if (null != disclaimerModel) {
            showDisclaimer(disclaimerModel);
        }
    }

    @Override
    public void onFetchProductDisclaimerFailure(String error) {

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
}
