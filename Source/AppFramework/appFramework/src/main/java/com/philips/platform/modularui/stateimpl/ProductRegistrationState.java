/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.modularui.stateimpl;

import android.content.Context;
import android.widget.Toast;

import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.prodreg.constants.ProdRegError;
import com.philips.cdp.prodreg.launcher.PRDependencies;
import com.philips.cdp.prodreg.launcher.PRInterface;
import com.philips.cdp.prodreg.launcher.PRLaunchInput;
import com.philips.cdp.prodreg.listener.ProdRegUiListener;
import com.philips.cdp.prodreg.register.Product;
import com.philips.cdp.prodreg.register.RegisteredProduct;
import com.philips.cdp.prodreg.register.UserWithProducts;
import com.philips.platform.appframework.AppFrameworkApplication;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.FlowManager;
import com.philips.platform.modularui.statecontroller.BaseState;
import com.philips.platform.modularui.statecontroller.UIStateData;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all initialization & Launching details of Product Registration
 */
public class ProductRegistrationState extends BaseState implements ProdRegUiListener {

    private Context activityContext;
    private FragmentLauncher fragmentLauncher;
    private Context applicationContext;

    public ProductRegistrationState(){
        super(AppStates.PR);
    }

    /**
     * BaseState overridden methods
     * @param uiLauncher requires the UiLauncher object
     */
    @Override
    public void navigate(UiLauncher uiLauncher) {
        fragmentLauncher = (FragmentLauncher) uiLauncher;
        activityContext = fragmentLauncher.getFragmentActivity();
        launchPR();
    }

    @Override
    public void init(Context context) {
        applicationContext = context;
        PRDependencies prodRegDependencies = new PRDependencies(AppFrameworkApplication.appInfra);

        UappSettings uappSettings = new UappSettings(applicationContext);
        new PRInterface().init(prodRegDependencies, uappSettings);
    }

    /**
     * ProdRegUiListener interface implementation methods
     * @param list
     * @param userWithProducts
     */
    @Override
    public void onProdRegContinue(List<RegisteredProduct> list, UserWithProducts userWithProducts) {

    }

    @Override
    public void onProdRegBack(List<RegisteredProduct> list, UserWithProducts userWithProducts) {

    }

    @Override
    public void onProdRegFailed(ProdRegError prodRegError) {
        Toast.makeText(activityContext,""+prodRegError.getDescription().toString(),Toast.LENGTH_SHORT).show();

    }

    /**
     * Launch PR method
     */
    public void launchPR(){
        ArrayList<Product> products = new ArrayList<>();
        products.add(((ProductRegistrationData)getUiStateData()).getProductData());
        PRLaunchInput prodRegLaunchInput;
        prodRegLaunchInput = new PRLaunchInput(products, false);
        prodRegLaunchInput.setProdRegUiListener(this);
        new PRInterface().launch(fragmentLauncher,prodRegLaunchInput);
    }

    /**
     * Data Model for CoCo is defined here to have minimal import files.
     */
    public class ProductRegistrationData extends UIStateData {
        private ArrayList<String> ctnList = null;

        public ArrayList<String> getCtnList() {
            return ctnList;
        }

        public void setCtnList(ArrayList<String> ctnList) {
            this.ctnList = ctnList;
        }

        public Product getProductData(){
            Product product = new Product(getCtnList().get(0), Sector.B2C, Catalog.CONSUMER);
            product.setSerialNumber("");
            product.setPurchaseDate("");
            product.setFriendlyName("");
            product.sendEmail(false);
            return product;
        }

    }
}
