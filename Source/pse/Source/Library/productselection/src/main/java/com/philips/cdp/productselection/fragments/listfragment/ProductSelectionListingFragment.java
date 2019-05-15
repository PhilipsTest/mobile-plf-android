package com.philips.cdp.productselection.fragments.listfragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.philips.cdp.productselection.ProductModelSelectionHelper;
import com.philips.cdp.productselection.R;
import com.philips.cdp.productselection.fragments.detailedscreen.DetailedScreenFragmentSelection;
import com.philips.cdp.productselection.fragments.homefragment.ProductSelectionBaseFragment;
import com.philips.cdp.productselection.utils.Constants;
import com.philips.cdp.productselection.utils.ProductSelectionLogger;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.platform.uid.view.widget.SearchBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ProductSelectionListingFragment class is used to showcase all possible CTNs and its details.
 *
 * @author : ritesh.jha@philips.com
 * @since : 29 Jan 2016
 */
public class ProductSelectionListingFragment extends ProductSelectionBaseFragment implements SearchBox.ExpandListener, SearchBox.QuerySubmitListener  {

    private String TAG = ProductSelectionListingFragment.class.getSimpleName();
    private ListView mProductListView = null;
    private ListViewWithOptions mProductAdapter = null;
    private ProgressDialog mSummaryDialog = null;
    private ArrayList<SummaryModel> productList = null;
    private SearchBox mSearchBox = null;

    private LinearLayout noresult = null;
    private TextView tvNoResult = null;
    private AppCompatAutoCompleteTextView mSearchTextView;
    private String mSearchText = "";


    public ProductSelectionListingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_listview, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        mSearchBox = view.findViewById(R.id.iap_search_box);

        setUpSearch();
        noresult = (LinearLayout) view.findViewById(R.id.ll_no_result_found);
        tvNoResult = view.findViewById(R.id.product_zero_results);

    }

    private void setUpSearch() {

        ImageView mClearIconView = mSearchBox.getClearIconView();
        mSearchBox.setExpandListener(this);
        mSearchBox.setQuerySubmitListener(this);
        mSearchBox.setSearchBoxHint(R.string.search);
        mSearchBox.setDecoySearchViewHint(R.string.search);
        mSearchTextView = mSearchBox.getSearchTextView();
        mSearchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do nothing
                mSearchText = String.valueOf(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                    showListView();
            }
        });

        mClearIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchTextView.getText().clear();
                showListView();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity()==null ) {return;}
        ProductSelectionLogger.i(TAG, "Displaying the list of products for user to select their product");
        mProductListView = (ListView) getView().findViewById(R.id.productListView);
        mSearchBox.clearFocus();
        injectSummaryDataList();
        mProductListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (isConnectionAvailable()) {

                    if(mSearchTextView.getText().length() == 0)
                    {
                        mUserSelectedProduct = (productList.get(position));
                    }else {
                        mUserSelectedProduct = mProductAdapter.getData();
                    }
                    DetailedScreenFragmentSelection detailedScreenFragmentSelection = new DetailedScreenFragmentSelection();
                    detailedScreenFragmentSelection.setUserSelectedProduct(mUserSelectedProduct);
                    showFragment(detailedScreenFragmentSelection);

                    Map<String, String> contextData = new HashMap<String, String>();
                    contextData.put(Constants.ACTION_NAME_SPECIAL_EVENT,
                            Constants.ACTION_VALUE_PRODUCT_VIEW);
                    contextData.put(Constants.ACTION_NAME_PRODUCTS, mUserSelectedProduct.getData().getProductTitle()
                            + ":" + mUserSelectedProduct.getData().getCtn());

                    ProductModelSelectionHelper.getInstance().getTaggingInterface().
                            trackActionWithInfo(Constants.ACTION_KEY_SEND_DATA, contextData);

                    mSearchTextView.setText("");
                    mSearchBox.setSearchCollapsed(true);
                    hideSoftInputboard();
                }
            }
        });

        if (getPreviousName() != null) {
            ProductModelSelectionHelper.getInstance().getTaggingInterface().trackPageWithInfo
                    (Constants.PAGE_LIST_SCREEN, getPreviousName(), getPreviousName());
            setPreviousPageName(Constants.PAGE_LIST_SCREEN);
        } else setPreviousPageName(Constants.PAGE_LIST_SCREEN);
    }

    private void hideSoftInputboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


    private void injectSummaryDataList() {
        SummaryModel[] summaryList = ProductModelSelectionHelper.getInstance().getProductModelSelectionType().getSummaryModelList();
        if(summaryList!=null){
            productList = new ArrayList<SummaryModel>();

            for (int i = 0; i < summaryList.length; i++) {
                productList.add(summaryList[i]);
            }
            if (productList.size() != 0) {
                mProductAdapter = new ListViewWithOptions(getActivity(), productList);
                mProductListView.setAdapter(mProductAdapter);
                mProductAdapter.notifyDataSetChanged();
            } else
                ProductModelSelectionHelper.getInstance().getProductSelectionListener().onProductModelSelected(mUserSelectedProduct);

        }
    }

    @Override
    public void onDestroyView() {
        if (mSummaryDialog != null) {

            if (!(getActivity().isFinishing()) && mSummaryDialog.isShowing()) {
                mSummaryDialog.dismiss();
                mSummaryDialog.cancel();
            }
        }
        super.onDestroyView();

    }

    private void showListView() {

       final String constrain = mSearchTextView.getText().toString().trim();

        System.out.println("constrain  "+constrain);

        if (productList != null && mProductAdapter.getFilter()!=null) {
            mProductAdapter.getFilter().filter(constrain,
                    new Filter.FilterListener() {
                        public void onFilterComplete(int count) {
                            mProductListView.setAdapter(mProductAdapter);
                            mProductListView.setVisibility(View.VISIBLE);

                            if(count ==0 ){
                                noresult.setVisibility(View.VISIBLE);
                                tvNoResult.setText(getContext().getResources().getString(R.string.pse_No_Result) + " "+mSearchText);
                                mProductListView.setVisibility(View.GONE);
                            }
                        }
                    });
        }

    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.pse_Find_Your_Product_Title);
    }

    @Override
    public void setViewParams(Configuration config) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSearchExpanded() {

    }

    @Override
    public void onSearchCollapsed() {

    }

    @Override
    public void onQuerySubmit(CharSequence charSequence) {

    }
}
