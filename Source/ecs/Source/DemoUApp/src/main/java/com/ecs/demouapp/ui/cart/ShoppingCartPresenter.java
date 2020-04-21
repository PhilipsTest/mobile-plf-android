/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.cart;

import android.content.Context;
import android.os.Message;
import android.util.Log;


import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.controller.AddressController;
import com.ecs.demouapp.ui.response.error.Error;
import com.ecs.demouapp.ui.response.error.ServerError;
import com.ecs.demouapp.ui.session.IAPNetworkError;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;

import com.philips.platform.ecs.model.address.ECSUserProfile;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.model.cart.ECSEntries;
import com.philips.platform.ecs.model.products.ECSProduct;


import java.util.List;


public class ShoppingCartPresenter extends AbstractShoppingCartPresenter
        implements AddressController.AddressListener {

    private AddressController mAddressController;



    public ShoppingCartPresenter() {
    }

    public ShoppingCartPresenter(Context context, ShoppingCartListener<?> listener) {
        super(context, listener);
        mAddressController = new AddressController(context, ShoppingCartPresenter.this);
    }


    @Override
    public void getCurrentCartDetails() {

        ECSUtility.getInstance().getEcsServices().fetchShoppingCart(new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                mLoadListener.onLoadFinished(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                Message message = new Message();
                String errMsg = null!=error?error.getMessage():"Could not fetch data";
                message.obj = errMsg;
                mLoadListener.onLoadError(message);
            }
        });
    }

    @Override
    public void deleteProduct(final ShoppingCartData summary) {
    }

    @Override
    public void deleteProduct(ECSEntries entriesEntity) {
        ECSUtility.getInstance().getEcsServices().updateShoppingCart(0, entriesEntity, new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                mLoadListener.onLoadFinished(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                Message message = new Message();
                message.obj = "Could not fetch data";
                mLoadListener.onLoadError(message);
            }
        });

    }

    @Override
    public void updateProductQuantity(ECSEntries entriesEntity, int count) {

        ECSUtility.getInstance().getEcsServices().updateShoppingCart(count, entriesEntity, new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                mLoadListener.onLoadFinished(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                Message message = new Message();
                String errMsg = null!=error?error.getMessage(): ECSErrorEnum.ECSsomethingWentWrong.getLocalizedErrorString();
                message.obj = errMsg;
                mLoadListener.onLoadError(message);
            }
        });
    }

    @Override
    public void updateProductQuantity(final ShoppingCartData data, final int count, final int quantityStatus) {

    }



    @Override
    public void addProductToCart(final Context context, String productCTN, final ECSCartListener
            iapHandlerListener, final boolean isFromBuyNow) {

    }

    @Override
    public void getProductCartCount(final Context context, final ECSCartListener
            iapCartListener) {

        ECSUtility.getInstance().getEcsServices().fetchShoppingCart(new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart carts) {

                 // You can check if cart is not there and call createCart here
                if (carts != null) {
                    iapCartListener.onSuccess(carts);
                } else {
                    Message message = new Message();
                    message.obj = "Error Fetching Cart";
                    iapCartListener.onFailure(message);
                }


            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                Message message = new Message();
                String errMsg = null!=error?error.getMessage(): ECSErrorEnum.ECSsomethingWentWrong.getLocalizedErrorString();
                message.obj = errMsg;
                iapCartListener.onFailure(message);
            }
        });

    }

    @Override
    public void addProductToCart(ECSProduct product, final ECSCartListener
            iapHandlerListener) {

        ECSUtility.getInstance().getEcsServices().addProductToShoppingCart(product, new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                iapHandlerListener.onSuccess(result);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                Message message = new Message();

                if(null!=error.getMessage()){
                    message.obj = error.getMessage();
                }

                iapHandlerListener.onFailure(message);
            }
        });

    }



    private boolean isNoCartError(final Message msg) {
        if (msg.obj instanceof IAPNetworkError) {
            ServerError error = ((IAPNetworkError) msg.obj).getServerError();
            if (error != null && error.getErrors() != null &&
                    error.getErrors().get(0) != null) {
                Error err = error.getErrors().get(0);
                //// TODO: 04-05-2016 add with proper string type or type check
                if ("No cart created yet.".equals(err.getMessage())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isValidPromotion(String code) {
        return code.toLowerCase().contains("free") && code.toLowerCase().contains("ship");
    }

    @Override
    public void onGetRegions(Message msg) {
        //NOP
    }

    @Override
    public void onGetUser(Message msg) {
        if (msg.obj instanceof IAPNetworkError) {
            return;
        } else if (msg.obj instanceof ECSUserProfile) {
            final ECSUserProfile user = (ECSUserProfile) msg.obj;
            if (user.getDefaultAddress() != null) {
                 //TODO
                //mAddressController.setDeliveryAddress(user.getDefaultAddress());
            } else {
                return;
            }
        }
    }

    @Override
    public void onCreateAddress(Message msg) {
        //NOP
    }

    @Override
    public void onGetAddress(Message msg) {
        //NOP
        Log.d("pabitra", "I am heere");
    }

    @Override
    public void onSetDeliveryAddress(Message msg) {
    }

    @Override
    public void onGetDeliveryModes(Message msg) {
        if ((msg.obj instanceof IAPNetworkError)) {
            return;
        }else if(msg.obj instanceof Exception){
            Exception exception = (Exception) msg.obj;
            ECSUtility.showECSAlertDialog(mContext,"Error",exception);
        } if ((msg.obj instanceof List )) {
            List<ECSDeliveryMode> deliveryModeList =( List<ECSDeliveryMode>) msg.obj;
            CartModelContainer.getInstance().setDeliveryModes(deliveryModeList);
            if (deliveryModeList.size() > 0) {
                mAddressController.setDeliveryMode(deliveryModeList.get(0));
            }
        }
    }

    @Override
    public void onSetDeliveryMode(Message msg) {
        return;
    }


}
