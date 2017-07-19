/*
 * (C) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 *
 */
package com.philips.platform.uid.view.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.ListPopupWindow;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsSpinner;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

import com.philips.platform.uid.utils.UIDUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UIPicker extends ListPopupWindow{

    private static final String TAG = "UIPicker";
    private static Method sClipToWindowEnabledMethod;
    private static Method sGetMaxAvailableHeightMethod;
    private static Method sSetEpicenterBoundsMethod;

    static {
        try {
            sClipToWindowEnabledMethod = PopupWindow.class.getDeclaredMethod(
                    "setClipToScreenEnabled", boolean.class);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well.");
        }
        try {
            sGetMaxAvailableHeightMethod = PopupWindow.class.getDeclaredMethod(
                    "getMaxAvailableHeight", View.class, int.class, boolean.class);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "Could not find method getMaxAvailableHeight(View, int, boolean)"
                    + " on PopupWindow. Oh well.");
        }
        try {
            sSetEpicenterBoundsMethod = PopupWindow.class.getDeclaredMethod(
                    "setEpicenterBounds", Rect.class);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "Could not find method setEpicenterBounds(Rect) on PopupWindow. Oh well.");
        }
    }

    private Activity activity;
    private ListAdapter adapter;
    private boolean shouldSetGravity = true;
    private boolean shouldSetHeight = true;
    private boolean shouldSetWidth = true;
    private boolean isBelowAnchorView = false;
    private final int LIST_EXPAND_MAX = 1;
    private int adapterCount;

    public UIPicker(@NonNull Context context) {
        this(context, null, android.support.v7.appcompat.R.attr.listPopupWindowStyle);
        activity = (Activity) context;
    }

    public UIPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.listPopupWindowStyle);
    }

    public UIPicker(@NonNull Context context, @Nullable AttributeSet attrs,
                           @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public UIPicker(@NonNull Context context, @Nullable AttributeSet attrs,
                           @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setAdapter(@Nullable ListAdapter adapter) {
        super.setAdapter(adapter);
        this.adapter = adapter;
        adapterCount = adapter.getCount();
    }

    @Override
    public void setDropDownGravity(int dropDownGravity) {
        super.setDropDownGravity(dropDownGravity);
        shouldSetGravity = false;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        shouldSetWidth = false;
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        shouldSetHeight = false;
    }

    public void shouldShowBelowAnchorView(boolean isBelowAnchorView){
        this.isBelowAnchorView = isBelowAnchorView;
    }

    @Override
    public void show() {



        /*Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int winHeight = activity.getWindow().getDecorView().getHeight();
        int softButtonHeight = winHeight - rect.bottom;
        setHeight(winHeight-softButtonHeight);*/


        if(shouldSetGravity){
            setDropDownGravity(Gravity.END);
        }

        if(shouldSetWidth){
            setContentWidth(measureContentWidth(adapter));
        }

        if(shouldSetHeight){
            setContentHeight();
        }

        if(!isBelowAnchorView){
            setVerticalOffset(-getAnchorView().getHeight());
        }


        setListItemExpandMax(this, LIST_EXPAND_MAX);



        //popup.showAtLocation(rootView, Gravity.BOTTOM, 0, winHeight-rect.bottom);

        super.show();
        setSelection(UIPicker.this.getSelectedItemPosition());
    }

    private void setListItemExpandMax(ListPopupWindow listPopupWindow, int max) {

        try {
            Method method = ListPopupWindow.class.getDeclaredMethod("setListItemExpandMax", Integer.TYPE);
            method.setAccessible(true);
            method.invoke(listPopupWindow, max);

            Method m2 = ListPopupWindow.class.getDeclaredMethod("getMaxAvailableHeight", Boolean.TYPE);
            m2.setAccessible(true);
            method.invoke(listPopupWindow, getAnchorView(), -getAnchorView().getHeight(), false);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private int getMaxAvailableHeight(
            @NonNull View anchor, int yOffset) {
        Rect displayFrame = null;
        final Rect visibleDisplayFrame = new Rect();

        anchor.getWindowVisibleDisplayFrame(visibleDisplayFrame);

        displayFrame = visibleDisplayFrame;



        final int[] anchorPos = new int[2];
        anchor.getLocationOnScreen(anchorPos);

        final int bottomEdge = displayFrame.bottom;

        final int distanceToBottom;

            distanceToBottom = bottomEdge - anchorPos[1] - yOffset;

        final int distanceToTop = anchorPos[1] - displayFrame.top + yOffset;

        // anchorPos[1] is distance from anchor to top of screen
        int returnedHeight = Math.max(distanceToBottom, distanceToTop);
        final Rect mTempRect = new Rect();
        if (getBackground() != null) {
            getBackground().getPadding(mTempRect);
            returnedHeight -= mTempRect.top + mTempRect.bottom;
        }

        return returnedHeight;

    }

    private void setContentHeight(){
        int maxHeight = getMaxAvailableHeight(getAnchorView(), getAnchorView().getHeight());
        int contentHeight = adapterCount * Math.round(UIDUtils.pxFromDp(activity, 56));
        if(contentHeight < maxHeight){
            setHeight(contentHeight);
        }
        else {
            if(isBelowAnchorView)
                setHeight(maxHeight -getAnchorView().getHeight());
            else
                setHeight(maxHeight);
        }

    }


    private int measureContentWidth(ListAdapter adapter) {

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;

        ViewGroup mMeasureParent = null;
        float minWidth = UIDUtils.pxFromDp(activity, 56);
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();

        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(activity);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        if(maxWidth > minWidth && maxWidth < 2 * minWidth){
            return Math.round(2 * minWidth);
        } else if(maxWidth > 2 * minWidth && maxWidth < 3 * minWidth){
            return Math.round(3 * minWidth);
        } else if(maxWidth > 3 * minWidth && maxWidth < 6 * minWidth){
            return Math.round(6 * minWidth);
        } else if(maxWidth > 6 * minWidth && maxWidth < 7 * minWidth){
            return Math.round(7 * minWidth);
        } else if(maxWidth < widthPixels){
            return maxWidth;
        } else{
            return widthPixels - Math.round(UIDUtils.pxFromDp(activity, 32));
        }
    }



    /*static class SavedState extends View.BaseSavedState {
        long selectedId;
        int position;

        *//**
         * Constructor called from {@link AbsSpinner#onSaveInstanceState()}
         *//*
        SavedState(Parcelable superState) {
            super(superState);
        }

        *//**
         * Constructor called from {@link #CREATOR}
         *//*
        SavedState(Parcel in) {
            super(in);
            selectedId = in.readLong();
            position = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(selectedId);
            out.writeInt(position);
        }

        @Override
        public String toString() {
            return "UIPicker.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " selectedId=" + selectedId
                    + " position=" + position + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }*/

    /*@Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.selectedId = getSelectedItemId();
        if (ss.selectedId >= 0) {
            ss.position = getSelectedItemPosition();
        } else {
            ss.position = INVALID_POSITION;
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        if (ss.selectedId >= 0) {
            mDataChanged = true;
            mNeedSync = true;
            mSyncRowId = ss.selectedId;
            mSyncPosition = ss.position;
            mSyncMode = SYNC_SELECTED_POSITION;
            requestLayout();
        }
    }*/


    /*@Override
    public void setAnchorView(@Nullable View anchor) {
        super.setAnchorView(anchor);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        setHeight(heightPixels - anchor.getBottom());
    }*/

    /*@Override
    public void show() {
        setUIDUIPickerHeight();
        if(!isBelowAnchorView){
            setVerticalOffset(-getAnchorView().getHeight());
        }
        //setVerticalOffset(getAnchorView().getHeight());
        super.show();
    }*/

    /*public void shouldShowBelowAnchorView(boolean isBelowAnchorView){
        this.isBelowAnchorView = isBelowAnchorView;
    }*/

    /*private void setUIDUIPickerHeight(){
        if(getHeight() == ViewGroup.LayoutParams.WRAP_CONTENT){
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int heightPixels = metrics.heightPixels;
            //int widthPixels = metrics.widthPixels;

            int[] coordinatesArray = new int[2];
            getAnchorView().getLocationInWindow(coordinatesArray);
            int anchorViewHeight = getAnchorView().getHeight();
            //setHeight(800);
            setHeight(heightPixels - (coordinatesArray[1] + anchorViewHeight));
            //setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }*/
}
