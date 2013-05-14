package com.wingedstone.babylearn;

import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class SlidePictureActivity extends FragmentActivity{
	
	// view container
	private ViewPager m_view_pager;
	private SlidePicturePagerAdapter m_view_slide_adapter;
	private LoadResourceTask m_load_task = null;
	private LinePageIndicator m_indicator;
	
	private boolean m_is_callback_from_camera = false;
	private Uri m_picture_uri;
	
	@Override
	public void onCreate(Bundle saved_instance_state) {
		super.onCreate(saved_instance_state);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.activity_pictureslide);
		m_view_pager = (ViewPager) findViewById(R.id.main_pager);
		m_view_slide_adapter = new SlidePicturePagerAdapter(getSupportFragmentManager());
		m_view_pager.setAdapter(m_view_slide_adapter);
		m_indicator = (LinePageIndicator) findViewById(R.id.pager_indicator);
		m_indicator.setViewPager(m_view_pager);
		m_indicator.setLineWidth(50);
		m_indicator.setStrokeWidth(5);
		Intent intent = getIntent();
		String key = intent.getStringExtra(Configures.intent_key_name);
		m_load_task = new LoadResourceTask();
		m_load_task.execute(key);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Configures.start_camera_request_code) {
			if (resultCode == RESULT_OK) {
				// camera returned picture
				// start ShareFragment , replace TakePictureFragment
				m_is_callback_from_camera = true;
			} else {
				// user cancelled?
				// do nothing
			}
			
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (m_is_callback_from_camera) {
			startShareFragment(m_picture_uri);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		m_is_callback_from_camera = false;
	}
	
	private void startShareFragment(Uri data) {
		m_view_slide_adapter.switchPicFragmentToShareFragment(data);
	}
	
	public boolean startCamera() {
		if (! Utils.isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE)) {
			return false;
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		m_picture_uri = Utils.getOutputMediaFileUri(Utils.MEDIA_TYPE_IMAGE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, m_picture_uri);
		startActivityForResult(intent, Configures.start_camera_request_code);
		return true;
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
				SlidePictureActivity.this.m_indicator.notifyDataSetChanged();
				m_load_task = null;
			}
		}	
		
		protected void onPreExecute() {
			m_load_task = this;
		}		
	}

}
