package com.philips.cdp.digitalcare.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.philips.cdp.digitalcare.R;

/**
 * Created by philips on 6/21/17.
 */

public class CustomSearchView extends android.support.v7.widget.AppCompatAutoCompleteTextView {

    private OnHideListView onHideListView;

    public CustomSearchView(Context context) {
        super(context);
    }

    /**
     * @param context
     */
    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOnTouchListener(new RightDrawableOnTouchListener(this) {

            @Override
            public boolean onDrawableTouch(MotionEvent event) {
                CustomSearchView.this.setText("");
                onHideListView.hideListView();
                return false;
            }
        });

        this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uid_search_icon, 0, 0, 0);

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    CustomSearchView.this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uid_search_icon, 0, 0, 0);
                } else {
                    CustomSearchView.this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uid_search_icon,
                            0, R.drawable.uid_texteditbox_clear_icon, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * @param context
     */
    public CustomSearchView(Context context, AttributeSet attrs, int defStlye) {
        super(context);
    }

    public interface OnHideListView {
        void hideListView();
    }

    public void setOnHideListListener(OnHideListView onHideListListner){
            this.onHideListView = onHideListListner;
    }
}