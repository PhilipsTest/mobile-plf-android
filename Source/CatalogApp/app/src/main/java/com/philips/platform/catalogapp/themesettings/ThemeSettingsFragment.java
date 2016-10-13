/*
 * (C) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 *
 */

package com.philips.platform.catalogapp.themesettings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.philips.platform.catalogapp.R;
import com.philips.platform.catalogapp.ThemeColorHelper;
import com.philips.platform.catalogapp.ThemeHelper;
import com.philips.platform.catalogapp.fragments.BaseFragment;
import com.philips.platform.uit.thememanager.ColorRange;
import com.philips.platform.uit.thememanager.ContentTonalRange;
import com.philips.platform.uit.thememanager.NavigationColor;
import com.philips.platform.uit.thememanager.UITHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ThemeSettingsFragment extends BaseFragment {

    ColorRange colorRange = ColorRange.GROUP_BLUE;
    String tonalRange;

    @Bind(R.id.colorRangeList)
    RecyclerView colorRangeListview;

    @Bind(R.id.tonalRangeList)
    RecyclerView tonalRangeListview;

    @Bind(R.id.notificationBarList)
    RecyclerView notificationBarListview;

    @Bind(R.id.accentColorRangeList)
    RecyclerView accentColorRangeList;

    @Bind(R.id.warningText)
    TextView warningText;

    private ThemeColorHelper themeColorHelper;

    int colorPickerWidth = 48;
    SharedPreferences defaultSharedPreferences;
    private ThemeColorAdapter themeColorAdapter;
    private ThemeColorAdapter tonalRangeAdapter;
    private ThemeColorAdapter navigationListAdapter;

    private ContentTonalRange contentTonalRange = ContentTonalRange.ULTRA_LIGHT;
    private NavigationColor navigationColor = NavigationColor.ULTRA_LIGHT;
    private ThemeHelper themeHelper;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_theme_settings, container, false);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        ButterKnife.bind(this, view);
        themeColorHelper = new ThemeColorHelper();
        themeHelper = new ThemeHelper(PreferenceManager.getDefaultSharedPreferences(getContext()));
        colorRange = themeHelper.initColorRange();
        navigationColor = themeHelper.initNavigationRange();
        contentTonalRange = themeHelper.initTonalRange();

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                initColorPickerWidth();
                buildColorRangeList();
                buildContentTonalRangeList(colorRange);
                buildNavigationList(colorRange);

                buildAccentColorsList(colorRange);
            }
        });

        return view;
    }

    private void initColorPickerWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        setLayoutWidthForTablet(widthPixels);

        float pageMargin = getResources().getDimension(R.dimen.themeSettingsPageMargin);
        colorPickerWidth = (int) ((widthPixels - pageMargin) / 8);
    }

    private void setLayoutWidthForTablet(int widthPixels) {
        if (widthPixels > 1200) {
            final View settingLayout = getView().findViewById(R.id.setting_layout);
            final ViewGroup.LayoutParams layoutParams = settingLayout.getLayoutParams();
            layoutParams.width = widthPixels / 2;
            widthPixels = widthPixels / 2;
        }
    }

    private void updateThemeSettingsLayout() {
        buildContentTonalRangeList(colorRange);
    }

    private void buildColorRangeList() {
        colorRangeListview.setAdapter(getColorRangeAdapter());

        setLayoutOrientation(colorRangeListview);
    }

    @NonNull
    private ThemeColorAdapter getColorRangeAdapter() {
        themeColorAdapter = new ThemeColorAdapter(themeColorHelper.getColorRangeItemsList(), new ThemeChangedListener() {
            @Override
            public void onColorRangeChanged(final String changedColorRange) {
                colorRange = ColorRange.valueOf(changedColorRange.toUpperCase());
                final SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                edit.putString(UITHelper.COLOR_RANGE, changedColorRange.toUpperCase());
                edit.commit();
                updateTonalRangeColors();
                updateNavigationRangeColors();
            }
        }, colorPickerWidth);
        return themeColorAdapter;
    }

    private void updateNavigationRangeColors() {
        tonalRangeAdapter.setColorModels(themeColorHelper.getContentTonalRangeItemsList(colorRange, getContext()));
    }

    private void updateTonalRangeColors() {
        navigationListAdapter.setColorModels(themeColorHelper.getNavigationColorRangeItemsList(colorRange, getContext()));
    }

    private void buildContentTonalRangeList(final ColorRange changedColorRange) {
        tonalRangeListview.setAdapter(getTonalRangeAdapter(changedColorRange));

        setLayoutOrientation(tonalRangeListview);
    }

    @NonNull
    private ThemeColorAdapter getTonalRangeAdapter(final ColorRange changedColorRange) {
        tonalRangeAdapter = new ThemeColorAdapter(themeColorHelper.getContentTonalRangeItemsList(changedColorRange, getContext()), new ThemeChangedListener() {
            @Override
            public void onColorRangeChanged(final String tonalRangeChanged) {
                final ThemeColorAdapter adapter = (ThemeColorAdapter) tonalRangeListview.getAdapter();
                final int selectedPosition = adapter.getSelectedPosition();
                final ContentTonalRange[] values = ContentTonalRange.values();
                final ContentTonalRange tonalRange = values[values.length - selectedPosition - 1];
                final SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                edit.putString(UITHelper.CONTENT_TONAL_RANGE, tonalRange.name());
                edit.commit();
                ThemeSettingsFragment.this.tonalRange = tonalRangeChanged;
            }
        }, colorPickerWidth);
        return tonalRangeAdapter;
    }

    private void buildNavigationList(final ColorRange colorRange) {
        notificationBarListview.setAdapter(getNavigationListAdapter(colorRange));

        setLayoutOrientation(notificationBarListview);
    }

    @NonNull
    private ThemeColorAdapter getNavigationListAdapter(final ColorRange colorRange) {
        navigationListAdapter = new ThemeColorAdapter(themeColorHelper.getNavigationColorRangeItemsList(colorRange, getContext()), new ThemeChangedListener() {
            @Override
            public void onColorRangeChanged(final String changedColorRange) {
                final ThemeColorAdapter adapter = (ThemeColorAdapter) notificationBarListview.getAdapter();
                final int selectedPosition = adapter.getSelectedPosition();
            }
        }, colorPickerWidth);
        return navigationListAdapter;
    }

    private void buildAccentColorsList(final ColorRange colorRange) {
        accentColorRangeList.setAdapter(new ThemeColorAdapter(themeColorHelper.getAccentColorsList(getContext(), colorRange), new ThemeChangedListener() {
            @Override
            public void onColorRangeChanged(final String changedColorRange) {
                updateThemeSettingsLayout();
            }
        }, colorPickerWidth));

        setLayoutOrientation(accentColorRangeList);
    }

    private void setLayoutOrientation(final RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public int getTitle() {
        return R.string.tittle_theme_settings;
    }
}

