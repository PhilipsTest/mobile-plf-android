/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.activity.IAPActivity;
import com.philips.cdp.di.iap.adapters.ProductCatalogAdapter;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.cart.ShoppingCartAPI;
import com.philips.cdp.di.iap.cart.ShoppingCartPresenter;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.controller.ControllerFactory;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.eventhelper.EventListener;
import com.philips.cdp.di.iap.products.ProductCatalogAPI;
import com.philips.cdp.di.iap.products.ProductCatalogData;
import com.philips.cdp.di.iap.products.ProductCatalogPresenter;
import com.philips.cdp.di.iap.response.products.PaginationEntity;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.IAPUtility;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.uid.text.utils.UIDClickableSpan;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;
import com.philips.platform.uid.view.widget.SearchBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProductCatalogFragment extends InAppBaseFragment
        implements EventListener, ProductCatalogPresenter.ProductCatalogListener, SearchBox.ExpandListener, SearchBox.QuerySubmitListener {

    public static final String TAG = ProductCatalogFragment.class.getName();

    private Context mContext;

    private TextView mEmptyCatalogText;

    private Label mPrivacy;
    private LinearLayout mPrivacyLayout;
    private View mView;

    private ProductCatalogAdapter mAdapter;
    private ShoppingCartAPI mShoppingCartAPI;
    private RecyclerView mRecyclerView;
    private SearchBox mSearchBox;
    private ProductCatalogAPI mPresenter;
    private List<ProductCatalogData> mProductCatalog = new CopyOnWriteArrayList<>();

    private final int page_size = 20;
    private int mTotalResults = 0;
    private int mCurrentPage = -1;
    private int mRemainingProducts = 0;
    private int mTotalPages = -1;
    private Bundle mBundle;
    private boolean mIsLoading = false;
    private RelativeLayout mParentLayout;
    private AppCompatAutoCompleteTextView mSearchTextView;
    private LinearLayout mBannerLayout;

    public static ProductCatalogFragment createInstance(Bundle args, InAppBaseFragment.AnimationType animType) {
        ProductCatalogFragment fragment = new ProductCatalogFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = ControllerFactory.getInstance()
                .getProductCatalogPresenter(mContext, this);
        mAdapter = new ProductCatalogAdapter(mContext, mProductCatalog);

        mTotalResults = IAPUtility.getInstance().getmTotalResults();
        mCurrentPage = IAPUtility.getInstance().getmCurrentPage();
        mRemainingProducts = IAPUtility.getInstance().getmRemainingProducts();
        mTotalPages = IAPUtility.getInstance().getmTotalPages();

    }


    private ArrayList<ProductCatalogData> getCachedProductList() {
        ArrayList<ProductCatalogData> mProductList = new ArrayList<>();
        HashMap<String, ProductCatalogData> productCatalogDataSaved =
                CartModelContainer.getInstance().getProductList();

        for (Map.Entry<String, ProductCatalogData> entry : productCatalogDataSaved.entrySet()) {
            if (entry != null) {
                mProductList.add(entry.getValue());
            }
        }
        return mProductList;
    }

    private void displayCategorisedProductList(ArrayList<String> categorisedProductList) {
        if (categorisedProductList.size() > 0) {
            createCustomProgressBar(mParentLayout, BIG);
            mPresenter.getCategorizedProductList(categorisedProductList);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventHelper.getInstance().registerEventNotification
                (String.valueOf(IAPConstant.IAP_LAUNCH_PRODUCT_DETAIL), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED), this);

        final View rootView = inflater.inflate(R.layout.iap_product_catalog_view, container, false);
        mRecyclerView = rootView.findViewById(R.id.product_catalog_recycler_view);
        mSearchBox = rootView.findViewById(R.id.iap_search_box);
        mBannerLayout = rootView.findViewById(R.id.ll_banner_place_holder);
        mView = rootView.findViewById(R.id.iap_productDetailsScreen_separator);
        mPrivacyLayout = rootView.findViewById(R.id.iap_privacyLayout);
        mPrivacy = rootView.findViewById(R.id.iap_privacy);
        privacyTextView(mPrivacy);

        if (IAPUtility.getInstance().getBannerView() != null) {
            if (IAPUtility.getInstance().getBannerView().getParent() != null) {
                ((ViewGroup) IAPUtility.getInstance().getBannerView().getParent()).removeAllViews();
            }
            mBannerLayout.addView(IAPUtility.getInstance().getBannerView());
            mBannerLayout.setVisibility(View.VISIBLE);
        }
        mParentLayout = rootView.findViewById(R.id.parent_layout);
        setUpSearch();

        mEmptyCatalogText = rootView.findViewById(R.id.iap_productCatalog_emptyProductCatalogText_lebel);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);

        mShoppingCartAPI = new ShoppingCartPresenter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerViewSeparatorItemDecoration(getContext()));

        mBundle = getArguments();

        return rootView;
    }


    private void privacyTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                (mContext.getString(R.string.iap_read)));
        spanTxt.append(" ");
        spanTxt.append(mContext.getString(R.string.iap_privacy));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showPrivacyFragment();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(R.attr.uidHyperlinkDefaultPressedTextColor);
            }
        }, spanTxt.length() - mContext.getString(R.string.iap_privacy).length(), spanTxt.length(), 0);
        spanTxt.append(" ");
        spanTxt.append(mContext.getString(R.string.iap_more_info));
        mPrivacy.setHighlightColor(Color.TRANSPARENT);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    private void showPrivacyFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(IAPConstant.IAP_PRIVACY_URL, IAPUtility.getInstance().getPrivacyUrl());
        addFragment(WebPrivacy.createInstance(bundle, AnimationType.NONE), null, true);
    }

    private void setUpSearch() {
        ImageView mClearIconView = mSearchBox.getClearIconView();
        mSearchBox.setExpandListener(this);
        mSearchBox.setSearchBoxHint(R.string.iap_search_box_hint);
        mSearchBox.setDecoySearchViewHint(R.string.iap_search_box_hint);
        mSearchBox.setQuerySubmitListener(this);
        mSearchTextView = mSearchBox.getSearchTextView();
        mSearchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    resetAdapter();
                    return;
                }
                String filterText = s.toString().toLowerCase(Locale.getDefault());
                mAdapter.filter(filterText);
            }
        });

        mClearIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchTextView.getText().clear();
                resetAdapter();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        boolean isLocalData = ControllerFactory.getInstance().isPlanB();

        if (!isLocalData &&
                CartModelContainer.getInstance().getProductList() != null
                && CartModelContainer.getInstance().getProductList().size() != 0) {
            onLoadFinished(getCachedProductList(), IAPUtility.getInstance().getPaginationEntity());
        } else {
            fetchProductList();
        }

        IAPAnalytics.trackPage(IAPAnalyticsConstant.PRODUCT_CATALOG_PAGE_NAME);

        setTitleAndBackButtonVisibility(R.string.iap_product_catalog, true);
        if (!ControllerFactory.getInstance().isPlanB()) {
            setCartIconVisibility(true);
            if (isUserLoggedIn())
                mShoppingCartAPI.getProductCartCount(mContext, mProductCountListener);
        }

        if(!(ControllerFactory.getInstance().isPlanB()) && (IAPUtility.getInstance().getPrivacyUrl() != null)) {
            mPrivacyLayout.setVisibility(View.VISIBLE);
            mView.setVisibility(View.VISIBLE);
        } else {
            mPrivacyLayout.setVisibility(View.GONE);
            mView.setVisibility(View.GONE);
        }

        mAdapter.tagProducts();
    }

    @Override
    public void onEventReceived(final String event) {
        if (event.equalsIgnoreCase(String.valueOf(IAPConstant.IAP_LAUNCH_PRODUCT_DETAIL))) {
            launchProductDetailFragment();
        } else if (event.equals(String.valueOf(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED))) {
            mIsLoading = false;
            hideProgressBar();
            onLoadError(NetworkUtility.getInstance().createIAPErrorMessage
                    ("", mContext.getString(R.string.iap_no_product_available)));
        }
    }

    private void launchProductDetailFragment() {
        ProductCatalogData productCatalogData = mAdapter.getTheProductDataForDisplayingInProductDetailPage();
        Bundle bundle = new Bundle();
        if (productCatalogData != null) {
            bundle.putString(IAPConstant.PRODUCT_TITLE, productCatalogData.getProductTitle());
            bundle.putString(IAPConstant.PRODUCT_CTN, productCatalogData.getCtnNumber());
            bundle.putString(IAPConstant.PRODUCT_PRICE, productCatalogData.getFormattedPrice());
            bundle.putString(IAPConstant.PRODUCT_VALUE_PRICE, productCatalogData.getPriceValue());
            bundle.putString(IAPConstant.PRODUCT_OVERVIEW, productCatalogData.getMarketingTextHeader());
            bundle.putString(IAPConstant.IAP_PRODUCT_DISCOUNTED_PRICE, productCatalogData.getDiscountedPrice());
            bundle.putString(IAPConstant.STOCK_LEVEL_STATUS, productCatalogData.getStockLevelStatus());
            bundle.putInt(IAPConstant.STOCK_LEVEL, productCatalogData.getStockLevel());
            bundle.putBoolean(IAPConstant.IS_PRODUCT_CATALOG, true);
            if (getArguments().getStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST) != null) {
                final ArrayList<String> list = getArguments().getStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST);
                bundle.putStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST, list);
            }
            addFragment(ProductDetailFragment.createInstance(bundle, AnimationType.NONE), ProductDetailFragment.TAG, true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventHelper.getInstance().unregisterEventNotification
                (String.valueOf(IAPConstant.IAP_LAUNCH_PRODUCT_DETAIL), this);
        EventHelper.getInstance().unregisterEventNotification
                (String.valueOf(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED), this);
    }

    @Override
    public boolean handleBackEvent() {
        if (getActivity() != null && getActivity() instanceof IAPActivity) {
            int count = getFragmentManager().getBackStackEntryCount();
            for (int i = 0; i < count; i++) {
                getFragmentManager().popBackStack();
            }
            finishActivity();
        }
        return super.handleBackEvent();
    }

    private void fetchProductList() {
        createCustomProgressBar(mParentLayout, BIG);

        if (mPresenter == null)
            mPresenter = ControllerFactory.getInstance().
                    getProductCatalogPresenter(mContext, this);

        if (ControllerFactory.getInstance().isPlanB() && isCategorizedFlow()) {
            mPresenter.getCategorizedProductList(getCategorizedCTNs());
        } else {
            mPresenter.getProductCatalog(++mCurrentPage, page_size, null);
            IAPUtility.getInstance().setmCurrentPage(mCurrentPage);
        }

    }

    private void loadMoreItems() {
        mIsLoading = true;
        if (mCurrentPage + 1 < mTotalPages) {
            fetchProductList();
        } else {

            if (mAdapter.getItemCount() == 0) {
                onLoadError(NetworkUtility.getInstance().createIAPErrorMessage
                        ("", mContext.getString(R.string.iap_no_product_available)));
            }
            hideProgressBar();
        }

    }

    @Override
    public void onLoadFinished(ArrayList<ProductCatalogData> dataFetched,
                               PaginationEntity paginationEntity) {
        IAPUtility.getInstance().setPaginationEntity(paginationEntity);
        if (dataFetched.size() > 0) {

            if (isCategorizedFlow()) {
                dataFetched = handleCategorizedFlow();
            }

            updateProductCatalogList(dataFetched);
            mAdapter.notifyDataSetChanged();
            mAdapter.tagProducts();
            mIapListener.onSuccess();


            if (paginationEntity == null) {
                hideProgressBar();
                return;
            }

            if (mTotalResults == 0)
                mRemainingProducts = paginationEntity.getTotalResults();

            IAPUtility.getInstance().setmRemainingProducts(mRemainingProducts);


            mTotalResults = paginationEntity.getTotalResults();
            IAPUtility.getInstance().setmTotalResults(mTotalResults);

            mCurrentPage = paginationEntity.getCurrentPage();


            mTotalPages = paginationEntity.getTotalPages();
            IAPUtility.getInstance().setmTotalPages(mTotalPages);

            mIsLoading = false;
            hideProgressBar();

            if (isCategorizedFlow() && shouldLoadMore()) {
                loadMoreItems();
            } else if(isCategorizedFlow() && dataFetched.size() == 0) {
                onLoadError(NetworkUtility.getInstance().createIAPErrorMessage
                        ("", mContext.getString(R.string.iap_no_product_available)));
            }

        } else {
            onLoadError(NetworkUtility.getInstance().createIAPErrorMessage
                    ("", mContext.getString(R.string.iap_no_product_available)));
        }
    }

    @Nullable
    private ArrayList<ProductCatalogData> handleCategorizedFlow() {
        ArrayList<ProductCatalogData> productCatalogList = new ArrayList<>();
        CartModelContainer container = CartModelContainer.getInstance();
        for (String ctn : getCategorizedCTNs()) {
            if (container.isProductCatalogDataPresent(ctn)) {
                productCatalogList.add(container.getProduct(ctn));
            }
        }
        return productCatalogList;
    }

    @Override
    public void onLoadError(IAPNetworkError error) {

        if (error.getMessage() != null
                && error.getMessage().equalsIgnoreCase(mContext.getResources().getString(R.string.iap_no_product_available))) {
            if (mAdapter.getItemCount() > 0) return;
            if (mRecyclerView != null && mEmptyCatalogText != null) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyCatalogText.setVisibility(View.VISIBLE);
                mSearchBox.setVisibility(View.GONE);
            }
        } else {
            if (mRecyclerView != null && mEmptyCatalogText != null) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyCatalogText.setVisibility(View.GONE);
            }
            NetworkUtility.getInstance().showErrorDialog(mContext, getFragmentManager(),
                    mContext.getString(R.string.iap_ok),
                    NetworkUtility.getInstance().getErrorTitleMessageFromErrorCode(mContext, error.getIAPErrorCode()),
                    NetworkUtility.getInstance().getErrorDescriptionMessageFromErrorCode(mContext, error));
        }

        mIapListener.onFailure(error.getIAPErrorCode());
        hideProgressBar();
    }

    private void updateProductCatalogList(final ArrayList<ProductCatalogData> dataFetched) {
        for (ProductCatalogData data : dataFetched) {
            if (!checkIfEntryExists(data))
                mProductCatalog.add(data);
        }
    }

    private boolean checkIfEntryExists(final ProductCatalogData data) {
        for (ProductCatalogData entry : mProductCatalog) {
            final String ctnNumber = entry.getCtnNumber();

            if (ctnNumber != null && ctnNumber.equalsIgnoreCase(data.getCtnNumber())) {
                return true;
            }
        }
        return false;
    }

    private RecyclerView.OnScrollListener
            mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager lay = (LinearLayoutManager) mRecyclerView
                    .getLayoutManager();

            int visibleItemCount = lay.getChildCount();
            int firstVisibleItemPosition = lay.findFirstVisibleItemPosition();

            //Check if scroll down
            if (isScrollDown(lay, visibleItemCount, firstVisibleItemPosition)) {

                if (shouldLoadMore()) {
                    loadMoreItems();
                }

            }
        }
    };

    private boolean shouldLoadMore() {
        return !mIsLoading && mRemainingProducts > page_size;
    }

    private boolean isScrollDown(LinearLayoutManager lay, int visibleItemCount, int firstVisibleItemPosition) {
        return (visibleItemCount + firstVisibleItemPosition) >= lay.getItemCount()
                && firstVisibleItemPosition >= 0;
    }

    @Override
    public void onSearchExpanded() {
        //Do nothing
    }

    @Override
    public void onSearchCollapsed() {
        resetAdapter();
    }

    void resetAdapter() {
        mAdapter.setSearchFocused(false);
        mAdapter.setData(mProductCatalog);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        mSearchBox.setSearchCollapsed(true);
        mProductCatalog.clear();
        resetAdapter();
        super.onStop();
    }

    @Override
    public void onQuerySubmit(CharSequence query) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
    }

    boolean isCategorizedFlow() {
        return mBundle != null && mBundle.getStringArrayList(IAPConstant.CATEGORISED_PRODUCT_CTNS) != null && mBundle.getStringArrayList(IAPConstant.CATEGORISED_PRODUCT_CTNS).size() != 0;
    }

    ArrayList<String> getCategorizedCTNs() {
        return mBundle.getStringArrayList(IAPConstant.CATEGORISED_PRODUCT_CTNS);
    }

}
