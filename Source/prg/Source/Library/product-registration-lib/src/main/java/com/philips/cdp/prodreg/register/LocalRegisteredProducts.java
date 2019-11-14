/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.prodreg.register;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.constants.RegistrationState;
import com.philips.cdp.prodreg.localcache.ProdRegCache;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.model.registeredproducts.model.Attributes;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalRegisteredProducts {

    private final String TAG = LogoutSessionListener.class.getSimpleName();
    private ProdRegCache prodRegCache;
    private String uuid;
    UserDataInterface userDataInterface;
    private Gson gson;

    public LocalRegisteredProducts(UserDataInterface userDataInterface) {
        this.userDataInterface = userDataInterface;
        prodRegCache = new ProdRegCache();
        gson = new Gson();
        if (userDataInterface != null) {
            ArrayList<String> detailsKey = new ArrayList<>();
            detailsKey.add(UserDetailConstants.UUID);
            try {
                HashMap<String, Object> userDetailsMap = userDataInterface.getUserDetails(detailsKey);
                uuid = userDetailsMap.get(UserDetailConstants.UUID).toString() != null ? userDetailsMap.get(UserDetailConstants.UUID).toString() : "";
            } catch (Exception e) {
                ProdRegLogger.d(TAG, "Exception in fetching uuid : " + e.getMessage());
            }
        }
    }

    void store(RegisteredProduct registeredProduct) {
        Gson gson = getGSon();
        Set<RegisteredProduct> registeredProducts = getLocalRegisteredProducts().getUniqueRegisteredProducts();
        if (registeredProducts.contains(registeredProduct))
            registeredProducts.remove(registeredProduct);
        registeredProducts.add(registeredProduct);
        getProdRegCache().storeStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY, gson.toJson(registeredProducts));
    }

    @NonNull
    protected Gson getGSon() {
        return gson;
    }

    protected Set<RegisteredProduct> getUniqueRegisteredProducts() {
        final String data = getProdRegCache().getStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY);
        Gson gson = getGSon();
        RegisteredProduct[] registeredProducts = getRegisteredProducts(gson, data);
        if (registeredProducts == null) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(registeredProducts));
    }

    public List<RegisteredProduct> getRegisteredProducts() {
        Gson gson = getGSon();
        String data = getProdRegCache().getStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY);
        RegisteredProduct[] products = getRegisteredProducts(gson, data);
        if (userDataInterface != null && userDataInterface.getUserLoggedInState().ordinal() >= UserLoggedInState.PENDING_HSDP_LOGIN.ordinal() && products != null) {

            ArrayList<RegisteredProduct> registeredProducts = new ArrayList<>();
            for (RegisteredProduct registeredProduct : products) {
                if (registeredProduct.getUserUUid().length() == 0 || registeredProduct.getUserUUid().equals(uuid)) {
                    registeredProduct.setUserUUid(uuid);
                    registeredProducts.add(registeredProduct);
                }
            }
            return registeredProducts;
        } else
            return new ArrayList<>();
    }

    protected RegisteredProduct[] getRegisteredProducts(final Gson gson, final String data) {
        return gson.fromJson(data, RegisteredProduct[].class);
    }

    public void updateRegisteredProducts(final RegisteredProduct registeredProduct) {
        Gson gson = getGSon();
        Set<RegisteredProduct> registeredProducts = getUniqueRegisteredProducts();
        if (registeredProducts.contains(registeredProduct)) {
            registeredProducts.remove(registeredProduct);
        }
        registeredProducts.add(registeredProduct);
        getProdRegCache().storeStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY, gson.toJson(registeredProducts));
    }

//    protected void migrateLegacyCache(final RegisteredProduct[] products) {
//        Set<RegisteredProduct> localRegisteredProducts = getUniqueRegisteredProducts();
//        for (RegisteredProduct registeredProduct : products) {
//            registeredProduct.setRegistrationState(RegistrationState.REGISTERED);
//            registeredProduct.setUserUUid(uuid);
//            if (localRegisteredProducts.contains(registeredProduct)) {
//                localRegisteredProducts.remove(registeredProduct);
//            }
//            localRegisteredProducts.add(registeredProduct);
//        }
//        removeCachedRegisteredProducts(Arrays.asList(products), localRegisteredProducts);
//        getProdRegCache().storeStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY, getGSon().toJson(localRegisteredProducts));
//    }

    protected void migrateLegacyCache(final Attributes[] products) {
        Set<RegisteredProduct> localRegisteredProducts = getUniqueRegisteredProducts();
        Set<RegisteredProduct> localRegisteredProducts2 = localRegisteredProducts;;
        localRegisteredProducts2.clear();

        for (Attributes registeredProduct : products) {
            RegisteredProduct registeredProduct1 = new RegisteredProduct(registeredProduct.getProductId(), null, null);
            registeredProduct1.setRegistrationState(RegistrationState.REGISTERED);
            registeredProduct1.setUserUUid(uuid);
            registeredProduct1.setSerialNumber(registeredProduct.getSerialNumber());
            if (localRegisteredProducts.contains(registeredProduct)) {
                localRegisteredProducts.remove(registeredProduct);
            }
            localRegisteredProducts.add(registeredProduct1);
            localRegisteredProducts2.add(registeredProduct1);
        }

        ArrayList<RegisteredProduct> dt = new ArrayList<>();
        dt.addAll(localRegisteredProducts2);
        removeCachedRegisteredProducts(dt, localRegisteredProducts);
        getProdRegCache().storeStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY, getGSon().toJson(localRegisteredProducts));
    }

    private Set<RegisteredProduct> removeCachedRegisteredProducts(final List<RegisteredProduct> products, final Set<RegisteredProduct> localRegisteredProducts) {
        for (final java.util.Iterator<RegisteredProduct> itr = localRegisteredProducts.iterator(); itr.hasNext(); ) {
            final RegisteredProduct registeredProduct = itr.next();
            if (registeredProduct.getRegistrationState() != null && registeredProduct.getRegistrationState() == RegistrationState.REGISTERED && !products.contains(registeredProduct)) {
                itr.remove();
            }
        }
        return localRegisteredProducts;
    }

    public LocalRegisteredProducts getLocalRegisteredProducts() {
        return this;
    }

    protected ProdRegCache getProdRegCache() {
        return prodRegCache;
    }


    public void removeProductFromCache(final RegisteredProduct registeredProduct) {
        Set<RegisteredProduct> registeredProducts = getUniqueRegisteredProducts();
        if (registeredProducts.contains(registeredProduct)) {
            registeredProducts.remove(registeredProduct);
        }
        getProdRegCache().storeStringData(ProdRegConstants.PRODUCT_REGISTRATION_KEY, getGSon().toJson(registeredProducts));
    }
}
