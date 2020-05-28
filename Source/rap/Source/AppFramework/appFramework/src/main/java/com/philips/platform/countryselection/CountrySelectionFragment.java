package com.philips.platform.countryselection;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.philips.cdp.registration.R2;
import com.philips.platform.appframework.R;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.baseapp.base.AbstractAppFrameworkBaseFragment;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountrySelectionFragment extends AbstractAppFrameworkBaseFragment implements CountrySelectionContract, View.OnClickListener {

    public static String TAG = "CountrySelectionFragment";

    @BindView(R2.id.country_recycler_view)
    RecyclerView countryListView;

    private Button continueButton;

    private CountrySelectionAdapter countryListAdapter;

    private CountrySelectionPresenter countrySelectionPresenter;

    private Context context;

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.country_selection_layout, null);
        ButterKnife.bind(this, view);
        initUI(view);

        countrySelectionPresenter = new CountrySelectionPresenter(context,this);
        countrySelectionPresenter.fetchSupportedCountryList(context);
        return view;
    }

    private void initUI(View view) {
        continueButton = (Button) view.findViewById(R.id.countrySelectionButton);
        continueButton.setOnClickListener(this);
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
        //getRegistrationFragment().onBackPressed();
    }

    @Override
    public void notifyCountryChange(Country country) {
        if (country.getCode().equalsIgnoreCase("TW")) {
            countrySelectionPresenter.changeCountryNameToTaiwan(context, country);
        }
        AppFrameworkApplication application = (AppFrameworkApplication) getActivity().getApplication();
        AppInfraInterface appInfra = application.getAppInfra();
        appInfra.getServiceDiscovery().setHomeCountry(country.getCode());

    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.USR_DLS_Country_Selection_Nav_Title_Text);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        if(v == continueButton){
            countrySelectionPresenter.navigate();
        }
    }
}
