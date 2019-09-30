/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.screens;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.activity.ECSActivity;
import com.ecs.demouapp.ui.adapters.ProductCatalogAdapter;
import com.ecs.demouapp.ui.analytics.ECSAnalytics;
import com.ecs.demouapp.ui.analytics.ECSAnalyticsConstant;
import com.ecs.demouapp.ui.cart.ShoppingCartAPI;
import com.ecs.demouapp.ui.cart.ShoppingCartPresenter;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.controller.ControllerFactory;
import com.ecs.demouapp.ui.eventhelper.EventHelper;
import com.ecs.demouapp.ui.eventhelper.EventListener;
import com.ecs.demouapp.ui.products.ProductCatalogAPI;
import com.ecs.demouapp.ui.session.IAPNetworkError;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.ecs.demouapp.ui.utils.NetworkUtility;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.products.PaginationEntity;
import com.philips.cdp.di.ecs.model.products.ECSProduct;
import com.philips.cdp.di.ecs.model.products.ECSProducts;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;
import com.philips.platform.uid.view.widget.SearchBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;


public class ProductCatalogFragment extends InAppBaseFragment
        implements EventListener, SearchBox.ExpandListener, SearchBox.QuerySubmitListener, ECSCallback<ECSProducts, Exception> {

    public static final String TAG = ProductCatalogFragment.class.getName();

    private Context mContext;

    private TextView mEmptyCatalogText;

    private ProductCatalogAdapter mAdapter;
    private ShoppingCartAPI mShoppingCartAPI;
    private RecyclerView mRecyclerView;
    private SearchBox mSearchBox;
    private ProductCatalogAPI mPresenter;
    private List<ECSProduct> mProduct = new CopyOnWriteArrayList<>();

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
    private ECSProducts products;

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
                .getProductCatalogPresenter();
        mAdapter = new ProductCatalogAdapter(mContext, mProduct);
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
                (String.valueOf(ECSConstant.IAP_LAUNCH_PRODUCT_DETAIL), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED), this);

        final View rootView = inflater.inflate(R.layout.ecs_product_catalog_view, container, false);
        mRecyclerView = rootView.findViewById(R.id.product_catalog_recycler_view);
        mSearchBox = rootView.findViewById(R.id.iap_search_box);
        mBannerLayout = rootView.findViewById(R.id.ll_banner_place_holder);
        TextView mPrivacyPolicy = rootView.findViewById(R.id.iap_privacy);

        mPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(WebPrivacy.createInstance(new Bundle(), AnimationType.NONE), null, true);
            }
        });

        if (ECSUtility.getInstance().getBannerView() != null) {
            if (ECSUtility.getInstance().getBannerView().getParent() != null) {
                ((ViewGroup) ECSUtility.getInstance().getBannerView().getParent()).removeAllViews();
            }
            mBannerLayout.addView(ECSUtility.getInstance().getBannerView());
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

        fetchProductList();

        ECSAnalytics.trackPage(ECSAnalyticsConstant.PRODUCT_CATALOG_PAGE_NAME);

        setTitleAndBackButtonVisibility(R.string.iap_product_catalog, true);

        if (!ControllerFactory.getInstance().isPlanB()) {
            setCartIconVisibility(true);
            if (isUserLoggedIn())
                mShoppingCartAPI.getProductCartCount(mContext, mProductCountListener);
        }

        mAdapter.tagProducts();
    }

    @Override
    public void onEventReceived(final String event) {
        if (event.equalsIgnoreCase(String.valueOf(ECSConstant.IAP_LAUNCH_PRODUCT_DETAIL))) {
            launchProductDetailFragment();
        } else if (event.equals(String.valueOf(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED))) {
            mIsLoading = false;
            hideProgressBar();
            onLoadError(NetworkUtility.getInstance().createIAPErrorMessage
                    ("", mContext.getString(R.string.iap_no_product_available)));
        }
    }

    private void launchProductDetailFragment() {
        ECSProduct productCatalogData = mAdapter.getTheProductDataForDisplayingInProductDetailPage();
        Bundle bundle = new Bundle();
        if (productCatalogData != null) {
            bundle.putString(ECSConstant.PRODUCT_TITLE, productCatalogData.getName());
            bundle.putString(ECSConstant.PRODUCT_CTN, productCatalogData.getCode());
            if(productCatalogData.getPrice()!=null)
            bundle.putString(ECSConstant.PRODUCT_PRICE, productCatalogData.getPrice().getFormattedValue());

            if(productCatalogData.getPrice()!=null)
            bundle.putString(ECSConstant.PRODUCT_VALUE_PRICE, String.valueOf(productCatalogData.getPrice().getValue()));

            bundle.putString(ECSConstant.PRODUCT_OVERVIEW, productCatalogData.getSummary().getMarketingTextHeader());

            if(productCatalogData.getDiscountPrice()!=null)
            bundle.putString(ECSConstant.IAP_PRODUCT_DISCOUNTED_PRICE, productCatalogData.getDiscountPrice().getFormattedValue());
            if(productCatalogData.getStock()!=null) {
                bundle.putString(ECSConstant.STOCK_LEVEL_STATUS, productCatalogData.getStock().getStockLevelStatus());
                bundle.putInt(ECSConstant.STOCK_LEVEL, productCatalogData.getStock().getStockLevel());
            }
            bundle.putBoolean(ECSConstant.IS_PRODUCT_CATALOG, true);
            bundle.putSerializable("ProductCatalogData", productCatalogData);
            if (getArguments().getStringArrayList(ECSConstant.IAP_IGNORE_RETAILER_LIST) != null) {
                final ArrayList<String> list = getArguments().getStringArrayList(ECSConstant.IAP_IGNORE_RETAILER_LIST);
                bundle.putStringArrayList(ECSConstant.IAP_IGNORE_RETAILER_LIST, list);
            }
            addFragment(ProductDetailFragment.createInstance(bundle, AnimationType.NONE), ProductDetailFragment.TAG, true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventHelper.getInstance().unregisterEventNotification
                (String.valueOf(ECSConstant.IAP_LAUNCH_PRODUCT_DETAIL), this);
        EventHelper.getInstance().unregisterEventNotification
                (String.valueOf(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED), this);
    }

    @Override
    public boolean handleBackEvent() {
        if (getActivity() != null && getActivity() instanceof ECSActivity) {
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
                    getProductCatalogPresenter();

        if (ControllerFactory.getInstance().isPlanB() && isCategorizedFlow()) {
            mPresenter.getCategorizedProductList(getCategorizedCTNs(),this);
        } else {
            mPresenter.getProductCatalog(++mCurrentPage, page_size, this);
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
                    NetworkUtility.getInstance().getErrorTitleMessageFromErrorCode(mContext, 2),
                    NetworkUtility.getInstance().getErrorDescriptionMessageFromErrorCode(mContext, new IAPNetworkError(null, 0, null)));
        }

        mIapListener.onFailure(2);
        hideProgressBar();
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
        mAdapter.setData(mProduct);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        mSearchBox.setSearchCollapsed(true);
        mProduct.clear();
        resetAdapter();
        mCurrentPage = -1;
        super.onStop();
    }

    @Override
    public void onQuerySubmit(CharSequence query) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
    }

    boolean isCategorizedFlow() {
        return mBundle != null && mBundle.getStringArrayList(ECSConstant.CATEGORISED_PRODUCT_CTNS) != null && mBundle.getStringArrayList(ECSConstant.CATEGORISED_PRODUCT_CTNS).size() != 0;
    }

    ArrayList<String> getCategorizedCTNs() {
        return mBundle.getStringArrayList(ECSConstant.CATEGORISED_PRODUCT_CTNS);
    }


    @Override
    public void onResponse(ECSProducts result) {



        PaginationEntity paginationEntity = result.getPagination();
        ECSUtility.getInstance().setPaginationEntity(paginationEntity);
        List<ECSProduct> products = result.getProducts();

        List<ECSProduct> cataGorizedList = new ArrayList<>();
        ArrayList<String> categorizedCTNs = getCategorizedCTNs();


        if (products.size() > 0) {

            if (isCategorizedFlow()) {

                for(ECSProduct product:products){

                    if(categorizedCTNs.contains(product.getCode())){

                        cataGorizedList.add(product);

                    }

                }
                products = cataGorizedList;
            }

            mProduct.addAll(products);
            mAdapter = new ProductCatalogAdapter(getActivity(),mProduct);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mAdapter.tagProducts();
            mIapListener.onSuccess();


            if (paginationEntity == null) {
                hideProgressBar();
                return;
            }

            if (mTotalResults == 0)
                mRemainingProducts = paginationEntity.getTotalResults();



            mTotalResults = paginationEntity.getTotalResults();

            mCurrentPage = paginationEntity.getCurrentPage();


            mTotalPages = paginationEntity.getTotalPages();

            mIsLoading = false;
            hideProgressBar();

            if (isCategorizedFlow() && shouldLoadMore()) {
                loadMoreItems();
            } else if(isCategorizedFlow() && mProduct.size() == 0) {
                onLoadError(NetworkUtility.getInstance().createIAPErrorMessage
                        ("", mContext.getString(R.string.iap_no_product_available)));
            }


        } else {
            onLoadError(NetworkUtility.getInstance().createIAPErrorMessage
                    ("", mContext.getString(R.string.iap_no_product_available)));
        }
    }


    @Override
    public void onFailure(Exception error, ECSError ecsError) {
        ECSUtility.showECSAlertDialog(mContext,"Error", error);
        //onLoadError(new IAPNetworkError(new VolleyError(error.getMessage()), errorCode, null));
    }
}
