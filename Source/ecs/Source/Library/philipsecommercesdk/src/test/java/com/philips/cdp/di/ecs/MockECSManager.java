package com.philips.cdp.di.ecs;

import com.philips.cdp.di.ecs.Address.MockCreateAddressRequest;
import com.philips.cdp.di.ecs.Address.MockDeleteAddressRequest;
import com.philips.cdp.di.ecs.Address.MockGetAddressRequest;
import com.philips.cdp.di.ecs.Address.MockSetDeliveryAddressRequest;
import com.philips.cdp.di.ecs.Address.MockUpdateAddressRequest;
import com.philips.cdp.di.ecs.Cart.MockAddProductToECSShoppingCartRequest;
import com.philips.cdp.di.ecs.Cart.MockCreateECSShoppingCartRequest;
import com.philips.cdp.di.ecs.Cart.MockGetECSShoppingCartsRequest;
import com.philips.cdp.di.ecs.Cart.MockUpdateECSShoppingCartQuantityRequest;
import com.philips.cdp.di.ecs.Config.MockGetConfigurationRequest;
import com.philips.cdp.di.ecs.Oath.MockOAuthRequest;
import com.philips.cdp.di.ecs.Payment.MockGetPaymentsRequest;
import com.philips.cdp.di.ecs.Payment.MockMakePaymentRequest;
import com.philips.cdp.di.ecs.Payment.MockPlaceOrderRequest;
import com.philips.cdp.di.ecs.Payment.MockSetPaymentMethodRequest;
import com.philips.cdp.di.ecs.ProductCatalog.MockGetProductListRequest;
import com.philips.cdp.di.ecs.ProductCatalog.MockGetProductSummaryListRequest;
import com.philips.cdp.di.ecs.ProductDetail.MockGetProductAssetRequest;
import com.philips.cdp.di.ecs.ProductDetail.MockGetProductDisclaimerRequest;
import com.philips.cdp.di.ecs.ProductForCTN.MockGetProductForRequest;
import com.philips.cdp.di.ecs.Region.MockGetRegionsRequest;
import com.philips.cdp.di.ecs.Voucher.MockGetVouchersRequest;
import com.philips.cdp.di.ecs.Voucher.MockRemoveVoucherRequest;
import com.philips.cdp.di.ecs.Voucher.MockSetVoucherRequest;
import com.philips.cdp.di.ecs.DeliveryMode.MockDeliveryModesRequest;
import com.philips.cdp.di.ecs.DeliveryMode.MockSetDeliveryModesRequest;
import com.philips.cdp.di.ecs.error.ECSError;
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
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail;
import com.philips.cdp.di.ecs.model.orders.ECSOrderHistory;
import com.philips.cdp.di.ecs.model.payment.ECSPayment;
import com.philips.cdp.di.ecs.model.payment.ECSPaymentProvider;
import com.philips.cdp.di.ecs.model.products.ECSProduct;
import com.philips.cdp.di.ecs.model.products.ECSProducts;
import com.philips.cdp.di.ecs.model.region.ECSRegion;
import com.philips.cdp.di.ecs.model.config.ECSConfig;
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData;
import com.philips.cdp.di.ecs.model.retailers.ECSRetailerList;
import com.philips.cdp.di.ecs.model.summary.ECSProductSummary;
import com.philips.cdp.di.ecs.model.voucher.ECSVoucher;
import com.philips.cdp.di.ecs.orderHistory.MockGetOrderDetailRequest;
import com.philips.cdp.di.ecs.orderHistory.MockGetOrderHistoryRequest;
import com.philips.cdp.di.ecs.request.AddProductToECSShoppingCartRequest;
import com.philips.cdp.di.ecs.request.CreateAddressRequest;
import com.philips.cdp.di.ecs.request.CreateECSShoppingCartRequest;
import com.philips.cdp.di.ecs.request.GetConfigurationRequest;
import com.philips.cdp.di.ecs.request.GetECSShoppingCartsRequest;
import com.philips.cdp.di.ecs.request.GetVouchersRequest;
import com.philips.cdp.di.ecs.retailer.MockGetRetailersInfoRequest;
import com.philips.cdp.di.ecs.userProfile.MockGetUserProfileRequest;

import java.util.List;

public class MockECSManager extends ECSManager {



    String jsonFileNameMockECSManager;

    public String getJsonFileNameMockECSManager() {
        return jsonFileNameMockECSManager;
    }

    public void setJsonFileNameMockECSManager(String jsonFileNameMockECSManager) {
        this.jsonFileNameMockECSManager = jsonFileNameMockECSManager;
    }


    @Override
    GetConfigurationRequest getConfigurationRequestObject(ECSCallback<ECSConfig, Exception> eCSCallback) {
        return new MockGetConfigurationRequest(getJsonFileNameMockECSManager(),eCSCallback);
    }

    @Override
    public void getRegions(ECSCallback<List<ECSRegion>, Exception> ecsCallback) {
        new MockGetRegionsRequest(getJsonFileNameMockECSManager(), new ECSCallback<List<ECSRegion>, Exception>() {
            @Override
            public void onResponse(List<ECSRegion> result) {
                ecsCallback.onResponse(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        }).executeRequest();
    }

    @Override
    public void getOAuth(ECSOAuthProvider oAuthInput, ECSCallback<ECSOAuthData, Exception> ecsCallback) {

        MockOAuthRequest mockOAuthRequest = new MockOAuthRequest(getJsonFileNameMockECSManager(), GrantType.JANRAIN,oAuthInput,ecsCallback);
        mockOAuthRequest.executeRequest();
    }

    @Override
    public void getProductList(int currentPage, int pageSize, ECSCallback<ECSProducts, Exception> ecsCallback) {


                MockGetProductListRequest getMockGetProductRequest = new MockGetProductListRequest(getJsonFileNameMockECSManager(),currentPage, pageSize, new ECSCallback<ECSProducts, Exception>() {
                    @Override
                    public void onResponse(ECSProducts result) {
                        //prepareProductSummaryURL(result,ecsCallback);
                        setJsonFileNameMockECSManager("PRXSummaryResponse.json");
                        new MockGetProductSummaryListRequest(getJsonFileNameMockECSManager(),"", new ECSCallback<ECSProductSummary, Exception>() {
                            @Override
                            public void onResponse(ECSProductSummary eCSProductSummary) {

                                updateProductsWithSummary(result,eCSProductSummary);
                                ecsCallback.onResponse(result);
                            }

                            @Override
                            public void onFailure(Exception error, ECSError ecsError) {

                            }
                       }).executeRequest();
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        ecsCallback.onFailure(error, ecsError);

                    }
                });
                getMockGetProductRequest.executeRequest();

            }


    @Override
    void getProductSummary(String url, ECSCallback<ECSProductSummary, Exception> eCSCallback) {
        new MockGetProductSummaryListRequest(getJsonFileNameMockECSManager(),url,eCSCallback).executeRequest();
    }

    @Override
    public void getProductDetail(ECSProduct product, ECSCallback<ECSProduct, Exception> ecsCallback) {
        new MockGetProductAssetRequest(getJsonFileNameMockECSManager(),"mockURL", new ECSCallback<Assets, Exception>() {
            @Override
            public void onResponse(Assets result) {
                product.setAssets(result);
                setJsonFileNameMockECSManager("PRXDisclaimers.json");
                new MockGetProductDisclaimerRequest(getJsonFileNameMockECSManager(),"mockURL", new ECSCallback<Disclaimers, Exception>() {
                    @Override
                    public void onResponse(Disclaimers result) {
                        product.setDisclaimers(result);
                        ecsCallback.onResponse(product);
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        // even if Disclaimer request fails the Product detail call be success as Asset has been already fetched
                        ecsCallback.onResponse(product);
                    }
                }).executeRequest();

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                // even if Disclaimer request fails the Product detail call be success as Asset has been already fetched
                ecsCallback.onFailure(error, ecsError);
            }
        }).executeRequest();
    }

    @Override
    public void getProductFor(String ctn, ECSCallback<ECSProduct, Exception> eCSCallback) {
        new MockGetProductForRequest(getJsonFileNameMockECSManager(),ctn,eCSCallback).executeRequest();

     /*   if (null != ECSConfig.INSTANCE.getSiteId()) { // hybris flow

            new MockGetProductForRequest("",ctn, new ECSCallback<Product, Exception>() {
                @Override
                public void onResponse(Product result) {
                    getSummaryForCTN(ctn, result, eCSCallback);
                }


                @Override
                public void onFailure(Exception error,String detailErrorMessage, int errorCode) {
                    eCSCallback.onFailure(new Exception(ECSErrorReason.ECS_GIVEN_PRODUCT_NOT_FOUND),detailErrorMessage, 5999);
                }
            }).executeRequest();

        } else { // Retailer flow
            getSummaryForCTN(ctn, null, eCSCallback);
        }*/
    }


    @Override
    CreateECSShoppingCartRequest createECSShoppingCartRequestObject(ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
        return new MockCreateECSShoppingCartRequest(getJsonFileNameMockECSManager(),ecsCallback);
    }



    @Override
    public GetECSShoppingCartsRequest getShoppingCartsRequestObject(ECSCallback<ECSShoppingCart, Exception> ecsCallback1) {
        return new MockGetECSShoppingCartsRequest(getJsonFileNameMockECSManager(),ecsCallback1 );
    }


    @Override
    AddProductToECSShoppingCartRequest addProductToECSShoppingCartRequestObject(String code, ECSCallback<Boolean, Exception> ecsCallback) {
        return new MockAddProductToECSShoppingCartRequest(getJsonFileNameMockECSManager(),code, ecsCallback);
    }

    @Override
    public void addProductToShoppingCart(ECSProduct product, ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
        new MockAddProductToECSShoppingCartRequest(getJsonFileNameMockECSManager(),product.getCode(), new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                setJsonFileNameMockECSManager("ShoppingCartSuccess.json");
                getECSShoppingCart(ecsCallback);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                //getECSShoppingCart(ecsCallback);
                ecsCallback.onFailure(error, ecsError);

            }
        }).executeRequest();
    }


    @Override
    public void updateQuantity(int quantity, ECSEntries entriesEntity, ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
        new MockUpdateECSShoppingCartQuantityRequest(getJsonFileNameMockECSManager(), new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                setJsonFileNameMockECSManager("UpdateShoppingCart_GetShoppingCartSuccess.json");
                getECSShoppingCart(ecsCallback);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                getECSShoppingCart(ecsCallback);
            }
        }, entriesEntity, quantity).executeRequest();
    }


    @Override
    GetVouchersRequest getVouchersRequestObject(ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {
        return new MockGetVouchersRequest(getJsonFileNameMockECSManager(),ecsCallback);
    }

     @Override
    public void setVoucher(String voucherCode, ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {
        new MockSetVoucherRequest(getJsonFileNameMockECSManager(),voucherCode, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                setJsonFileNameMockECSManager("GetVoucherSuccess.json");
                getVoucher(ecsCallback);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        }).executeRequest();
    }



    @Override
    public void removeVoucher(String voucherCode, ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {
        new MockRemoveVoucherRequest(getJsonFileNameMockECSManager(),voucherCode, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                setJsonFileNameMockECSManager("EmptyJson.json");
                getVoucher(ecsCallback);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        }).executeRequest();
    }


    @Override
    CreateAddressRequest createAddressRequestObject(ECSAddress address, ECSCallback<ECSAddress, Exception> ecsCallback) {
        return new MockCreateAddressRequest(getJsonFileNameMockECSManager(),address, ecsCallback);
    }

    @Override
    public void createNewAddress(ECSAddress address, ECSCallback<ECSAddress, Exception> ecsCallback ,boolean newAddress) {
        new MockCreateAddressRequest(getJsonFileNameMockECSManager(), address, new ECSCallback<ECSAddress, Exception>() {
            @Override
            public void onResponse(ECSAddress result) {
                setJsonFileNameMockECSManager("ShippingAddressListSuccess.json");

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        }).executeRequest();

    }


    @Override
    public void getListSavedAddress(ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
        MockGetAddressRequest mockGetAddressRequest=  new MockGetAddressRequest(getJsonFileNameMockECSManager(), new ECSCallback<List<ECSAddress>, Exception>() {
            @Override
            public void onResponse(List<ECSAddress> result) {
                ecsCallback.onResponse(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
            ecsCallback.onFailure(error, ecsError);
            }
        });
        mockGetAddressRequest.getParams();
        mockGetAddressRequest.getHeader();
        mockGetAddressRequest.getMethod();
        mockGetAddressRequest.getStringSuccessResponseListener();
        mockGetAddressRequest.executeRequest();
    }

    @Override
    public void updateAddress(ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback) {
        MockUpdateAddressRequest mockUpdateAddressRequest=  new MockUpdateAddressRequest(getJsonFileNameMockECSManager(), address, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                ecsCallback.onResponse(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        });
        mockUpdateAddressRequest.getParams();
        mockUpdateAddressRequest.getHeader();
        mockUpdateAddressRequest.getMethod();
        mockUpdateAddressRequest.getStringSuccessResponseListener();
        mockUpdateAddressRequest.executeRequest();
    }

    @Override
    public void deleteAddress(ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback) {
        MockDeleteAddressRequest mockDeleteAddressRequest=  new MockDeleteAddressRequest(getJsonFileNameMockECSManager(), address, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                setJsonFileNameMockECSManager("ShippingAddressListSuccess.json");
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                ecsCallback.onFailure(error, ecsError);
            }
        });
        mockDeleteAddressRequest.getParams();
        mockDeleteAddressRequest.getHeader();
        mockDeleteAddressRequest.getMethod();
        mockDeleteAddressRequest.getStringSuccessResponseListener();
        mockDeleteAddressRequest.executeRequest();
    }

    @Override
    public void setDeliveryAddress(ECSAddress address, ECSCallback<Boolean, Exception> ecsCallback) {
        MockSetDeliveryAddressRequest mockSetDeliveryAddressRequest=  new MockSetDeliveryAddressRequest(getJsonFileNameMockECSManager(), address.getId(), new ECSCallback<Boolean, Exception>() {
           @Override
           public void onResponse(Boolean result) {
               ecsCallback.onResponse(result);
           }

           @Override
           public void onFailure(Exception error, ECSError ecsError) {
               ecsCallback.onFailure(error, ecsError);
           }
       });
        mockSetDeliveryAddressRequest.getParams();
        mockSetDeliveryAddressRequest.getHeader();
        mockSetDeliveryAddressRequest.getMethod();
        mockSetDeliveryAddressRequest.getStringSuccessResponseListener();
        mockSetDeliveryAddressRequest.executeRequest();
    }

    @Override
    public void getDeliveryModes(ECSCallback<List<ECSDeliveryMode>, Exception> ecsCallback) {
        new MockDeliveryModesRequest(ecsCallback,getJsonFileNameMockECSManager()).executeRequest();
    }

    @Override
    public void setDeliveryMode(String deliveryModeID, ECSCallback<Boolean, Exception> ecsCallback) {
        new MockSetDeliveryModesRequest(deliveryModeID,ecsCallback,getJsonFileNameMockECSManager()).executeRequest();
    }

    @Override
    public void getPayments(ECSCallback<List<ECSPayment>, Exception> ecsCallback) {
        new MockGetPaymentsRequest(getJsonFileNameMockECSManager(),ecsCallback).executeRequest();
    }

    @Override
    public void setPaymentMethod(String paymentDetailsId, ECSCallback<Boolean, Exception> ecsCallback) {
        new MockSetPaymentMethodRequest(paymentDetailsId,ecsCallback,getJsonFileNameMockECSManager()).executeRequest();
    }

    @Override
    public void getOrderHistory(int pageNumber, int pageSize, ECSCallback<ECSOrderHistory, Exception> ecsCallback) {
        new MockGetOrderHistoryRequest(getJsonFileNameMockECSManager(),pageNumber,ecsCallback).executeRequest();
    }

    @Override
    public void getOrderDetail(String orderId, ECSCallback<ECSOrderDetail, Exception> ecsCallback) {
        new MockGetOrderDetailRequest(getJsonFileNameMockECSManager(),orderId,ecsCallback).executeRequest();
    }

    @Override
    public void getRetailers(String productID, ECSCallback<ECSRetailerList, Exception> ecsCallback) {
        new MockGetRetailersInfoRequest(getJsonFileNameMockECSManager(),ecsCallback,productID).executeRequest();
    }

    @Override
    public void makePayment(ECSOrderDetail orderDetail, ECSAddress billingAddress, ECSCallback<ECSPaymentProvider, Exception> ecsCallback) {
        new MockMakePaymentRequest(getJsonFileNameMockECSManager(),orderDetail,billingAddress,ecsCallback).executeRequest();
    }

    @Override
    public void submitOrder(String cvv, ECSCallback<ECSOrderDetail, Exception> ecsCallback) {
        new MockPlaceOrderRequest(getJsonFileNameMockECSManager(),cvv,ecsCallback).executeRequest();
    }

    @Override
    public void getUserProfile(ECSCallback<ECSUserProfile, Exception> ecsCallback) {
        new MockGetUserProfileRequest(getJsonFileNameMockECSManager(),ecsCallback).executeRequest();
    }
}
