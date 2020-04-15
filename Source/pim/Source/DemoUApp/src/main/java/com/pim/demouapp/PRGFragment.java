package com.pim.demouapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.philips.cdp.prodreg.constants.ProdRegError;
import com.philips.cdp.prodreg.launcher.PRDependencies;
import com.philips.cdp.prodreg.launcher.PRInterface;
import com.philips.cdp.prodreg.launcher.PRLaunchInput;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.listener.ProdRegUiListener;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.register.Product;
import com.philips.cdp.prodreg.register.RegisteredProduct;
import com.philips.cdp.prodreg.register.UserWithProducts;
import com.philips.cdp.prxclient.PrxConstants;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pim.PIMInterface;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.uappinput.UappSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class PRGFragment extends Fragment implements View.OnClickListener {
    private Button fragment_btn_2;
    private final String userRegServiceID = "userreg.janrain.api.v2";
    private PRLaunchInput prLaunchInput;
    private PRInterface prInterface;
    private UserDataInterface userDataInterface;
    private EditText ctnEditText;
    private AppInfraInterface mAppInfraInterface;

    @SuppressLint("ValidFragment")
    public PRGFragment(UserDataInterface userDataInterface, AppInfraInterface appInfraInterface) {
        this.userDataInterface = userDataInterface;
        this.mAppInfraInterface = appInfraInterface;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prg_launch, container, false);
        fragment_btn_2 = (Button) view.findViewById(R.id.fragment_button_2);
        fragment_btn_2.setOnClickListener(this);
        ctnEditText = (EditText)view.findViewById(R.id.reg_edit_ctn);
        return view;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fragment_button_2) {
            registerProduct(false, "pp_flow");

        }
    }

    private void registerProduct(final boolean isActivity, final String type) {
        Product product = new Product(ctnEditText.getText().toString(), PrxConstants.Sector.B2C, PrxConstants.Catalog.CONSUMER);
        product.setSerialNumber("");
        product.setPurchaseDate("");
        product.setFriendlyName("");
        product.sendEmail(true);
        initServiceDiscoveryLocale();
        invokeProdRegFragment(product, isActivity, type);
    }

    private void initServiceDiscoveryLocale() {
        final ServiceDiscoveryInterface serviceDiscoveryInterface = mAppInfraInterface.getServiceDiscovery();

        //serviceDiscoveryInterface.getServiceLocaleWithCountryPreference();
        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(userRegServiceID);
        serviceDiscoveryInterface.getServicesWithLanguagePreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                String locale = urlMap.get(userRegServiceID).getLocale();
                if (null == locale) {
                    // Toast.makeText(this., "Not able to set country code since locale is null", Toast.LENGTH_SHORT).show();
                } else {
                    PRUiHelper.getInstance().setLocale(locale);
                    String localeArr[] = locale.split("_");
                    PRUiHelper.getInstance().setCountryCode(localeArr[1].trim().toUpperCase());
                }
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                // Toast.makeText(fragmentActivity, message, Toast.LENGTH_SHORT).show();
            }
        }, null);

    }


    private void invokeProdRegFragment(Product product, final boolean isActivity, final String type) {
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
//        FragmentLauncher fragLauncher = new FragmentLauncher(
//                fragmentActivity, R.id.mainContainer, new ActionBarListener() {
//            @Override
//            public void updateActionBar(@StringRes final int i, final boolean b) {
//                MainActivity mainActivity = (MainActivity) fragmentActivity;
//                mainActivity.setTitle(i);
//            }
//
//            @Override
//            public void updateActionBar(final String s, final boolean b) {
//
//            }
//        });
// fragLauncher.setCustomAnimation(0, 0);
        prInterface = new PRInterface();
        prInterface.init(new PRDependencies(mAppInfraInterface, userDataInterface), new UappSettings(getContext()));
        FragmentLauncher fragmentLauncher = new FragmentLauncher(this.getActivity(), R.id.pimDemoU_mainFragmentContainer, null);
        if (type.equalsIgnoreCase("app_flow")) {
            prLaunchInput = new PRLaunchInput(products, true);
        } else {
            prLaunchInput = new PRLaunchInput(products, false);
        }
        prLaunchInput.setProdRegUiListener(getProdRegUiListener());

        prLaunchInput.setBackgroundImageResourceId(R.drawable.uid_slider_background);

        prLaunchInput.setMandatoryProductRegistration(false);
//        prLaunchInput.setMandatoryRegisterButtonText(mandatoryEditText.getText().toString());

        prInterface.launch(fragmentLauncher, prLaunchInput);
    }


    @NonNull
    private ProdRegUiListener getProdRegUiListener() {
        return new ProdRegUiListener() {
            @Override
            public void onProdRegContinue(final List<RegisteredProduct> registeredProduct, final UserWithProducts userWithProduct) {
                ProdRegLogger.v("PRGFragment", registeredProduct.get(0).getRegistrationState() + "");
            }

            @Override
            public void onProdRegBack(final List<RegisteredProduct> registeredProduct, final UserWithProducts userWithProduct) {
                ProdRegLogger.v("PRGFragment", registeredProduct.get(0).getProdRegError() + "");
            }

            @Override
            public void onProdRegFailed(final ProdRegError prodRegError) {
                ProdRegLogger.v("", "PRGFragment, prodRegError.getDescription()");
                if (prodRegError == ProdRegError.USER_NOT_SIGNED_IN) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), prodRegError.getDescription(), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
}