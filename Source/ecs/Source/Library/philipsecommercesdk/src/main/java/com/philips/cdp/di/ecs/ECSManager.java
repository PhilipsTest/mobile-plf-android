/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs;

import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.error.ECSErrorEnum;
import com.philips.cdp.di.ecs.error.ECSErrorWrapper;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.integration.ECSOAuthProvider;
import com.philips.cdp.di.ecs.integration.GrantType;
import com.philips.cdp.di.ecs.model.address.ECSAddress;
import com.philips.cdp.di.ecs.model.address.ECSDeliveryMode;
import com.philips.cdp.di.ecs.model.address.ECSUserProfile;
import com.philips.cdp.di.ecs.model.asset.Assets;
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart;
import com.philips.cdp.di.ecs.model.cart.ECSEntries;
import com.philips.cdp.di.ecs.model.disclaimer.Disclaimers;
import com.philips.cdp.di.ecs.model.orders.ECSOrderHistory;
import com.philips.cdp.di.ecs.model.orders.ECSOrders;
import com.philips.cdp.di.ecs.model.orders.Entries;
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail;
import com.philips.cdp.di.ecs.model.payment.ECSPayment;
import com.philips.cdp.di.ecs.model.payment.ECSPaymentProvider;
import com.philips.cdp.di.ecs.model.products.ECSProduct;
import com.philips.cdp.di.ecs.model.products.ECSProducts;
import com.philips.cdp.di.ecs.model.region.ECSRegion;
import com.philips.cdp.di.ecs.model.config.ECSConfig;
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData;
import com.philips.cdp.di.ecs.model.retailers.ECSRetailerList;
import com.philips.cdp.di.ecs.model.summary.Data;
import com.philips.cdp.di.ecs.model.summary.ECSProductSummary;
import com.philips.cdp.di.ecs.model.voucher.ECSVoucher;
import com.philips.cdp.di.ecs.prx.serviceDiscovery.AssetServiceDiscoveryRequest;
import com.philips.cdp.di.ecs.prx.serviceDiscovery.DisclaimerServiceDiscoveryRequest;
import com.philips.cdp.di.ecs.prx.serviceDiscovery.ProductSummaryListServiceDiscoveryRequest;
import com.philips.cdp.di.ecs.prx.serviceDiscovery.ServiceDiscoveryRequest;
import com.philips.cdp.di.ecs.request.CreateAddressRequest;
import com.philips.cdp.di.ecs.request.DeleteAddressRequest;
import com.philips.cdp.di.ecs.request.GetAddressRequest;
import com.philips.cdp.di.ecs.request.GetDeliveryModesRequest;
import com.philips.cdp.di.ecs.request.GetOrderDetailRequest;
import com.philips.cdp.di.ecs.request.GetOrderHistoryRequest;
import com.philips.cdp.di.ecs.request.GetPaymentsRequest;
import com.philips.cdp.di.ecs.request.GetRegionsRequest;
import com.philips.cdp.di.ecs.request.GetRetailersInfoRequest;
import com.philips.cdp.di.ecs.request.GetUserProfileRequest;
import com.philips.cdp.di.ecs.request.GetVouchersRequest;
import com.philips.cdp.di.ecs.request.MakePaymentRequest;
import com.philips.cdp.di.ecs.request.RemoveVoucherRequest;
import com.philips.cdp.di.ecs.request.SetDeliveryAddressRequest;
import com.philips.cdp.di.ecs.request.SetDeliveryModesRequest;
import com.philips.cdp.di.ecs.request.SetPaymentMethodRequest;
import com.philips.cdp.di.ecs.request.SetVoucherRequest;
import com.philips.cdp.di.ecs.request.SubmitOrderRequest;
import com.philips.cdp.di.ecs.request.UpdateAddressRequest;
import com.philips.cdp.di.ecs.request.UpdateECSShoppingCartQuantityRequest;
import com.philips.cdp.di.ecs.request.CreateECSShoppingCartRequest;
import com.philips.cdp.di.ecs.request.AddProductToECSShoppingCartRequest;
import com.philips.cdp.di.ecs.request.GetConfigurationRequest;
import com.philips.cdp.di.ecs.request.GetECSShoppingCartsRequest;
import com.philips.cdp.di.ecs.request.GetProductAssetRequest;
import com.philips.cdp.di.ecs.request.GetProductDisclaimerRequest;
import com.philips.cdp.di.ecs.request.GetProductListRequest;
import com.philips.cdp.di.ecs.request.GetProductForRequest;
import com.philips.cdp.di.ecs.request.GetProductSummaryListRequest;
import com.philips.cdp.di.ecs.request.OAuthRequest;
import com.philips.cdp.di.ecs.util.ECSConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.philips.cdp.di.ecs.error.ECSNetworkError.getErrorLocalizedErrorMessage;


 class ECSManager {

    static int threadCount = 0;

    void getHybrisConfig(ECSCallback<Boolean, Exception> ecsCallback) {

        new GetConfigurationRequest(new ECSCallback<ECSConfig, Exception>() {
            @Override
            public void onResponse(ECSConfig result) {
                ECSConfiguration.INSTANCE.setSiteId(result.getSiteId());
                ECSConfiguration.INSTANCE.setRootCategory(result.getRootCategory());
                ecsCallback.onResponse(true);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onResponse(false);
                //ecsCallback.onFailure(error, ecsError);
            }
        }).executeRequest();

    }

    void getHybrisConfigResponse(ECSCallback<ECSConfig, Exception> ecsCallback) {

        ECSCallback ecsCallback1 =  new ECSCallback<ECSConfig, Exception>() {
            @Override
            public void onResponse(ECSConfig result) {
                ECSConfiguration.INSTANCE.setSiteId(result.getSiteId());
                ECSConfiguration.INSTANCE.setRootCategory(result.getRootCategory());
                result.setLocale(ECSConfiguration.INSTANCE.getLocale());
                result.setHybris(true);
                ecsCallback.onResponse(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ECSConfig hybrisConfigResponse = new ECSConfig();
                hybrisConfigResponse.setLocale(ECSConfiguration.INSTANCE.getLocale());
                hybrisConfigResponse.setHybris(false);
                ecsCallback.onResponse(hybrisConfigResponse);
            }
        };
        GetConfigurationRequest getConfigurationRequest = getConfigurationRequestObject(ecsCallback1);
        getConfigurationRequest.executeRequest();
    }

    GetConfigurationRequest getConfigurationRequestObject(ECSCallback<ECSConfig, Exception> eCSCallback){
        return new GetConfigurationRequest(eCSCallback);
    }


     void getOAuth(ECSOAuthProvider oAuthInput, ECSCallback<ECSOAuthData, Exception> ecsCallback) {
        new OAuthRequest(GrantType.JANRAIN,oAuthInput, ecsCallback).executeRequest();
    }

    void refreshOAuth(ECSOAuthProvider oAuthInput, ECSCallback<ECSOAuthData, Exception> ecsCallback) {
        new OAuthRequest(GrantType.REFRESH_TOKEN,oAuthInput, ecsCallback).executeRequest();
    }

    //========================================= Start of PRX Product List, Product Detail & Product for CTN =======================================

     void getProductList(int currentPage, int pageSize, final ECSCallback<ECSProducts, Exception> finalEcsCallback) {

        GetProductListRequest getProductListRequest = new GetProductListRequest(currentPage, pageSize, new ECSCallback<ECSProducts, Exception>() {
            @Override
            public void onResponse(ECSProducts result) {

                if(result.getProducts()!=null && result.getProducts().size()!=0) {
                    prepareProductSummaryURL(result, finalEcsCallback);
                }else{
                    finalEcsCallback.onResponse(result);
                }

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                finalEcsCallback.onFailure(error, ecsError);
            }
        });
        getProductListRequest.executeRequest();

    }

     void getProductFor(String ctn, ECSCallback<ECSProduct, Exception> eCSCallback) {
        if (null != ECSConfiguration.INSTANCE.getSiteId()) { // hybris flow
            ECSCallback ecsCallback1 =  new ECSCallback<ECSProduct, Exception>() {
                @Override
                public void onResponse(ECSProduct result) {
                    getSummaryForCTN(ctn, result, eCSCallback);
                }

                @Override
                public void onFailure(Exception error, ECSError ecsError) {
                    eCSCallback.onFailure(error, ecsError);

                }
            };
            GetProductForRequest getProductForRequest = getProductForRequestObject(ctn,ecsCallback1);
            getProductForRequest.executeRequest();
        } else { // Retailer flow
            getSummaryForCTN(ctn, null, eCSCallback);
        }


    }
    GetProductForRequest getProductForRequestObject(String ctn, ECSCallback<ECSProduct, Exception> ecsCallback){
        return new GetProductForRequest(ctn,  ecsCallback);
    }


    private static void productDetail(ECSProduct product, ECSCallback<ECSProduct, Exception> ecsCallback) {
        threadCount++;
        if (threadCount == 2) {
            ecsCallback.onResponse(product);
            threadCount =0;
        }
    }

     void getProductDetail(ECSProduct product, ECSCallback<ECSProduct, Exception> ecsCallback) {
        Thread assets = new Thread() {

            @Override
            public void run() {
                new AssetServiceDiscoveryRequest(product.getCode()).getRequestUrlFromAppInfra(new ServiceDiscoveryRequest.OnUrlReceived() {
                    @Override
                    public void onSuccess(String url) {
                        getProductAsset(url, new ECSCallback<Assets, Exception>() {
                            @Override
                            public void onResponse(Assets result) {
                                if (null != result) {
                                    product.setAssets(result);
                                    System.out.println("getProductAsset Success");
                                    productDetail(product, ecsCallback);
                                } else {
                                    ECSErrorWrapper ecsErrorWrapper = getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,null,null);
                                    ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
                                }
                            }

                            @Override
                            public void onFailure(Exception error, ECSError ecsError) {
                                System.out.println("getProductAsset failure");
                                ecsCallback.onFailure(error, ecsError);
                            }
                        });
                    }

                    @Override
                    public void onError(ERRORVALUES errorvalues, String s) {
                          ECSErrorWrapper ecsErrorWrapper = getErrorLocalizedErrorMessage(ECSErrorEnum.ECSsomethingWentWrong,null,s);
                        ecsCallback.onFailure(ecsErrorWrapper.getException(), ecsErrorWrapper.getEcsError());
                    }
                });
            }
        };

        Thread disclaimer = new Thread() {

            @Override
            public void run() {
                new DisclaimerServiceDiscoveryRequest(product.getCode()).getRequestUrlFromAppInfra(new ServiceDiscoveryRequest.OnUrlReceived() {
                    @Override
                    public void onSuccess(String url) {
                        getProductDisclaimer(url, new ECSCallback<Disclaimers, Exception>() {
                            @Override
                            public void onResponse(Disclaimers result) {
                                // here result can come as null if Disclaimer not present for given product
                                // but still Product Detail will be success as asset is already fetched
                                product.setDisclaimers(result);
                                productDetail(product, ecsCallback);
                            }

                            @Override
                            public void onFailure(Exception error, ECSError ecsError) {
                                // even if Disclaimer request fails the Product detail call be success as Asset has been already fetched
                                productDetail(product, ecsCallback);
                            }
                        });
                    }

                    @Override
                    public void onError(ERRORVALUES errorvalues, String s) {
                        // even if Disclaimer request fails the Product detail call be success as Asset has been already fetched
                        ecsCallback.onResponse(product);
                    }
                });
            }
        };
        assets.start();
        disclaimer.start();
    }

    //=========================================End of PRX Product List, Product Detail & Product for CTN =======================================


    //============================================== Start of PRX (Summary, Asset & Disclaimer) ================================================

    void getSummaryForCTN(String ctn, ECSProduct product, ECSCallback<ECSProduct, Exception> eCSCallback) {
        ECSProducts products = new ECSProducts();
        ArrayList<String> ctns = new ArrayList<>();
        if (null == product) {
            product = new ECSProduct();
            product.setCode(ctn);
        }
        List<ECSProduct> productList = new ArrayList<ECSProduct>();
        products.setProducts(productList);
        products.getProducts().add(product);
        ctns.add(ctn);
        getProductSummary(products, new ECSCallback<ECSProducts, Exception>() {
            @Override
            public void onResponse(ECSProducts result) {
                eCSCallback.onResponse(result.getProducts().get(0)); // one and only product
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                eCSCallback.onFailure(error, ecsError);
            }
        }, ctns);
    }

    void prepareProductSummaryURL(ECSProducts result, final ECSCallback<ECSProducts, Exception> ecsCallback) {
        List<ECSProduct> productsEntities = result.getProducts();
        ArrayList<String> ctns = new ArrayList<>();
        for (ECSProduct product : productsEntities) {
            ctns.add(product.getCode());
        }
        getProductSummary(result, ecsCallback, ctns);
    }

    private void getProductSummary(ECSProducts result, ECSCallback<ECSProducts, Exception> ecsCallback, ArrayList<String> ctns) {
        //Call PRX here
        ProductSummaryListServiceDiscoveryRequest productSummaryListServiceDiscoveryRequest = prepareProductSummaryListRequest(ctns);
        productSummaryListServiceDiscoveryRequest.getRequestUrlFromAppInfra(new ServiceDiscoveryRequest.OnUrlReceived() {
            @Override
            public void onSuccess(String url) {
                getProductSummary(url, new ECSCallback<ECSProductSummary, Exception>() {
                    @Override
                    public void onResponse(ECSProductSummary ecsProductSummary) {
                        updateProductsWithSummary(result, ecsProductSummary);
                        ecsCallback.onResponse(result);

                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        ecsCallback.onFailure(error, ecsError);
                    }
                });
            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {

            }
        });
    }

    void updateProductsWithSummary(ECSProducts products, ECSProductSummary ecsProductSummary) {
        HashMap<String, Data> summaryCtnMap = new HashMap<>();
        ArrayList<ECSProduct> productArrayList = new ArrayList<>(); // set back products for which summaries are available
        if (ecsProductSummary.isSuccess()) {
            for (Data data : ecsProductSummary.getData()) {
                summaryCtnMap.put(data.getCtn(), data);
            }
        }

        for (ECSProduct product : products.getProducts()) {
            Data productSummaryData = summaryCtnMap.get(product.getCode());
            if (productSummaryData != null) {
                product.setSummary(productSummaryData);
                productArrayList.add(product);
            }
        }
        products.setProducts(productArrayList);
    }

    void getProductSummary(String url, ECSCallback<ECSProductSummary, Exception> eCSCallback) {
        new GetProductSummaryListRequest(url, eCSCallback).executeRequest();
    }

    void getProductAsset(String url, ECSCallback<Assets, Exception> eCSCallback) {
        new GetProductAssetRequest(url, eCSCallback).executeRequest();
    }

    void getProductDisclaimer(String url, ECSCallback<Disclaimers, Exception> eCSCallback) {
        new GetProductDisclaimerRequest(url, eCSCallback).executeRequest();
    }


    private ProductSummaryListServiceDiscoveryRequest prepareProductSummaryListRequest(List<String> ctns) {
        return new ProductSummaryListServiceDiscoveryRequest(ctns);

    }

     void getSummary(List<String> ctns, ECSCallback<List<ECSProduct>, Exception> ecsCallback) {

        ECSProducts products = new ECSProducts();
        ArrayList<ECSProduct> productArrayList = new ArrayList<>();
        for (String ctn : ctns) {
            ECSProduct product = new ECSProduct();
            product.setCode(ctn);
            productArrayList.add(product);
        }
        products.setProducts(productArrayList);
        prepareProductSummaryURL(products, new ECSCallback<ECSProducts, Exception>() {
            @Override
            public void onResponse(ECSProducts result) {
                ecsCallback.onResponse(result.getProducts());
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        });
    }
    //============================================== End of PRX (Summary, Asset & Disclaimer) ================================================


    //=====================================================Start of Shopping Cart ===========================================================

    void getECSShoppingCart(ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
        ECSCallback ecsCallback1 =  new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart ecsShoppingCart) {
                if (ecsShoppingCart.getEntries()!=null && ecsShoppingCart.getEntries().size()!=0) {
                    //Preparing products to get summary Data
                    List<ECSProduct> productList = new ArrayList<>();

                    for (ECSEntries entriesEntity : ecsShoppingCart.getEntries()) {
                        productList.add(entriesEntity.getProduct());
                    }
                    ECSProducts products = new ECSProducts();
                    products.setProducts(productList);
                    //get Summary Data Here
                    ECSCallback<ECSProducts, Exception> ecsCallbackProduct = new ECSCallback<ECSProducts, Exception>() {
                        @Override
                        public void onResponse(ECSProducts result) {
                            ecsCallback.onResponse(ecsShoppingCart);
                        }

                        @Override
                        public void onFailure(Exception error, ECSError ecsError) {
                            ecsCallback.onFailure(error, ecsError);
                        }
                    };
                    prepareProductSummaryURL(products, ecsCallbackProduct);

                } else {
                    ecsCallback.onResponse(ecsShoppingCart);
                }
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        };

        GetECSShoppingCartsRequest  getECSShoppingCartsRequest = getShoppingCartsRequestObject(ecsCallback1);

        getECSShoppingCartsRequest.executeRequest();

    }

      GetECSShoppingCartsRequest getShoppingCartsRequestObject( ECSCallback<ECSShoppingCart, Exception> ecsCallback1 ){
         return  new GetECSShoppingCartsRequest(ecsCallback1);


    }

    void createECSShoppingCart(ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
        ECSCallback<ECSShoppingCart, Exception> ecsCallback1 = new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart ecsShoppingCart) {
                ecsCallback.onResponse(ecsShoppingCart);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        };
        createECSShoppingCartRequestObject(ecsCallback1).executeRequest();

    }

    CreateECSShoppingCartRequest createECSShoppingCartRequestObject(ECSCallback<ECSShoppingCart, Exception> ecsCallback){
        return new CreateECSShoppingCartRequest(ecsCallback);
    }
    // AddProduct to Cart
     void addProductToShoppingCart(ECSProduct product, ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
         ECSCallback<Boolean, Exception> ecsCallback1= new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                getECSShoppingCart(ecsCallback);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                // getECSShoppingCart(ecsCallback);
                ecsCallback.onFailure(error, ecsError);
            }
        };

         addProductToECSShoppingCartRequestObject(product.getCode(),ecsCallback1).executeRequest();
    }
    AddProductToECSShoppingCartRequest addProductToECSShoppingCartRequestObject(String code, ECSCallback<Boolean, Exception> ecsCallback ){
        return new AddProductToECSShoppingCartRequest(code, ecsCallback);
    }

     void updateQuantity(int quantity, ECSEntries entriesEntity, ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
         ECSCallback<Boolean, Exception> ecsCallback1 = new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                getECSShoppingCart(ecsCallback);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        };
         updateECSShoppingCartQuantityRequestObject(ecsCallback1,entriesEntity,quantity).executeRequest();
    }

    UpdateECSShoppingCartQuantityRequest updateECSShoppingCartQuantityRequestObject(ECSCallback<Boolean, Exception> ecsCallback, ECSEntries entriesEntity, int quantity){
        return new UpdateECSShoppingCartQuantityRequest (ecsCallback,entriesEntity,quantity);
    }


     void setVoucher(String voucherCode, ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {
         ECSCallback<Boolean, Exception> ecsCallback1= new  ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                getVoucher(ecsCallback);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        };
         new SetVoucherRequest(voucherCode,ecsCallback1).executeRequest();
    }


     void getVoucher(ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {
         getVouchersRequestObject(ecsCallback).executeRequest();
    }
    GetVouchersRequest getVouchersRequestObject(ECSCallback<List<ECSVoucher>, Exception> ecsCallback){
        return new GetVouchersRequest(ecsCallback);
    }

     void removeVoucher(String voucherCode, ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {

        new RemoveVoucherRequest(voucherCode, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                getVoucher(ecsCallback);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        }).executeRequest();
    }
    //===================================================== End of Shopping Cart ======================================================


    //===================================================== start of Delivery Mode ====================================================
     void getDeliveryModes(ECSCallback<List<ECSDeliveryMode>, Exception> ecsCallback) {
        new GetDeliveryModesRequest(ecsCallback).executeRequest();
    }

     void setDeliveryMode(String deliveryModeID, ECSCallback<Boolean, Exception> ecsCallback) {
        new SetDeliveryModesRequest(deliveryModeID, ecsCallback).executeRequest();
    }

     void getRegions(ECSCallback<List<ECSRegion>, Exception> ecsCallback) {
        new GetRegionsRequest(ecsCallback).executeRequest();
    }
    //===================================================== End of Delivery Mode ====================================================


    //===================================================== Start of Address ====================================================
     void getListSavedAddress(ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
        new GetAddressRequest(ecsCallback).executeRequest();
    }

     void setDeliveryAddress(ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback) {
        new SetDeliveryAddressRequest(address.getId(), ecsCallback).executeRequest();
    }
    //===================================================== End of Delivery Mode ====================================================

     void createNewAddress(ECSAddress address, ECSCallback<ECSAddress, Exception> ecsCallback, boolean singleAddress) {
         ECSCallback<ECSAddress, Exception> ecsCallback1= new  ECSCallback<ECSAddress, Exception>() {
            @Override
            public void onResponse(ECSAddress result) {
                // Address is created and now Address list needs to be called
                ecsCallback.onResponse(result);

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        };
         createAddressRequestObject(address,ecsCallback1).executeRequest();
    }

    CreateAddressRequest createAddressRequestObject(ECSAddress address, ECSCallback<ECSAddress, Exception> ecsCallback){
        return new CreateAddressRequest(address,ecsCallback);
    }

     void createNewAddress(ECSAddress address, ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
        new CreateAddressRequest(address, new ECSCallback<ECSAddress, Exception>() {
            @Override
            public void onResponse(ECSAddress result) {
                // Address is created and now Address list needs to be called
                getListSavedAddress(ecsCallback);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        }).executeRequest();
    }

     void updateAddress(ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback) {
        new UpdateAddressRequest(address, ecsCallback).executeRequest();
    }

     void deleteAddress(ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback) {
        new DeleteAddressRequest(address, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                ecsCallback.onResponse(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        }).executeRequest();
    }

    public void deleteAndFetchAddress(ECSAddress address, ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
        deleteAddress(address, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                getListSavedAddress(ecsCallback);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        });
    }

     void getRetailers(String productID, ECSCallback<ECSRetailerList, Exception> ecsCallback) {
        new GetRetailersInfoRequest(ecsCallback, productID).executeRequest();
    }
    //===================================================== End of Address ====================================================


    //===================================================== Start of Payment ====================================================

     void getPayments(ECSCallback<List<ECSPayment>, Exception> ecsCallback) {
        new GetPaymentsRequest(ecsCallback).executeRequest();
    }

     void setPaymentMethod(String paymentDetailsId, ECSCallback<Boolean, Exception> ecsCallback) {
        new SetPaymentMethodRequest(paymentDetailsId, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                ecsCallback.onResponse(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        }).executeRequest();
    }

     void getOrderHistory(int pageNumber, int pageSize, ECSCallback<ECSOrderHistory, Exception> ecsCallback) {
        new GetOrderHistoryRequest(pageNumber,pageSize, ecsCallback).executeRequest();
    }


     void submitOrder(String cvv, ECSCallback<ECSOrderDetail, Exception> ecsCallback) {
        new SubmitOrderRequest(cvv, ecsCallback).executeRequest();
    }


     void makePayment(ECSOrderDetail orderDetail, ECSAddress billingAddress, ECSCallback<ECSPaymentProvider, Exception> ecsCallback) {
        new MakePaymentRequest(orderDetail, billingAddress, ecsCallback).executeRequest();
    }


     void getOrderDetail(String orderId, ECSCallback<ECSOrderDetail, Exception> ecsCallback) {

        new GetOrderDetailRequest(orderId, new ECSCallback<ECSOrderDetail, Exception>() {
            @Override
            public void onResponse(ECSOrderDetail orderDetail) {
                if (orderDetail == null || orderDetail.getEntries() == null || orderDetail.getEntries().size() == 0) {
                    ecsCallback.onResponse(orderDetail);
                    return;
                }
                // Get PRX Summary Data
                ArrayList<String> ctns = new ArrayList<>();
                ArrayList<ECSProduct> productsFromDirectEntry = new ArrayList<>();
                ArrayList<ECSProduct> productsFromDeliveryGroupEntry = new ArrayList<>();
                for (Entries entries : orderDetail.getDeliveryOrderGroups().get(0).getEntries()) {
                    productsFromDeliveryGroupEntry.add(entries.getProduct());
                }
                //Products found in direct Entries
                for (Entries entries : orderDetail.getEntries()) {
                    productsFromDirectEntry.add(entries.getProduct());
                    ctns.add(entries.getProduct().getCode());
                }
                ProductSummaryListServiceDiscoveryRequest productSummaryListServiceDiscoveryRequest = prepareProductSummaryListRequest(ctns);

                //get PRX summary URL
                productSummaryListServiceDiscoveryRequest.getRequestUrlFromAppInfra(new ServiceDiscoveryRequest.OnUrlReceived() {
                    @Override
                    public void onSuccess(String url) {

                        getProductSummary(url, new ECSCallback<ECSProductSummary, Exception>() {
                            @Override
                            public void onResponse(ECSProductSummary ecsProductSummary) {
                                setSummaryToProductsFromDirectEntry(ecsProductSummary, productsFromDirectEntry);
                                setSummaryToProductsDeliveryGroupEntry(ecsProductSummary, productsFromDeliveryGroupEntry);
                                ecsCallback.onResponse(orderDetail);
                            }

                            @Override
                            public void onFailure(Exception error, ECSError ecsError) {
                                ecsCallback.onFailure(error, ecsError);
                            }
                        });
                    }

                    @Override
                    public void onError(ERRORVALUES errorvalues, String s) {
                    }
                });
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        }).executeRequest();
    }

    private void setSummaryToProductsDeliveryGroupEntry(ECSProductSummary ecsProductSummary, ArrayList<ECSProduct> productsFromDeliveryGroupEntry) {
        ArrayList<Data> data = ecsProductSummary.getData();
        for (Data d : data) {
            setProductData(d, productsFromDeliveryGroupEntry);
        }
    }

    private void setProductData(Data data, ArrayList<ECSProduct> products) {
        for (ECSProduct product : products) {
            if (product.getCode().equalsIgnoreCase(data.getCtn())) {
                product.setSummary(data);
                return;
            }
        }
    }

    private void setSummaryToProductsFromDirectEntry(ECSProductSummary ecsProductSummary, ArrayList<ECSProduct> productsFromDirectEntry) {
        ArrayList<Data> data = ecsProductSummary.getData();
        for (Data d : data) {
            setProductData(d, productsFromDirectEntry);
        }
    }

     void getUserProfile(ECSCallback<ECSUserProfile, Exception> ecsCallback) {
        new GetUserProfileRequest(ecsCallback).executeRequest();
    }

     void getOrderDetail(ECSOrders orders, ECSCallback<ECSOrders, Exception> ecsCallback) {
        getOrderDetail(orders.getCode(), new ECSCallback<ECSOrderDetail, Exception>() {
            @Override
            public void onResponse(ECSOrderDetail result) {
                orders.setOrderDetail(result);
                ecsCallback.onResponse(orders);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        });
    }

    public void refreshAuth(ECSOAuthProvider oAuthInput, ECSCallback<ECSOAuthData, Exception> ecsListener) {
        new OAuthRequest(GrantType.REFRESH_TOKEN,oAuthInput,ecsListener).executeRequest();
    }

    public void updateAndFetchAddress(ECSAddress address, ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
       updateAddress(address, new ECSCallback<Boolean, Exception>() {
           @Override
           public void onResponse(Boolean result) {
               getListSavedAddress(ecsCallback);
           }

           @Override
           public void onFailure(Exception error, ECSError ecsError) {
               ecsCallback.onFailure(error, ecsError);
           }
       });

    }

    public void setAndFetchDeliveryAddress(ECSAddress address, ECSCallback<List<ECSAddress>, Exception> ecsCallback) {

        setDeliveryAddress(address, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                getListSavedAddress(ecsCallback);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        });
    }


}


