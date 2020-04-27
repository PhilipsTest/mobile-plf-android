
package com.ecs.demotestuapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ecs.demotestuapp.R;
import com.ecs.demotestuapp.model.PropertyItem;
import com.ecs.demotestuapp.util.ECSDataHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.integration.ECSOAuthProvider;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.ecs.model.address.ECSUserProfile;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.model.config.ECSConfig;
import com.philips.platform.ecs.model.oauth.ECSOAuthData;
import com.philips.platform.ecs.model.products.ECSProduct;
import com.philips.platform.ecs.model.products.ECSProducts;
import com.philips.platform.ecs.model.voucher.ECSVoucher;

import java.util.List;


public class EcsDemoResultActivity extends AppCompatActivity {

    TextView tvResult;
    PropertyItem propertyItem;

    ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        tvResult = findViewById(R.id.tv_result);
        progressBar = findViewById(R.id.progressBar);

        Bundle bundle = getIntent().getExtras();
        propertyItem = (PropertyItem) bundle.getSerializable("property");

    }

    @Override
    protected void onStart() {
        super.onStart();
        executeAPI(propertyItem.apiNumber);
    }

    private void executeAPI(int apiNumber) {

        switch (apiNumber){

            case 0:

                ECSDataHolder.INSTANCE.getEcsServices().configureECS(new ECSCallback<Boolean, Exception>() {
                    @Override
                    public void onResponse(Boolean result) {

                        progressBar.setVisibility(View.GONE);
                        tvResult.setVisibility(View.VISIBLE);
                        tvResult.setText("Success\n\n"+result);
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        showError(error,ecsError);
                    }
                });

                break;

            case 1:

                ECSDataHolder.INSTANCE.getEcsServices().configureECSToGetConfiguration(new ECSCallback<ECSConfig, Exception>() {
                    @Override
                    public void onResponse(ECSConfig result) {

                        ECSDataHolder.INSTANCE.setEcsConfig(result);
                        showSuccess(getJsonStringFromObject(result));

                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {

                        showError(error, ecsError);


                    }
                });

                break;

            case 2:

                ECSDataHolder.INSTANCE.getEcsServices().hybrisOAthAuthentication(new ECSOAuthProvider() {
                    @Override
                    public String getOAuthID() {
                        return ECSDataHolder.INSTANCE.getJanrainID();
                    }
                }, new ECSCallback<ECSOAuthData, Exception>() {
                    @Override
                    public void onResponse(ECSOAuthData result) {
                        ECSDataHolder.INSTANCE.setEcsoAuthData(result);
                        showSuccess(getJsonStringFromObject(result));
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        showError(error, ecsError);
                    }
                });

                break;

            case 3:

                ECSDataHolder.INSTANCE.getEcsServices().hybrisRefreshOAuth(new ECSOAuthProvider() {
                    @Override
                    public String getOAuthID() {
                        if(ECSDataHolder.INSTANCE.getEcsoAuthData()!=null) {
                            return ECSDataHolder.INSTANCE.getEcsoAuthData().getRefreshToken();
                        }
                        return null;
                    }
                }, new ECSCallback<ECSOAuthData, Exception>() {
                    @Override
                    public void onResponse(ECSOAuthData result) {
                        ECSDataHolder.INSTANCE.setEcsoAuthData(result);
                        showSuccess(getJsonStringFromObject(result));
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        showError(error, ecsError);
                    }
                });
                break;

            case 4:

                ECSDataHolder.INSTANCE.getEcsServices().fetchProducts(propertyItem.fetchProductInput.pageNumber, propertyItem.fetchProductInput.pageSize, new ECSCallback<ECSProducts, Exception>() {
                    @Override
                    public void onResponse(ECSProducts result) {
                        ECSDataHolder.INSTANCE.setEcsProducts(result);
                        showSuccess(getJsonStringFromObject(result));
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        showError(error, ecsError);
                    }
                });
                break;

            case 5:

                ECSDataHolder.INSTANCE.getEcsServices().fetchProduct(propertyItem.getCtns().get(0), new ECSCallback<ECSProduct, Exception>() {
                    @Override
                    public void onResponse(ECSProduct result) {
                        showSuccess(getJsonStringFromObject(result));
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        showError(error, ecsError);
                    }
                });

                break;
            case 6:

                ECSDataHolder.INSTANCE.getEcsServices().fetchProductSummaries(propertyItem.getCtns(), new ECSCallback<List<ECSProduct>, Exception>() {
                    @Override
                    public void onResponse(List<ECSProduct> result) {
                        showSuccess(getJsonStringFromObject(result));
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        showError(error, ecsError);
                    }
                });

                break;
            case 7:

                break;
            case 8:

                ECSDataHolder.INSTANCE.getEcsServices().fetchShoppingCart(new ECSCallback<ECSShoppingCart, Exception>() {
                    @Override
                    public void onResponse(ECSShoppingCart result) {

                        ECSDataHolder.INSTANCE.setEcsShoppingCart(result);
                        showSuccess(getJsonStringFromObject(result));
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        showError(error, ecsError);
                    }
                });

                break;

            case 9:

                break;
            case 10:

                break;

            case 11:

                break;
            case 12:
                ECSDataHolder.INSTANCE.getEcsServices().fetchAppliedVouchers(new ECSCallback<List<ECSVoucher>, Exception>() {
                    @Override
                    public void onResponse(List<ECSVoucher> result) {

                        ECSDataHolder.INSTANCE.setVouchers(result);
                        showSuccess(getJsonStringFromObject(result));
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        showError(error, ecsError);
                    }
                });


                break;
            case 13:

                break;
            case 14:

                ECSDataHolder.INSTANCE.getEcsServices().fetchDeliveryModes(new ECSCallback<List<ECSDeliveryMode>, Exception>() {
                    @Override
                    public void onResponse(List<ECSDeliveryMode> result) {
                        ECSDataHolder.INSTANCE.setEcsDeliveryModes(result);
                        showSuccess(getJsonStringFromObject(result));
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        showError(error, ecsError);
                    }
                });

                break;
            case 15:

                ECSDataHolder.INSTANCE.getEcsServices().setDeliveryMode(ECSDataHolder.INSTANCE.getEcsDeliveryMode(), new ECSCallback<Boolean, Exception>() {
                    @Override
                    public void onResponse(Boolean result) {
                        progressBar.setVisibility(View.GONE);
                        tvResult.setVisibility(View.VISIBLE);
                        tvResult.setText("Success\n\n"+result);
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        showError(error, ecsError);
                    }
                });
                break;
            case 16:

                ECSDataHolder.INSTANCE.getEcsServices().fetchSavedAddresses(new ECSCallback<List<ECSAddress>, Exception>() {
                    @Override
                    public void onResponse(List<ECSAddress> result) {

                        ECSDataHolder.INSTANCE.setEcsAddressList(result);
                        showSuccess(getJsonStringFromObject(result));
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {
                        showError(error, ecsError);
                    }
                });

                break;
            case 17:

                break;
            case 18:

                break;
            case 19:

                break;
            case 20:

                break;
            case 21:

                break;
            case 22:

                break;
            case 23:

                break;
            case 24:

                break;
            case 25:

                break;
            case 26:

                break;
            case 27:

                break;
            case 28:

                break;
            case 29:

                break;
            case 30:

                break;
            case 31:

                ECSDataHolder.INSTANCE.getEcsServices().fetchUserProfile(new ECSCallback<ECSUserProfile, Exception>() {
                    @Override
                    public void onResponse(ECSUserProfile result) {

                        ECSDataHolder.INSTANCE.setEcsUserProfile(result);

                        showSuccess( getJsonStringFromObject(result));
                    }

                    @Override
                    public void onFailure(Exception error, ECSError ecsError) {

                        showError(error,ecsError);

                    }
                });

                break;

            case 32:

                break;

            case 33:

                break;
            default:
                showSuccess("Error\n"+" API not found");
        }
    }

    private void showError(Exception exception, ECSError ecsError) {
        progressBar.setVisibility(View.GONE);
        tvResult.setVisibility(View.VISIBLE);
        tvResult.setText("Error \n"+ exception.getLocalizedMessage() + "\nError type \n" + ecsError.getErrorType() + "\nError code \n" + ecsError.getErrorcode());
    }

    private void showSuccess(String jsonStringFromObject) {
        progressBar.setVisibility(View.GONE);
        tvResult.setVisibility(View.VISIBLE);
        tvResult.setText( jsonStringFromObject);
    }

    String getJsonStringFromObject(Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return "Success\n" + gson.toJson(object);
    }

}