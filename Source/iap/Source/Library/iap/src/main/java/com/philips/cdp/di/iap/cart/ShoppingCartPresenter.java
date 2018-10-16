/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.cart;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.controller.AddressController;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.model.CartAddProductRequest;
import com.philips.cdp.di.iap.model.CartCreateRequest;
import com.philips.cdp.di.iap.model.CartDeleteProductRequest;
import com.philips.cdp.di.iap.model.CartUpdateProductQuantityRequest;
import com.philips.cdp.di.iap.model.DeleteCartRequest;
import com.philips.cdp.di.iap.model.DeleteVoucherRequest;
import com.philips.cdp.di.iap.model.GetAppliedVoucherRequest;
import com.philips.cdp.di.iap.model.GetCartsRequest;
import com.philips.cdp.di.iap.model.GetCurrentCartRequest;
import com.philips.cdp.di.iap.prx.PRXSummaryExecutor;
import com.philips.cdp.di.iap.response.addresses.DeliveryModes;
import com.philips.cdp.di.iap.response.addresses.GetDeliveryModes;
import com.philips.cdp.di.iap.response.addresses.GetUser;
import com.philips.cdp.di.iap.response.carts.AppliedOrderPromotionEntity;
import com.philips.cdp.di.iap.response.carts.Carts;
import com.philips.cdp.di.iap.response.carts.CartsEntity;
import com.philips.cdp.di.iap.response.carts.EntriesEntity;
import com.philips.cdp.di.iap.response.carts.PromotionEntity;
import com.philips.cdp.di.iap.response.error.Error;
import com.philips.cdp.di.iap.response.error.ServerError;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.session.RequestCode;
import com.philips.cdp.di.iap.session.RequestListener;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.ModelConstants;
import com.philips.cdp.prxclient.datamodels.summary.Data;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.philips.cdp.di.iap.session.RequestCode.DELETE_VOUCHER;
import static com.philips.cdp.di.iap.session.RequestCode.GET_APPLIED_VOUCHER;

public class ShoppingCartPresenter extends AbstractShoppingCartPresenter
        implements AbstractModel.DataLoadListener, AddressController.AddressListener {

    private CartsEntity mCurrentCartData = null;
    private AddressController mAddressController;
    private final static String  PROMOTION = "US-freeshipping";

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
        GetCurrentCartRequest model = new GetCurrentCartRequest(getStore(), null, this);
        getHybrisDelegate().sendRequest(0, model, model);
    }

    @Override
    public void deleteProduct(final ShoppingCartData summary) {
        Map<String, String> query = new HashMap<>();
        query.put(ModelConstants.ENTRY_CODE, String.valueOf(summary.getEntryNumber()));
        query.put(ModelConstants.PRODUCT_CODE, String.valueOf(summary.getCtnNumber()));
        CartDeleteProductRequest model = new CartDeleteProductRequest(getStore(), query,
                new AbstractModel.DataLoadListener() {
                    @Override
                    public void onModelDataLoadFinished(final Message msg) {
                        //Track product delete action
                        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                                IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.PRODUCT_REMOVED);
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
        HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.PRODUCT_CODE, data.getCtnNumber());
        query.put(ModelConstants.PRODUCT_QUANTITY, String.valueOf(count));
        query.put(ModelConstants.PRODUCT_ENTRYCODE, String.valueOf(data.getEntryNumber()));

        CartUpdateProductQuantityRequest model = new CartUpdateProductQuantityRequest(getStore(),
                query, new AbstractModel.DataLoadListener() {
            @Override
            public void onModelDataLoadFinished(final Message msg) {
                if (quantityStatus == 1) {
                    //Track Add to cart action
                    IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                            IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.ADD_TO_CART);
                } else if (quantityStatus == 0) {
                    //Track product delete action
                    IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                            IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.PRODUCT_REMOVED);
                }
                getCurrentCartDetails();
            }

            @Override
            public void onModelDataError(final Message msg) {
                IAPLog.d(IAPConstant.SHOPPING_CART_PRESENTER, msg.obj.toString());
                mLoadListener.onLoadError(msg);
            }
        });
        getHybrisDelegate().sendRequest(0, model, model);
    }

    public void deleteCart(final Context context, final IAPCartListener iapHandlerListener) {
        HybrisDelegate delegate = HybrisDelegate.getInstance(context);
        DeleteCartRequest model = new DeleteCartRequest(delegate.getStore(), null, null);
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

    public void createCart(final Context context, final IAPCartListener iapHandlerListener,
                           final String ctnNumber, final boolean isBuy) {
        HybrisDelegate delegate = HybrisDelegate.getInstance(context);
        CartCreateRequest model = new CartCreateRequest(delegate.getStore(), null, null);
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
    public void addProductToCart(final Context context, String productCTN, final IAPCartListener
            iapHandlerListener,
                                 final boolean isFromBuyNow) {
        if (productCTN == null) return;
        HashMap<String, String> params = new HashMap<>();
        params.put(ModelConstants.PRODUCT_CODE, productCTN);
        HybrisDelegate delegate = HybrisDelegate.getInstance(context);
        CartAddProductRequest model = new CartAddProductRequest(delegate.getStore(), params, null);
        delegate.sendRequest(RequestCode.ADD_TO_CART, model, new RequestListener() {
            @Override
            public void onSuccess(final Message msg) {
                if (isFromBuyNow) {
                    EventHelper.getInstance().notifyEventOccurred(IAPConstant.IAP_LAUNCH_SHOPPING_CART);
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
    public void getProductCartCount(final Context context, final IAPCartListener
            iapCartListener) {
        HybrisDelegate delegate = HybrisDelegate.getInstance(context);
        GetCartsRequest model = new GetCartsRequest(delegate.getStore(), null, null);
        delegate.sendRequest(RequestCode.GET_CART, model, new RequestListener() {
                    @Override
                    public void onSuccess(final Message msg) {
                        if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
                            createCart(context, iapCartListener, null, false);
                        } else {
                            Carts carts = (Carts) msg.obj;
                            if (carts != null && carts.getCarts() != null) {
                                if (carts.getCarts().size() > 1) {
                                    deleteCart(context, iapCartListener);
                                } else {
                                    int quantity = 0;
                                    int totalItems = carts.getCarts().get(0).getTotalItems();
                                    List<EntriesEntity> entries = carts.getCarts().get(0).getEntries();
                                    if (totalItems != 0 && null != entries) {
                                        for (int i = 0; i < entries.size(); i++) {
                                            quantity = quantity + entries.get(i).getQuantity();
                                        }
                                    }
                                    if (iapCartListener != null) {
                                        iapCartListener.onSuccess(quantity);
                                    }
                                }
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
    public void buyProduct(final Context context, final String ctnNumber, final IAPCartListener
            iapHandlerListener) {
        if (ctnNumber == null) return;
        HybrisDelegate delegate = HybrisDelegate.getInstance(context);
        GetCartsRequest model = new GetCartsRequest(delegate.getStore(), null, null);
        delegate.sendRequest(RequestCode.GET_CART, model, new RequestListener() {
            @Override
            public void onSuccess(final Message msg) {
                if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
                    createCart(context, iapHandlerListener, ctnNumber, true);
                } else if (msg.obj instanceof Carts) {
                    Carts getCarts = (Carts) msg.obj;
                    if (null != getCarts) {
                        int totalItems = getCarts.getCarts().get(0).getTotalItems();
                        List<EntriesEntity> entries = getCarts.getCarts().get(0).getEntries();
                        if (totalItems != 0 && null != entries) {
                            boolean isProductAvailable = false;
                            for (int i = 0; i < entries.size(); i++) {
                                if (entries.get(i).getProduct().getCode().equalsIgnoreCase(ctnNumber)) {
                                    isProductAvailable = true;
                                    EventHelper.getInstance().notifyEventOccurred(IAPConstant.IAP_LAUNCH_SHOPPING_CART);
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
                                                final IAPCartListener iapHandlerListener,
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
            EventHelper.getInstance().notifyEventOccurred(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED);
        } else {
            handleModelDataError(msg);
        }
    }

    private void processResponseFromPRX(final Message msg) {
        if (msg.obj instanceof HashMap) {
            notifyListChanged();
        } else {
            EventHelper.getInstance().notifyEventOccurred(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED);
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
        ArrayList<String> ctnsToBeRequestedForPRX = new ArrayList<>();
        List<EntriesEntity> entries = mCurrentCart.getEntries();

        for (EntriesEntity entry : entries) {
            ctnsToBeRequestedForPRX.add(entry.getProduct().getCode());
        }
        PRXSummaryExecutor builder = new PRXSummaryExecutor(mContext, ctnsToBeRequestedForPRX, this);
        builder.preparePRXDataRequest();

    }

    private void notifyListChanged() {
        ArrayList<ShoppingCartData> products = mergeResponsesFromHybrisAndPRX();
        refreshList(products);
    }

    private ArrayList<ShoppingCartData> mergeResponsesFromHybrisAndPRX() {
        CartsEntity cartsEntity = mCurrentCartData;
        List<EntriesEntity> entries = cartsEntity.getEntries();
        return getShoppingCartDatas(cartsEntity, entries);
    }

    public ArrayList<ShoppingCartData> getShoppingCartDatas(CartsEntity cartsEntity, List<EntriesEntity> entries) {
        HashMap<String, SummaryModel> list = CartModelContainer.getInstance().getPRXSummaryList();
        ArrayList<ShoppingCartData> products = new ArrayList<>();
        String ctn;
        for (EntriesEntity entry : entries) {
            ctn = entry.getProduct().getCode();
            applyPromotion(cartsEntity);
            ShoppingCartData cartItem = new ShoppingCartData(entry, cartsEntity.getDeliveryMode());
            cartItem.setVatInclusive(cartsEntity.isNet());
            Data data;
            if (list.containsKey(ctn)) {
                data = list.get(ctn).getData();
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
            //required for Tagging
            cartItem.setCategory(cartsEntity.getEntries().get(0).getProduct().getCategories().get(0).getCode());
            products.add(cartItem);
        }
        return products;
    }
    public void applyPromotion(CartsEntity cartsEntity) {
        List<AppliedOrderPromotionEntity> appliedOrderPromotions = cartsEntity.getAppliedOrderPromotions();
        if(appliedOrderPromotions!=null && appliedOrderPromotions.size()!=0){
            for(int i=0;i< appliedOrderPromotions.size() ;i++ ) {
                PromotionEntity promotion = appliedOrderPromotions.get(i).getPromotion();
                if(promotion !=null && promotion.getCode().equalsIgnoreCase(PROMOTION)) {
                    String currentDeliveryCost = cartsEntity.getDeliveryMode().getDeliveryCost().getFormattedValue();
                    String newDeliveryCost = currentDeliveryCost.replace(currentDeliveryCost.substring(1, (currentDeliveryCost.length())), " 0.0");
                    cartsEntity.getDeliveryMode().getDeliveryCost().setFormattedValue(newDeliveryCost);
                    break;
                }
            }
        }
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
            GetUser user = (GetUser) msg.obj;
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
        //mAddressController.getDeliveryModes();
    }

    @Override
    public void onGetDeliveryModes(Message msg) {
        if ((msg.obj instanceof IAPNetworkError)) {
            return;
        } else if ((msg.obj instanceof GetDeliveryModes)) {
            GetDeliveryModes deliveryModes = (GetDeliveryModes) msg.obj;
            List<DeliveryModes> deliveryModeList = deliveryModes.getDeliveryModes();
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


    private void getAppliedVoucherCode( final ArrayList<ShoppingCartData> aData) {

        final HybrisDelegate delegate = HybrisDelegate.getInstance(mContext);
        GetAppliedVoucherRequest request = new GetAppliedVoucherRequest(delegate.getStore(), null, new AbstractModel.DataLoadListener() {
            @Override
            public void onModelDataLoadFinished(Message msg) {
                final String voucherCode ;
                int requestCode = msg.what;
                if(requestCode==GET_APPLIED_VOUCHER){
                    if(msg.obj instanceof String){
                        voucherCode=(String)msg.obj;
                        aData.get(0).setAppliedVoucherCode(voucherCode);
                        refreshList(aData);
                    }
                }
            }
            @Override
            public void onModelDataError(Message msg) {

            }
        });
        delegate.sendRequest(GET_APPLIED_VOUCHER, request, request);
    }


    @Override
    public void deleteAppliedVoucher(String voucherCode) {
        final HybrisDelegate delegate = HybrisDelegate.getInstance(mContext);

        DeleteVoucherRequest deleteVoucherRequest = new DeleteVoucherRequest(delegate.getStore(), null, new AbstractModel.DataLoadListener() {
            @Override
            public void onModelDataLoadFinished(Message msg) {
                int requestCode = msg.what;
                if(requestCode==DELETE_VOUCHER){
                    if(msg.obj==null){
                        Log.v("Vouchers Delete", "Success");
                        getCurrentCartDetails();// refresh as voucher is removed
                    }

                }
            }

            @Override
            public void onModelDataError(Message msg) {
                Log.v("Vouchers Delete", "failure");
            }
        }, voucherCode);
        delegate.sendRequest(DELETE_VOUCHER, deleteVoucherRequest, deleteVoucherRequest);
    }

}
