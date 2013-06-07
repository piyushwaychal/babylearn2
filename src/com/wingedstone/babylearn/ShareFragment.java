package com.wingedstone.babylearn;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.bladefury.sharekit.ShareContent;
import com.bladefury.sharekit.ShareContentFactory;
import com.bladefury.sharekit.ShareManager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ShareFragment extends Fragment implements OnClickListener{
	private Uri m_sharing_picture_uri;
	private ImageView m_imageview;
	private ImageButton m_share_button;
	private Button m_wechat_session;
	private Button m_wechat_timeline;
	
	static ShareFragment newInstance(Uri bm) {
		final ShareFragment sf = new ShareFragment();
		sf.setSharingPicture(bm);
		return sf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = (View) inflater.inflate(R.layout.fragment_share, container, false);
		m_imageview = (ImageView) rootView.findViewById(R.id.preview_picture);
		m_share_button = (ImageButton) rootView.findViewById(R.id.share_weibo);
		m_wechat_session = (Button) rootView.findViewById(R.id.share_wechat_session);
		m_wechat_timeline = (Button) rootView.findViewById(R.id.share_wechat_timeline);
		m_wechat_session.setOnClickListener(this);
		m_wechat_timeline.setOnClickListener(this);
		m_share_button.setOnClickListener(this);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new LoadBitmapTask().execute(m_sharing_picture_uri);
	}

	@Override
	public void onClick(View v) {
		SlidePictureActivity activity = (SlidePictureActivity) getActivity();
		ShareContent c = ShareContentFactory.createShareContentFromImageFile(
				m_sharing_picture_uri.getPath(), 
				getResources().getString(R.string.app_name),
				"",
				getResources().getString(R.string.share_weibo_default));		
		
		switch (v.getId()) {
		case R.id.share_weibo:
			activity.mShareKit.shareContent(ShareManager.WEIBO, c);						
			break;
		case R.id.share_wechat_session:
			if(activity.mShareKit.canShare(ShareManager.WECHAT_SESSION)) {
				activity.mShareKit.shareContent(ShareManager.WECHAT_SESSION, c);							
			}
			else {
				Toast.makeText(activity, getResources().getString(R.string.wechat_not_installed), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.share_wechat_timeline:
			if (activity.mShareKit.canShare(ShareManager.WECHAT_TIMELINE)) {
				activity.mShareKit.shareContent(ShareManager.WECHAT_TIMELINE, c);										
			}
			else {
				Toast.makeText(activity, getResources().getString(R.string.wechat_not_installed), Toast.LENGTH_SHORT).show();				
			}
			break;
		}
	}

	public Uri getSharingPicture() {
		return m_sharing_picture_uri;
	}

	public void setSharingPicture(Uri m_sharing_picture) {
		this.m_sharing_picture_uri = m_sharing_picture;
	}

	private class LoadBitmapTask extends AsyncTask<Uri, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(Uri... params) {
			File file = new File(params[0].getPath());
			Bitmap bm = null;
			try {
				InputStream ins = new FileInputStream(file);
				bm = Utils.decodeSampleBitmapFromInputStream(ins,
						Configures.GetRequestWidth(getActivity()),
						true, getActivity()); 
			}
			catch (Exception e) {
			}	
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result == null) {
				// error
				Log.e("zhangge", "empty bitmap loaded");
				Toast.makeText(ShareFragment.this.getActivity(),
						getResources().getString(R.string.save_photo_fail), 
						Toast.LENGTH_LONG)
						.show();
			}
			else {
				Toast.makeText(ShareFragment.this.getActivity(),
						getResources().getString(R.string.save_photo_success), 
						Toast.LENGTH_SHORT)
						.show();
				ShareFragment.this.m_imageview.setImageBitmap(result);
				m_share_button.setVisibility(View.VISIBLE);
				m_wechat_session.setVisibility(View.VISIBLE);
				m_wechat_timeline.setVisibility(View.VISIBLE);
			}
		}
	}
}
