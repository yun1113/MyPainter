package com.example.painter;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import db_connect.DBConnector;
import gallerymaterial.BaseActivity;
import slidingtab.GalleryViewPageAdapter;
import slidingtab.SlidingTabLayout;

public class Gallery extends BaseActivity {

	Toolbar toolbar;
	ViewPager pager;
	GalleryViewPageAdapter adapter;
	SlidingTabLayout tabs;
	CharSequence Titles[] = {"My Gallery", "Collections"};
	int Numboftabs = 2;
	Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		// Creating The Toolbar and setting it as the Toolbar for the activity
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);

		bundle = getIntent().getExtras();

		// Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
		adapter = new GalleryViewPageAdapter(getSupportFragmentManager(), Titles, Numboftabs,bundle);

		// Assigning ViewPager View and setting the adapter
		pager = (ViewPager) findViewById(R.id.view_pager);
		pager.setAdapter(adapter);

		// Assiging the Sliding Tab Layout View
		tabs = (SlidingTabLayout) findViewById(R.id.tabs);
		tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

		// Setting Custom Color for the Scroll bar indicator of the Tab View
		tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
			@Override
			public int getIndicatorColor(int position) {
				return getResources().getColor(R.color.gorse);
			}
		});

		// Setting the ViewPager For the SlidingTabsLayout
		tabs.setViewPager(pager);
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_gallery;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}