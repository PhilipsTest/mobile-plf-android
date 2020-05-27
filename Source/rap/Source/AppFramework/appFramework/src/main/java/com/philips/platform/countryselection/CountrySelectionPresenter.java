package com.philips.platform.countryselection;

import android.content.Context;
import android.text.TextUtils;

import com.philips.cdp.registration.CountryComparator;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.dao.Country;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RegUtility;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class CountrySelectionPresenter {

    private final CountrySelectionContract countrySelectionContract;

    public CountrySelectionPresenter(CountrySelectionContract countrySelectionContract) {
        this.countrySelectionContract = countrySelectionContract;
    }

    void fetchSupportedCountryList(Context context) {

        Set<Country> countryTreeSet = new TreeSet<>(new CountryComparator());
        String[] countryCodes = RegUtility.supportedCountryList().toArray(new String[RegUtility.supportedCountryList().size()]);
        for (String countryCode : countryCodes) {
            Country country = RegUtility.getCountry(countryCode, context);

            if (country.getCode().equalsIgnoreCase("TW")) {
                changeCountryNameToChineseTaipei(context, country);
            }
            countryTreeSet.add(country);
        }
        countrySelectionContract.updateRecyclerView(setSelectedCountryOnTopOfList(new ArrayList<>(countryTreeSet), context));
    }

    private ArrayList<Country> setSelectedCountryOnTopOfList(ArrayList<Country> countries, Context context) {


        String alreadySelectedCountryCode = RegistrationHelper.getInstance().getCountryCode();

        if (TextUtils.isEmpty(alreadySelectedCountryCode)) return countries;

        int alreadySelectedCountryIndex = 0;
        Country alreadySelectedCountry = null;

        for (int i = 0; i < countries.size(); i++) {

            Country country = countries.get(i);
            if (country.getCode().equalsIgnoreCase(alreadySelectedCountryCode)) {
                alreadySelectedCountryIndex = i;
                alreadySelectedCountry = country;
                if (country.getCode().equalsIgnoreCase("TW")) {
                    changeCountryNameToTaiwan(context, country);
                }
                break;
            }
        }

        countries.remove(alreadySelectedCountryIndex);
        countries.add(0, alreadySelectedCountry);

        return countries;
    }

    private Country changeCountryNameToChineseTaipei(Context context, Country country) {
        country.setName(context.getString(R.string.USR_Country_TWGC));
        return country;
    }

    protected Country changeCountryNameToTaiwan(Context context, Country country) {
        country.setName(context.getString(R.string.USR_Country_TW));
        return country;
    }

}