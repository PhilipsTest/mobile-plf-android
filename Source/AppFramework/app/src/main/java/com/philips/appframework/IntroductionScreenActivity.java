package com.philips.appframework;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.philips.cdp.uikit.UiKitActivity;
import com.philips.cdp.uikit.customviews.CircleIndicator;
import com.shamanland.fonticon.FontIconView;

/**
 * Created by 310240027 on 5/31/2016.
 */
public class IntroductionScreenActivity extends UiKitActivity {
    private FontIconView appframework_leftarrow,appframework_rightarrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.app_framework_introduction_activity);
        getWindow().getDecorView().setBackground(getResources().getDrawable(R.drawable.introduction_start_page_bg));
        final ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        appframework_leftarrow = (FontIconView) findViewById(R.id.appframework_leftarrow);
        appframework_rightarrow = (FontIconView) findViewById(R.id.appframework_rightarrow);

        final CircleIndicator mIndicator = (CircleIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);




        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(mPager.getCurrentItem()){
                    case 0: getWindow().getDecorView().setBackground(getResources().getDrawable(R.drawable.introduction_start_page_bg));
                        break;
                    case 1: getWindow().getDecorView().setBackground(getResources().getDrawable(R.drawable.introduction_center_page_bg));
                        break;
                    case 2: getWindow().getDecorView().setBackground(getResources().getDrawable(R.drawable.introduction_end_page_bg));
                        break;
                    default:
                        getWindow().getDecorView().setBackground(getResources().getDrawable(R.drawable.introduction_start_page_bg));

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        appframework_rightarrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mPager.getCurrentItem() < mPager.getRight())
                    mPager.setCurrentItem(mPager.getCurrentItem()+1,true);
            }
        });

        appframework_leftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPager.getCurrentItem() > mPager.getLeft())
                    mPager.setCurrentItem(mPager.getCurrentItem()-1,true);
            }
        });
    }
}
