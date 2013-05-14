package com.wingedstone.babylearn;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ShareFragment extends Fragment implements OnClickListener{
	private Uri m_sharing_picture_uri;
	private ImageView m_imageview;
	
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
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new LoadBitmapTask().execute(m_sharing_picture_uri);
	}

	@Override
	public void onClick(View v) {
		
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
				bm = Utils.decodeSampleBitmapFromInputStream(ins, 600, 0, true); 
			}
			catch (Exception e) {
			}	
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result == null) {
				// error
				Log.e("zhangge", "empty bitmap loaded");
			}
			else {
				ShareFragment.this.m_imageview.setImageBitmap(result);
			}
		}
	}
}
