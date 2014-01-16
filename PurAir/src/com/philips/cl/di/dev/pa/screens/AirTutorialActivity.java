package com.philips.cl.di.dev.pa.screens;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.screens.adapters.ViewPagerAdapter;
import com.philips.cl.di.dev.pa.util.Fonts;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AirTutorialActivity extends ActionBarActivity {

	private ViewPagerAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	private ActionBar mActionBar;
	private ScrollView mScrollViewRight;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mActionBarDrawerToggle;
	private Menu menu;
	private static final int[] TITLE_LIST= new int[]{
		R.string.tutorial_title_1,
		R.string.tutorial_title_2,
		R.string.tutorial_title_3,
		R.string.tutorial_title_4,
		R.string.tutorial_title_5
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.air_tutorial);
		initActionBar();

		mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);

		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mIndicator = indicator;
		indicator.setViewPager(mPager);
		indicator.setSnap(true);

		final float density = getResources().getDisplayMetrics().density;
		indicator.setPageColor(0xFF5D6577);
		indicator.setFillColor(0xFFB9BBC7);   
		indicator.setStrokeWidth(0.1f*density);

		//to change ActionBar title on each swipe
		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				setActionBarTitle(TITLE_LIST[position]);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	/*Initialize action bar */
	private void initActionBar() {
		mActionBar = getSupportActionBar();
		mActionBar.setIcon(null);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		mActionBar.setCustomView(R.layout.action_bar);	
		setActionBarTitle(R.string.tutorial_title_1);
	}

	/*Sets Action bar title */
	public void setActionBarTitle(int tutorialTitle) {    	
		TextView textView = (TextView) findViewById(R.id.action_bar_title);
		textView.setTypeface(Fonts.getGillsansLight(this));
		textView.setTextSize(24);
		textView.setText(this.getText(tutorialTitle));
	}

	/*Sets the right menu*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
		MenuItem item = menu.getItem(0);		
		item.setIcon(R.drawable.close_icon_blue);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		switch (item.getItemId()) {
		case R.id.right_menu:
			AirTutorialActivity.this.finish();
			break;
		}
		return false;
	}

}
