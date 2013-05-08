package com.wingedstone.babylearn;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SlidePictureFragment extends Fragment {
	
	static final String parcel_key = "bitmap";
	
	private Bitmap m_bitmap;
	
	private ImageView m_image_view;
	
	static SlidePictureFragment newInstance(Bitmap bm) {
		final SlidePictureFragment f = new SlidePictureFragment();
		final Bundle args = new Bundle();
		args.putParcelable(parcel_key, bm);
		f.setArguments(args);
		return f;
	}
	
	public SlidePictureFragment () {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			m_bitmap = args.getParcelable(parcel_key);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = (View) inflater.inflate(R.layout.view_picture, container, false);
		m_image_view = (ImageView)rootView.findViewById(R.id.pic_image_view);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		m_image_view.setImageBitmap(m_bitmap);
	}
}
