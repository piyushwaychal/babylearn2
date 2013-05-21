package com.wingedstone.babylearn;

import java.io.File;

import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class SlidePictureActivity extends FragmentActivity implements IWeiboHandler.Response{
	
	// view container
	private ViewPager m_view_pager;
	private SlidePicturePagerAdapter m_view_slide_adapter;
	private LoadResourceTask m_load_task = null;
	private LinePageIndicator m_indicator;
	
	private boolean m_is_callback_from_camera = false;
	private Uri m_picture_uri;
	private String m_picture_path;
	private String m_title;
	
	private IWeiboAPI m_weibo_api = null;
	
	@Override
	public void onCreate(Bundle saved_instance_state) {
		super.onCreate(saved_instance_state);
		//getActionBar().setTitle()
		ActionBar ab = getActionBar();
		ab.setLogo(R.drawable.ic_action_back);
		ab.setCustomView(R.layout.actionbar_slide);
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		ab.setHomeButtonEnabled(true);
		
		setContentView(R.layout.activity_pictureslide);
		m_view_pager = (ViewPager) findViewById(R.id.main_pager);
		m_view_slide_adapter = new SlidePicturePagerAdapter(getSupportFragmentManager());
		m_view_pager.setAdapter(m_view_slide_adapter);
		m_indicator = (LinePageIndicator) findViewById(R.id.pager_indicator);
		m_indicator.setViewPager(m_view_pager);
		m_indicator.setLineWidth(Configures.indicator_line_width);
		m_weibo_api = WeiboSDK.createWeiboAPI(this, Configures.weibo_appkey);
		m_indicator.setStrokeWidth(Configures.indicator_stoke_width);
		dispatchIntent();
//		TextView v = (TextView) ab.getCustomView().findViewById(R.id.text_content);
//		v.setText(m_title);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.slide, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		dispatchIntent();
	}

	private void dispatchIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();
		if (action == "com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY") {
			m_weibo_api.responseListener(intent, this);
		} else {
			String key = intent.getStringExtra(Configures.intent_key_name);
			m_load_task = new LoadResourceTask();
			m_load_task.execute(key);
		}
	}
	
	public void shareToWeibo(Bitmap bm) {
		ImageObject imageObject = new ImageObject();
		imageObject.setImageObject(bm);
		// 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		// 图片消息
		weiboMessage.mediaObject = imageObject;
		// 初始化从三方到微博的消息请求
		SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
		req.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
		req.message = weiboMessage;
		// 发送请求消息到微博
		m_weibo_api.sendRequest(this, req);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_share:
			Toast.makeText(this, getResources().getString(R.string.share_not_available), Toast.LENGTH_LONG).show();			
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
			//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
			//sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(
		    //          Environment.DIRECTORY_DCIM))));
			String[] paths = {m_picture_path};
			MediaScannerConnection.scanFile(this, paths,
					null, null);
			Toast.makeText(this,
					getResources().getString(R.string.save_photo_success), 
					Toast.LENGTH_LONG)
					.show();
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
	public void onResponse(BaseResponse baseResp) {
	   switch (baseResp.errCode) {
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_OK:
            Toast.makeText(this, "成功！！", Toast.LENGTH_LONG).show();
            // :TODO disable share button
            break;
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, baseResp.errMsg + ":失败！！", Toast.LENGTH_LONG).show();
            break;
        }
	}

}
