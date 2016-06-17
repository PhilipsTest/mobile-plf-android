package com.philips.cdp.prodreg.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp.prodreg.RegistrationState;
import com.philips.cdp.prodreg.alert.ProdRegLoadingFragment;
import com.philips.cdp.prodreg.error.ErrorHandler;
import com.philips.cdp.prodreg.listener.DialogOkButtonListener;
import com.philips.cdp.prodreg.listener.MetadataListener;
import com.philips.cdp.prodreg.listener.RegisteredProductsListener;
import com.philips.cdp.prodreg.listener.SummaryListener;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponse;
import com.philips.cdp.prodreg.model.summary.ProductSummaryResponse;
import com.philips.cdp.prodreg.register.ProdRegHelper;
import com.philips.cdp.prodreg.register.Product;
import com.philips.cdp.prodreg.register.RegisteredProduct;
import com.philips.cdp.prodreg.util.ProdRegConstants;
import com.philips.cdp.product_registration_lib.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.listener.RegistrationTitleBarListener;
import com.philips.cdp.registration.ui.traditional.RegistrationFragment;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegistrationLaunchHelper;

import java.util.List;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ProdRegProcessFragment extends ProdRegBaseFragment {

    public static final String TAG = ProdRegProcessFragment.class.getName();
    private Product currentProduct;
    private Bundle dependencyBundle;
    private int count = 0;
    private ProdRegLoadingFragment prodRegLoadingFragment;

    @Override
    public String getActionbarTitle() {
        return getActivity().getString(R.string.prodreg_actionbar_title);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prodreg_process, container, false);
        showProgressAlertDialog("Looking for your products");
        return view;
    }

    private void showProgressAlertDialog(final String description) {
        final FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            prodRegLoadingFragment = new ProdRegLoadingFragment();
            prodRegLoadingFragment.show(activity.getSupportFragmentManager(), "dialog");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    prodRegLoadingFragment.setDescription(description);
                }
            }, 200);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final FragmentActivity activity = getActivity();
        final Bundle arguments = getArguments();

        if (activity != null && !activity.isFinishing() && arguments != null) {
            currentProduct = (Product) arguments.getSerializable(ProdRegConstants.PROD_REG_PRODUCT);
            User user = new User(activity);
            if (!user.isUserSignIn()) {
                count = count + 1;
                if (count < 2) {
                    RegistrationLaunchHelper.launchRegistrationActivityWithAccountSettings(getActivity());
                } else {
                    clearFragmentStack();
                }
            } else {
                getRegisteredProducts();
            }
        }
    }

    private void launchRegistrationFragment() {
        try {
            final FragmentActivity activity = getActivity();
            if (activity != null && !activity.isFinishing()) {

                RegistrationFragment registrationFragment = new
                        RegistrationFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(RegConstants.ACCOUNT_SETTINGS, true);
                registrationFragment.setArguments(bundle);
                registrationFragment.setOnUpdateTitleListener(new RegistrationTitleBarListener() {
                    @Override
                    public void updateRegistrationTitle(final int i) {

                    }

                    @Override
                    public void updateRegistrationTitleWithBack(final int i) {

                    }

                    @Override
                    public void updateRegistrationTitleWithOutBack(final int i) {

                    }
                });
                FragmentTransaction fragmentTransaction =
                        activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(getId(), registrationFragment,
                        RegConstants.REGISTRATION_FRAGMENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void makeSummaryRequest() {
        final FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing() && currentProduct != null) {
            dependencyBundle.putSerializable(ProdRegConstants.PROD_REG_PRODUCT, currentProduct);
            currentProduct.getProductSummary(getActivity(), currentProduct, getSummaryListener());
        }
    }

    @NonNull
    private SummaryListener getSummaryListener() {
        return new SummaryListener() {
            @Override
            public void onSummaryResponse(final ProductSummaryResponse productSummaryResponse) {
                if (productSummaryResponse != null) {
                    dependencyBundle.putSerializable(ProdRegConstants.PROD_REG_PRODUCT_SUMMARY, productSummaryResponse.getData());
                    final ProdRegRegistrationFragment prodRegRegistrationFragment = new ProdRegRegistrationFragment();
                    prodRegRegistrationFragment.setArguments(dependencyBundle);
                    if (prodRegLoadingFragment != null) prodRegLoadingFragment.dismiss();
                    showFragment(prodRegRegistrationFragment);
                }
            }

            @Override
            public void onErrorResponse(final String errorMessage, final int responseCode) {
                if (prodRegLoadingFragment != null) prodRegLoadingFragment.dismiss();
                showAlertOnError("Summary Failed", new ErrorHandler().getError(responseCode).getDescription());
            }
        };
    }

    private void getRegisteredProducts() {
        final FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            if (currentProduct != null) {
                ProdRegHelper prodRegHelper = new ProdRegHelper();
                prodRegHelper.getSignedInUserWithProducts().getRegisteredProducts(getRegisteredProductsListener());
            }
        }
    }

    @NonNull
    private RegisteredProductsListener getRegisteredProductsListener() {
        return new RegisteredProductsListener() {
            @Override
            public void getRegisteredProductsSuccess(final List<RegisteredProduct> registeredProducts, final long timeStamp) {
                    if (!isCtnRegistered(registeredProducts, currentProduct) && getActivity() != null && !getActivity().isFinishing()) {
                        currentProduct.getProductMetadata(getActivity(), getMetadataListener());
                    } else {
                        if (prodRegLoadingFragment != null) prodRegLoadingFragment.dismiss();
                        showFragment(new ProdRegConnectionFragment());
                    }
                }
        };
    }

    protected boolean isCtnRegistered(final List<RegisteredProduct> registeredProducts, final Product product) {
        for (RegisteredProduct result : registeredProducts) {
            if (product.getCtn().equalsIgnoreCase(result.getCtn()) && product.getSerialNumber().equals(result.getSerialNumber()) && result.getRegistrationState() == RegistrationState.REGISTERED) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    private MetadataListener getMetadataListener() {
        return new MetadataListener() {
            @Override
            public void onMetadataResponse(final ProductMetadataResponse productMetadataResponse) {
                if (productMetadataResponse != null) {
                    dependencyBundle = new Bundle();
                    dependencyBundle.putSerializable(ProdRegConstants.PROD_REG_PRODUCT_METADATA, productMetadataResponse.getData());
                    makeSummaryRequest();
                }
            }

            @Override
            public void onErrorResponse(final String errorMessage, final int responseCode) {
                if (prodRegLoadingFragment != null) prodRegLoadingFragment.dismiss();
                showAlertOnError("Metadata Failed", new ErrorHandler().getError(responseCode).getDescription());
            }
        };
    }

    @Override
    public DialogOkButtonListener getDialogOkButtonListener() {
        return new DialogOkButtonListener() {
            @Override
            public void onOkButtonPressed() {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    clearFragmentStack();
                }
            }
        };
    }
}
