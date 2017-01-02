package com.philips.platform.uid.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.philips.platform.uid.R;
import com.philips.platform.uid.view.widget.EditText;

public class ClearEditTextIconHandler extends EditTextIconHandler {

    private Drawable drawable;

    public ClearEditTextIconHandler(@NonNull final EditText editText) {
        super(editText);
    }

    @Override
    public void processIconTouch(@NonNull final Drawable drawable, @NonNull final MotionEvent event) {
        editText.setText("");
        editText.setHint(editText.getHint());
    }

    @Override
    public Drawable getIconDrawable() {
        if (drawable == null) {
            drawable = getDrawable(R.drawable.uid_texteditbox_clear_icon);
        }
        return drawable;
    }
}
