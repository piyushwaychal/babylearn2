package com.wingedstone.babylearn;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

public class SlidePictureActivity extends FragmentActivity{
	
	// view container
	private ViewPager m_view_pager;
	private SlidePicturePagerAdapter m_view_slide_adapter;
	private LoadResourceTask m_load_task = null;
	
	public void onCreate(Bundle saved_instance_state) {
		super.onCreate(saved_instance_state);
		
		setContentView(R.layout.activity_pictureslide);
		m_view_pager = (ViewPager) findViewById(R.id.main_pager);
		m_view_slide_adapter = new SlidePicturePagerAdapter(getSupportFragmentManager());
		m_view_pager.setAdapter(m_view_slide_adapter);
		Intent intent = getIntent();
		String key = intent.getStringExtra(Configures.intent_key_name);
		m_load_task = new LoadResourceTask();
		m_load_task.execute(key);
	}
	
	private class LoadResourceTask extends AsyncTask<String, Integer, ResourceFile> {

		@Override
		protected ResourceFile doInBackground(String... params) {
			ResourceFile rf = ResourceFileManager.getResourceIfExists(params[0], SlidePictureActivity.this);
			if (rf != null && rf.prepareForSlides()) {
				return rf;
			}
			return null;
		}
		
		protected void onPostExecute(ResourceFile rf) {
			if (rf == null) {
				// severe failure
			}
			else {
				SlidePictureActivity.this.m_view_slide_adapter.SetBitMapList(rf.getSlidePictures());
				SlidePictureActivity.this.m_view_slide_adapter.notifyDataSetChanged();
				m_load_task = null;
			}
		}	
		
		protected void onPreExecute() {
			m_load_task = this;
		}		
	}

}
