package com.mec.demouapp;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.philips.platform.mec.screens.catalog.MECProductCatalogCategorizedFragment;
import com.philips.platform.mec.screens.catalog.MECProductCatalogFragment;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BaseDemoActivity extends AppCompatActivity implements ActionBarListener {
    private final int DEFAULT_THEME = R.style.Theme_DLS_Blue_UltraLight;
    private ImageView mBackImage;
    private TextView mTitleTextView;
    private FrameLayout mShoppingCart;
    private BaseDemoFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTheme();
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.base_demo_activity);
        showAppVersion();
        fragment=new BaseDemoFragment();
        actionBar();

        setFragment(fragment);

    }


    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_base_demo, fragment);
       // fragmentTransaction.addToBackStack(BaseDemoFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }



    private void showAppVersion() {
        String code = null;
        try {
            code = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            //IAPLog.e(IAPLog.LOG, e.getMessage());
        }
        TextView versionView = findViewById(R.id.demoappversion);
        versionView.setText(String.valueOf(code));
    }

    private void initTheme() {
        UIDHelper.injectCalligraphyFonts();
        int themeResourceID = new ThemeHelper(this).getThemeResourceId();
        int themeIndex = themeResourceID;
        if (themeIndex <= 0) {
            themeIndex = DEFAULT_THEME;
        }
        getTheme().applyStyle(themeIndex, true);
        UIDHelper.init(new ThemeConfiguration(this, ContentColor.ULTRA_LIGHT, NavigationColor.BRIGHT, AccentRange.ORANGE));

    }

    private void actionBar() {
        FrameLayout frameLayout = findViewById(R.id.mec_demo_app_header_back_button_framelayout);
        mShoppingCart = findViewById(R.id.mec_demo_app_shopping_cart_icon);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });

        mBackImage = findViewById(R.id.mec_demo_app_iv_header_back_button);
        Drawable mBackDrawable = VectorDrawableCompat.create(getResources(), R.drawable.mec_demo_app_back_arrow,getTheme());
        mBackImage.setBackground(mBackDrawable);
        mTitleTextView = findViewById(R.id.mec_demo_app_header_title);
        //fragment = new BaseDemoFragment();
        fragment.setTextView(mTitleTextView);
        setTitle(getString(R.string.demo_app_name));
      /*  mShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitleTextView.setText(title);
    }

    @Override
    public void updateActionBar(int resId, boolean enableBackKey) {

    }

    /**
     * For setting the title of action bar and to set back key Enabled/Disabled
     *
     * @param resString     The String to be displayed
     * @param enableBackKey To set back key enabled or disabled
     * @since 1.0.0
     */
    @Override
    public void updateActionBar(String resString, boolean enableBackKey) {

    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);
//    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFrag = fragmentManager.findFragmentById(R.id.container_base_demo);
        boolean backState = false;
        if (currentFrag != null && currentFrag instanceof BackEventListener) {
            backState = ((BackEventListener) currentFrag).handleBackEvent();
        }
        if (!backState) {
            super.onBackPressed();
        }
        //TODO
        if(currentFrag instanceof MECProductCatalogFragment || currentFrag instanceof MECProductCatalogCategorizedFragment){
            actionBar();
            showAppVersion();
        }

    }

}
