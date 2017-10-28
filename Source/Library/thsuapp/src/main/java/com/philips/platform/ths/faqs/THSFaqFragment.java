package com.philips.platform.ths.faqs;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.uappframework.listener.ActionBarListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class THSFaqFragment extends THSBaseFragment{
    public static final String TAG = THSFaqFragment.class.getSimpleName();

    THSFaqPresenter mThsFaqPresenter;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<FaqBean>> listDataChild;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.ths_faqs_expandable_list_layout, container, false);
        mThsFaqPresenter = new THSFaqPresenter(this);
        mThsFaqPresenter.getFaq();
        expListView = (ExpandableListView) view.findViewById(R.id.lv_expand);

        ActionBarListener actionBarListener = getActionBarListener();
        if(null != actionBarListener){
            actionBarListener.updateActionBar(getString(R.string.ths_how_it_works),true);
        }

        return view;
    }

    protected void updateFaqs(HashMap map){
        final Set set = map.keySet();

        listDataHeader = new ArrayList<>(set);
        listDataChild = new HashMap();
        listDataChild = map;

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        for(int i=0;i<listDataHeader.size();i++){
            expListView.expandGroup(i);
        }
    }
}
