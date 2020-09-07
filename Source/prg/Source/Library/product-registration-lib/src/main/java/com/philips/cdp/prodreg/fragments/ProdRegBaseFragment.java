/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.prodreg.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.philips.cdp.prodreg.activity.ProdRegBaseActivity;
import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.constants.ProdRegError;
import com.philips.cdp.prodreg.error.ErrorHandler;
import com.philips.cdp.prodreg.error.ProdRegErrorMap;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.listener.ProdRegUiListener;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.register.RegisteredProduct;
import com.philips.cdp.prodreg.register.UserWithProducts;
import com.philips.cdp.product_registration_lib.R;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.DialogConstants;
import com.philips.platform.uid.view.widget.AlertDialogFragment;
import com.philips.platform.uid.view.widget.Label;

import java.util.List;

abstract class ProdRegBaseFragment extends Fragment implements BackEventListener {

    private static String TAG = ProdRegBaseFragment.class.getSimpleName();
    private static ActionBarListener mActionbarUpdateListener;
    private int mEnterAnimation = 0;
    private int mExitAnimation = 0;
    private AlertDialogFragment alertDialogFragment;
    private static final long serialVersionUID = -6635233525340545668L;


    public abstract int getActionbarTitleResId();

    public abstract String getActionbarTitle();

    public abstract boolean getBackButtonState();

    public abstract List<RegisteredProduct> getRegisteredProducts();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbarTitle();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideKeyboard();
        mActionbarUpdateListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
    }

    public void showFragment(Fragment fragment, FragmentLauncher fragmentLauncher,
                             int startAnimation, int endAnimation) {
        ProdRegLogger.i(TAG, "Product Registration Base Fragment -- Fragment Invoke");
        final FragmentActivity fragmentActivity = fragmentLauncher.getFragmentActivity();
        mActionbarUpdateListener = fragmentLauncher.getActionbarListener();
        int containerId = fragmentLauncher.getParentContainerResourceID();
        if (fragmentActivity != null && !fragmentActivity.isFinishing()) {
            initAnimation(startAnimation, endAnimation, fragmentActivity);
            addFragment(fragment, fragmentActivity, containerId);
        }
    }

    protected void showFragment(Fragment fragment) {
        final FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null && !fragmentActivity.isFinishing()) {
            addFragment(fragment, fragmentActivity, getId());
        }
    }

    protected void hideKeyboard() {
        if (isVisible()) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * Updating action bar title. The text has to be updated at each fragment
     * seletion/creation.
     */
    private void setActionbarTitle() {
        if (mActionbarUpdateListener != null) {
            mActionbarUpdateListener.updateActionBar(getActionbarTitleResId(), getBackButtonState());
            mActionbarUpdateListener.updateActionBar(getActionbarTitle(), getBackButtonState());
        }
    }

    protected void handleCallBack(final boolean onBack) {
        final ProdRegUiListener prodRegUiListener = PRUiHelper.getInstance().getProdRegUiListener();
        final UserWithProducts signedInUserWithProducts = new UserWithProducts(getContext(),  null,PRUiHelper.getInstance().getUserDataInstance());
        if (onBack && prodRegUiListener != null)
            prodRegUiListener.onProdRegBack(getRegisteredProducts(), signedInUserWithProducts);
        else if (prodRegUiListener != null)
            prodRegUiListener.onProdRegContinue(getRegisteredProducts(), signedInUserWithProducts);
    }

    private void addFragment(final Fragment fragment, final FragmentActivity fragmentActivity, final int containerId) {
        try {
            FragmentTransaction fragmentTransaction = fragmentActivity
                    .getSupportFragmentManager().beginTransaction();
            if (mEnterAnimation != 0 && mExitAnimation != 0) {
                fragmentTransaction.setCustomAnimations(mEnterAnimation,
                        mExitAnimation, mEnterAnimation, mExitAnimation);
            }
            final String simpleName = fragment.getClass().getSimpleName();
            fragmentTransaction.add(containerId, fragment, simpleName);
            Fragment currentFrag = fragmentActivity.getSupportFragmentManager()
                    .findFragmentById(getId());

            if (!(currentFrag instanceof ProdRegBaseFragment))
                fragmentTransaction.addToBackStack(ProdRegConstants.PROD_REG_VERTICAL_TAG);
            else
                fragmentTransaction.addToBackStack(simpleName);

            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            ProdRegLogger.e(TAG, e.getMessage());
        }
    }

    private void initAnimation(final int startAnimation, final int endAnimation, final FragmentActivity fragmentActivity) {
        final String startAnim;
        final String endAnim;
        if ((startAnimation != 0) && (endAnimation != 0)) {
            startAnim = fragmentActivity.getResources().getResourceName(startAnimation);
            endAnim = fragmentActivity.getResources().getResourceName(endAnimation);

            String packageName = fragmentActivity.getPackageName();
            mEnterAnimation = fragmentActivity.getResources().getIdentifier(startAnim,
                    "anim", packageName);
            mExitAnimation = fragmentActivity.getResources().getIdentifier(endAnim, "anim",
                    packageName);
        }
    }

    protected void showAlertOnError(final int statusCode) {
        try {
            final FragmentActivity activity = getActivity();
            if (activity != null && !activity.isFinishing()) {
                final ProdRegErrorMap prodRegErrorMap = new ErrorHandler().getError(activity, statusCode);
                ProdRegLogger.e(TAG, "Error Code : " + statusCode + " Error Description :" + prodRegErrorMap.getDescription());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("error_dialog");
                if (prev != null) {
                    ft.remove(prev);
                    ft.commitAllowingStateLoss();
                }
                if (ProdRegError.NO_INTERNET_AVAILABLE.getCode() == statusCode ||
                        ProdRegError.NETWORK_ERROR.getCode() == statusCode) {
                    showNetworkDialog(prodRegErrorMap.getTitle(), prodRegErrorMap.getDescription(),
                            "error_dialog");
                } else {
                    showErrorDialog(prodRegErrorMap.getTitle(), prodRegErrorMap.getDescription(),
                            statusCode, "error_dialog");
                }

            }
        } catch (IllegalStateException e) {
            ProdRegLogger.e(TAG, e.getMessage());
        }
    }

    private void showNetworkDialog(String title, String description, String error_dialog) {

        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(getContext())
                .setDialogType(DialogConstants.TYPE_ALERT)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(R.string.PRG_OK, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissAlertOnError();
                    }
                });
        alertDialogFragment = builder.create();
        alertDialogFragment.show(getFragmentManager(), error_dialog);
    }

    protected void dismissAlertOnError() {
        final FragmentActivity activity = getActivity();
        if (activity != null && !activity.isDestroyed()) {
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("error_dialog");
            if (prev instanceof AlertDialogFragment && alertDialogFragment != null) {
                alertDialogFragment.dismissAllowingStateLoss();
                alertDialogFragment = null;
            }
        }


    }

    public boolean clearFragmentStack() {
        final FragmentActivity activity = getActivity();
        try {
            if (activity != null && !activity.isFinishing()) {
                if (activity instanceof ProdRegBaseActivity) {
                    activity.finish();
                } else {
                    FragmentManager fragManager = activity.getSupportFragmentManager();
                    return fragManager.popBackStackImmediate(ProdRegConstants.PROD_REG_VERTICAL_TAG,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        } catch (IllegalStateException e) {
            ProdRegLogger.e(TAG, e.getMessage());
        }
        return false;
    }

    protected void unRegisterProdRegListener() {
        PRUiHelper.getInstance().unRegisterProdRegListener();
    }

    @Override
    public boolean handleBackEvent() {
        return false;
    }

    /**
     * setting the imageview with aspect ration 16:9
     */
    public void setImgageviewwithAspectRation(ImageView imageView) {
        float aspectRatio;
        int width = getResources().getDisplayMetrics().widthPixels;
        if (width > 680) {
            aspectRatio = (16 / 9);
            imageView.getLayoutParams().height = (int) ((width) / aspectRatio);
        } else {
            aspectRatio = (12 / 5);
            imageView.getLayoutParams().height = (int) ((width) / aspectRatio);
        }
    }

    public void showProdRegLoadingDialog(final String title, String tag) {

        if (alertDialogFragment != null && alertDialogFragment.isVisible()) {
            return;
        }
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        LayoutInflater lf = layoutInflater.cloneInContext(UIDHelper.getPopupThemedContext(getContext()));

        View view = lf.inflate(R.layout.prodreg_progress_dialog, null);
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(getContext())
                .setDialogType(DialogConstants.TYPE_DIALOG)
                .setDialogView(view)
                .setCancelable(false);
        alertDialogFragment = builder.create();
        alertDialogFragment.show(getFragmentManager(), tag);
        Label close = (Label) view.findViewById(R.id.dialogDescription);
        close.setText(title);

    }

    public void dismissProdRegLoadingDialog() {
////        final FragmentActivity activity = getActivity();
////        if (activity != null && !activity.isFinishing()) {
////            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("prg_dialog");
////            if (prev instanceof AlertDialogFragment && alertDialogFragment != null) {
////                alertDialogFragment.dismissAllowingStateLoss();
////                alertDialogFragment = null;
//            }
//        }
        if (alertDialogFragment != null && getActivity() != null && !getActivity().isFinishing()) {
            alertDialogFragment.dismissAllowingStateLoss();
            alertDialogFragment = null;

        }
    }


    public void showErrorDialog(String title, String description, final int statusCode, String tag) {
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(getContext())
                .setDialogType(DialogConstants.TYPE_ALERT)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(R.string.PRG_OK, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissAlertOnError();
                        final FragmentActivity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            clearFragmentStack();
                            PRUiHelper.getInstance().getProdRegUiListener().onProdRegFailed(ProdRegError.fromId(statusCode));
                            unRegisterProdRegListener();
                            if (activity instanceof ProdRegBaseActivity) {
                                getActivity().finish();
                            }
                        }
                    }
                });
        alertDialogFragment = builder.create();
        alertDialogFragment.show(getFragmentManager(), tag);
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (alertDialogFragment != null && alertDialogFragment.getDialog().isShowing()) {
//            alertDialogFragment.dismiss();
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProdRegLoadingDialog();
        dismissAlertOnError();
    }

}
