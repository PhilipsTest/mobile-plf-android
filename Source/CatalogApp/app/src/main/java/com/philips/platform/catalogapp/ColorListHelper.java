/*
 * (C) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 *
 */

package com.philips.platform.catalogapp;

import android.content.Context;
import android.content.res.Resources;

import com.philips.platform.catalogapp.themesettings.ColorModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ColorListHelper {
    public int[] getDefaultColorRangeArray() {
        return new int[]{
                R.color.uikit_group_blue_level_45,
                R.color.uikit_blue_level_45,
                R.color.uikit_aqua_level_45,
                R.color.uikit_green_level_45,
                R.color.uikit_orange_level_45,
                R.color.uikit_pink_level_45,
                R.color.uikit_purple_level_45,
                R.color.uikit_gray_level_45};
    }

    public int[] getTonalColors(final Resources resources, final String colorRangeResourceName, final String packageName) {
        return new int[]{
                getColorResourceId(resources, colorRangeResourceName, "80", packageName),
                getColorResourceId(resources, colorRangeResourceName, "50", packageName),
                getColorResourceId(resources, colorRangeResourceName, "35", packageName),
                getColorResourceId(resources, colorRangeResourceName, "20", packageName),
                R.color.uikitColorWhite
        };
    }

    int getColorResourceId(final Resources resources, final String basecolor, final String level, final String packageName) {
        return resources.getIdentifier(String.format(Locale.getDefault(), "uikit_%s_level_%s", basecolor, level), "color", packageName);
    }

    List<ColorModel> getTonalRangeItemsList(final String changedColorRange, final Context context) {
        final List<ColorModel> tonalRangelist = new ArrayList<>();
        int[] color = getTonalColors(context.getResources(), changedColorRange, context.getPackageName());
        tonalRangelist.add(new ColorModel("VD", color[0]));
        tonalRangelist.add(new ColorModel("B", color[1]));
        tonalRangelist.add(new ColorModel("L", color[2]));
        tonalRangelist.add(new ColorModel("VL", color[3]));
        tonalRangelist.add(new ColorModel("UL", color[4]));
        return tonalRangelist;
    }

    List<ColorModel> getColorRangeItemsList() {
        final List<ColorModel> colorRangeModelsList = new ArrayList<>();
        int[] color = getDefaultColorRangeArray();
        colorRangeModelsList.add(new ColorModel("GB", "group_blue", color[0]));
        colorRangeModelsList.add(new ColorModel("Bl", "blue", color[1]));
        colorRangeModelsList.add(new ColorModel("Aq", "aqua", color[2]));
        colorRangeModelsList.add(new ColorModel("Gr", "green", color[3]));
        colorRangeModelsList.add(new ColorModel("Or", "orange", color[4]));
        colorRangeModelsList.add(new ColorModel("Pi", "pink", color[5]));
        colorRangeModelsList.add(new ColorModel("Pu", "purple", color[6]));
        colorRangeModelsList.add(new ColorModel("Gr", "gray", color[7]));

        return colorRangeModelsList;
    }

    public List<ColorModel> getNavigationColorRangeItemsList(final String colorRange, final Context context) {
        return getTonalRangeItemsList(colorRange, context);
    }

    public List<ColorModel> getDimColors(final String packageName, final Resources resources, final String colorResourcePlaceHolder) {

        final List<ColorModel> dimColorsList = new ArrayList<>();

        final int[] color = getDimColorArray(resources, packageName, colorResourcePlaceHolder);
        dimColorsList.add(new ColorModel("90", color[0]));
        dimColorsList.add(new ColorModel("85", color[1]));
        return dimColorsList;
    }

    private int[] getDimColorArray(final Resources resources, final String packageName, final String colorResourcePlaceHolder) {
        return new int[]{
                getColorResourceId(resources, colorResourcePlaceHolder, "90", packageName),
                getColorResourceId(resources, colorResourcePlaceHolder, "85", packageName),
        };
    }

    public List<ColorModel> getPrimaryColors(final Resources resources, final String colorResourcePlaceHolder, final String packageName) {
        final List<ColorModel> primaryColors = new ArrayList<>();
        final int[] color = getPrimarycontrolColors(resources, colorResourcePlaceHolder, packageName);
        primaryColors.add(new ColorModel("UL", color[0]));
        primaryColors.add(new ColorModel("VL", color[1]));
        primaryColors.add(new ColorModel("L", color[2]));
        primaryColors.add(new ColorModel("B", color[3]));
        primaryColors.add(new ColorModel("VD", color[4]));
        return primaryColors;
    }

    private int[] getPrimarycontrolColors(final Resources resources, final String colorResourcePlaceHolder, final String packageName) {
        return new int[]{
                getColorResourceId(resources, colorResourcePlaceHolder, "45", packageName),
                getColorResourceId(resources, colorResourcePlaceHolder, "45", packageName),
                getColorResourceId(resources, colorResourcePlaceHolder, "75", packageName),
                getColorResourceId(resources, colorResourcePlaceHolder, "75", packageName),
                getColorResourceId(resources, colorResourcePlaceHolder, "45", packageName),
        };
    }

    public List<ColorModel> getAccentColorsList(final Context context, final String colorRange) {
        return getAccentColorRangeItemsList();
    }

    List<ColorModel> getAccentColorRangeItemsList() {
        final List<ColorModel> colorRangeModelsList = new ArrayList<>();
        int[] color = getDefaultColorRangeArray();
        colorRangeModelsList.add(new ColorModel("GB", color[0]));
        colorRangeModelsList.add(new ColorModel("Bl", color[1]));
        colorRangeModelsList.add(new ColorModel("Aq", color[2]));
        colorRangeModelsList.add(new ColorModel("Gr", color[3]));
        colorRangeModelsList.add(new ColorModel("Or", color[4]));
        colorRangeModelsList.add(new ColorModel("Pi", color[5]));
        colorRangeModelsList.add(new ColorModel("Pu", color[6]));
        colorRangeModelsList.add(new ColorModel("Gr", color[7]));

        return colorRangeModelsList;
    }
}

