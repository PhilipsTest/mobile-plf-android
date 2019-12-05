/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.cdp.prodreg.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.cdp.prodreg.activity.ProdRegBaseActivity;
import com.philips.cdp.prodreg.constants.AnalyticsConstants;
import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.constants.ProdRegError;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.register.RegisteredProduct;
import com.philips.cdp.prodreg.tagging.ProdRegTagging;
import com.philips.cdp.product_registration_lib.R;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.uid.view.widget.Button;

import java.util.List;

public class ProdRegFirstLaunchFragment extends ProdRegBaseFragment {

    public static final String TAG = ProdRegFirstLaunchFragment.class.getName();
    private List<RegisteredProduct> registeredProducts;
    private int resId = 0;
    private Bundle dependencies;
    private ImageView productImage;
    private TextView benefitsMessage;
    private Button registerButton;
    private static final long serialVersionUID = -6635233525340545669L;


    @Override
    public int getActionbarTitleResId() {
        return R.string.PRG_NavBar_Title;
    }

    @Override
    public String getActionbarTitle() {
        return getString(R.string.PRG_NavBar_Title);
    }

    @Override
    public boolean getBackButtonState() {
        return false;
    }

    @Override
    public List<RegisteredProduct> getRegisteredProducts() {
        return registeredProducts;
    }

    Button registerLater;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prodreg_first_launch, container, false);
        registerButton = view.findViewById(R.id.prg_welcomeScreen_yes_button);
        registerLater = view.findViewById(R.id.prg_welcomeScreen_no_button);
        productImage = view.findViewById(R.id.prg_welcomeScreem_product_image);
        benefitsMessage = view.findViewById(R.id.prg_welcomeScreen_benefit_label);
        benefitsMessage.setText(getString(R.string.PRG_ReceiveUpdates_Lbltxt) + "\n" +
                getString(R.string.prod_reg_benefits_conf_message));
        registerButton.setOnClickListener(onClickRegister());
        registerLater.setOnClickListener(onClickNoThanks());
        ProdRegTagging.trackPage(AnalyticsConstants.PRG_BENEFITS);
        return view;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dependencies = getArguments();
        if (dependencies != null) {
            registeredProducts = (List<RegisteredProduct>) dependencies.getSerializable(ProdRegConstants.MUL_PROD_REG_CONSTANT);
            resId = dependencies.getInt(ProdRegConstants.PROD_REG_FIRST_IMAGE_ID);
            if (resId != 0) {
                productImage.setVisibility(View.VISIBLE);
                productImage.setImageDrawable(getResources().getDrawable(resId, getActivity().getTheme()));
                productImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                productImage.requestLayout();
            }
            boolean hideNoThanksButton = dependencies.getBoolean(ProdRegConstants.PROD_REG_FIRST_NO_THANKS_BTN_VISIBLE);
            if(!hideNoThanksButton ) {

                //Registration warranty button text can be configured only in case of no thanks button is hidden.
                registerLater.setVisibility(View.GONE);

                String registerBtnText = dependencies.getString(ProdRegConstants.PROD_REG_FIRST_REG_BTN_TEXT);

                if(registerBtnText!= null && registerBtnText.trim().length() > 0) {
                    registerButton.setText(dependencies.getString(ProdRegConstants.PROD_REG_FIRST_REG_BTN_TEXT));
                }
            }
        }
    }

    @NonNull
    private View.OnClickListener onClickNoThanks() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                clearFragmentStack();
                handleCallBack(true);
                unRegisterProdRegListener();
            }
        };
    }

    @NonNull
    private View.OnClickListener onClickRegister() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final FragmentActivity activity = getActivity();
                UserDataInterface userDataInterface = PRUiHelper.getInstance().getUserDataInstance();
                if (userDataInterface != null && PRUiHelper.getInstance().getUserDataInstance().getUserLoggedInState().ordinal()
                        >= UserLoggedInState.PENDING_HSDP_LOGIN.ordinal()) {
                    final  ProdRegRegistrationFragment processFragment = new ProdRegRegistrationFragment();
                    processFragment.setArguments(dependencies);
                    registerButton.setClickable(false);
                    showFragment(processFragment);
                } else {
                    clearFragmentStack();
                    PRUiHelper.getInstance().getProdRegUiListener().onProdRegFailed(ProdRegError.USER_NOT_SIGNED_IN);
                    unRegisterProdRegListener();
                    if (activity != null && !activity.isFinishing() && activity instanceof ProdRegBaseActivity) {
                        activity.finish();
                    }
                }
            }
        };
    }
}
