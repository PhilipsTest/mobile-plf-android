package com.philips.platform.countryselection;

import com.philips.cdp.registration.dao.Country;

import java.util.ArrayList;

public interface CountrySelectionContract {

     void initRecyclerView();

     void updateRecyclerView(ArrayList<Country> countries);

     void popCountrySelectionFragment();

     void notifyCountryChange(Country country);
}