package com.philips.platform.uid.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.MotionEvent;

import com.philips.platform.uid.R;
import com.philips.platform.uid.view.widget.EditText;

public class PasswordEditTextIconHandler extends EditTextIconHandler {
    private EditText editText;
    private Drawable showPasswordDrawable;
    private Drawable hidePasswordDrawable;

    public PasswordEditTextIconHandler(final EditText editText) {
        super(editText);
        this.editText = editText;
        this.editText.setTextIsSelectable(false);
    }

    @Override
    public void handleTouch(final Drawable drawable, final MotionEvent event) {
        setShown(false);
        editText.setTransformationMethod(getToggledTransformationMethod());
        show();
    }

    @Override
    public Drawable getIconDrawable() {
        return editText.isPasswordVisible() ? getHidePasswordDrawable(editText.getContext().getTheme()) :
                getShowPasswordDrawable();
    }

    @Nullable
    private TransformationMethod getToggledTransformationMethod() {
        return editText.isPasswordVisible() ? PasswordTransformationMethod.getInstance() : null;
    }

    private Drawable getShowPasswordDrawable() {
        if (showPasswordDrawable == null) {
            showPasswordDrawable = getPasswordDrawable(R.drawable.uid_texteditbox_show_password_icon);
        }
        return showPasswordDrawable;
    }

    private VectorDrawableCompat getPasswordDrawable(final int drawableResourceId) {
        return VectorDrawableCompat.create(editText.getResources(), drawableResourceId, editText.getContext().getTheme());
    }

    private Drawable getHidePasswordDrawable(final Resources.Theme theme) {
        if (hidePasswordDrawable == null) {
            hidePasswordDrawable = getPasswordDrawable(R.drawable.uid_texteditbox_hide_password_icon);
        }
        return hidePasswordDrawable;
    }
}
