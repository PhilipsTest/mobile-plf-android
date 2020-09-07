/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.prodreg.register;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.constants.ProdRegError;
import com.philips.cdp.prodreg.constants.RegistrationState;
import com.philips.cdp.prodreg.fragments.ProdRegFindSerialFragment;
import com.philips.cdp.prodreg.fragments.ProdRegRegistrationFragment;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.listener.MetadataListener;
import com.philips.cdp.prodreg.listener.ProdRegListener;
import com.philips.cdp.prodreg.listener.SummaryListener;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponse;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponseData;
import com.philips.cdp.prodreg.model.summary.Data;
import com.philips.cdp.prodreg.model.summary.ProductSummaryResponse;
import com.philips.cdp.prodreg.util.ProdRegUtil;

import java.util.ArrayList;
import java.util.List;

import static com.philips.cdp.prodreg.constants.AnalyticsConstants.PRG_PROD_REG_SUCCESS_EVENT;
import static com.philips.cdp.prodreg.constants.AnalyticsConstants.SEND_DATA;
import static com.philips.cdp.prodreg.constants.AnalyticsConstants.SPECIAL_EVENTS;

public class ProdRegRegistrationController {

    public static final String TAG = ProdRegRegistrationController.class.getSimpleName();

    public static final int UNKNOWN = -1;

    public interface RegisterControllerCallBacks {
        void isValidDate(boolean validDate);

        void isValidSerialNumber(boolean validSerialNumber);

        void setSummaryView(Data summaryData);

        void setProductView(RegisteredProduct registeredProduct);

        void requireFields(boolean requireDate, boolean requireSerialNumber);

        void logEvents(String tag, String data);

        void tagEvents(String event, String key, String value);

        void showSuccessLayout();

        void hideProgress();

        void showAlreadyRegisteredDialog(RegisteredProduct registeredProduct);

        void dismissLoadingDialog();

        void exitProductRegistration();

        void showLoadingDialog();

        void showFragment(Fragment fragment);

        void showAlertOnError(int responseCode);

        void buttonClickable(boolean isClickable);

        void updateProductCache();
    }

    private boolean isProductRegistered = false;
    private RegisterControllerCallBacks registerControllerCallBacks;
    private ProductMetadataResponseData productMetadataResponseData;
    private RegisteredProduct registeredProduct;
    private ProdRegUtil prodRegUtil = new ProdRegUtil();

    public ProdRegRegistrationController(final RegisterControllerCallBacks registerControllerCallBacks, final FragmentActivity fragmentActivity) {
        this.registerControllerCallBacks = registerControllerCallBacks;
        this.fragmentActivity = fragmentActivity;
    }

    public boolean isApiCallingProgress() {
        return isApiCallingProgress;
    }

    public void handleState() {
        if (getRegisteredProduct().isProductAlreadyRegistered(getLocalRegisteredProducts())) {
            registeredProduct = getRegisteredProduct().getRegisteredProductIfExists(getLocalRegisteredProducts());
            registerControllerCallBacks.showAlreadyRegisteredDialog(getRegisteredProduct());
            updateRegisteredProductsList(registeredProduct);
        }
    }

    @NonNull
    protected LocalRegisteredProducts getLocalRegisteredProducts() {
        return new LocalRegisteredProducts(PRUiHelper.getInstance().getUserDataInstance());
    }

    @SuppressWarnings("unchecked")
    public void init(final Bundle bundle) {
        if (bundle != null) {
            this.dependencyBundle = bundle;
            registeredProducts = (ArrayList<RegisteredProduct>) bundle.getSerializable(ProdRegConstants.MUL_PROD_REG_CONSTANT);
            registeredProduct = (RegisteredProduct) bundle.getSerializable(ProdRegConstants.PROD_REG_PRODUCT);
            productMetadataResponseData = (ProductMetadataResponseData) bundle.getSerializable(ProdRegConstants.PROD_REG_PRODUCT_METADATA);
            final Data summaryData = (Data) bundle.getSerializable(ProdRegConstants.PROD_REG_PRODUCT_SUMMARY);
            updateSummaryView(summaryData);
            updateProductView();
            registerControllerCallBacks.updateProductCache();
        } else {
            registerControllerCallBacks.exitProductRegistration();
        }
    }

    private void updateSummaryView(final Data summaryData) {
        registerControllerCallBacks.setSummaryView(summaryData);
    }

    private void updateProductView() {
        if (registeredProduct != null) {
            registerControllerCallBacks.setProductView(getRegisteredProduct());
            handleRequiredFieldState(registeredProduct);
        }
    }

    private void handleRequiredFieldState(RegisteredProduct registeredProduct) {
        if (productMetadataResponseData != null) {
            final boolean requiredSerialNumber = productMetadataResponseData.getRequiresSerialNumber().equalsIgnoreCase("true");
            final boolean isValidSerialNumber = prodRegUtil.isValidSerialNumber(requiredSerialNumber, productMetadataResponseData.getSerialNumberFormat(), registeredProduct.getSerialNumber());
            if (registeredProduct.getSerialNumber().length() > 0) {
                registerControllerCallBacks.isValidSerialNumber(isValidSerialNumber);
            }
            final boolean requireSerialNumber = requiredSerialNumber && !isValidSerialNumber;
            registerControllerCallBacks.requireFields(productMetadataResponseData.getRequiresDateOfPurchase().equalsIgnoreCase("true"), requireSerialNumber);
        }
    }

    public boolean isValidSerialNumber(final String serialNumber) {
        boolean isValidSerialNumber = true;
        if (productMetadataResponseData != null) {
            final boolean requiredSerialNumber = productMetadataResponseData.
                    getRequiresSerialNumber().equalsIgnoreCase("true");

            isValidSerialNumber = prodRegUtil.isValidSerialNumber(requiredSerialNumber,
                    productMetadataResponseData.getSerialNumberFormat(), serialNumber);
            if (!isValidSerialNumber) {
                registerControllerCallBacks.isValidSerialNumber(isValidSerialNumber);
            }
        }
        return isValidSerialNumber;
    }

    public boolean isValidDate(final String text) {
        final boolean validDate = prodRegUtil.isValidDate(text);
        registerControllerCallBacks.isValidDate(validDate);
        return validDate;
    }

    public void registerProduct(final String purchaseDate, final String serialNumber) {
        final boolean validDate = validatePurchaseDate(purchaseDate);
        final boolean validSerialNumber = isValidSerialNumber(serialNumber);
        if (!isApiCallingProgress && validDate && validSerialNumber && getRegisteredProduct() != null) {
            registerControllerCallBacks.logEvents(TAG, "Registering product with product details as CTN::" + getRegisteredProduct().getCtn());
            if (getRegisteredProduct().getRegistrationState() != RegistrationState.REGISTERED)
                getRegisteredProduct().setPurchaseDate(purchaseDate);
            getRegisteredProduct().setSerialNumber(serialNumber);

            UserWithProducts userWithProducts = new UserWithProducts(fragmentActivity,getProdRegListener(),PRUiHelper.getInstance().getUserDataInstance());
            userWithProducts.registerProduct(getRegisteredProduct());
        } else {
            registerControllerCallBacks.hideProgress();
            registerControllerCallBacks.showAlertOnError(UNKNOWN);
        }

    }

    private boolean validatePurchaseDate(final String purchaseDate) {
        boolean validPurchaseDate = true;
        if (productMetadataResponseData != null && productMetadataResponseData.getRequiresDateOfPurchase().equalsIgnoreCase("true")) {
            validPurchaseDate = isValidDate(purchaseDate);
        }
        return validPurchaseDate;
    }

    @NonNull
    protected ProdRegListener getProdRegListener() {
        return new ProdRegListener() {
            @Override
            public void onProdRegSuccess(RegisteredProduct registeredProduct, UserWithProducts userWithProducts) {
                registerControllerCallBacks.logEvents(TAG, "Product registered successfully");
                isProductRegistered = true;
                if (fragmentActivity != null && !fragmentActivity.isFinishing()) {
                    ProdRegRegistrationController.this.registeredProduct = registeredProduct;
                    registerControllerCallBacks.buttonClickable(true);
                    registerControllerCallBacks.dismissLoadingDialog();
                    updateRegisteredProductsList(registeredProduct);
                    registerControllerCallBacks.tagEvents(SEND_DATA, SPECIAL_EVENTS, PRG_PROD_REG_SUCCESS_EVENT);
                    registerControllerCallBacks.showSuccessLayout();
                }
            }

            @Override
            public void onProdRegFailed(RegisteredProduct registeredProduct, UserWithProducts userWithProducts) {
                registerControllerCallBacks.logEvents(TAG, "Product registration failed");
                if (fragmentActivity != null && !fragmentActivity.isFinishing()) {
                   // ProdRegRegistrationController.this.registeredProduct = registeredProduct;
                    ProdRegRegistrationController.this.registeredProduct.setProdRegError(registeredProduct.getProdRegError());
                    ProdRegRegistrationController.this.registeredProduct.setRegistrationState(registeredProduct.getRegistrationState());
                    ProdRegRegistrationController.this.registeredProduct.setUserUUid(registeredProduct.getUserUUid());
                    registerControllerCallBacks.buttonClickable(true);
                    registerControllerCallBacks.dismissLoadingDialog();
                    if (registeredProduct.getProdRegError() != ProdRegError.PRODUCT_ALREADY_REGISTERED) {
                        registerControllerCallBacks.showAlertOnError(registeredProduct.getProdRegError().getCode());
                    } else {
                        registerControllerCallBacks.showAlreadyRegisteredDialog(registeredProduct);
                        updateRegisteredProductsList(registeredProduct);
                    }
                }
            }
        };
    }

    public RegisteredProduct getRegisteredProduct() {
        return registeredProduct;
    }

    public List<RegisteredProduct> getRegisteredProducts() {
        updateRegisteredProductsList(registeredProduct);
        return registeredProducts;
    }

    private void updateRegisteredProductsList(final RegisteredProduct registeredProduct) {
        if (registeredProducts != null) {
            registeredProducts.remove(registeredProduct);
            registeredProducts.add(registeredProduct);
        }
    }

    public void onClickFindSerialNumber() {
        registerControllerCallBacks.showFragment(getFindSerialNumberFragment());
    }

    @NonNull
    private ProdRegFindSerialFragment getFindSerialNumberFragment() {
        final ProdRegFindSerialFragment prodRegFindSerialFragment = new ProdRegFindSerialFragment();
        prodRegFindSerialFragment.setArguments(dependencyBundle);
        return prodRegFindSerialFragment;
    }

    public boolean isProductRegistered() {
        return isProductRegistered;
    }


    @SuppressWarnings("unchecked")
    public void process(final Bundle arguments) {
        if (arguments != null) {
            registeredProducts = (ArrayList<RegisteredProduct>) arguments.getSerializable(ProdRegConstants.MUL_PROD_REG_CONSTANT);
            dependencyBundle = arguments;
            if (registeredProducts != null && registeredProducts.size() > 0) {
                currentProduct = registeredProducts.get(0);
                if (!isApiCallingProgress && fragmentActivity != null && !fragmentActivity.isFinishing()) {
                    if (!TextUtils.isEmpty(currentProduct.getCtn())) {
                        currentProduct.getProductMetadata(fragmentActivity, getMetadataListener());
                    } else {
                        currentProduct.setProdRegError(ProdRegError.MISSING_CTN);
                        registerControllerCallBacks.dismissLoadingDialog();
                        registerControllerCallBacks.showAlertOnError(ProdRegError.MISSING_CTN.getCode());
                    }
                }
            } else {
                registerControllerCallBacks.exitProductRegistration();
                PRUiHelper.getInstance().getProdRegUiListener().onProdRegFailed(ProdRegError.PRODUCTS_NOT_FOUND);
            }
        }
    }

    private void doSummaryRequest() {
        if (fragmentActivity != null && !fragmentActivity.isFinishing() && currentProduct != null) {
            dependencyBundle.putSerializable(ProdRegConstants.PROD_REG_PRODUCT, currentProduct);
            currentProduct.getProductSummary(fragmentActivity, currentProduct, getSummaryListener());
        }
    }

    @NonNull
    protected MetadataListener getMetadataListener() {
        return new MetadataListener() {
            @Override
            public void onMetadataResponse(final ProductMetadataResponse productMetadataResponse) {
                if (productMetadataResponse != null) {
                    dependencyBundle.putSerializable(ProdRegConstants.PROD_REG_PRODUCT_METADATA, productMetadataResponse.getData());
                    doSummaryRequest();
                }
            }

            @Override
            public void onErrorResponse(final String errorMessage, final int responseCode) {
                registerControllerCallBacks.dismissLoadingDialog();
                registerControllerCallBacks.showAlertOnError(responseCode);
            }
        };
    }

    @NonNull
    protected SummaryListener getSummaryListener() {
        return new SummaryListener() {
            @Override
            public void onSummaryResponse(final ProductSummaryResponse productSummaryResponse) {
                if (productSummaryResponse != null) {
                    dependencyBundle.putSerializable(ProdRegConstants.PROD_REG_PRODUCT_SUMMARY, productSummaryResponse.getData());
                    registerControllerCallBacks.dismissLoadingDialog();
                    init(dependencyBundle);
//                    final ProdRegRegistrationFragment prodRegRegistrationFragment = getProdRegRegistrationFragment();
//                    prodRegRegistrationFragment.setArguments(dependencyBundle);
//                    processControllerCallBacks.showFragment(prodRegRegistrationFragment);
                }
            }

            @Override
            public void onErrorResponse(final String errorMessage, final int responseCode) {
                registerControllerCallBacks.dismissLoadingDialog();
                init(dependencyBundle);
//                prodRegRegistrationFragment.setArguments(dependencyBundle);
                registerControllerCallBacks.dismissLoadingDialog();
//                processControllerCallBacks.showFragment(prodRegRegistrationFragment);
            }
        };
    }

    @NonNull
    protected ProdRegRegistrationFragment getProdRegRegistrationFragment() {
        return new ProdRegRegistrationFragment();
    }


    public boolean isLaunchedRegistration() {
        return launchedRegistration;
    }

    public void setLaunchedRegistration(final boolean launchedRegistration) {
        this.launchedRegistration = launchedRegistration;
    }


    public List<RegisteredProduct> getRegisteredProductsList() {
        updateRegisteredProductsList(currentProduct);
        return registeredProducts;
    }

    private RegisteredProduct currentProduct;
    private Bundle dependencyBundle;
    private boolean launchedRegistration = false;
    private boolean isApiCallingProgress = false;
    private FragmentActivity fragmentActivity;
    private ArrayList<RegisteredProduct> registeredProducts;
}
