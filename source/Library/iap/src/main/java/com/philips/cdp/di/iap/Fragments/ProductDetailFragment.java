package com.philips.cdp.di.iap.Fragments;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.ShoppingCart.IAPCartListener;
import com.philips.cdp.di.iap.ShoppingCart.PRXProductAssetBuilder;
import com.philips.cdp.di.iap.ShoppingCart.ShoppingCartPresenter;
import com.philips.cdp.di.iap.adapters.ImageAdapter;
import com.philips.cdp.di.iap.model.ModelConstants;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.uikit.customviews.CircleIndicator;
import com.philips.cdp.uikit.drawable.VectorDrawable;

import java.util.ArrayList;

public class ProductDetailFragment extends BaseAnimationSupportFragment implements PRXProductAssetBuilder.AssetListener {
    public static final String TAG = ProductDetailFragment.class.getName();
    private Context mContext;
    TextView mProductDescription;
    TextView mCTN;
    TextView mPrice;
    TextView mProductOverview;
    Button mAddToCart;
    Button mBuyFromRetailors;
    ArrayList<String> mAsset;
    ImageAdapter mAdapter;
    ViewPager mPager;
    Bundle mBundle;
    private ShoppingCartPresenter mShoppingCartPresenter;
    private boolean mLaunchedFromProductCatalog = false;
    private String mCTNValue;
    private String mProductTitle;
    private TextView mProductDiscountedPrice;

    private IAPCartListener mBuyProductListener = new IAPCartListener() {
        @Override
        public void onSuccess(final int count) {
//            Utility.dismissProgressDialog();
        }

        @Override
        public void onFailure(final Message msg) {
            Utility.dismissProgressDialog();

            IAPNetworkError iapNetworkError = (IAPNetworkError) msg.obj;
            if (null != iapNetworkError.getServerError()) {
                checkForOutOfStock(iapNetworkError, msg);
            } else {
                NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), getContext());
            }

        }
    };

    public static ProductDetailFragment createInstance(Bundle args, AnimationType animType) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mShoppingCartPresenter = new ShoppingCartPresenter(getFragmentManager());
        View rootView = inflater.inflate(R.layout.iap_product_details_screen, container, false);
        mBundle = getArguments();

        mCTNValue = mBundle.getString(IAPConstant.PRODUCT_CTN);
        mLaunchedFromProductCatalog = mBundle.getBoolean(IAPConstant.IS_PRODUCT_CATALOG, false);
        mProductTitle = mBundle.getString(IAPConstant.PRODUCT_TITLE);
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mAdapter = new ImageAdapter(getFragmentManager(), mLaunchedFromProductCatalog);
        mPager.setAdapter(mAdapter);

        CircleIndicator indicator = (CircleIndicator) rootView.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        mProductDescription = (TextView) rootView.findViewById(R.id.product_description);
        mCTN = (TextView) rootView.findViewById(R.id.ctn);
        mPrice = (TextView) rootView.findViewById(R.id.individual_price);
        mProductOverview = (TextView) rootView.findViewById(R.id.product_overview);
        mAddToCart = (Button) rootView.findViewById(R.id.add_to_cart);
        mBuyFromRetailors = (Button) rootView.findViewById(R.id.buy_from_retailor);
        mProductDiscountedPrice = (TextView) rootView.findViewById(R.id.tv_discounted_price);

        populateViewFromBundle();
        makeAssetRequest();
        return rootView;
    }

    private void populateViewFromBundle() {
        mProductDescription.setText(mBundle.getString(IAPConstant.PRODUCT_TITLE));
        mCTN.setText(mBundle.getString(IAPConstant.PRODUCT_CTN));
        mPrice.setText(mBundle.getString(IAPConstant.PRODUCT_PRICE));
        mProductOverview.setText(mBundle.getString(IAPConstant.PRODUCT_OVERVIEW));
        String discountedPrice = mBundle.getString(IAPConstant.IAP_PRODUCT_DISCOUNTED_PRICE);

        if (mLaunchedFromProductCatalog) {
            mProductDiscountedPrice.setVisibility(View.VISIBLE);
            updateCount(mBundle.getInt(IAPConstant.IAP_PRODUCT_COUNT));
            mPrice.setPaintFlags(mPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if (discountedPrice != null || discountedPrice != "" || !discountedPrice.isEmpty()) {
                mProductDiscountedPrice.setText(discountedPrice);
            }
        }
    }

    private void makeAssetRequest() {
        if (!Utility.isProgressDialogShowing()) {
            Utility.showProgressDialog(getContext(), getString(R.string.iap_please_wait));
        }
        String ctn = mBundle.getString(IAPConstant.PRODUCT_CTN);
        PRXProductAssetBuilder builder = new PRXProductAssetBuilder(mContext, ctn, this);
        builder.build();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.iap_shopping_cart_item);
        if (mBundle != null && mLaunchedFromProductCatalog) {
            mAddToCart.setVisibility(View.VISIBLE);
            Drawable shoppingCartIcon = VectorDrawable.create(mContext, R.drawable.iap_shopping_cart);
            mAddToCart.setCompoundDrawablesWithIntrinsicBounds(shoppingCartIcon, null, null, null);
            mBuyFromRetailors.setVisibility(View.VISIBLE);
            setCartIconVisibility(View.VISIBLE);
            mAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    buyProduct(mCTNValue);
                }
            });
            mBuyFromRetailors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    buyFromRetailers();
                }
            });
            mProductDiscountedPrice.setVisibility(View.VISIBLE);
            setTitle(mProductTitle);
        } else {
            setCartIconVisibility(View.GONE);
        }
    }

    private void buyFromRetailers() {
        Bundle bundle = new Bundle();
        bundle.putString(ModelConstants.PRODUCT_CODE, mCTNValue);
        addFragment(BuyFromRetailersFragment.createInstance(bundle, AnimationType.NONE), BuyFromRetailersFragment.TAG);
    }

    @Override
    public void onFetchAssetSuccess(final Message msg) {
        IAPLog.d(IAPConstant.PRODUCT_DETAIL_FRAGMENT, "Success");
        mAsset = (ArrayList<String>) msg.obj;
        mAdapter.setAsset(mAsset);
        mAdapter.notifyDataSetChanged();
        if (Utility.isProgressDialogShowing())
            Utility.dismissProgressDialog();
    }

    @Override
    public void onFetchAssetFailure(final Message msg) {
        IAPLog.d(IAPConstant.PRODUCT_DETAIL_FRAGMENT, "Failure");
        if (Utility.isProgressDialogShowing())
            Utility.dismissProgressDialog();
        NetworkUtility.getInstance().
                showErrorMessage(msg, getFragmentManager(), getContext());
    }

    void buyProduct(final String ctnNumber) {
        Utility.showProgressDialog(getContext(), getString(R.string.iap_please_wait));
        mShoppingCartPresenter.buyProduct(getContext(), ctnNumber, mBuyProductListener);
    }

    private void checkForOutOfStock(final IAPNetworkError iapNetworkError, Message msg) {
        com.philips.cdp.di.iap.response.error.Error error = iapNetworkError.getServerError().getErrors().get(0);
        String type = error.getType();
        if (type.equalsIgnoreCase(IAPConstant.INSUFFICIENT_STOCK_LEVEL_ERROR)) {
            String subject = error.getMessage();
            NetworkUtility.getInstance().showErrorDialog(getFragmentManager(), getString(R.string.iap_ok),
                    getString(R.string.iap_out_of_stock), subject);
        } else {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), getContext());
        }
    }
}
