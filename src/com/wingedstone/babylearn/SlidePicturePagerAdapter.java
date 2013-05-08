package com.wingedstone.babylearn;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SlidePicturePagerAdapter extends FragmentStatePagerAdapter {
	
	private ArrayList<Bitmap> m_bm_list = null;
	
	public SlidePicturePagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public void SetBitMapList(ArrayList<Bitmap> bml) {
		m_bm_list = bml;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		if (m_bm_list != null && arg0 < m_bm_list.size()) {
			return SlidePictureFragment.newInstance(m_bm_list.get(arg0));
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (m_bm_list == null) {
			return 0;
		}
		return m_bm_list.size();
	}

}
