/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.cart;

import android.content.Context;
import android.os.Message;


import com.ecs.demouapp.ui.analytics.ECSAnalytics;
import com.ecs.demouapp.ui.analytics.ECSAnalyticsConstant;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.controller.AddressController;
import com.ecs.demouapp.ui.eventhelper.EventHelper;
import com.ecs.demouapp.ui.model.AbstractModel;
import com.ecs.demouapp.ui.model.CartAddProductRequest;
import com.ecs.demouapp.ui.model.CartCreateRequest;
import com.ecs.demouapp.ui.model.CartDeleteProductRequest;
import com.ecs.demouapp.ui.model.CartUpdateProductQuantityRequest;
import com.ecs.demouapp.ui.model.DeleteCartRequest;
import com.ecs.demouapp.ui.model.GetCartsRequest;
import com.ecs.demouapp.ui.model.GetCurrentCartRequest;
import com.ecs.demouapp.ui.prx.PRXSummaryListExecutor;
import com.ecs.demouapp.ui.response.addresses.DeliveryModes;
import com.ecs.demouapp.ui.response.addresses.GetDeliveryModes;
import com.ecs.demouapp.ui.response.addresses.GetUser;
import com.ecs.demouapp.ui.response.carts.AppliedOrderPromotionEntity;
import com.ecs.demouapp.ui.response.carts.CartsEntity;
import com.ecs.demouapp.ui.response.carts.EntriesEntity;
import com.ecs.demouapp.ui.response.carts.PromotionEntity;
import com.ecs.demouapp.ui.response.error.Error;
import com.ecs.demouapp.ui.response.error.ServerError;
import com.ecs.demouapp.ui.session.HybrisDelegate;
import com.ecs.demouapp.ui.session.IAPNetworkError;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.session.RequestCode;
import com.ecs.demouapp.ui.session.RequestListener;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.ECSLog;
import com.ecs.demouapp.ui.utils.ModelConstants;
import com.ecs.demouapp.ui.utils.Utility;
import com.philips.cdp.prxclient.datamodels.summary.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShoppingCartPresenter extends AbstractShoppingCartPresenter
        implements AbstractModel.DataLoadListener, AddressController.AddressListener {

    private CartsEntity mCurrentCartData = null;
    private AddressController mAddressController;



    public ShoppingCartPresenter() {
    }

    public ShoppingCartPresenter(Context context, ShoppingCartListener<?> listener) {
        super(context, listener);
        mAddressController = new AddressController(context, ShoppingCartPresenter.this);
    }

    public void setHybrisDelegate(HybrisDelegate delegate) {
        mHybrisDelegate = delegate;
    }

    @Override
    public void getCurrentCartDetails() {
        final GetCurrentCartRequest model = new GetCurrentCartRequest(getStore(), null, this);
        getHybrisDelegate().sendRequest(0, model, model);
    }

    @Override
    public void deleteProduct(final ShoppingCartData summary) {
        final Map<String, String> query = new HashMap<>();
        query.put(ModelConstants.ENTRY_CODE, String.valueOf(summary.getEntryNumber()));
        query.put(ModelConstants.PRODUCT_CODE, String.valueOf(summary.getCtnNumber()));
        CartDeleteProductRequest model = new CartDeleteProductRequest(getStore(), query,
                new AbstractModel.DataLoadListener() {
                    @Override
                    public void onModelDataLoadFinished(final Message msg) {
                        //Track product delete action
                        ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                                ECSAnalyticsConstant.SPECIAL_EVENTS, ECSAnalyticsConstant.PRODUCT_REMOVED);
                        getCurrentCartDetails();
                    }

                    @Override
                    public void onModelDataError(final Message msg) {
                        mLoadListener.onLoadError(msg);
                    }
                });
        getHybrisDelegate().sendRequest(0, model, model);
    }

    @Override
    public void updateProductQuantity(final ShoppingCartData data, final int count, final int quantityStatus) {
        final HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.PRODUCT_CODE, data.getCtnNumber());
        query.put(ModelConstants.PRODUCT_QUANTITY, String.valueOf(count));
        query.put(ModelConstants.PRODUCT_ENTRYCODE, String.valueOf(data.getEntryNumber()));

        CartUpdateProductQuantityRequest model = new CartUpdateProductQuantityRequest(getStore(),
                query, new AbstractModel.DataLoadListener() {
            @Override
            public void onModelDataLoadFinished(final Message msg) {
                if (quantityStatus == 1) {
                    ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                            ECSAnalyticsConstant.SPECIAL_EVENTS, ECSAnalyticsConstant.ADD_TO_CART);
                } else if (quantityStatus == 0) {
                    ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                            ECSAnalyticsConstant.SPECIAL_EVENTS, ECSAnalyticsConstant.PRODUCT_REMOVED);
                }
                getCurrentCartDetails();
            }

            @Override
            public void onModelDataError(final Message msg) {
                ECSLog.d(ECSConstant.SHOPPING_CART_PRESENTER, msg.obj.toString());
                mLoadListener.onLoadError(msg);
            }
        });
        getHybrisDelegate().sendRequest(0, model, model);
    }

    public void deleteCart(final Context context, final ECSCartListener iapHandlerListener) {
        final HybrisDelegate delegate = HybrisDelegate.getInstance(context);
        final DeleteCartRequest model = new DeleteCartRequest(delegate.getStore(), null, null);
        delegate.sendRequest(RequestCode.DELETE_CART, model, new RequestListener() {
            @Override
            public void onSuccess(Message msg) {
                if (iapHandlerListener != null) {
                    getProductCartCount(context, iapHandlerListener);
                }
            }

            @Override
            public void onError(Message msg) {
                if (iapHandlerListener != null) {
                    iapHandlerListener.onFailure(msg);
                }
            }
        });
    }

    public void createCart(final Context context, final ECSCartListener iapHandlerListener,
                           final String ctnNumber, final boolean isBuy) {
        final HybrisDelegate delegate = HybrisDelegate.getInstance(context);
        final CartCreateRequest model = new CartCreateRequest(delegate.getStore(), null, null);
        delegate.sendRequest(RequestCode.CREATE_CART, model, new RequestListener() {
            @Override
            public void onSuccess(final Message msg) {
                mAddressController = new AddressController(context, ShoppingCartPresenter.this);

                mAddressController.getUser();

                if (isBuy) {
                    addProductToCart(context, ctnNumber, iapHandlerListener, true);
                } else {
                    if (iapHandlerListener != null) {
                        iapHandlerListener.onSuccess(0);
                    }
                }
            }

            @Override
            public void onError(final Message msg) {
                if (iapHandlerListener != null) {
                    iapHandlerListener.onFailure(msg);
                }
            }
        });
    }

    @Override
    public void addProductToCart(final Context context, String productCTN, final ECSCartListener
            iapHandlerListener,
                                 final boolean isFromBuyNow) {
        if (productCTN == null) return;
        final HashMap<String, String> params = new HashMap<>();
        params.put(ModelConstants.PRODUCT_CODE, productCTN);
        final HybrisDelegate delegate = HybrisDelegate.getInstance(context);
        final CartAddProductRequest model = new CartAddProductRequest(delegate.getStore(), params, null);
        delegate.sendRequest(RequestCode.ADD_TO_CART, model, new RequestListener() {
            @Override
            public void onSuccess(final Message msg) {
                if (isFromBuyNow) {
                    EventHelper.getInstance().notifyEventOccurred(ECSConstant.IAP_LAUNCH_SHOPPING_CART);
                    if (iapHandlerListener != null) {
                        iapHandlerListener.onSuccess(0);
                    }
                } else if (iapHandlerListener != null) {
                    iapHandlerListener.onSuccess(0);
                }
            }

            @Override
            public void onError(final Message msg) {
                if (iapHandlerListener != null) {
                    iapHandlerListener.onFailure(msg);
                }
            }
        });
    }

    @Override
    public void getProductCartCount(final Context context, final ECSCartListener
            iapCartListener) {
        final HybrisDelegate delegate = HybrisDelegate.getInstance(context);
        final GetCartsRequest model = new GetCartsRequest(delegate.getStore(), null, null);
        delegate.sendRequest(RequestCode.GET_CART, model, new RequestListener() {
                    @Override
                    public void onSuccess(final Message msg) {
                        if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
                            createCart(context, iapCartListener, null, false);
                        } else {
                            CartsEntity carts = (CartsEntity) msg.obj;
                            if (carts != null) {
                                int totalItems = carts.getTotalItems();
                                int quantity = 0;
                                if (carts.getEntries() != null) {
                                    List<EntriesEntity> entries = carts.getEntries();
                                    if (totalItems != 0 && null != entries) {
                                        for (int i = 0; i < entries.size(); i++) {
                                            quantity = quantity + entries.get(i).getQuantity();
                                        }
                                    }
                                }
                                iapCartListener.onSuccess(quantity);
                            } else {
                                Message message = new Message();
                                message.obj = "Error Fetching Cart";
                                iapCartListener.onFailure(message);
                            }
                        }
                    }

                    @Override
                    public void onError(final Message msg) {
                        handleNoCartErrorOrNotifyError(msg, context, iapCartListener, null, false);
                    }
                }

        );
    }

    @Override
    public void buyProduct(final Context context, final String ctnNumber, final ECSCartListener
            iapHandlerListener) {
        if (ctnNumber == null) return;
        final HybrisDelegate delegate = HybrisDelegate.getInstance(context);
        final GetCartsRequest model = new GetCartsRequest(delegate.getStore(), null, null);
        delegate.sendRequest(RequestCode.GET_CART, model, new RequestListener() {
            @Override
            public void onSuccess(final Message msg) {
                if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
                    createCart(context, iapHandlerListener, ctnNumber, true);
                } else if (msg.obj instanceof CartsEntity) {
                    CartsEntity getCarts = (CartsEntity) msg.obj;
                    if (null != getCarts) {
                        int totalItems = getCarts.getTotalItems();

                        List<EntriesEntity> entries = getCarts.getEntries();
                        if (totalItems != 0 && null != entries) {
                            boolean isProductAvailable = false;
                            for (int i = 0; i < entries.size(); i++) {
                                if (entries.get(i).getProduct().getCode().equalsIgnoreCase(ctnNumber)) {
                                    isProductAvailable = true;
                                    EventHelper.getInstance().notifyEventOccurred(ECSConstant.IAP_LAUNCH_SHOPPING_CART);
                                    break;
                                }
                            }
                            if (!isProductAvailable)
                                addProductToCart(context, ctnNumber, iapHandlerListener, true);
                            if (iapHandlerListener != null) {
                                iapHandlerListener.onSuccess(0);
                            }
                        } else {
                            addProductToCart(context, ctnNumber, iapHandlerListener, true);
                        }
                    }
                    // }
                }
            }

            @Override
            public void onError(final Message msg) {
                handleNoCartErrorOrNotifyError(msg, context, iapHandlerListener, ctnNumber, true);
            }
        });
    }


    private void handleNoCartErrorOrNotifyError(final Message msg, final Context context,
                                                final ECSCartListener iapHandlerListener,
                                                final String ctnNumber,
                                                final boolean isBuy) {
        if (isNoCartError(msg)) {
            createCart(context, iapHandlerListener, ctnNumber, isBuy);
        } else if (iapHandlerListener != null) {
            iapHandlerListener.onFailure(msg);
        }
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

    @Override
    public void onModelDataLoadFinished(final Message msg) {
        if (processResponseFromHybrisForGetCart(msg)) return;
        processResponseFromPRX(msg);
    }

    @Override
    public void onModelDataError(final Message msg) {
        if (isNoCartError(msg)) {
            EventHelper.getInstance().notifyEventOccurred(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED);
        } else {
            handleModelDataError(msg);
        }
    }

    private void processResponseFromPRX(final Message msg) {
        if (msg.obj instanceof HashMap) {
            notifyListChanged();
        } else {
            EventHelper.getInstance().notifyEventOccurred(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED);
        }
    }

    private boolean processResponseFromHybrisForGetCart(final Message msg) {
        if (msg.obj instanceof CartsEntity) {
            mCurrentCartData = (CartsEntity) msg.obj;
            if (null != mCurrentCartData.getEntries()) {
                makePrxCall(mCurrentCartData);
                return true;
            }
        }
        return false;
    }

    private void makePrxCall(final CartsEntity mCurrentCart) {
        final ArrayList<String> ctnsToBeRequestedForPRX = new ArrayList<>();
        final List<EntriesEntity> entries = mCurrentCart.getEntries();

        for (EntriesEntity entry : entries) {
            ctnsToBeRequestedForPRX.add(entry.getProduct().getCode());
        }
        PRXSummaryListExecutor builder = new PRXSummaryListExecutor(mContext, ctnsToBeRequestedForPRX, this);
        builder.preparePRXDataRequest();

    }

    private void notifyListChanged() {
        final ArrayList<ShoppingCartData> products = mergeResponsesFromHybrisAndPRX();
        refreshList(products);
    }

    private ArrayList<ShoppingCartData> mergeResponsesFromHybrisAndPRX() {
        final CartsEntity cartsEntity = mCurrentCartData;
        final List<EntriesEntity> entries = cartsEntity.getEntries();
        return getShoppingCartDatas(cartsEntity, entries);
    }

    public ArrayList<ShoppingCartData> getShoppingCartDatas(CartsEntity cartsEntity, List<EntriesEntity> entries) {
        final ArrayList<Data> list = (ArrayList<Data>) CartModelContainer.getInstance().getPRXSummaryList();
        final ArrayList<ShoppingCartData> products = new ArrayList<>();
        String ctn;
        for (EntriesEntity entry : entries) {
            ctn = entry.getProduct().getCode();
            applyPromotion(cartsEntity);
            final ShoppingCartData cartItem = new ShoppingCartData(entry, cartsEntity.getDeliveryMode());
            cartItem.setVatInclusive(cartsEntity.isNet());
            Data data;
            if (CartModelContainer.getInstance().isPRXSummaryPresent(ctn)) {
                data = CartModelContainer.getInstance().getProductSummary(ctn);
            } else {
                continue;
            }
            if (entry.getProduct().getDiscountPrice() != null)
                cartItem.setDiscountPrice(entry.getProduct().getDiscountPrice().getValue());
            cartItem.setImageUrl(data.getImageURL());
            cartItem.setProductTitle(data.getProductTitle());
            cartItem.setCtnNumber(ctn);
            cartItem.setQuantity(entry.getQuantity());
            cartItem.setFormattedPrice(entry.getBasePrice().getFormattedValue());
            cartItem.setValuePrice(String.valueOf(entry.getBasePrice().getValue()));
            cartItem.setFormattedTotalPriceWithTax(cartsEntity.getTotalPriceWithTax().getFormattedValue());
            cartItem.setFormattedTotalPrice(entry.getTotalPrice().getFormattedValue());
            cartItem.setTotalItems(cartsEntity.getTotalItems());
            cartItem.setMarketingTextHeader(data.getMarketingTextHeader());
            cartItem.setDeliveryAddressEntity(cartsEntity.getDeliveryAddress());
            cartItem.setVatValue(cartsEntity.getTotalTax().getFormattedValue());
            cartItem.setVatActualValue(String.valueOf(((int) cartsEntity.getTotalTax().getValue())));
            cartItem.setDeliveryItemsQuantity(cartsEntity.getDeliveryItemsQuantity());
            cartItem.setTotalDiscounts(cartsEntity.getTotalDiscounts().getFormattedValue());
            cartItem.setAppliedOrderPromotionEntityList(cartsEntity.getAppliedOrderPromotions());
            cartItem.setAppliedVouchers(cartsEntity.getAppliedVouchers());
            //required for Tagging
            cartItem.setCategory(cartsEntity.getEntries().get(0).getProduct().getCategories().get(0).getCode());
            products.add(cartItem);
        }
        return products;
    }

    public void applyPromotion(CartsEntity cartsEntity) {
        final List<AppliedOrderPromotionEntity> appliedOrderPromotions = cartsEntity.getAppliedOrderPromotions();
        if (appliedOrderPromotions != null && appliedOrderPromotions.size() != 0) {
            for (int i = 0; i < appliedOrderPromotions.size(); i++) {
                final PromotionEntity promotion = appliedOrderPromotions.get(i).getPromotion();
                if (promotion != null && isValidPromotion(promotion.getCode())) {
                    Utility.setPromotionRunning(true);
                    break;
                } else {
                    Utility.setPromotionRunning(false);
                }

            }
        } else {
            Utility.setPromotionRunning(false);
        }
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
        } else if (msg.obj instanceof GetUser) {
            final GetUser user = (GetUser) msg.obj;
            if (user.getDefaultAddress() != null) {
                mAddressController.setDeliveryAddress(user.getDefaultAddress().getId());
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
    }

    @Override
    public void onSetDeliveryAddress(Message msg) {
    }

    @Override
    public void onGetDeliveryModes(Message msg) {
        if ((msg.obj instanceof IAPNetworkError)) {
            return;
        } else if ((msg.obj instanceof GetDeliveryModes)) {
            final GetDeliveryModes deliveryModes = (GetDeliveryModes) msg.obj;
            final List<DeliveryModes> deliveryModeList = deliveryModes.getDeliveryModes();
            CartModelContainer.getInstance().setDeliveryModes(deliveryModeList);
            if (deliveryModeList.size() > 0) {
                mAddressController.setDeliveryMode(deliveryModeList.get(0).getCode());
            }
        }
    }

    @Override
    public void onSetDeliveryMode(Message msg) {
        return;
    }


}
