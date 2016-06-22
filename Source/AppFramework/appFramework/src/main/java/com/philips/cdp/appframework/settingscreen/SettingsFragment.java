/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.appframework.settingscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.philips.cdp.appframework.AppFrameworkBaseFragment;
import com.philips.cdp.appframework.R;
import com.philips.cdp.registration.User;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * SettingsFragment is the Base Class of all existing fragments.
 *
 * @author: ritesh.jha@philips.com
 * @since: June 17, 2016
 */
public class SettingsFragment extends AppFrameworkBaseFragment {

    private ListViewSettings mAdapter = null;
    private ListView mList = null;
    private static String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.settings_screen_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_settings, container, false);

        mList = (ListView) view.findViewById(R.id.listwithouticon);

        String[] settingsItemArray = getActivity().getResources().getStringArray(R.array.settingsScreen_list);

        /*
        * Use asList method of Arrays class to convert Java String array to ArrayList
        */
        ArrayList<String> settingsItemList = new ArrayList<String>(Arrays.asList(settingsItemArray));

//        User userRegistration = new User(getActivity());
//
//        if(!userRegistration.isUserSignIn()){
//            settingsItemList = filterListForRegistration(settingsItemList);
//        }

        mAdapter = new ListViewSettings(getActivity(), settingsItemList);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("ListViewWithoutIcons")) {
                mAdapter.setSavedBundle(savedInstanceState.getBundle("ListViewWithoutIcons"));
            }
        }

        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                Toast.makeText(getActivity(), "settings clicked", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private ArrayList<String> filterListForRegistration(ArrayList<String> settingsItemList) {
        ArrayList<String> userRegistrationDependentList = new ArrayList<String>();
        userRegistrationDependentList.add(getStringText(R.string.settings_list_item_notify));
        userRegistrationDependentList.add(getStringText(R.string.settings_list_item_purchases));
        userRegistrationDependentList.add(getStringText(R.string.settings_list_item_order_history));

        settingsItemList.removeAll(userRegistrationDependentList);

        return settingsItemList;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("ListViewWithoutIcons", mAdapter.getSavedBundle());
    }
}
