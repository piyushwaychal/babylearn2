package com.wingedstone.babylearn;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class TakePictureFragment extends Fragment implements View.OnClickListener {
	
	private ImageButton m_take_picture_button;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = (View) inflater.inflate(R.layout.fragment_take_picture, container, false);
		m_take_picture_button = (ImageButton) rootView.findViewById(R.id.button_take_picture);
		m_take_picture_button.setOnClickListener(this);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == m_take_picture_button.getId()) {
			((SlidePictureActivity) getActivity()).startCamera();
		}
		
	}
	
}
