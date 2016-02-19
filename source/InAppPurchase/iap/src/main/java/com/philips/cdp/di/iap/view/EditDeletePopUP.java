/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.uikit.customviews.UIKitListPopupWindow;
import com.philips.cdp.uikit.drawable.VectorDrawable;
import com.philips.cdp.uikit.utils.RowItem;

import java.util.ArrayList;
import java.util.List;

public class EditDeletePopUP {

    private final int OPTION_EDIT = 0;
    private final int OPTION_DELETE = 1;

    public static final String EVENT_EDIT = "event_edit";
    public static final String EVENT_DELETE = "event_delete";

    UIKitListPopupWindow mPopUp;
        AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            String event = position == OPTION_EDIT ? EVENT_EDIT : EVENT_DELETE;
            EventHelper.getInstance().notifyEventOccurred(event);
            dismiss();
        }
    };

    public EditDeletePopUP(Context context, View anchor) {
        mPopUp = new UIKitListPopupWindow(context, anchor,
                UIKitListPopupWindow.UIKIT_Type.UIKIT_BOTTOMLEFT, createRowItems(context));
        mPopUp.setOnItemClickListener(mListener);
    }

    public void show() {
        mPopUp.show();
    }

    public void dismiss() {
        mPopUp.dismiss();
    }

    private List<RowItem> createRowItems(Context context) {
        List<RowItem> rowItems = new ArrayList<RowItem>();
        String edit = context.getResources().getString(R.string.iap_edit);
        String delete = context.getResources().getString(R.string.iap_delete);
        String[] desc = {edit, delete};
        rowItems.add(new RowItem(VectorDrawable.create(context, R.drawable.iap_edit_icon_17x17), desc[0]));
        rowItems.add(new RowItem(ContextCompat.getDrawable(context, R.drawable.iap_trash_bin), desc[1]));
        return rowItems;
    }

    public boolean isShowing() {
        return mPopUp.isShowing();
    }
}