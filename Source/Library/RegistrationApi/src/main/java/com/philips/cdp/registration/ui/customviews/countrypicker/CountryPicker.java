package com.philips.cdp.registration.ui.customviews.countrypicker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.ui.utils.FontLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CountryPicker extends DialogFragment implements
        Comparator<Country> {
    /**
     * View components
     */
    private EditText searchEditText;
    private ListView countryListView;

    /**
     * Adapter for the listview
     */
    private CountryAdapter adapter;

    /**
     * Hold all countries, sorted by country name
     */
    private List<Country> allCountriesList;

    /**
     * Hold countries that matched user query
     */
    private List<Country> selectedCountriesList;

    /**
     * Listener to which country user selected
     */
    private CountryChangeListener listener;

    /**
     * Set listener
     *
     * @param listener
     */
    public void setListener(CountryChangeListener listener) {
        this.listener = listener;
    }

    public EditText getSearchEditText() {
        return searchEditText;
    }

    public ListView getCountryListView() {
        return countryListView;
    }


    /**
     * Get all countries with code and name from res/raw/countries.json
     *
     * @return
     */
    private List<Country> getAllCountries() {
        if (allCountriesList == null) {
            try {
                allCountriesList = new ArrayList<Country>();
                String[] recourseList = this.getResources().getStringArray(R.array.country_code);
                for (int i = 0; i < recourseList.length; i++) {
                    Country country = new Country();
                    country.setCode(recourseList[i]);
                    Locale nameLocale = new Locale("", recourseList[i]);
                    country.setName(nameLocale.getDisplayCountry());
                    allCountriesList.add(country);
                }
                // Sort the all countries list based on country name
                Collections.sort(allCountriesList, this);
                // Initialize selected countries with all countries
                selectedCountriesList = new ArrayList<Country>();
                selectedCountriesList.addAll(allCountriesList);
                // Return
                return allCountriesList;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Create view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.reg_country_picker, null);

        // Get countries from the json
        getAllCountries();

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Get view components
        searchEditText = (EditText) view
                .findViewById(R.id.reg_country_picker_search);

        FontLoader.getInstance().setTypeface(searchEditText,"CentraleSans-Book.OTF");
        countryListView = (ListView) view
                .findViewById(R.id.reg_country_picker_listview);

        // Set adapter
        adapter = new CountryAdapter(getActivity(), selectedCountriesList);
        countryListView.setAdapter(adapter);

        // Inform listener
        countryListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (listener != null) {
                    Country country = selectedCountriesList.get(position);
                    listener.onSelectCountry(country.getName(),
                            country.getCode());
                }
            }
        });

        // Search for which countries matched user query
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

        return view;
    }

    /**
     * Search allCountriesList contains text and put result into
     * selectedCountriesList
     *
     * @param text
     */
    @SuppressLint("DefaultLocale")
    private void search(String text) {
        selectedCountriesList.clear();

        for (Country country : allCountriesList) {
            if (country.getName().toLowerCase(Locale.ENGLISH)
                    .contains(text.toLowerCase())) {
                selectedCountriesList.add(country);
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Support sorting the countries list
     */
    @Override
    public int compare(Country lhs, Country rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }

}
