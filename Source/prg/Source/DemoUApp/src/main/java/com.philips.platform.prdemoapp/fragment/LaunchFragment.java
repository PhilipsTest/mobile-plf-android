package com.philips.platform.prdemoapp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.prdemoapp.activity.MainActivity;
import com.philips.platform.prdemoapp.theme.fragments.BaseFragment;
import com.philips.platform.prdemoapplibrary.R;
import com.philips.platform.uappframework.launcher.ActivityLauncher;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class LaunchFragment extends BaseFragment implements View.OnClickListener {

    private TextView configurationTextView;
    private Button user_registration_button, pr_button, reg_list_button,btn_set_theme;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.launch_fragment, container, false);
        setUp(view);
        return view;
    }

    private void setUp(final View view) {
        initViews(view);
        setOnClickListeners();

    }

    @NonNull
    public void setOnClickListeners() {
        user_registration_button.setOnClickListener(this);
        pr_button.setOnClickListener(this);
        reg_list_button.setOnClickListener(this);
    }

    private void initViews(final View view) {
        user_registration_button = (Button) view.findViewById(R.id.btn_user_registration);
        pr_button = (Button) view.findViewById(R.id.btn_product_registration);
        reg_list_button = (Button) view.findViewById(R.id.btn_register_list);
        configurationTextView = (TextView) view.findViewById(R.id.configuration);
        configurationTextView.setText(RegistrationConfiguration.getInstance().getRegistrationEnvironment());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_user_registration) {
            launchUserRegistration();

        } else if (i == R.id.btn_product_registration) {
            showFragment(new ManualRegistrationFragment(), ManualRegistrationFragment.TAG);

        } else if (i == R.id.btn_register_list) {
            showFragment(new ProductListFragment(), ProductListFragment.TAG);

        } else {
        }
    }

    private void launchUserRegistration() {
        URLaunchInput urLaunchInput;
        ActivityLauncher activityLauncher;
        URInterface urInterface;

        PRUiHelper.getInstance().getAppInfraInstance().getTagging().setPreviousPage("demoapp:home");
        urLaunchInput = new URLaunchInput();
        //urLaunchInput.setEndPointScreen(RegistrationLaunchMode.ACCOUNT_SETTINGS);
        urLaunchInput.setEndPointScreen(RegistrationLaunchMode.USER_DETAILS);
        urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);
        urLaunchInput.setUserRegistrationUIEventListener(new UserRegistrationUIEventListener() {
            @Override
            public void onUserRegistrationComplete(final Activity activity) {
                activity.finish();
            }

            @Override
            public void onPrivacyPolicyClick(final Activity activity) {

            }

            @Override
            public void onTermsAndConditionClick(final Activity activity) {

            }

            @Override
            public void onPersonalConsentClick(Activity activity) {

            }
        });
        activityLauncher = new ActivityLauncher(getActivity(), ActivityLauncher.
        ActivityOrientation.SCREEN_ORIENTATION_SENSOR, ((MainActivity) getActivity()).getThemeConfig(),
                ((MainActivity) getActivity()).getThemeResourceId(), null);


        urInterface = new URInterface();
        urInterface.launch(activityLauncher, urLaunchInput);
    }

    private void showFragment(BaseFragment  fragment, final String TAG) {
        /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.parent_layout, fragment,
                TAG);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commitAllowingStateLoss();*/
        ((MainActivity) getActivity()).getNavigationController().switchFragment(fragment);
    }

    @Override
    public int getPageTitle() {
        return 0;
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                    Log.e("gif--","fragment back key is clicked");
//                    getActivity().getSupportFragmentManager().popBackStack("LaunchFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    return true;
//                }
//                return false;
//            }
//        });
//    }
}
