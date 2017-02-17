package com.philips.platform.catalogapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.philips.platform.catalogapp.MainActivity;
import com.philips.platform.catalogapp.R;
import com.philips.platform.catalogapp.dataUtils.GridAdapter;
import com.philips.platform.catalogapp.dataUtils.GridData;
import com.philips.platform.catalogapp.dataUtils.GridDataHelper;

import java.util.ArrayList;

public class GridViewFragment extends BaseFragment {

    private GridView gridView;
    private RelativeLayout relativeLayout;
    private ArrayList<GridData> cardList;
    GridAdapter adapter;

    @Override
    public int getPageTitle() {
        return R.string.page_title_gridView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        populateList();

        GridDataHelper gridDataHelper = new GridDataHelper(getContext());

        View view = inflater.inflate(R.layout.fragment_gridview, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.trigger_gridview_settings);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getNavigationController().switchFragment(new GridViewSettingsFragment());
            }
        });

        int color = gridDataHelper.isDarkBackgroundEnabled() ? R.color.uidColorBlack : R.color.uidColorWhite;
        gridView.setBackgroundResource(color);

        int spacing = gridDataHelper.isEnlargedGutterEnabled() ? R.dimen.grid_spacing_enlarged : R.dimen.grid_spacing_normal;
        gridView.setHorizontalSpacing(getResources().getDimensionPixelSize(spacing));
        gridView.setVerticalSpacing(getResources().getDimensionPixelSize(spacing));

        adapter = new GridAdapter(getContext(), cardList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                GridData gridData = cardList.get(position);
                gridData.toggleFavorite();
                cardList.remove(position);
                cardList.add(position, gridData);
                gridView.invalidateViews();
                adapter.updateGrid(cardList);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void populateList() {
        Context mContext = getContext();
        cardList = new ArrayList<>();
        cardList.add(new GridData(R.drawable.gridview_asset_1, mContext.getString(R.string.gridView_title_short), mContext.getString(R.string.gridView_description_short), false));
        cardList.add(new GridData(R.drawable.gridview_asset_2, mContext.getString(R.string.gridView_title_short), mContext.getString(R.string.gridView_description_short), false));
        cardList.add(new GridData(R.drawable.gridview_asset_3, mContext.getString(R.string.gridView_title_short), mContext.getString(R.string.gridView_description_short), false));
        cardList.add(new GridData(R.drawable.gridview_asset_4, mContext.getString(R.string.gridView_title_short), mContext.getString(R.string.gridView_description_short), false));
        cardList.add(new GridData(R.drawable.gridview_asset_5, mContext.getString(R.string.gridView_title_short), mContext.getString(R.string.gridView_description_short), false));
        cardList.add(new GridData(R.drawable.gridview_asset_6, mContext.getString(R.string.gridView_title_short), mContext.getString(R.string.gridView_description_short), false));
        cardList.add(new GridData(R.drawable.gridview_asset_7, mContext.getString(R.string.gridView_title_long), mContext.getString(R.string.gridView_description_long), false));
    }
}
