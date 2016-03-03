package com.philips.cdp.productselection.fragments.listfragment;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.philips.cdp.productselection.ProductModelSelectionHelper;
import com.philips.cdp.productselection.R;
import com.philips.cdp.productselection.fragments.detailedscreen.DetailedScreenFragmentSelection;
import com.philips.cdp.productselection.fragments.homefragment.ProductSelectionBaseFragment;
import com.philips.cdp.productselection.prx.PrxSummaryDataListener;
import com.philips.cdp.productselection.prx.PrxWrapper;
import com.philips.cdp.productselection.utils.Constants;
import com.philips.cdp.productselection.utils.ProductSelectionLogger;
import com.philips.cdp.prxclient.prxdatamodels.summary.SummaryModel;
import com.philips.cdp.tagging.Tagging;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ProductSelectionListingFragment class is used to showcase all possible CTNs and its details.
 *
 * @author : ritesh.jha@philips.com
 * @since : 29 Jan 2016
 */
public class ProductSelectionListingFragment extends ProductSelectionBaseFragment {

    private static final int UPDATE_UI = 0;
    private String TAG = ProductSelectionListingFragment.class.getSimpleName();
    private ListView mProductListView = null;
    private ListViewWithOptions mProductAdapter = null;
    private ProgressDialog mSummaryDialog = null;
    private ArrayList<SummaryModel> productList = null;
    private Handler mHandler = null;

    public ProductSelectionListingFragment(Handler handler) {
        mHandler = handler;
    }

    public ProductSelectionListingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_listview, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProductListView = (ListView) getActivity().findViewById(R.id.productListView);

        injectSummaryDataList();
        // getSummaryDataFromPRX();

        mProductListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (isConnectionAvailable()) {
                    mUserSelectedProduct = (productList.get(position));
                    if (!isLaunchedAsTabletLandscape()) {
                        DetailedScreenFragmentSelection detailedScreenFragmentSelection = new DetailedScreenFragmentSelection();
                        detailedScreenFragmentSelection.setUserSelectedProduct(mUserSelectedProduct);
                        showFragment(detailedScreenFragmentSelection);
                    } else {
                        setListViewRequiredInTablet(false);
                        mHandler.sendEmptyMessageDelayed(UPDATE_UI, 1000);
                    }

                    Map<String, Object> contextData = new HashMap<String, Object>();
                    contextData.put(Constants.ACTION_NAME_SPECIAL_EVENT,
                            Constants.ACTION_VALUE_PRODUCT_VIEW);
                    contextData.put(Constants.ACTION_NAME_PRODUCTS, mUserSelectedProduct.getData().getProductTitle()
                            + ":" + mUserSelectedProduct.getData().getCtn());

                    Tagging.trackMultipleActions(Constants.ACTION_KEY_SEND_DATA, contextData);
                }
            }
        });

        Tagging.trackPage(Constants.PAGE_LIST_SCREEN, getPreviousName());
        setPreviousPageName(Constants.PAGE_LIST_SCREEN);
    }


    private void injectSummaryDataList() {
        SummaryModel[] summaryList = ProductModelSelectionHelper.getInstance().getProductModelSelectionType().getSummaryModelList();
        ProductSelectionLogger.d(TAG, "Summary List : " + summaryList.length);
        productList = new ArrayList<SummaryModel>();

        for (int i = 0; i < summaryList.length; i++) {
            productList.add(summaryList[i]);
        }

        if (isLaunchedAsTabletLandscape()) {
            try {
                mUserSelectedProduct = (summaryList[0]);
                setListViewRequiredInTablet(true);
                mHandler.sendEmptyMessageDelayed(UPDATE_UI, 1000);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        if (productList.size() != 0) {
            mProductAdapter = new ListViewWithOptions(getActivity(), productList);
            mProductListView.setAdapter(mProductAdapter);
            mProductAdapter.notifyDataSetChanged();
        } else
            ProductModelSelectionHelper.getInstance().getProductSelectionListener().onProductModelSelected(mUserSelectedProduct);

    }

    private void getSummaryDataFromPRX() {

        if (mSummaryDialog == null)
            mSummaryDialog = new ProgressDialog(getActivity(), R.style.loaderTheme);
        mSummaryDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
        mSummaryDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loader));
        mSummaryDialog.setCancelable(true);
        if (!(getActivity().isFinishing()))
            mSummaryDialog.show();

        final String[] ctnList = ProductModelSelectionHelper.getInstance().getProductModelSelectionType().getHardCodedProductList();

        productList = new ArrayList<SummaryModel>();

        for (int i = 0; i < ctnList.length; i++) {
            final String ctn = ctnList[i];
            PrxWrapper prxWrapperCode = new PrxWrapper(getActivity().getApplicationContext(), ctn,
                    ProductModelSelectionHelper.getInstance().getProductModelSelectionType().getSector(),
                    ProductModelSelectionHelper.getInstance().getLocale().toString(),
                    ProductModelSelectionHelper.getInstance().getProductModelSelectionType().getCatalog());

            prxWrapperCode.requestPrxSummaryData(new PrxSummaryDataListener() {
                @Override
                public void onSuccess(SummaryModel summaryModel) {
                    productList.add(summaryModel);

                    if (!isLaunchedAsTabletLandscape() && productList.size() == 1) {
                        try {
                            mUserSelectedProduct = (productList.get(0));
                            ProductModelSelectionHelper.getInstance().getProductSelectionListener().onProductModelSelected(mUserSelectedProduct);
                            setListViewRequiredInTablet(true);
                            mHandler.sendEmptyMessageDelayed(UPDATE_UI, 1000);
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }

                    String[] ctnList = ProductModelSelectionHelper.getInstance().getProductModelSelectionType().getHardCodedProductList();
                    if (ctn == ctnList[ctnList.length - 1]) {

                        if (productList.size() != 0) {
                            mProductAdapter = new ListViewWithOptions(getActivity(), productList);
                            mProductListView.setAdapter(mProductAdapter);
                            mProductAdapter.notifyDataSetChanged();

                            if (getActivity() != null)
                                if (!(getActivity().isFinishing()) && mSummaryDialog.isShowing()) {
                                    mSummaryDialog.dismiss();
                                    mSummaryDialog.cancel();
                                }
                        } else {
                            ProductModelSelectionHelper.getInstance().getProductSelectionListener().onProductModelSelected(mUserSelectedProduct);
                            clearBackStackHistory(getActivity());
                        }
                    }
                }

                @Override
                public void onFail(String errorMessage) {
                    ProductSelectionLogger.e(TAG, " Error : " + errorMessage);
                    String[] ctnList = ProductModelSelectionHelper.getInstance().getProductModelSelectionType().getHardCodedProductList();
                    if (ctn == ctnList[ctnList.length - 1]) {

                        if (productList.size() != 0) {
                            mProductAdapter = new ListViewWithOptions(getActivity(), productList);
                            mProductListView.setAdapter(mProductAdapter);
                            mProductAdapter.notifyDataSetChanged();
                            if (getActivity() != null)
                                if (!(getActivity().isFinishing()) && mSummaryDialog.isShowing()) {
                                    mSummaryDialog.dismiss();
                                    mSummaryDialog.cancel();
                                }
                        } else {
                            ProductModelSelectionHelper.getInstance().getProductSelectionListener().onProductModelSelected(mUserSelectedProduct);
                            clearBackStackHistory(getActivity());
                        }
                    }
                }
            }, TAG);
        }

    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.Product_Title);
    }


    @Override
    public void setViewParams(Configuration config) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
