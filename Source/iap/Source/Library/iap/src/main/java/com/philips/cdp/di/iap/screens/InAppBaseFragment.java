/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.activity.IAPActivity;
import com.philips.cdp.di.iap.cart.IAPCartListener;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.controller.AddressController;
import com.philips.cdp.di.iap.integration.IAPListener;
import com.philips.cdp.di.iap.response.addresses.DeliveryModes;
import com.philips.cdp.di.iap.response.addresses.GetDeliveryModes;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.IAPUtility;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.view.widget.ProgressBar;

import java.util.List;

public abstract class InAppBaseFragment extends Fragment implements BackEventListener {
    private Context mContext;
    private ActionBarListener mActionbarUpdateListener;
    protected IAPListener mIapListener;

    String mTitle = "";

    protected final int SMALL = 0;
    protected final int MEDIUM = 1;
    protected final int BIG = 2;



    protected IAPCartListener mProductCountListener = new IAPCartListener() {
        @Override
        public void onSuccess(final int count) {
            updateCount(count);
            hideProgressBar();
        }

        @Override
        public void onFailure(final Message msg) {
            hideProgressBar();
        }
    };
    private ProgressBar mPTHBaseFragmentProgressBar;

    public void setActionBarListener(ActionBarListener actionBarListener, IAPListener iapListener) {
        mActionbarUpdateListener = actionBarListener;
        mIapListener = iapListener;
    }

    public enum AnimationType {
        NONE
    }

    protected void setTitleAndBackButtonVisibility(int resourceId, boolean isVisible) {
        mTitle = getString(resourceId);
        if (mActionbarUpdateListener != null)
           mActionbarUpdateListener.updateActionBar(resourceId, isVisible);

    }


    protected void setTitleAndBackButtonVisibility(String title, boolean isVisible) {
        mTitle = title;
        if (mActionbarUpdateListener != null)
            mActionbarUpdateListener.updateActionBar(title, isVisible);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(CartModelContainer.getInstance().getAppInfraInstance() == null) {
            moveToVerticalAppByClearingStack();
        }
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        NetworkUtility.getInstance().dismissErrorDialog();
        hideProgressBar();
    }

   /* public void showProgressDialog(Context context, String message) {
        mProgressDialog = new ProgressDialog(UIDHelper.getPopupThemedContext(context));
        mProgressDialog.getWindow().setGravity(Gravity.CENTER);
        mProgressDialog.setCancelable(false);
        //mProgressDialog.setMessage(message + "...");

        if ((!mProgressDialog.isShowing()) && !((Activity) context).isFinishing()) {
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progressbar_dls);
        }
    }



    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }*/

 /*   public boolean isProgressDialogShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }*/

  /*  public void changeProgressMessage(String message) {
        mProgressDialog.setMessage(message);
    }*/

    public void addFragment(InAppBaseFragment newFragment,
                            String newFragmentTag,boolean isReplaceWithBackStack) {
        if (mActionbarUpdateListener == null || mIapListener == null)
            new RuntimeException("ActionBarListner and IAPListner cant be null");
        else {

            newFragment.setActionBarListener(mActionbarUpdateListener, mIapListener);
            if (getActivity() != null && !getActivity().isFinishing()) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                final String simpleName = newFragment.getClass().getSimpleName();
                if(isReplaceWithBackStack) {
                    transaction.replace(getId(), newFragment, simpleName);
                    transaction.addToBackStack(newFragmentTag);
                }else {
                    transaction.replace(getId(), newFragment, simpleName);
                    //transaction.addToBackStack(null);
                }

                transaction.commitAllowingStateLoss();

                IAPLog.d(IAPLog.LOG, "Add fragment " + newFragment.getClass().getSimpleName() + "   ("
                        + newFragmentTag + ")");
            }


        }
    }

    public void showFragment(String fragmentTag) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().getSupportFragmentManager().popBackStackImmediate(fragmentTag, 0);

        }
    }


    public void showAddressFragment(String fragmentTag) {
           getFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            addFragment(AddressSelectionFragment.createInstance(new Bundle(),
                    AnimationType.NONE), AddressSelectionFragment.TAG,true);
    }


    public boolean moveToPreviousFragment() {
        return getFragmentManager().popBackStackImmediate();
    }

    public void showProductCatalogFragment(String fragmentTag) {
        Fragment fragment = getFragmentManager().findFragmentByTag(ProductCatalogFragment.TAG);
        if (fragment == null) {
            getFragmentManager().popBackStack(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            addFragment(ProductCatalogFragment.createInstance(new Bundle(),
                    AnimationType.NONE), ProductCatalogFragment.TAG,true);
        } else {
            getFragmentManager().popBackStack(ProductCatalogFragment.TAG, 0);
        }
    }

    public void clearFragmentStack() {
        if (getActivity() != null && getActivity() instanceof IAPActivity && !getActivity().isFinishing()) {
            FragmentManager fragManager = getActivity().getSupportFragmentManager();
            int count = fragManager.getBackStackEntryCount();
            for (; count >= 0; count--) {
                List<Fragment> fragmentList = fragManager.getFragments();
                if (fragmentList != null && fragmentList.size() > 0) {
                    fragManager.popBackStack();
                }
            }
        }
    }

    public void moveToVerticalAppByClearingStack() {
        clearFragmentStack();
        finishActivity();
    }

    protected boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mContext != null && !NetworkUtility.getInstance().isNetworkAvailable(connectivityManager)) {
            NetworkUtility.getInstance().showErrorDialog(mContext,
                    getFragmentManager(), mContext.getString(R.string.iap_ok),
                    mContext.getString(R.string.iap_you_are_offline), mContext.getString(R.string.iap_no_internet));
            return false;
        } else {
            return true;
        }
    }

    public void updateCount(final int count) {
        if (mIapListener != null) {
            mIapListener.onGetCartCount(count);
            hideProgressBar();
        }
    }

    public void setCartIconVisibility(final boolean shouldShow) {
        if (mIapListener != null) {
            if(isUserLoggedIn()) {
                mIapListener.updateCartIconVisibility(shouldShow);
            }else{
                mIapListener.updateCartIconVisibility(false);
            }
        }
    }

    protected void finishActivity() {
        if (getActivity() != null && getActivity() instanceof IAPActivity && !getActivity().isFinishing()) {
            getActivity().finish();
        }
    }

    @Override
    public boolean handleBackEvent() {
        return false;
    }

    public void handleDeliveryMode(Message msg, AddressController addressController) {
        if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
            hideProgressBar();
        } else if ((msg.obj instanceof IAPNetworkError)) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
            hideProgressBar();
        } else if ((msg.obj instanceof GetDeliveryModes)) {
            GetDeliveryModes deliveryModes = (GetDeliveryModes) msg.obj;
            List<DeliveryModes> deliveryModeList = deliveryModes.getDeliveryModes();
            if (deliveryModeList.size() > 0) {
                CartModelContainer.getInstance().setDeliveryModes(deliveryModeList);
               // addressController.setDeliveryMode(deliveryModeList.get(0).getCode());
            }
        }
    }


    public void createCustomProgressBar(ViewGroup group, int size) {
        if(getContext() == null) return;
        ViewGroup parentView = (ViewGroup) getView();
        ViewGroup layoutViewGroup = group;
        if (parentView != null) {
            group = parentView;
        }

        switch (size) {
            case BIG:
                getContext().getTheme().applyStyle(R.style.PTHCircularPBBig, true);
                break;
            case SMALL:
                getContext().getTheme().applyStyle(R.style.PTHCircularPBSmall, true);
                break;
            case MEDIUM:
                getContext().getTheme().applyStyle(R.style.PTHCircularPBMedium, true);
                break;
            default:
                getContext().getTheme().applyStyle(R.style.PTHCircularPBMedium, true);
                break;
        }

        mPTHBaseFragmentProgressBar = new ProgressBar(getContext(), null, R.attr.pth_cirucular_pb);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPTHBaseFragmentProgressBar.setLayoutParams(params);

        try {
            group.addView(mPTHBaseFragmentProgressBar);
        } catch (Exception e) {
            if(layoutViewGroup!=null) {
                layoutViewGroup.addView(mPTHBaseFragmentProgressBar);
            }
        }

        if (mPTHBaseFragmentProgressBar != null) {
            mPTHBaseFragmentProgressBar.setVisibility(View.VISIBLE);
            if(getActivity()!=null) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    }

    public void hideProgressBar() {
        if (mPTHBaseFragmentProgressBar != null) {
            mPTHBaseFragmentProgressBar.setVisibility(View.GONE);
            if(getActivity()!=null) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    }

    public boolean shouldGiveCallBack(){
       return IAPUtility.getInstance().getIapOrderFlowCompletion()!=null && !IAPUtility.getInstance().getIapOrderFlowCompletion().shouldPopToProductList();
    }

    public void sendCallback(boolean isSuccess){
        moveToVerticalAppByClearingStack();
        if(isSuccess){
            IAPUtility.getInstance().getIapOrderFlowCompletion().didPlaceOrder();
        }else{
            IAPUtility.getInstance().getIapOrderFlowCompletion().didCancelOrder();
        }
    }

    protected boolean isUserLoggedIn(){
        return  IAPUtility.getInstance().getUserDataInterface() != null && IAPUtility.getInstance().getUserDataInterface().getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN ;
    }
}
