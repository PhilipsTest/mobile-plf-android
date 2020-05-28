package com.philips.platform.countryselection;


import java.util.ArrayList;

public interface CountrySelectionContract {

     void initRecyclerView();

     void updateRecyclerView(ArrayList<Country> countries);

     void popCountrySelectionFragment();

     void notifyCountryChange(Country country);
}