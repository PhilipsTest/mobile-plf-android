package com.philips.cdp.registration.ui.traditional;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.R2;
import com.philips.cdp.registration.dao.Country;
import com.philips.cdp.registration.ui.traditional.countryselection.CountrySelectionAdapter;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountrySelectionFragment extends RegistrationBaseFragment implements CountrySelectionContract {
    @BindView(R2.id.country_recycler_view)
    RecyclerView countryListView;

    private CountrySelectionAdapter countryListAdapter;

    private CountrySelectionPresenter countrySelectionPresenter;

    private Context context;

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }


    @Override
    protected void setViewParams(Configuration config, int width) {
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);

    }

    @Override
    public int getTitleResourceId() {
        return R.string.reg_DLS_Country_Selection_Nav_Title_Text;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.country_selection_layout, null);
        ButterKnife.bind(this, view);
        initUI(view);

        countrySelectionPresenter = new CountrySelectionPresenter(this);
        countrySelectionPresenter.fetchSupportedCountryList(context);
        return view;
    }

    private void initUI(View view) {
        handleOrientationOnView(view);
        initRecyclerView();
    }

    @Override
    public void initRecyclerView() {
        countryListView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        countryListView.setLayoutManager(mLayoutManager);
        RecyclerViewSeparatorItemDecoration separatorItemDecoration = new RecyclerViewSeparatorItemDecoration(getContext());
        countryListView.addItemDecoration(separatorItemDecoration);
        countryListView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void updateRecyclerView(ArrayList<Country> countries) {

        countryListAdapter = new CountrySelectionAdapter(countries, this);
        countryListView.setAdapter(countryListAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void popCountrySelectionFragment() {
        getRegistrationFragment().onBackPressed();
    }

    @Override
    public void notifyCountryChange(Country country) {
        Intent intent = new Intent();
        if (country.getCode().equalsIgnoreCase("TW")) {
            countrySelectionPresenter.changeCountryNameToTaiwan(context, country);
        }
        intent.putExtra(RegConstants.KEY_BUNDLE_COUNTRY_CODE, country.getCode());
        intent.putExtra(RegConstants.KEY_BUNDLE_COUNTRY_NAME, country.getName());
        getTargetFragment().onActivityResult(
                getTargetRequestCode(),
                Activity.RESULT_OK,
                intent);
    }
}
