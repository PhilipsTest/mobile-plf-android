/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.prxsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.prxclient.PRXDependencies;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.datamodels.assets.Asset;
import com.philips.cdp.prxclient.datamodels.assets.AssetModel;
import com.philips.cdp.prxclient.datamodels.assets.Assets;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.cdp.prxclient.datamodels.support.RichText;
import com.philips.cdp.prxclient.datamodels.support.RichTexts;
import com.philips.cdp.prxclient.datamodels.support.SupportModel;
import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.request.ProductAssetRequest;
import com.philips.cdp.prxclient.request.ProductSummaryRequest;
import com.philips.cdp.prxclient.request.ProductSupportRequest;
import com.philips.cdp.prxclient.request.PrxRequest;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;

import java.util.List;

public class PrxLauncherActivity extends AppCompatActivity {

    private static final String TAG = PrxLauncherActivity.class.getSimpleName();

    private String selectedCtn, selectedCountry;

    private String mRequestTag = null;
    Spinner mSector_spinner_prx, mSector_catalog_prx, spinner_ctn, spinner_country;
    private String mSector[], mCatalog[], mCtn[], mCountry[];
    Sector selectedSector;
    Catalog selectedCatalog;

    PRXDependencies prxDependencies;
    AppInfraInterface mAppInfra;
    private ListView listview;
    private ImageView imageView;
    private TextView summaryTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Button mSummaryButton = (Button) findViewById(R.id.summary_reqst_button);
        Button msupportButton = (Button) findViewById(R.id.support_rqst_button);
        Button mAssetButton = (Button) findViewById(R.id.assets_reqst_button);
        imageView = (ImageView) findViewById(R.id.imageView);
        summaryTextView = (TextView) findViewById(R.id.summaryText);
        listview = (ListView) findViewById(R.id.details);

        mAppInfra = new AppInfra.Builder().build(this);
        prxDependencies = new PRXDependencies(mAppInfra);
        // setting sector spinner
        mSector_spinner_prx = (Spinner) findViewById(R.id.prxSpinnerSector);
        mSector = getResources().getStringArray(R.array.sector_list);
        spinner_ctn = (Spinner) findViewById(R.id.prxSpinnerCTN);
        mCtn = getResources().getStringArray(R.array.ctn_list);

        spinner_country = (Spinner) findViewById(R.id.prxSpinnerCountry);
        mCountry = getResources().getStringArray(R.array.country_code);

        ArrayAdapter<String> mSector_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mSector);
        mSector_spinner_prx.setAdapter(mSector_adapter);
        mSector_spinner_prx.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSector = Sector.valueOf(parent.getAdapter().getItem(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> ctn_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                mCtn);
        spinner_ctn.setAdapter(ctn_adapter);

        spinner_ctn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCtn = parent.getAdapter().getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> country_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                mCountry);
        spinner_country.setAdapter(country_adapter);

        spinner_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = parent.getAdapter().getItem(position).toString();
                mAppInfra.getServiceDiscovery().setHomeCountry(selectedCountry.toUpperCase());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // setting catalog spinner
        mSector_catalog_prx = (Spinner) findViewById(R.id.prxSpinnerCatalog);
        mCatalog = getResources().getStringArray(R.array.catalog_list);
        ArrayAdapter<String> mCatalogy_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mCatalog);
        mSector_catalog_prx.setAdapter(mCatalogy_adapter);
        mSector_catalog_prx.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCatalog = Catalog.valueOf(parent.getAdapter().getItem(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSummaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productSummaryRequest();
            }
        });
        msupportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productSupportRequest();
            }
        });
        mAssetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productAssetRequest();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void productAssetRequest() {
        ProductAssetRequest mProductAssetBuilder = new ProductAssetRequest(selectedCtn, mRequestTag);
        mProductAssetBuilder.setSector(selectedSector);
        mProductAssetBuilder.setCatalog(selectedCatalog);
        onRequestManagerCalled(mProductAssetBuilder);
    }

    private void productSupportRequest() {
        ProductSupportRequest mProductSupportBuilder = new ProductSupportRequest(selectedCtn, mRequestTag);
        mProductSupportBuilder.setSector(selectedSector);
        mProductSupportBuilder.setCatalog(selectedCatalog);
        onRequestManagerCalled(mProductSupportBuilder);
    }

    private void productSummaryRequest() {
        ProductSummaryRequest mProductSummeryBuilder = new ProductSummaryRequest(selectedCtn, mRequestTag);
        mProductSummeryBuilder.setSector(selectedSector);
        mProductSummeryBuilder.setCatalog(selectedCatalog);
        onRequestManagerCalled(mProductSummeryBuilder);
    }

    private void onRequestManagerCalled(PrxRequest prxRequest) {
        RequestManager mRequestManager = new RequestManager();
        mRequestManager.init(getApplicationContext(), prxDependencies);
        Log.d(TAG, "Positive Request");
        mRequestManager.executeRequest(prxRequest, new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                String str = responseData.getClass().toString();
                if (responseData instanceof SummaryModel) {
                    SummaryModel mSummaryModel = (SummaryModel) responseData;
                    //aiLogging.log(AppInfraLogging.LogLevel.DEBUG,TAG,"Support Response Data AI : " + mSummaryModel.isSuccess());
                    Log.d(TAG, "Support Response Data : " + mSummaryModel.isSuccess());
                    com.philips.cdp.prxclient.datamodels.summary.Data mData = mSummaryModel.getData();
                    String url = mData.getImageURL();
                    imageView.setVisibility(View.VISIBLE);
                    summaryTextView.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.GONE);
                    Glide.with(PrxLauncherActivity.this).load(url)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView);
                    summaryTextView.setText("Des:" + " " + mData.getWow() + "\t\n" + "Price:" + mData.getPrice().getFormattedDisplayPrice()
                            + "\t\n" + " ProductTitle: " + " " + mData.getProductTitle() + " " + "\t\n" + "SubTitle: " + " " + mData.getSubWOW());
                    Log.d(TAG, " SummaryModel Positive Response Data : " + mSummaryModel.isSuccess());
                    Log.d(TAG, " SummaryModel Positive Response Data Brand: " + mData.getBrand());
                    Log.d(TAG, " SummaryModel Positive Response Data CTN: " + mData.getCtn());
                    Log.d(TAG, " SummaryModel Positive Response Data Product Title: " + mData.getProductTitle());

                } else if (responseData instanceof AssetModel) {
                    AssetModel mAssetModel = (AssetModel) responseData;
                    Log.d(TAG, "Support Response Data : " + mAssetModel.isSuccess());
                    com.philips.cdp.prxclient.datamodels.assets.Data myyData = mAssetModel.getData();
                    Assets assets = myyData.getAssets();
                    List<Asset> asset = assets.getAsset();
                    imageView.setVisibility(View.GONE);
                    summaryTextView.setVisibility(View.GONE);
                    listview.setVisibility(View.VISIBLE);
                    AssetModelAdapter adapter = new AssetModelAdapter(PrxLauncherActivity.this, asset);
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    listview.setOnItemClickListener(null);
                    Log.d(TAG, " AssetModel Positive Response Data : " + mAssetModel.isSuccess());
                    Log.d(TAG, " AssetModel Positive Response Data assets : " + myyData.getAssets());

                } else {
                    SupportModel mSupportModel = (SupportModel) responseData;
                    Log.d(TAG, "Support Response Data : " + mSupportModel.isSuccess());
                    com.philips.cdp.prxclient.datamodels.support.Data msupportData = mSupportModel.getData();
                    RichTexts text = msupportData.getRichTexts();
                    List<RichText> listText = text.getRichText();
                    imageView.setVisibility(View.GONE);
                    summaryTextView.setVisibility(View.GONE);
                    listview.setVisibility(View.VISIBLE);
                    SupportModelAdapter adapter = new SupportModelAdapter(PrxLauncherActivity.this, listText);
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    listview.setOnItemClickListener(null);
                    Log.d(TAG, " SupportModel Positive Response Data : " + mSupportModel.isSuccess());
                    Log.d(TAG, " SupportModel Positive Response Data RichText: " + msupportData.getRichTexts());
                }
            }

            @Override
            public void onResponseError(PrxError prxError) {
                Log.d(TAG, "Response Error Message PRX: " + prxError.getDescription());
                summaryTextView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                listview.setVisibility(View.GONE);
                summaryTextView.setText(prxError.getDescription() + " " + "Code" + " " + prxError.getStatusCode());

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
