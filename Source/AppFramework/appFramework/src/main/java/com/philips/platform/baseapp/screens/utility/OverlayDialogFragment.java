package com.philips.platform.baseapp.screens.utility;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.philips.platform.appframework.R;

/**
 * Created by philips on 8/10/17.
 */

public class OverlayDialogFragment extends DialogFragment {

    private static final String TAG = OverlayDialogFragment.class.getSimpleName();
    String stringId = null;
    int drawableId = 0;
    private static final String DESCRIPTION_TEXT =  "descriptionText";
    private static final String DRAWABLE_ID=  "drawableId";

    public static OverlayDialogFragment newInstance(String stringRes, int drawableResId) {
        OverlayDialogFragment overlayDialogFragment = new OverlayDialogFragment();

        Bundle args = new Bundle();
        args.putString(DESCRIPTION_TEXT, stringRes);
        args.putInt(DRAWABLE_ID, drawableResId);
        overlayDialogFragment.setArguments(args);

        return overlayDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stringId = getArguments().getString(DESCRIPTION_TEXT);
        drawableId = getArguments().getInt(DRAWABLE_ID);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.af_overlay_layout);
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        dialog.getWindow().setLayout(size.x, size.y);
        ColorDrawable colorDrawable =new ColorDrawable(getResources().getColor(R.color.overlay_background));
        colorDrawable.setAlpha(110);
        dialog.getWindow().setBackgroundDrawable(colorDrawable);
        try {
            dialog.findViewById(R.id.imageHelp).setBackgroundResource(drawableId);
        } catch (Exception e) {
            RALog.d(TAG, "Invalid drawable res id.");
        }
        ((TextView) dialog.findViewById(R.id.textView_overlay_subText)).setText(stringId);

        dialog.findViewById(R.id.dialogBackground).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return dialog;
    }

}
