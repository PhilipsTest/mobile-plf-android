package com.philips.cdp.uikit.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.philips.cdp.uikit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class MultiStateControls extends ToggleButton {

    private static final String TAG = MultiStateControls.class.getSimpleName();

    private static final String KEY_BUTTON_STATES = "button_states";
    private static final String KEY_INSTANCE_STATE = "instance_state";

    List<View> buttons;
    boolean mMultipleChoice = false;
    private LinearLayout mainLayout;

    public MultiStateControls(Context context) {
        super(context, null);
        if (this.isInEditMode()) {
            return;
        }
    }

    public MultiStateControls(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (this.isInEditMode()) {
            return;
        }
        int[] set = {
                android.R.attr.entries
        };
        TypedArray a = context.obtainStyledAttributes(attrs, set);
        CharSequence[] texts = a.getTextArray(0);
        a.recycle();

        setElements(texts, null, new boolean[texts.length]);
    }

    /**
     * Set multiple buttons with the specified texts and default
     * initial values. Initial states are allowed, but both
     * arrays must be of the same size.
     *
     * @param texts            An array of CharSequences for the buttons
     * @param imageResourceIds an optional icon to show, either text, icon or both needs to be set.
     * @param selected         The default value for the buttons
     */
    public void setElements(CharSequence[] texts, int[] imageResourceIds, boolean[] selected) {
        final int textCount = texts != null ? texts.length : 0;
        final int iconCount = imageResourceIds != null ? imageResourceIds.length : 0;
        final int elementCount = Math.max(textCount, iconCount);
        if (elementCount == 0) {
            throw new IllegalArgumentException("neither texts nor images are setup");
        }

        boolean enableDefaultSelection = true;
        if (selected == null || elementCount != selected.length) {
            Log.d(TAG, "Invalid selection array");
            enableDefaultSelection = false;
        }

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mainLayout == null) {
            mainLayout = (LinearLayout) inflater.inflate(R.layout.uikit_controls, this, true);
        }
        mainLayout.removeAllViews();

        this.buttons = new ArrayList<>();
        for (int i = 0; i < elementCount; i++) {
            Button button;
            button = (Button) inflater.inflate(R.layout.uikit_toggle_button, mainLayout, false);
            button.setText(texts != null ? texts[i] : "");
            if (imageResourceIds != null && imageResourceIds[i] != 0) {
                button.setCompoundDrawablesWithIntrinsicBounds(imageResourceIds[i], 0, 0, 0);
            }
            final int position = i;
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    setValue(position);
                }

            });
            mainLayout.addView(button);
            if (enableDefaultSelection) {
                setButtonState(button, selected[i]);
            }
            this.buttons.add(button);
        }
    }

    public void setButtonState(View button, boolean selected) {
        if (button == null) {
            return;
        }
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.baseColor, R.attr.controlCount});
        int baseColor = typedArray.getColor(0, -1);
        int whiteColor = context.getResources().getColor(R.color.uikit_white);
        typedArray.recycle();
        button.setSelected(selected);
        if (selected) {
            button.setBackgroundColor(baseColor);
        } else {
            button.setBackgroundColor(whiteColor);
        }
        if (button instanceof Button) {
            int style = selected ? R.style.WhiteBoldText : R.style.PrimaryNormalText;
            ((AppCompatButton) button).setTextAppearance(this.getContext(), style);
        }
    }

    public int getValue() {
        for (int i = 0; i < this.buttons.size(); i++) {
            if (buttons.get(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }

    public void setValue(int position) {
        for (int i = 0; i < this.buttons.size(); i++) {
            if (mMultipleChoice) {
                if (i == position) {
                    View b = buttons.get(i);
                    if (b != null) {
                        setButtonState(b, !b.isSelected());
                    }
                }
            } else {
                if (i == position) {
                    setButtonState(buttons.get(i), true);
                } else if (!mMultipleChoice) {
                    setButtonState(buttons.get(i), false);
                }
            }
        }
        super.setValue(position);
    }

    public boolean[] getStates() {
        int size = this.buttons == null ? 0 : this.buttons.size();
        boolean[] result = new boolean[size];
        for (int i = 0; i < size; i++) {
            result[i] = this.buttons.get(i).isSelected();
        }
        return result;
    }

    public void setStates(boolean[] selected) {
        if (this.buttons == null || selected == null ||
                this.buttons.size() != selected.length) {
            return;
        }
        int count = 0;
        for (View b : this.buttons) {
            setButtonState(b, selected[count]);
            count++;
        }
    }

    /**
     * If multiple choice is enabled, the user can select multiple
     * values simultaneously.
     *
     * @param enable
     */
    public void enableMultipleChoice(boolean enable) {
        this.mMultipleChoice = enable;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putBooleanArray(KEY_BUTTON_STATES, getStates());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setStates(bundle.getBooleanArray(KEY_BUTTON_STATES));
            state = bundle.getParcelable(KEY_INSTANCE_STATE);
        }
        super.onRestoreInstanceState(state);
    }

}
