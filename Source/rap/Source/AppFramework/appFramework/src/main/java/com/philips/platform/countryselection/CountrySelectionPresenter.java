package com.philips.platform.countryselection;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

//import com.philips.cdp.registration.CountryComparator;
//import com.philips.cdp.registration.R;
//import com.philips.cdp.registration.dao.Country;
//import com.philips.cdp.registration.settings.RegistrationHelper;
//import com.philips.cdp.registration.ui.utils.RegUtility;
//import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class CountrySelectionPresenter {

    private final CountrySelectionContract countrySelectionContract;
    private Context mContext;
    private CountrySelectionFragmentView fragmentView;
    private FragmentActivity activity;

    public CountrySelectionPresenter(Context context, CountrySelectionContract countrySelectionContract) {
        mContext = context;
        activity = (FragmentActivity) context;
        this.countrySelectionContract = countrySelectionContract;
    }

    private static String[] defaultSupportedHomeCountries = new String[]{"AE", "BH", "BY","EG", "KW", "LB", "OM", "QA", "EG", "RW", "BG", "CZ", "DK", "AT", "CH", "DE", "GR", "AU", "CA", "GB", "HK", "ID", "IE", "IN", "MY", "NZ", "PH", "PK", "SA", "SG", "US", "ZA", "AR", "CL", "CO", "ES", "MX", "PE", "EE", "FI", "BE", "FR", "HR", "HU", "IT", "JP", "KR", "LT", "LV", "NL", "NO", "PL", "BR", "PT", "RO", "RU", "UA", "SI", "SK", "SE", "TH", "TR", "VN", "CN", "TW"};


    void fetchSupportedCountryList(Context context) {

        Set<Country> countryTreeSet = new TreeSet<>(new CountryComparator());

        for (String countryCode : defaultSupportedHomeCountries) {
            Country country = getCountry(countryCode, context);

            if (country.getCode().equalsIgnoreCase("TW")) {
                changeCountryNameToChineseTaipei(context, country);
            }
            countryTreeSet.add(country);
        }
        countrySelectionContract.updateRecyclerView(setSelectedCountryOnTopOfList(new ArrayList<>(countryTreeSet), context));
    }

    private ArrayList<Country> setSelectedCountryOnTopOfList(ArrayList<Country> countries, Context context) {


        String alreadySelectedCountryCode = getApplicationContext().getAppInfra().getServiceDiscovery().getHomeCountry();

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

    public void navigate(){
        BaseFlowManager targetFlowManager = getTargetFlowManager();
        BaseState baseState = null;
        try {
            baseState = targetFlowManager.getNextState(targetFlowManager.getCurrentState(), "launchRegistrationComponent");
        } catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                e) {
            Toast.makeText(mContext, mContext.getString(com.philips.platform.appframework.R.string.RA_something_wrong), Toast.LENGTH_SHORT).show();
        }

        if (null != baseState) {
            baseState.init(getApplicationContext());
            baseState.navigate(getFragmentLauncher());
        }
    }

   private FragmentLauncher getFragmentLauncher(){
        return new FragmentLauncher(activity,com.philips.platform.appframework.R.id.welcome_frame_container,(ActionBarListener) activity);
   }

    protected BaseFlowManager getTargetFlowManager() {
        return getApplicationContext().getTargetFlowManager();
    }

    protected AppFrameworkApplication getApplicationContext() {
        return (AppFrameworkApplication) mContext.getApplicationContext();
    }

    private static Country getCountry(String mSelectedCountryCode, Context mContext) {

        String key = getCountryKey(mSelectedCountryCode);
        String countryName = new Locale("", mSelectedCountryCode).getDisplayCountry();

        int identifier = mContext.getResources().getIdentifier(key, "string", mContext.getApplicationContext().getPackageName());

        if (identifier != 0) {
            try {
                countryName = mContext.getApplicationContext().getString(identifier);

            } catch (Exception resourcesNotFoundException) {
            }
        }

        return new Country(mSelectedCountryCode, countryName);

    }

    private static String getCountryKey(String mSelectedCountryCode) {
        return "REFAPP_Country_" + mSelectedCountryCode;
    }
}