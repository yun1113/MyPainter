package com.example.painter;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.TextView;

import java.lang.reflect.Field;

public class PersonalSetting extends ActionBarActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_setting);

		setActionBar();
	}


	private void setActionBar() {
		Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
		toolBar.setTitle(R.string.personal_setting);

		setSupportActionBar(toolBar);
		toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolBar.setOnMenuItemClickListener(onMenuItemClick);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);


	}

	private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener(){

		public boolean onMenuItemClick(MenuItem menuItem){
			switch (menuItem.getItemId()){
				case R.id.save:
					Log.e("TEST","save");
					break;
				case R.id.changPic:
					Log.e("TEST","changePic");
					break;
			}
			return true;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personal_setting, menu);
		//ActionBar actionBar = this.getSupportActionBar();
		//actionBar.setDisplayHomeAsUpEnabled(true);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		switch(item.getItemId()){
			case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
