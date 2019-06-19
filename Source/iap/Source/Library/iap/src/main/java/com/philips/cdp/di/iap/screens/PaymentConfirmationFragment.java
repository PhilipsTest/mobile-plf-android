/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.AlertListener;
import com.philips.cdp.di.iap.utils.ModelConstants;

import java.util.HashMap;

public class PaymentConfirmationFragment extends InAppBaseFragment
        implements AlertListener {
    public static final String TAG = PaymentConfirmationFragment.class.getName();
    private Context mContext;

    private TextView mConfirmationText;
    private TextView mConfirmWithEmail;
    private TextView mOrderNumber,mOrderNumberText;
    private boolean isOrderSuccess =false;
    private boolean isCancelled = false;

    //private TwoButtonDialogFragment mDialog;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.iap_order_confirmation,
                container, false);
        initViews(view);
        updatePaymentUI();
        return view;
    }

    private void initViews(ViewGroup viewGroup) {
        mConfirmationText = viewGroup.findViewById(R.id.tv_thank_you_title);
        mConfirmWithEmail = viewGroup.findViewById(R.id.tv_confirmation_email_shortly);
        mOrderNumber = viewGroup.findViewById(R.id.tv_order_number_val);
        mOrderNumberText = viewGroup.findViewById(R.id.tv_order_number);
        final Button mOKButton = viewGroup.findViewById(R.id.ok_btn);
        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!isNetworkConnected()) return;
                handleExit();
            }
        });
    }

    private void updatePaymentUI() {
        Bundle arguments = getArguments();
        boolean isPaymentSuccessful = arguments.getBoolean(ModelConstants.PAYMENT_SUCCESS_STATUS, false);
        isCancelled = arguments.getBoolean(ModelConstants.PAYMENT_CANCELLED,false);
        if (isPaymentSuccessful) {
            updatePaymentSuccessUI(arguments);
            isOrderSuccess =true;
        } else {
            updatePaymentFailureUI();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        IAPAnalytics.trackPage(IAPAnalyticsConstant.PAYMENT_CONFIRMATION_PAGE_NAME);
        setTitleAndBackButtonVisibility(R.string.iap_confirmation, false);
        setCartIconVisibility(false);
    }

    private void updatePaymentFailureUI() {
        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                IAPAnalyticsConstant.PAYMENT_STATUS, IAPAnalyticsConstant.FAILED);

        setPaymentTitle(getFailureTitle());
        mConfirmWithEmail.setVisibility(View.INVISIBLE);
    }

    private int getFailureTitle() {
        if(isCancelled) return R.string.iap_payment_cancelled;
       return R.string.iap_payment_failed;
    }

    private void updatePaymentSuccessUI(final Bundle arguments) {
        if (arguments != null) {
            if (arguments.containsKey(ModelConstants.ORDER_NUMBER)) {
                mOrderNumber.setVisibility(View.VISIBLE);
                mOrderNumberText.setVisibility(View.VISIBLE);
                mOrderNumber.setText(arguments.getString(ModelConstants.ORDER_NUMBER));
                HashMap<String, String> contextData = new HashMap<>();
                contextData.put(IAPAnalyticsConstant.PURCHASE_ID, mOrderNumber.getText().toString());
                contextData.put(IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.PURCHASE);
                contextData.put(IAPAnalyticsConstant.PAYMENT_STATUS, IAPAnalyticsConstant.SUCCESS);
                IAPAnalytics.trackMultipleActions(IAPAnalyticsConstant.SEND_DATA, contextData);
            }
            String email = HybrisDelegate.getInstance(mContext).getStore().getJanRainEmail();
            if (arguments.containsKey(ModelConstants.EMAIL_ADDRESS)) {
                email = arguments.getString(ModelConstants.EMAIL_ADDRESS);
            }
            String emailConfirmation = getString(R.string.iap_confirmation_email_msg);
            Spanned boldCount;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                boldCount = Html.fromHtml(emailConfirmation+ "  <b>" + email + "</b>",Html.FROM_HTML_MODE_LEGACY);
            } else {
                boldCount = Html.fromHtml(emailConfirmation+ "  <b>" + email + "</b>");
            }

            mConfirmWithEmail.setText(boldCount);
            setPaymentTitle(R.string.iap_thank_for_order);
        }
    }

    private void setPaymentTitle(final int iap_thank_for_order) {
        mConfirmationText.setText(iap_thank_for_order);
    }

    private void handleExit() {
        Fragment fragment = getFragmentManager().findFragmentByTag(BuyDirectFragment.TAG);
        if (fragment != null) {
            if(shouldGiveCallBack()){
                sendCallback(isOrderSuccess);
                return;
            }
            moveToVerticalAppByClearingStack();
        } else {
            if(shouldGiveCallBack()){
                sendCallback(isOrderSuccess);
                return;
            }
            moveToProductCatalog();
        }
    }

    private void moveToProductCatalog() {
        Fragment fragment = getFragmentManager().findFragmentByTag(ProductCatalogFragment.TAG);
        if (fragment == null) {
            getFragmentManager().popBackStack(ShoppingCartFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            addFragment(ProductCatalogFragment.createInstance(new Bundle(), AnimationType.NONE),
                    ProductCatalogFragment.TAG,true);
        } else {
            getFragmentManager().popBackStack(ProductCatalogFragment.TAG, 0);
        }
    }


    public static PaymentConfirmationFragment createInstance(final Bundle bundle, final AnimationType animType) {
        PaymentConfirmationFragment fragment = new PaymentConfirmationFragment();
        bundle.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onPositiveBtnClick() {
        handleExit();
    }

    @Override
    public void onNegativeBtnClick() {
        // Do Nothing
    }
}