/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.productregistration;

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
import com.philips.platform.appframework.R;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.baseapp.base.UIStateData;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains all initialization & Launching details of Product Registration
 */
public class ProductRegistrationState extends BaseState implements ProdRegUiListener {

    private Context activityContext;
    private FragmentLauncher fragmentLauncher;
    private Context applicationContext;
    private ArrayList<String> ctnList = null;

    public ArrayList<String> getCtnList() {
        return ctnList;
    }

    public void setCtnList(ArrayList<String> ctnList) {
        this.ctnList = ctnList;
    }

    public  ArrayList<Product> getProductList(){
        Product product = new Product(getCtnList().get(0), Sector.B2C, Catalog.CONSUMER);
        product.setSerialNumber("");
        product.setPurchaseDate("");
        product.setFriendlyName("");
        product.sendEmail(false);
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        return products;
    }

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
        updateDataModel();
        launchPR();
    }

    public AppFrameworkApplication getApplicationContext(){
      return  (AppFrameworkApplication)activityContext.getApplicationContext();
    }

    @Override
    public void init(Context context) {
        applicationContext = context;
        PRDependencies prodRegDependencies = new PRDependencies(((AppFrameworkApplication)applicationContext).getAppInfra());

        UappSettings uappSettings = new UappSettings(applicationContext);
        new PRInterface().init(prodRegDependencies, uappSettings);
    }

    @Override
    public void updateDataModel() {
        setCtnList(new ArrayList<>(Arrays.asList(getApplicationContext().getResources().getStringArray(R.array.productselection_ctnlist))));
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

        PRLaunchInput prodRegLaunchInput;
        prodRegLaunchInput = new PRLaunchInput(getProductList(), false);
        prodRegLaunchInput.setProdRegUiListener(this);
        new PRInterface().launch(fragmentLauncher,prodRegLaunchInput);
    }

    /**
     * Data Model for CoCo is defined here to have minimal import files.
     */
    public class ProductRegistrationData extends UIStateData {


    }
}
