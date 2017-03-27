package com.philips.platform.catalogapp.fragments;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.platform.catalogapp.R;
import com.philips.platform.catalogapp.dataUtils.GridDataHelper;
import com.philips.platform.catalogapp.databinding.FragmentGridviewSettingsBinding;


public class GridViewSettingsFragment extends BaseFragment {


    public ObservableBoolean isSecondaryActionEnabled = new ObservableBoolean(Boolean.TRUE);
    public ObservableBoolean isDarkBackroungEnabled = new ObservableBoolean(Boolean.FALSE);
    public ObservableBoolean isEnlargedGutterEnabled = new ObservableBoolean(Boolean.FALSE);
    public ObservableBoolean isGridDisableEnabled = new ObservableBoolean(Boolean.FALSE);
    public ObservableInt templateSelection = new ObservableInt();

    private GridDataHelper gridDataHelper;
    private FragmentGridviewSettingsBinding gridviewSettingsBinding;

    @Override
    public int getPageTitle() {
        return R.string.page_title_gridview_settings;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gridviewSettingsBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_gridview_settings,container,false);
        gridviewSettingsBinding.setFrag(this);

        gridDataHelper = new GridDataHelper(getContext());
        initSavedSettings();

        return gridviewSettingsBinding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        getCheckedRadioButtonTemplate();
        gridDataHelper.setSecondaryActionEnabled(isSecondaryActionEnabled.get());
        gridDataHelper.setDarkBackgroundEnabled(isDarkBackroungEnabled.get());
        gridDataHelper.setEnlargedGutterEnabled(isEnlargedGutterEnabled.get());
        gridDataHelper.setSetDisableStateEnabled(isGridDisableEnabled.get());
        gridDataHelper.setTemplateSelection(templateSelection.get());

    }

    private void initSavedSettings(){
        setSecondaryActionEnabled(gridDataHelper.isSecondaryActionEnabled());
        setDarkBackgroundEnabled(gridDataHelper.isDarkBackgroundEnabled());
        setEnlargedGutterEnabled(gridDataHelper.isEnlargedGutterEnabled());
        setGridDisableEnabled(gridDataHelper.isSetDisableStateEnabled());
        setTemplateSelection(gridDataHelper.getTemplateSelection());
    }

    public void setSecondaryActionEnabled(boolean isSecondaryActionEnabled){
        this.isSecondaryActionEnabled.set(isSecondaryActionEnabled);
    }

    public void setDarkBackgroundEnabled(boolean isDarkBackroundEnabled){
        this.isDarkBackroungEnabled.set(isDarkBackroundEnabled);
    }

    public void setEnlargedGutterEnabled(boolean isEnlargedGutterEnabled){
        this.isEnlargedGutterEnabled.set(isEnlargedGutterEnabled);
    }

    public void setGridDisableEnabled(boolean isGridDisableEnabled){
        this.isGridDisableEnabled.set(isGridDisableEnabled);
    }

    public void setTemplateSelection(int templateSelection){
        this.templateSelection.set(templateSelection);
    }

    private void getCheckedRadioButtonTemplate() {
        int checkedRadioButtonId = gridviewSettingsBinding.radioGroupTemplates.getCheckedRadioButtonId();
        switch (checkedRadioButtonId){
            case R.id.without_secondary_content:
                setTemplateSelection(1);
                break;
            case R.id.double_line_footer:
                setTemplateSelection(2);
                break;
            case R.id.single_line_header:
                setTemplateSelection(3);
                break;
            default:
                setTemplateSelection(1);
        }
    }
}
