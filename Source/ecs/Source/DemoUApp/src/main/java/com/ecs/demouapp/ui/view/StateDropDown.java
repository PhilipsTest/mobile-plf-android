package com.ecs.demouapp.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;


import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.adapters.UIPickerAdapter;
import com.philips.cdp.di.ecs.model.region.RegionsList;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.view.widget.UIPicker;

public class StateDropDown {

    public interface StateListener {
        void onStateSelect(View view, String state);

        void stateRegionCode(String regionCode);
    }

    private UIPicker mPopUp;
    private View mAnchor;
    private StateListener mStateListener;
    private Context mContext;
    private RegionsList mRegionList;


    public StateDropDown(StateListener stateListener) {
        mStateListener = stateListener;
        //createPopUp(anchor, context);
    }

    public void createPopUp(final View anchor, final Context context) {
        mAnchor=anchor;
        Context popupThemedContext = UIDHelper.getPopupThemedContext(context);
        mPopUp = new UIPicker(popupThemedContext);
        //TODO
       // mRegionList = CartModelContainer.getInstance().getRegionList();

        ArrayAdapter adapter = new UIPickerAdapter(popupThemedContext, R.layout.ecs_uipicker_item_text, createRowItems(mRegionList));
        mPopUp.setAdapter(adapter);
        mPopUp.setAnchorView(anchor);
        mPopUp.setModal(true);
        mPopUp.setOnItemClickListener(mListener);
    }

    String[] createRowItems(RegionsList regionsList) {
        String[] rowItems = new String[regionsList.getRegions().size()];

        if (regionsList != null) {
            for (int i = 0; i < regionsList.getRegions().size(); i++) {
                rowItems[i] = regionsList.getRegions().get(i).getName();
            }
        }
        return rowItems;
    }

    AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

            String isocode = mRegionList.getRegions().get(position).getIsocode();
            callOnStateSelect(isocode);
        }
    };

     void callOnStateSelect(String isocode) {

        String stateCode = isocode.substring(isocode.length() - 2);
        mStateListener.stateRegionCode(isocode);
        mStateListener.onStateSelect(mAnchor, stateCode);
        dismiss();
    }

    public void show() {
        if (!isShowing()) {
            mPopUp.show();
        }
    }

    public void dismiss() {
        mPopUp.dismiss();
    }

    public boolean isShowing() {
        return mPopUp.isShowing();
    }

}
