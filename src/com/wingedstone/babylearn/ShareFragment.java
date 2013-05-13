package com.wingedstone.babylearn;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class ShareFragment extends Fragment implements OnClickListener{
	private Bitmap m_sharing_picture;
	
	static ShareFragment newInstance(Bitmap bm) {
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
		final View rootView = (View) inflater.inflate(R.layout.fragment_take_picture, container, false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		
	}

	public Bitmap getSharingPicture() {
		return m_sharing_picture;
	}

	public void setSharingPicture(Bitmap m_sharing_picture) {
		this.m_sharing_picture = m_sharing_picture;
	}
}
