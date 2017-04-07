/*
 * (C) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 *
 */
package com.philips.platform.catalogapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.philips.platform.catalogapp.MainActivity;
import com.philips.platform.catalogapp.NavigationController;
import com.philips.platform.catalogapp.R;
import com.philips.platform.uid.drawable.SeparatorDrawable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class ComponentListFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    ListView listView;
    private HashMap<Integer, String> itemsMap = new HashMap<Integer, String>();
    private NavigationController navigationController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_component_list, container, false);
        listView = (ListView) view.findViewById(R.id.componentList);
        navigationController = ((MainActivity) getActivity()).getNavigationController();
        setListItems();
        return view;
    }

    private void setListItems() {
        String[] strings = getDemoItems().values().toArray(new String[1]);
        final SeparatorDrawable separatorDrawable = new SeparatorDrawable(getContext());
        listView.setDivider(separatorDrawable);
        listView.setDividerHeight(separatorDrawable.getHeight());

        listView.setAdapter(new ArrayAdapter<>(this.getContext(), R.layout.component_list_text, strings));
        listView.setOnItemClickListener(this);
    }

    private Map<Integer, String> getDemoItems() {
        itemsMap = new LinkedHashMap<Integer, String>();
        itemsMap.put(0, getString(R.string.page_title_buttons));
        itemsMap.put(1, getString(R.string.page_title_textbox));
        itemsMap.put(2, getString(R.string.page_title_settings));
        itemsMap.put(3, getString(R.string.page_title_alertDialog));
        itemsMap.put(4, getString(R.string.page_title_progress_bar));
        itemsMap.put(5, getString(R.string.page_title_label));
        itemsMap.put(6, getString(R.string.page_title_checkbox));
        itemsMap.put(7, getString(R.string.page_title_separator));
        itemsMap.put(8, getString(R.string.page_title_recyclerview));
        itemsMap.put(9, getString(R.string.page_title_date_time_picker));
        itemsMap.put(10, getString(R.string.page_title_gridView));
        itemsMap.put(11, getString(R.string.page_title_notification_bar));
        itemsMap.put(12, getString(R.string.page_title_accent));
        return sortMap(itemsMap);
    }

    private Map<Integer, String> sortMap(final HashMap<Integer, String> map) {
        TreeMap<Integer, String> sortedMap = new TreeMap<>(new IntegerComparator(map));

        sortedMap.putAll(map);

        return sortedMap;
    }

    private int getKeyFromValue(String value) {
        for (Map.Entry<Integer, String> entry : itemsMap.entrySet()) {
            if (entry.getValue().equals(value))
                return entry.getKey();
        }
        return 0;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        int key = getKeyFromValue((String) textView.getText());
        // TODO: 9/13/2016 : Handle this properly with enums. Right now enable so that we can test buttons
        switch (key) {
            case 0:
                navigationController.switchFragment(new ButtonFragment());
                break;
            case 1:
                navigationController.switchFragment(new EditTextFragment());
                break;
            case 2:
                navigationController.switchFragment(new ToggleSwitchFragment());
                break;
            case 3:
                navigationController.switchFragment(new DialogComponentFragment());
                break;
            case 4:
                navigationController.switchFragment(new ProgressBarFragment());
                break;
            case 5:
                navigationController.switchFragment(new LabelFragment());
                break;
            case 6:
                navigationController.switchFragment(new CheckBoxFragment());
                break;
            case 7:
                navigationController.switchFragment(new SeparatorFragment());
                break;
            case 8:
                navigationController.switchFragment(new RecyclerViewFragment());
                break;
            case 9:
                navigationController.switchFragment(new PickersFragment());
                break;
            case 10:
                navigationController.switchFragment(new GridViewFragment());
                break;
            case 11:
                navigationController.switchFragment(new NotificationBarFragment());
            case 12:
                navigationController.switchFragment(new AccentFragment());
                break;
        }
    }

    @Override
    public int getPageTitle() {
        return R.string.catalog_app_name;
    }

    private static class IntegerComparator implements Comparator<Integer> {
        private final HashMap<Integer, String> map;

        public IntegerComparator(final HashMap<Integer, String> map) {
            this.map = map;
        }

        @Override
        public int compare(final Integer key1, final Integer key2) {
            return map.get(key1).compareTo(map.get(key2));
        }
    }
}