package com.philips.cdp.ui.catalog.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.cdp.ui.catalog.R;
import com.philips.cdp.uikit.drawable.VectorDrawable;

/**
 * <b></b> ActionBarLauncher is class to demonstrate the use of Action Up Button </b>
 * <p/>
 * <p/>
 * <b></b>Inorder to use Make use of this, infalte the custom Layout (uikit_action_bar.xml) to the Android default layout</b><br>
 * <pre>
 * ActionBar mActionBar = this.getSupportActionBar();
 * mActionBar.setDisplayShowHomeEnabled(false);
 * mActionBar.setDisplayShowTitleEnabled(false);
 * ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
 * ActionBar.LayoutParams.MATCH_PARENT,
 * ActionBar.LayoutParams.WRAP_CONTENT,
 * Gravity.CENTER);
 * View mCustomView = LayoutInflater.from(this).inflate(R.layout.uikit_action_bar, null); // layout which contains your button.
 * mActionBar.setCustomView(mCustomView, params);
 * mActionBar.setDisplayShowCustomEnabled(true);
 * </pre>
 */
public class ActionBarLauncher extends CatalogActivity {

    /**
     * Get the ActionBar and inflate Custom Action Bar and also set onClickListener for the arrow Button
     * <li>Infalte uikit_action_bar.xml to the Android default Action Bar</li>
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar mActionBar = this.getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.uikit_action_bar, null); // layout which contains your button.

        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.text);

        FrameLayout frameLayout = (FrameLayout) mCustomView.findViewById(R.id.UpButton);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                finish();
            }
        });

        ImageView arrowImage = (ImageView) mCustomView
                .findViewById(R.id.arrow);
        arrowImage.setImageDrawable(VectorDrawable.create(this, R.drawable.uikit_up_arrow));

        mActionBar.setCustomView(mCustomView, params);
        mActionBar.setDisplayShowCustomEnabled(true);

        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
    }
}
