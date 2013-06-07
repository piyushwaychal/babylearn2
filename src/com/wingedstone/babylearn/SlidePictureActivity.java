package com.wingedstone.babylearn;

import java.io.File;

import com.bladefury.sharekit.ShareManager;
import com.bladefury.sharekit.SupportFragmentActivity;
import com.bladefury.sharekit.impl_interface.IShareInstance;
import com.viewpagerindicator.LinePageIndicator;

import android.app.ActionBar;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SlidePictureActivity extends SupportFragmentActivity {
	
	// view container
	private ViewPager m_view_pager;
	private SlidePicturePagerAdapter m_view_slide_adapter;
	private LoadResourceTask m_load_task = null;
	private LinePageIndicator m_indicator;
	
	private boolean m_is_callback_from_camera = false;
	private Uri m_picture_uri;
	private String m_picture_path;
	
	@Override
	public void onCreate(Bundle saved_instance_state) {
		super.onCreate(saved_instance_state);
		ActionBar ab = getActionBar();
		ab.setCustomView(R.layout.actionbar_title_and_back);
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		ab.setDisplayShowHomeEnabled(false);
		View ab_root = ab.getCustomView();
		ImageButton back = (ImageButton) ab_root.findViewById(R.id.action_back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		TextView title = (TextView) ab_root.findViewById(R.id.action_title);
		title.setText(R.string.slide_activity_title);
		
		
		setContentView(R.layout.activity_pictureslide);
		m_view_pager = (ViewPager) findViewById(R.id.main_pager);
		m_view_slide_adapter = new SlidePicturePagerAdapter(getSupportFragmentManager());
		m_view_pager.setAdapter(m_view_slide_adapter);
		m_indicator = (LinePageIndicator) findViewById(R.id.pager_indicator);
		m_indicator.setViewPager(m_view_pager);
		m_indicator.setLineWidth(Configures.indicator_line_width);
		m_indicator.setStrokeWidth(Configures.indicator_stoke_width);
		
		dispatchIntent();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		dispatchIntent();
	}

	private void dispatchIntent() {
		Intent intent = getIntent();
		String key = intent.getStringExtra(Configures.intent_key_name);
		if (key != null) {
			m_load_task = new LoadResourceTask();
			m_load_task.execute(key);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
			String[] paths = {m_picture_path};
			MediaScannerConnection.scanFile(this, paths,
					null, null);
			
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
		File f = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
		m_picture_uri = Uri.fromFile(f);
		m_picture_path = f.getAbsolutePath();
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

	@Override
	public void onSuccess(int ins) {
		Toast.makeText(SlidePictureActivity.this, 
				getResources().getString(R.string.errcode_success), 
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFail(int ins, int reason) {
		switch (reason) {
		case IShareInstance.ShareCallbackHandler.REASON_USER_CANCEL:
			// do nothing
			break;

		default:
			Toast.makeText(SlidePictureActivity.this, 
					getResources().getString(R.string.errcode_unknown), 
					Toast.LENGTH_SHORT).show();
			break;
		}
	}


	@Override
	protected String getWeiboKey() {
		return Configures.weibo_appkey;
	}

	@Override
	protected String getWeiboRedirectUrl() {
		return Configures.weibo_redirect_url;
	}

	@Override
	protected String getWechatKey() {
		return Configures.wechat_appkey;
	}

}
